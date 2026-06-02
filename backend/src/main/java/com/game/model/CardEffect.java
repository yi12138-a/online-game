package com.game.model;

import java.io.Serializable;

public class CardEffect implements Serializable {
    private static final long serialVersionUID = 1L;

    public String type;   // "damage", "block", "draw", "energy", "vulnerable", "weak",
                          // "strength", "heal", "blockNext", "damageMulti", "dexterity"
    public int value;
    public int secondary;

    public CardEffect() {}

    public CardEffect(String type, int value, int secondary) {
        this.type = type;
        this.value = value;
        this.secondary = secondary;
    }
}
