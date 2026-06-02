package com.game.dto;

import java.util.List;

public class GameStateResponse {
    public String phase;
    public boolean ok = true;

    // Player
    public String playerName;
    public int hp, maxHP;
    public int gold;
    public int floor, act;
    public int energy, maxEnergy;
    public int block;
    public int strength, dexterity;
    public int vulnerable, weak;
    public int ritual;

    // Hand (cards currently in hand)
    public List<CardDTO> hand;

    // Deck size info
    public int drawSize;
    public int discardSize;
    public int deckSize;

    // Enemy
    public String enemyName;
    public String enemyId;
    public int enemyHP, enemyMaxHP;
    public int enemyBlock;
    public int enemyVulnerable, enemyWeak;
    public String enemyIntentDesc;
    public String enemyType;

    // Battle
    public List<String> battleLog;
    public boolean battleOver;
    public boolean playerWin;
    public int combatGold;

    // Map
    public List<MapOptionDTO> mapOptions;

    // Reward
    public List<CardDTO> rewardCards;

    // Shop
    public List<CardDTO> shopCards;
    public String shopRelicName;
    public String shopRelicDesc;
    public int shopCardPrice;
    public int shopRelicPrice;

    // Relics
    public List<String> relicNames;
    public List<String> relicDescs;

    public String message;

    // DTO for cards
    public static class CardDTO {
        public String id;
        public String name;
        public String description;
        public int cost;
        public String type;
        public String color;
        public String rarity;
        public String costDisplay;
    }

    public static class MapOptionDTO {
        public String type;
        public String label;
        public String icon;
        public String description;
    }
}
