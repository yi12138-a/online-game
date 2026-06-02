package com.game.service;

import com.game.model.Relic;
import java.util.*;

public class RelicLibrary {

    public static final List<Relic> ALL = List.of(
        new Relic("burning_blood", "燃烧之血", "战斗结束后恢复 6 点生命", "combat_end", 6),
        new Relic("regal_pillow", "皇家枕头", "休息时额外恢复 15% 生命", "rest", 15),
        new Relic("vajra", "金刚杵", "战斗开始时获得 1 点力量", "combat_start", 1),
        new Relic("smooth_stone", "光滑石头", "战斗开始时获得 1 点敏捷", "combat_start", 1),
        new Relic("shuriken", "手里剑", "每打出 3 张攻击牌，获得 1 点力量", "attack_count", 1),
        new Relic("blood_vial", "血瓶", "战斗开始时恢复 2 点生命", "combat_start_heal", 2),
        new Relic("happy_flower", "快乐花", "每 3 回合获得 1 点能量", "turn_count", 1),
        new Relic("preserved_insect", "昆虫标本", "精英敌人生命降低 25%", "elite_weaken", 25)
    );

    public static Relic random(Random r) {
        return ALL.get(r.nextInt(ALL.size()));
    }

    public static Relic getById(String id) {
        return ALL.stream().filter(r -> r.id.equals(id)).findFirst().orElse(null);
    }
}
