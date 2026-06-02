package com.game.model;

import java.io.Serializable;

public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String id;
    public int hp, maxHP;
    public int block;
    public int strength;    // adds to attack damage
    public int dexterity;   // adds to block gained

    public int vulnerable;  // turns remaining, +50% damage taken
    public int weak;        // turns remaining, -25% damage dealt

    public String intentType;      // "attack", "defend", "buff", "debuff", "attackDefend"
    public int intentValue;        // damage or block amount
    public int intentSecondary;    // secondary value
    public String intentDesc;      // "攻击 12", "格挡 8", etc.

    public String[] pattern;       // intent pattern array
    public int patternIndex;
    public String type;            // "normal", "elite", "boss"

    public Enemy() {}

    public Enemy(String id, String name, int maxHP, String[] pattern, String type) {
        this.id = id;
        this.name = name;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.pattern = pattern;
        this.patternIndex = 0;
        this.type = type;
        this.intentType = "none";
        this.intentDesc = "...";
    }

    public boolean isAlive() { return hp > 0; }

    public void takeDamage(int dmg) {
        if (dmg <= 0) return;
        if (vulnerable > 0) dmg = (int)(dmg * 1.5);
        if (block > 0) {
            int blocked = Math.min(block, dmg);
            block -= blocked;
            dmg -= blocked;
        }
        hp = Math.max(0, hp - dmg);
    }

    public String getIntentText() {
        return intentDesc;
    }

    public void nextIntent() {
        if (pattern == null || pattern.length == 0) {
            intentType = "attack";
            intentValue = 5;
            intentDesc = "攻击 5";
            return;
        }
        String action = pattern[patternIndex % pattern.length];
        patternIndex++;
        parseAction(action);
    }

    private void parseAction(String action) {
        String[] parts = action.split(":");
        String cmd = parts[0];
        switch (cmd) {
            case "A" -> {
                int dmg = Integer.parseInt(parts[1]);
                intentType = "attack";
                intentValue = dmg;
                intentDesc = "⚔️ 攻击 " + dmg;
            }
            case "D" -> {
                int blk = Integer.parseInt(parts[1]);
                intentType = "defend";
                intentValue = blk;
                intentDesc = "🛡️ 格挡 " + blk;
            }
            case "B" -> {
                int str = Integer.parseInt(parts[1]);
                intentType = "buff";
                intentValue = str;
                intentDesc = "⬆️ 获得 " + str + " 力量";
            }
            case "AD" -> {
                intentType = "attackDefend";
                intentValue = Integer.parseInt(parts[1]);
                intentSecondary = Integer.parseInt(parts[2]);
                intentDesc = "⚔️" + intentValue + " 🛡️" + intentSecondary;
            }
            case "W" -> {
                int wk = Integer.parseInt(parts[1]);
                intentType = "debuff";
                intentValue = wk;
                intentDesc = "⬇️ 施加 " + wk + " 虚弱";
            }
            case "V" -> {
                int vul = Integer.parseInt(parts[1]);
                intentType = "debuff";
                intentValue = vul;
                intentDesc = "💔 施加 " + vul + " 易伤";
            }
            case "BIG" -> {
                int bigDmg = Integer.parseInt(parts[1]);
                intentType = "attack";
                intentValue = bigDmg;
                intentDesc = "💥 重击 " + bigDmg;
            }
        }
    }
}
