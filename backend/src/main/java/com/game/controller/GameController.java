package com.game.controller;

import com.game.dto.GameStateResponse;
import com.game.model.*;
import com.game.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private static final String KEY = "gs";
    private final GameService svc = new GameService();

    // ── START ──
    @PostMapping("/start")
    public GameStateResponse start(@RequestBody Map<String, String> body, HttpSession session) {
        String name = body.getOrDefault("name", "冒险者");
        GameState gs = svc.startGame(name);
        session.setAttribute(KEY, gs);
        return toResponse(gs);
    }

    // ── CHOOSE ROOM (map) ──
    @PostMapping("/choose-room")
    public GameStateResponse chooseRoom(@RequestBody Map<String, Integer> body, HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        int choice = body.getOrDefault("choice", 0);
        svc.enterRoom(gs, choice);
        return toResponse(gs);
    }

    // ── PLAY CARD ──
    @PostMapping("/play-card")
    public GameStateResponse playCard(@RequestBody Map<String, Integer> body, HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        int index = body.getOrDefault("index", -1);
        String result = svc.playCard(gs, index);
        GameStateResponse resp = toResponse(gs);
        resp.message = result;
        return resp;
    }

    // ── END TURN ──
    @PostMapping("/end-turn")
    public GameStateResponse endTurn(HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        svc.endTurn(gs);
        return toResponse(gs);
    }

    // ── CHOOSE REWARD ──
    @PostMapping("/choose-reward")
    public GameStateResponse chooseReward(@RequestBody Map<String, Integer> body, HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        int index = body.getOrDefault("index", -1);
        svc.chooseReward(gs, index);
        return toResponse(gs);
    }

    // ── REST ──
    @PostMapping("/rest")
    public GameStateResponse rest(HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        svc.rest(gs);
        return toResponse(gs);
    }

    // ── SHOP ──
    @PostMapping("/buy-card")
    public GameStateResponse buyCard(@RequestBody Map<String, Integer> body, HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        int index = body.getOrDefault("index", -1);
        String msg = svc.buyCard(gs, index);
        GameStateResponse resp = toResponse(gs);
        resp.message = msg;
        return resp;
    }

    @PostMapping("/buy-relic")
    public GameStateResponse buyRelic(HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        String msg = svc.buyRelic(gs);
        GameStateResponse resp = toResponse(gs);
        resp.message = msg;
        return resp;
    }

    @PostMapping("/leave-shop")
    public GameStateResponse leaveShop(HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) return error("请先开始游戏");
        svc.leaveShop(gs);
        return toResponse(gs);
    }

    // ── GET STATE ──
    @GetMapping("/state")
    public GameStateResponse getState(HttpSession session) {
        GameState gs = (GameState) session.getAttribute(KEY);
        if (gs == null) {
            GameStateResponse resp = new GameStateResponse();
            resp.phase = "welcome";
            resp.ok = true;
            return resp;
        }
        return toResponse(gs);
    }

    // ── BUILD RESPONSE ──
    private GameStateResponse toResponse(GameState gs) {
        GameStateResponse r = new GameStateResponse();
        r.phase = gs.phase;
        r.playerName = gs.playerName;
        r.hp = gs.hp;
        r.maxHP = gs.maxHP;
        r.gold = gs.gold;
        r.floor = gs.floor;
        r.act = gs.act;
        r.energy = gs.energy;
        r.maxEnergy = gs.maxEnergy;
        r.block = gs.block;
        r.strength = gs.strength;
        r.dexterity = gs.dexterity;
        r.vulnerable = gs.vulnerable;
        r.weak = gs.weak;
        r.ritual = gs.ritual;

        // Hand
        r.hand = new ArrayList<>();
        for (Card c : gs.hand) r.hand.add(cardDTO(c));

        r.drawSize = gs.drawPile.size();
        r.discardSize = gs.discardPile.size();
        r.deckSize = gs.deck.size();

        // Enemy
        if (gs.enemy != null) {
            r.enemyName = gs.enemy.name;
            r.enemyId = gs.enemy.id;
            r.enemyHP = gs.enemy.hp;
            r.enemyMaxHP = gs.enemy.maxHP;
            r.enemyBlock = gs.enemy.block;
            r.enemyVulnerable = gs.enemy.vulnerable;
            r.enemyWeak = gs.enemy.weak;
            r.enemyIntentDesc = gs.enemy.getIntentText();
            r.enemyType = gs.enemy.type;
        }

        // Battle log
        r.battleLog = new ArrayList<>(gs.battleLog);
        r.battleOver = gs.battleOver;
        r.playerWin = gs.playerWin;
        r.combatGold = gs.combatGold;

        // Map
        r.mapOptions = new ArrayList<>();
        for (var opt : gs.mapOptions) {
            var dto = new GameStateResponse.MapOptionDTO();
            dto.type = opt.type;
            dto.label = opt.label;
            dto.icon = opt.icon;
            dto.description = opt.description;
            r.mapOptions.add(dto);
        }

        // Reward
        r.rewardCards = new ArrayList<>();
        for (Card c : gs.rewardCards) r.rewardCards.add(cardDTO(c));

        // Shop
        r.shopCards = new ArrayList<>();
        for (Card c : gs.shopCards) r.shopCards.add(cardDTO(c));
        if (gs.shopRelic != null) {
            r.shopRelicName = gs.shopRelic.name;
            r.shopRelicDesc = gs.shopRelic.description;
        }
        r.shopCardPrice = gs.shopCardPrice;
        r.shopRelicPrice = gs.shopRelicPrice;

        // Relics
        r.relicNames = new ArrayList<>();
        r.relicDescs = new ArrayList<>();
        for (Relic relic : gs.relics) {
            r.relicNames.add(relic.name);
            r.relicDescs.add(relic.description);
        }

        // Reset battle log after reading (so frontend doesn't get stale data)
        // Actually keep it — frontend needs to see latest log

        return r;
    }

    private GameStateResponse.CardDTO cardDTO(Card c) {
        var d = new GameStateResponse.CardDTO();
        d.id = c.id;
        d.name = c.name;
        d.description = c.description;
        d.cost = c.cost;
        d.type = c.type;
        d.color = c.color;
        d.rarity = c.rarity;
        d.costDisplay = c.getCostDisplay();
        return d;
    }

    private GameStateResponse error(String msg) {
        GameStateResponse r = new GameStateResponse();
        r.ok = false;
        r.phase = "welcome";
        r.message = msg;
        return r;
    }
}
