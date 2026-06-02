package com.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    public String id;
    public String name;
    public String description;
    public int cost;          // energy cost
    public String type;       // "attack", "skill", "power"
    public String color;      // "#e74c3c" attack, "#3498db" skill, "#9b59b6" power
    public String rarity;     // "basic", "common", "uncommon", "rare"
    public List<CardEffect> effects = new ArrayList<>();

    public Card() {}

    public Card(String id, String name, String desc, int cost, String type, String rarity) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.cost = cost;
        this.type = type;
        this.rarity = rarity;
        this.color = type.equals("attack") ? "#e74c3c" : type.equals("skill") ? "#3498db" : "#9b59b6";
    }

    public Card addEffect(String etype, int value) {
        effects.add(new CardEffect(etype, value, 0));
        return this;
    }

    public Card addEffect(String etype, int value, int secondary) {
        effects.add(new CardEffect(etype, value, secondary));
        return this;
    }

    public String getCostDisplay() { return cost == -1 ? "X" : String.valueOf(cost); }
}
