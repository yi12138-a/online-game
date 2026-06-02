package com.game.service;

import com.game.model.Enemy;
import java.util.*;

public class EnemyLibrary {

    public static Enemy create(String id) {
        Enemy src = ALL.get(id);
        if (src == null) return null;
        return new Enemy(src.id, src.name, src.maxHP, src.pattern, src.type);
    }

    public static Enemy randomNormal(int act, Random r) {
        List<String> pool = act == 1 ? NORMAL_ACT1 : act == 2 ? NORMAL_ACT2 : NORMAL_ACT3;
        return create(pool.get(r.nextInt(pool.size())));
    }

    public static Enemy randomElite(int act, Random r) {
        List<String> pool = act == 1 ? ELITE_ACT1 : act == 2 ? ELITE_ACT2 : ELITE_ACT3;
        return create(pool.get(r.nextInt(pool.size())));
    }

    public static Enemy boss(int act) {
        return create(BOSSES.get(act - 1));
    }

    // patterns: A:dmg=attack, D:blk=defend, B:str=buff, AD:dmg:blk, W:amt=applyWeak, V:amt=applyVuln, BIG:dmg=bigAttack
    public static final Map<String, Enemy> ALL = new LinkedHashMap<>();

    static {
        // ── Act1 normal ──
        reg(new Enemy("cultist", "邪教徒", 50, new String[]{"A:6","A:6","B:3"}, "normal"));
        reg(new Enemy("jaw_worm", "大颚虫", 44, new String[]{"A:11","AD:7:6","B:3"}, "normal"));
        reg(new Enemy("looter", "抢劫犯", 48, new String[]{"A:10","D:6","A:10"}, "normal"));
        reg(new Enemy("slime_s", "酸性史莱姆", 32, new String[]{"A:8","A:8","W:2"}, "normal"));

        // ── Act2 normal ──
        reg(new Enemy("chosen", "天选者", 60, new String[]{"A:12","B:4","A:12"}, "normal"));
        reg(new Enemy("slaver", "奴隶贩子", 56, new String[]{"A:14","W:2","A:14"}, "normal"));
        reg(new Enemy("bandit", "土匪", 52, new String[]{"A:13","D:8","A:13"}, "normal"));

        // ── Act3 normal ──
        reg(new Enemy("darkling", "暗影生物", 64, new String[]{"A:15","B:5","A:15"}, "normal"));
        reg(new Enemy("wraith", "幽灵", 58, new String[]{"A:16","V:2","A:16"}, "normal"));

        // ── Elites ──
        reg(new Enemy("gremlin_nob", "地精大头", 88, new String[]{"A:8","B:3","BIG:18"}, "elite"));
        reg(new Enemy("lagavulin", "拉加瓦林", 112, new String[]{"A:18","A:20","D:12","A:18"}, "elite"));
        reg(new Enemy("taskmaster", "监工", 96, new String[]{"A:16","W:2","A:20","D:10"}, "elite"));
        reg(new Enemy("giant_head", "巨石之首", 130, new String[]{"A:13","A:15","A:18","BIG:30"}, "elite"));

        // ── Bosses ──
        reg(new Enemy("slime_boss", "史莱姆王", 140, new String[]{"A:18","A:18","AD:12:8","BIG:25"}, "boss"));
        reg(new Enemy("guardian", "守护者", 160, new String[]{"A:16","D:12","A:16","BIG:28"}, "boss"));
        reg(new Enemy("champ", "冠军", 180, new String[]{"A:20","B:6","A:20","BIG:35"}, "boss"));
    }

    private static void reg(Enemy e) { ALL.put(e.id, e); }

    static final List<String> NORMAL_ACT1 = List.of("cultist","jaw_worm","looter","slime_s");
    static final List<String> NORMAL_ACT2 = List.of("chosen","slaver","bandit","cultist","jaw_worm");
    static final List<String> NORMAL_ACT3 = List.of("darkling","wraith","chosen","slaver","bandit");
    static final List<String> ELITE_ACT1 = List.of("gremlin_nob","lagavulin");
    static final List<String> ELITE_ACT2 = List.of("gremlin_nob","taskmaster");
    static final List<String> ELITE_ACT3 = List.of("taskmaster","giant_head");
    static final List<String> BOSSES = List.of("slime_boss","guardian","champ");
}
