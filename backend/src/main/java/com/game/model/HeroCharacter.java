package com.game.model;

import java.util.ArrayList;
import java.util.List;

public class HeroCharacter extends Character {
    public List<String> skillList;

    public HeroCharacter() {
        super();
        skillList = new ArrayList<>();
    }

    public HeroCharacter(String name, int HP, int attack, int defense) {
        super(name, HP, attack, defense);
        skillList = new ArrayList<>();
    }
}
