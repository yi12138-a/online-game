package com.game.model;

import java.io.Serializable;
import java.util.*;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    // Phase
    public String phase;  // "welcome", "map", "battle", "reward", "shop", "rest", "gameover", "victory"

    // Player
    public String playerName;
    public int hp, maxHP;
    public int gold;
    public int floor, act;
    public int energy, maxEnergy;
    public int block;
    public int strength;     // +damage per attack
    public int dexterity;    // +block per defend
    public int vulnerable;   // turns
    public int weak;         // turns
    public int ritual;       // demon form: +strength per turn

    // Deck
    public List<Card> deck = new ArrayList<>();
    public List<Card> drawPile = new ArrayList<>();
    public List<Card> hand = new ArrayList<>();
    public List<Card> discardPile = new ArrayList<>();
    public List<Card> exhaustPile = new ArrayList<>();

    // Relics
    public List<Relic> relics = new ArrayList<>();

    // Enemy (battle)
    public Enemy enemy;

    // Map
    public List<MapOption> mapOptions = new ArrayList<>();

    // Battle
    public List<String> battleLog = new ArrayList<>();
    public boolean battleOver;
    public boolean playerWin;
    public int combatGold;

    // Reward
    public List<Card> rewardCards = new ArrayList<>();

    // Shop
    public List<Card> shopCards = new ArrayList<>();
    public Relic shopRelic;
    public int shopCardPrice = 50;
    public int shopRelicPrice = 150;
    public int restHealPercent = 30;
    public int attackCountThisCombat;  // for shuriken

    // ─── helpers ───

    public void shuffleDrawPile() {
        Collections.shuffle(drawPile);
    }

    public void drawCards(int n) {
        for (int i = 0; i < n; i++) {
            if (drawPile.isEmpty()) {
                if (discardPile.isEmpty()) break;
                drawPile.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(drawPile);
            }
            if (!drawPile.isEmpty()) {
                hand.add(drawPile.remove(0));
            }
        }
    }

    public void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    public void startTurn() {
        energy = maxEnergy;
        block = 0;
        if (ritual > 0) { strength += ritual; }
        drawCards(5);
    }

    public boolean isAlive() { return hp > 0; }

    public enum RoomType {
        ENEMY, ELITE, REST, SHOP, BOSS
    }

    public static class MapOption implements Serializable {
        private static final long serialVersionUID = 1L;
        public String type;  // enemy, elite, rest, shop, boss
        public String label;
        public String icon;
        public String description;
    }
}
