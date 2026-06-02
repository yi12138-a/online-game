package com.game.model;

import java.io.Serializable;

public class Relic implements Serializable {
    private static final long serialVersionUID = 1L;

    public String id;
    public String name;
    public String description;
    public String type;  // "start", "combat_start", "combat_end", "rest", "shop"
    public int value;

    public Relic() {}

    public Relic(String id, String name, String description, String type, int value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }
}
