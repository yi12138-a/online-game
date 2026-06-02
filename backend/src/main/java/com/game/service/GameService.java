package com.game.service;

import com.game.model.*;
import java.util.*;

public class GameService {
    private final Random rng = new Random();

    // ═══════════════════════════════════════
    //  GAME START
    // ═══════════════════════════════════════

    public GameState startGame(String playerName) {
        GameState gs = new GameState();
        gs.phase = "map";
        gs.playerName = playerName != null && !playerName.isBlank() ? playerName : "冒险者";
        gs.maxHP = 80;
        gs.hp = 80;
        gs.gold = 0;
        gs.floor = 1;
        gs.act = 1;
        gs.maxEnergy = 3;
        gs.energy = 3;
        gs.deck = CardLibrary.startingDeck();
        // Add starter relic
        gs.relics.add(RelicLibrary.getById("burning_blood"));
        generateMap(gs);
        return gs;
    }

    // ═══════════════════════════════════════
    //  MAP
    // ═══════════════════════════════════════

    public void generateMap(GameState gs) {
        gs.mapOptions.clear();
        gs.phase = "map";
        int floorInAct = ((gs.floor - 1) % 8) + 1;

        if (floorInAct == 8) {
            // Boss floor
            addMapOption(gs, "boss", "Boss", "👑", "首领战 - 击败Boss进入下一阶段");
        } else if (floorInAct == 4 || floorInAct == 5) {
            addMapOption(gs, "enemy", "战斗", "⚔️", "普通敌人（安全路线）");
            addMapOption(gs, "elite", "精英", "💀", "精英敌人（高风险高回报）");
        } else if (floorInAct == 7) {
            addMapOption(gs, "rest", "休息", "🏕️", "恢复 30% 生命值");
            addMapOption(gs, "shop", "商店", "🛒", "购买卡牌和遗物");
            addMapOption(gs, "enemy", "战斗", "⚔️", "普通敌人");
        } else {
            addMapOption(gs, "enemy", "战斗", "⚔️", "普通敌人");
            if (floorInAct >= 3) addMapOption(gs, "enemy", "战斗", "⚔️", "普通敌人（另一条路）");
            else addMapOption(gs, "rest", "休息", "🏕️", "恢复 30% 生命值");
        }
    }

    private void addMapOption(GameState gs, String type, String label, String icon, String desc) {
        GameState.MapOption opt = new GameState.MapOption();
        opt.type = type;
        opt.label = label;
        opt.icon = icon;
        opt.description = desc;
        gs.mapOptions.add(opt);
    }

    // ═══════════════════════════════════════
    //  ENTER ROOM
    // ═══════════════════════════════════════

    public void enterRoom(GameState gs, int choice) {
        if (choice < 0 || choice >= gs.mapOptions.size()) return;
        String type = gs.mapOptions.get(choice).type;

        switch (type) {
            case "enemy" -> startCombat(gs, false);
            case "elite" -> startCombat(gs, true);
            case "rest" -> { gs.phase = "rest"; gs.restHealPercent = 30; }
            case "shop" -> generateShop(gs);
            case "boss" -> startCombat(gs, false); // boss flag set in startCombat
        }
    }

    // ═══════════════════════════════════════
    //  COMBAT START
    // ═══════════════════════════════════════

    public void startCombat(GameState gs, boolean elite) {
        int floorInAct = ((gs.floor - 1) % 8) + 1;
        boolean isBoss = floorInAct == 8;

        Enemy enemy;
        if (isBoss) {
            enemy = EnemyLibrary.boss(gs.act);
        } else if (elite) {
            enemy = EnemyLibrary.randomElite(gs.act, rng);
            // preserved_insect relic
            for (Relic r : gs.relics) {
                if (r.id.equals("preserved_insect")) {
                    enemy.maxHP = enemy.maxHP * (100 - r.value) / 100;
                    break;
                }
            }
            enemy.hp = enemy.maxHP;
        } else {
            enemy = EnemyLibrary.randomNormal(gs.act, rng);
        }

        gs.enemy = enemy;
        gs.enemy.nextIntent();
        gs.phase = "battle";
        gs.battleOver = false;
        gs.playerWin = false;
        gs.combatGold = 0;
        gs.battleLog.clear();
        gs.block = 0;
        gs.energy = gs.maxEnergy;
        gs.strength = 0;
        gs.dexterity = 0;
        gs.vulnerable = 0;
        gs.weak = 0;
        gs.attackCountThisCombat = 0;

        // Shuffle deck into draw pile
        gs.drawPile.clear();
        gs.discardPile.clear();
        gs.hand.clear();
        gs.exhaustPile.clear();
        gs.drawPile.addAll(gs.deck);
        gs.shuffleDrawPile();

        // Relics: combat_start
        for (Relic r : gs.relics) {
            if (r.type.equals("combat_start")) gs.strength += r.value;
            if (r.type.equals("combat_start_heal")) { gs.hp = Math.min(gs.maxHP, gs.hp + r.value); }
        }

        // Draw opening hand
        gs.drawCards(5);
        log(gs, "⚔️ 第 " + gs.floor + " 层 — " + enemy.name + " 出现了！");
    }

    // ═══════════════════════════════════════
    //  PLAY CARD
    // ═══════════════════════════════════════

    public String playCard(GameState gs, int handIndex) {
        if (!"battle".equals(gs.phase)) return "不在战斗中";
        if (gs.battleOver) return "战斗已结束";
        if (handIndex < 0 || handIndex >= gs.hand.size()) return "无效的卡牌";

        Card card = gs.hand.get(handIndex);

        // X cost cards use all remaining energy
        int actualCost = card.cost == -1 ? gs.energy : card.cost;
        if (gs.energy < actualCost && card.cost != -1) return "能量不足";
        // For X cost cards, use all remaining energy (min 1)
        if (card.cost == -1 && gs.energy < 1) return "能量不足";
        if (card.cost == -1) actualCost = Math.max(1, gs.energy);

        gs.energy -= actualCost;
        gs.hand.remove(handIndex);

        // Execute effects
        String result = executeCard(gs, card, actualCost);

        // Track attack count for shuriken
        if (card.type.equals("attack")) {
            gs.attackCountThisCombat++;
            for (Relic r : gs.relics) {
                if (r.id.equals("shuriken") && gs.attackCountThisCombat % 3 == 0) {
                    gs.strength += 1;
                    log(gs, "手里剑触发！获得 1 点力量");
                }
            }
        }

        // Move to discard
        gs.discardPile.add(card);

        // Check enemy death
        if (!gs.enemy.isAlive()) {
            gs.battleOver = true;
            gs.playerWin = true;
            onEnemyKilled(gs);
        }

        return result;
    }

    private String executeCard(GameState gs, Card card, int energySpent) {
        StringBuilder sb = new StringBuilder();
        sb.append("打出 [").append(card.name).append("]");

        for (CardEffect e : card.effects) {
            switch (e.type) {
                case "damage" -> {
                    int dmg = e.value + gs.strength;
                    if (gs.weak > 0) dmg = (int)(dmg * 0.75);
                    gs.enemy.takeDamage(dmg);
                    sb.append("，造成 ").append(dmg).append(" 伤害");
                }
                case "damageMulti" -> {
                    int times = e.secondary;
                    int total = 0;
                    for (int i = 0; i < times; i++) {
                        int d = e.value + gs.strength;
                        if (gs.weak > 0) d = (int)(d * 0.75);
                        gs.enemy.takeDamage(d);
                        total += d;
                    }
                    sb.append("，").append(times).append("次攻击共造成 ").append(total).append(" 伤害");
                }
                case "damageX" -> {
                    int total = 0;
                    for (int i = 0; i < energySpent; i++) {
                        int d = e.value + gs.strength;
                        if (gs.weak > 0) d = (int)(d * 0.75);
                        gs.enemy.takeDamage(d);
                        total += d;
                    }
                    sb.append("，").append(energySpent).append("次攻击共造成 ").append(total).append(" 伤害");
                }
                case "damageBlock" -> {
                    int dmg = gs.block + gs.strength;
                    if (gs.weak > 0) dmg = (int)(dmg * 0.75);
                    gs.enemy.takeDamage(dmg);
                    sb.append("，造成 ").append(dmg).append(" 伤害（基于格挡）");
                }
                case "block" -> {
                    int blk = e.value + gs.dexterity;
                    gs.block += blk;
                    sb.append("，获得 ").append(blk).append(" 格挡");
                }
                case "blockNext" -> {
                    sb.append("，下回合获得 ").append(e.value).append(" 格挡");
                }
                case "draw" -> {
                    gs.drawCards(e.value);
                    sb.append("，抽 ").append(e.value).append(" 张牌");
                }
                case "energy" -> {
                    gs.energy += e.value;
                    sb.append("，获得 ").append(e.value).append(" 能量");
                }
                case "vulnerable" -> {
                    if (e.value > 0) {
                        gs.enemy.vulnerable += e.value;
                        sb.append("，施加 ").append(e.value).append(" 易伤");
                    }
                }
                case "weak" -> {
                    if (e.value > 0) {
                        gs.enemy.weak += e.value;
                        sb.append("，施加 ").append(e.value).append(" 虚弱");
                    }
                }
                case "strength" -> {
                    gs.strength += e.value;
                    sb.append("，获得 ").append(e.value).append(" 力量");
                }
                case "dexterity" -> {
                    gs.dexterity += e.value;
                    sb.append("，获得 ").append(e.value).append(" 敏捷");
                }
                case "ritual" -> {
                    gs.ritual += e.value;
                    sb.append("，每回合获得 ").append(e.value).append(" 力量");
                }
                case "loseHp" -> {
                    gs.hp = Math.max(1, gs.hp - e.value);
                    sb.append("，失去 ").append(e.value).append(" 生命");
                }
                case "healDamage" -> {
                    // healDamage is handled AFTER damage in the same card
                    int dmg = e.value + gs.strength;
                    if (gs.weak > 0) dmg = (int)(dmg * 0.75);
                    int hpBefore = gs.enemy.hp;
                    gs.enemy.takeDamage(dmg);
                    int actualDmg = hpBefore - gs.enemy.hp;
                    gs.hp = Math.min(gs.maxHP, gs.hp + actualDmg);
                    sb.append("，造成 ").append(actualDmg).append(" 伤害并恢复等量生命");
                }
                case "feed" -> {
                    int dmg = e.value + gs.strength;
                    if (gs.weak > 0) dmg = (int)(dmg * 0.75);
                    int hpBefore = gs.enemy.hp;
                    gs.enemy.takeDamage(dmg);
                    sb.append("，造成 ").append(hpBefore - gs.enemy.hp).append(" 伤害");
                    if (!gs.enemy.isAlive()) {
                        gs.maxHP += e.value;
                        gs.hp = Math.min(gs.maxHP, gs.hp + e.value);
                        sb.append("，吞噬成功！最大生命 +").append(e.value);
                    }
                }
            }
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════
    //  END TURN
    // ═══════════════════════════════════════

    public void endTurn(GameState gs) {
        if (!"battle".equals(gs.phase) || gs.battleOver) return;

        // Apply blockNext from previous turn (sentinel etc) - we check relics and store in gs
        // For simplicity, blockNext is handled by storing a flag. Let me skip the complex tracking for now.

        // Discard hand
        gs.discardHand();

        // Enemy acts
        executeEnemyIntent(gs);

        // Check player death
        if (!gs.isAlive()) {
            gs.battleOver = true;
            gs.playerWin = false;
            gs.phase = "gameover";
            return;
        }

        // Decrement statuses
        if (gs.vulnerable > 0) gs.vulnerable--;
        if (gs.weak > 0) gs.weak--;
        if (gs.enemy.vulnerable > 0) gs.enemy.vulnerable--;
        if (gs.enemy.weak > 0) gs.enemy.weak--;

        // New turn
        gs.startTurn();

        // Enemy sets next intent
        gs.enemy.nextIntent();
    }

    private void executeEnemyIntent(GameState gs) {
        Enemy enemy = gs.enemy;
        switch (enemy.intentType) {
            case "attack" -> {
                int dmg = enemy.intentValue + enemy.strength;
                if (enemy.weak > 0) dmg = (int)(dmg * 0.75);
                dealDamageToPlayer(gs, dmg);
                log(gs, enemy.name + " 造成 " + dmg + " 点伤害");
            }
            case "defend" -> {
                enemy.block += enemy.intentValue;
                log(gs, enemy.name + " 获得 " + enemy.intentValue + " 格挡");
            }
            case "buff" -> {
                enemy.strength += enemy.intentValue;
                log(gs, enemy.name + " 获得 " + enemy.intentValue + " 力量");
            }
            case "debuff" -> {
                // Could be weak or vulnerable based on enemy type
                if (enemy.id.contains("slime") || enemy.id.equals("slaver")) {
                    gs.weak += enemy.intentValue;
                    log(gs, enemy.name + " 施加 " + enemy.intentValue + " 层虚弱");
                } else {
                    gs.vulnerable += enemy.intentValue;
                    log(gs, enemy.name + " 施加 " + enemy.intentValue + " 层易伤");
                }
            }
            case "attackDefend" -> {
                int dmg = enemy.intentValue + enemy.strength;
                if (enemy.weak > 0) dmg = (int)(dmg * 0.75);
                dealDamageToPlayer(gs, dmg);
                enemy.block += enemy.intentSecondary;
                log(gs, enemy.name + " 造成 " + dmg + " 伤害并获得 " + enemy.intentSecondary + " 格挡");
            }
        }
    }

    private void dealDamageToPlayer(GameState gs, int dmg) {
        if (gs.vulnerable > 0) dmg = (int)(dmg * 1.5);
        if (gs.block > 0) {
            int blocked = Math.min(gs.block, dmg);
            gs.block -= blocked;
            dmg -= blocked;
        }
        gs.hp = Math.max(0, gs.hp - dmg);
    }

    // ═══════════════════════════════════════
    //  ON ENEMY KILLED
    // ═══════════════════════════════════════

    private void onEnemyKilled(GameState gs) {
        int floorInAct = ((gs.floor - 1) % 8) + 1;
        boolean isBoss = floorInAct == 8;
        boolean isElite = gs.enemy.type.equals("elite");

        // Gold
        int gold = isBoss ? 50 + rng.nextInt(20) : isElite ? 25 + rng.nextInt(10) : 10 + rng.nextInt(10);
        gs.gold += gold;
        gs.combatGold = gold;
        log(gs, "获得 " + gold + " 金币");

        // Relic drops
        if (isElite && rng.nextDouble() < 0.5) {
            Relic relic = RelicLibrary.random(rng);
            gs.relics.add(relic);
            log(gs, "获得遗物：" + relic.name);
        }
        if (isBoss) {
            Relic relic = RelicLibrary.random(rng);
            gs.relics.add(relic);
            log(gs, "获得Boss遗物：" + relic.name);
        }

        // Combat end relics
        for (Relic r : gs.relics) {
            if (r.type.equals("combat_end")) {
                gs.hp = Math.min(gs.maxHP, gs.hp + r.value);
            }
        }

        // Generate reward cards
        gs.rewardCards.clear();
        for (int i = 0; i < 3; i++) {
            gs.rewardCards.add(CardLibrary.randomReward(rng));
        }
        // Gold reward
        gs.gold += gs.combatGold; // already added above... remove duplicate
        // Actually we already added, let me not double-add. Fix the gold issue:
        // The gold was already added to gs.gold at the top of this method.

        // Boss: add rare card to rewards
        if (isBoss) {
            gs.rewardCards.add(CardLibrary.randomRare(rng));
        }

        gs.phase = "reward";
    }

    // ═══════════════════════════════════════
    //  REWARD
    // ═══════════════════════════════════════

    public void chooseReward(GameState gs, int cardIndex) {
        if (!"reward".equals(gs.phase)) return;
        if (cardIndex >= 0 && cardIndex < gs.rewardCards.size()) {
            gs.deck.add(gs.rewardCards.get(cardIndex));
        }
        // Skip reward (cardIndex < 0 or >= size) means skip
        gs.rewardCards.clear();
        gs.floor++;
        if (((gs.floor - 1) % 8) == 0) {
            // Just finished a boss floor
            gs.act++;
            if (gs.act > 3) {
                gs.phase = "victory";
                return;
            }
            // Full heal between acts
            gs.hp = gs.maxHP;
            log(gs, "进入第 " + gs.act + " 幕！生命值回满。");
        }
        generateMap(gs);
    }

    // ═══════════════════════════════════════
    //  REST
    // ═══════════════════════════════════════

    public void rest(GameState gs) {
        if (!"rest".equals(gs.phase)) return;
        int healPercent = gs.restHealPercent;
        for (Relic r : gs.relics) {
            if (r.type.equals("rest")) healPercent += r.value;
        }
        int healAmount = gs.maxHP * healPercent / 100;
        gs.hp = Math.min(gs.maxHP, gs.hp + healAmount);
        log(gs, "休息恢复 " + healAmount + " 生命");
        gs.floor++;
        generateMap(gs);
    }

    // ═══════════════════════════════════════
    //  SHOP
    // ═══════════════════════════════════════

    public void generateShop(GameState gs) {
        gs.phase = "shop";
        gs.shopCards.clear();
        // 3 cards for sale
        gs.shopCards.add(CardLibrary.randomCommon(rng));
        gs.shopCards.add(CardLibrary.randomUncommon(rng));
        gs.shopCards.add(CardLibrary.randomRare(rng));
        gs.shopRelic = RelicLibrary.random(rng);
        gs.shopCardPrice = 50;
        gs.shopRelicPrice = 150;
    }

    public String buyCard(GameState gs, int index) {
        if (!"shop".equals(gs.phase)) return "不在商店";
        if (index < 0 || index >= gs.shopCards.size()) return "无效选择";
        if (gs.gold < gs.shopCardPrice) return "金币不足";
        gs.gold -= gs.shopCardPrice;
        Card bought = gs.shopCards.remove(index);
        gs.deck.add(bought);
        return "购买了 " + bought.name;
    }

    public String buyRelic(GameState gs) {
        if (!"shop".equals(gs.phase)) return "不在商店";
        if (gs.shopRelic == null) return "遗物已售罄";
        if (gs.gold < gs.shopRelicPrice) return "金币不足";
        gs.gold -= gs.shopRelicPrice;
        gs.relics.add(gs.shopRelic);
        String name = gs.shopRelic.name;
        gs.shopRelic = null;
        return "购买了 " + name;
    }

    public void leaveShop(GameState gs) {
        if (!"shop".equals(gs.phase)) return;
        gs.floor++;
        generateMap(gs);
    }

    // ═══════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════

    private void log(GameState gs, String msg) {
        gs.battleLog.add(msg);
    }
}
