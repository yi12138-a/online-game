package com.game.service;

import com.game.model.Card;
import java.util.*;

public class CardLibrary {

    public static Card create(String id) {
        return ALL.get(id); // returns a copy conceptually, but we use same ref for data
        // In practice, we create new card copies when adding to deck
    }

    public static Card copyOf(String id) {
        Card src = ALL.get(id);
        if (src == null) return null;
        Card c = new Card(src.id, src.name, src.description, src.cost, src.type, src.rarity);
        c.color = src.color;
        for (var e : src.effects) c.addEffect(e.type, e.value, e.secondary);
        return c;
    }

    public static final Map<String, Card> ALL = new LinkedHashMap<>();

    static {
        // ── Basic cards (starting deck) ──
        reg(new Card("strike", "打击", "造成 6 点伤害", 1, "attack", "basic")
            .addEffect("damage", 6));
        reg(new Card("defend", "防御", "获得 5 点格挡", 1, "skill", "basic")
            .addEffect("block", 5));
        reg(new Card("bash", "猛击", "造成 8 点伤害，施加 2 层易伤", 2, "attack", "basic")
            .addEffect("damage", 8)
            .addEffect("vulnerable", 2));

        // ── Common attack cards ──
        reg(new Card("cleave", "顺劈斩", "造成 8 点伤害", 1, "attack", "common")
            .addEffect("damage", 8));
        reg(new Card("clothesline", "飞踢", "造成 12 点伤害，施加 2 层虚弱", 2, "attack", "common")
            .addEffect("damage", 12)
            .addEffect("weak", 2));
        reg(new Card("pommel_strike", "剑柄打击", "造成 9 点伤害，抽 1 张牌", 1, "attack", "common")
            .addEffect("damage", 9)
            .addEffect("draw", 1));
        reg(new Card("twin_strike", "双重打击", "造成 5 点伤害 2 次", 1, "attack", "common")
            .addEffect("damageMulti", 5, 2));
        reg(new Card("heavy_blade", "重刃", "造成 14 点伤害", 2, "attack", "common")
            .addEffect("damage", 14));
        reg(new Card("thunderclap", "雷霆一击", "造成 4 点伤害，施加 1 层易伤", 1, "attack", "common")
            .addEffect("damage", 4)
            .addEffect("vulnerable", 1));

        // ── Common skill cards ──
        reg(new Card("shrug_off", "耸肩无视", "获得 8 点格挡，抽 1 张牌", 1, "skill", "common")
            .addEffect("block", 8)
            .addEffect("draw", 1));
        reg(new Card("armaments", "武装", "获得 5 点格挡", 1, "skill", "common")
            .addEffect("block", 5));

        // ── Uncommon cards ──
        reg(new Card("iron_wave", "铁壁", "造成 5 点伤害，获得 5 点格挡", 1, "attack", "uncommon")
            .addEffect("damage", 5)
            .addEffect("block", 5));
        reg(new Card("body_slam", "金刚臂", "造成等同于当前格挡值的伤害", 1, "attack", "uncommon")
            .addEffect("damageBlock", 0));  // special: damage = current block
        reg(new Card("sentinel", "哨兵", "获得 8 点格挡，下回合获得 4 点格挡", 1, "skill", "uncommon")
            .addEffect("block", 8)
            .addEffect("blockNext", 4));
        reg(new Card("power_through", "硬撑", "获得 15 点格挡", 1, "skill", "uncommon")
            .addEffect("block", 15));
        reg(new Card("shockwave", "震荡波", "造成 6 点伤害，施加 1 层虚弱和 1 层易伤", 1, "skill", "uncommon")
            .addEffect("damage", 6)
            .addEffect("weak", 1)
            .addEffect("vulnerable", 1));
        reg(new Card("whirlwind", "旋风斩", "造成 4 点伤害 X 次（X=剩余能量）", -1, "attack", "uncommon")
            .addEffect("damageX", 4));
        reg(new Card("battle_trance", "战斗专注", "抽 3 张牌", 0, "skill", "uncommon")
            .addEffect("draw", 3));
        reg(new Card("inflame", "燃烧", "获得 2 点力量", 1, "power", "uncommon")
            .addEffect("strength", 2));

        // ── Rare cards ──
        reg(new Card("reaper", "收割", "造成 4 点伤害，恢复等量生命", 2, "attack", "rare")
            .addEffect("damage", 4)
            .addEffect("healDamage", 0));  // special: heal equal to unblocked damage
        reg(new Card("offering", "祭品", "失去 6 点生命，获得 2 点能量，抽 3 张牌", 0, "skill", "rare")
            .addEffect("loseHp", 6)
            .addEffect("energy", 2)
            .addEffect("draw", 3));
        reg(new Card("demon_form", "恶魔形态", "获得 3 点力量，每回合再获得 1 点力量", 3, "power", "rare")
            .addEffect("strength", 3)
            .addEffect("ritual", 1));
        reg(new Card("bludgeon", "重锤", "造成 32 点伤害", 3, "attack", "rare")
            .addEffect("damage", 32));
        reg(new Card("impervious", "坚不可摧", "获得 30 点格挡", 2, "skill", "rare")
            .addEffect("block", 30));
        reg(new Card("feed", "吞噬", "造成 10 点伤害，若击杀则提升 4 最大生命", 1, "attack", "rare")
            .addEffect("damage", 10)
            .addEffect("feed", 4));
    }

    private static void reg(Card c) {
        ALL.put(c.id, c);
    }

    // ── Card pools ──
    public static Card randomCommon(Random r) { return copyOf(COMMON_IDS.get(r.nextInt(COMMON_IDS.size()))); }
    public static Card randomUncommon(Random r) { return copyOf(UNCOMMON_IDS.get(r.nextInt(UNCOMMON_IDS.size()))); }
    public static Card randomRare(Random r) { return copyOf(RARE_IDS.get(r.nextInt(RARE_IDS.size()))); }

    public static Card randomReward(Random r) {
        double roll = r.nextDouble();
        if (roll < 0.1) return randomRare(r);       // 10% rare
        if (roll < 0.45) return randomUncommon(r);   // 35% uncommon
        return randomCommon(r);                      // 55% common
    }

    private static final List<String> COMMON_IDS = List.of(
        "cleave","clothesline","pommel_strike","twin_strike","heavy_blade",
        "thunderclap","shrug_off","armaments"
    );
    private static final List<String> UNCOMMON_IDS = List.of(
        "iron_wave","body_slam","sentinel","power_through","shockwave",
        "whirlwind","battle_trance","inflame"
    );
    private static final List<String> RARE_IDS = List.of(
        "reaper","offering","demon_form","bludgeon","impervious","feed"
    );

    public static List<Card> startingDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 4; i++) { deck.add(copyOf("strike")); deck.add(copyOf("defend")); }
        deck.add(copyOf("bash"));
        return deck;
    }
}
