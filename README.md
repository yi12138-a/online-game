# 尖塔卡牌 — 文字肉鸽

基于 SpringBoot + Vue3 的在线卡牌肉鸽游戏，灵感来自《杀戮尖塔》(Slay the Spire)。打开网页即玩，无需注册登录。

## 游戏特性

### 核心玩法
- **卡牌战斗**：回合制卡牌对战，每回合 3 点能量，合理分配打出攻击/技能/能力牌
- **牌组构筑**：基础牌组 9 张（4×打击 + 4×防御 + 1×猛击），通过战斗奖励扩充牌组
- **敌人 AI**：敌人有固定的意图模式，显示下回合行动，玩家可以针对性策略
- **状态效果**：力量、敏捷、易伤、虚弱、格挡等丰富的战斗机制

### 卡牌系统
- **26 种卡牌**：Basic×3 + Common×8 + Uncommon×8 + Rare×6
- 攻击牌：打击、顺劈斩、飞踢、剑柄打击、双重打击、重刃、雷霆一击、铁壁、金刚臂、旋风斩、收割、重锤、吞噬
- 技能牌：防御、耸肩无视、武装、哨兵、硬撑、震荡波、战斗专注、祭品、坚不可摧
- 能力牌：燃烧、恶魔形态、猛击

### 地图与房间
- **3 幕攀登**：每幕 8 层（含 Boss），共 24 层
- **4 种房间**：普通战斗、精英战斗（高风险高回报）、休息处（恢复生命）、商店（购买卡牌/遗物）
- **Boss 战**：每幕末尾的强力敌人

### 遗物系统
- **8 种遗物**：燃烧之血、皇家枕头、金刚杵、光滑石头、手里剑、血瓶、快乐花、昆虫标本
- 战斗开始/结束、休息时、商店等不同触发时机

## 项目结构

```
TextFightinggame/
├── backend/                              # SpringBoot 后端
│   ├── pom.xml
│   └── src/main/java/com/game/
│       ├── GameApplication.java
│       ├── config/CorsConfig.java
│       ├── controller/GameController.java   # REST API (10 个接口)
│       ├── service/GameService.java         # 核心战斗逻辑
│       ├── service/CardLibrary.java         # 26 张卡牌定义
│       ├── service/EnemyLibrary.java        # 13 种敌人定义
│       ├── service/RelicLibrary.java        # 8 种遗物定义
│       ├── model/ (Card, Enemy, Relic, GameState, CardEffect)
│       └── dto/GameStateResponse.java
├── frontend/                             # Vue3 前端
│   ├── src/
│   │   ├── App.vue                       # 主状态机
│   │   ├── api/game.js                   # API 封装
│   │   ├── style.css                     # 暗黑主题样式
│   │   └── components/
│   │       ├── WelcomeScreen.vue         # 开始页面
│   │       ├── MapScreen.vue             # 地图/选路
│   │       ├── BattleScreen.vue          # 卡牌战斗
│   │       ├── RewardScreen.vue          # 卡牌奖励选择
│   │       ├── RestScreen.vue            # 休息处
│   │       ├── ShopScreen.vue            # 商店
│   │       ├── GameOverScreen.vue        # 结束/通关
│   │       └── HealthBar.vue             # 血条组件
│   ├── package.json
│   └── vite.config.js
├── start.bat                             # Windows 一键启动
├── start.ps1                             # PowerShell 一键启动
└── 宝塔部署指南.md
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 + JDK 17 |
| 前端 | Vue 3 + Vite + Axios |
| 状态 | HttpSession (无需数据库) |
| 构建 | Maven + npm |

## 本地运行

### 方法一：一键启动

双击 `start.bat`（Windows）或用 PowerShell 运行 `start.ps1`

### 方法二：手动启动

**1. 后端**
```bash
cd backend
mvn spring-boot:run
# 运行在 http://localhost:8080
```

**2. 前端**
```bash
cd frontend
npm install
npm run dev
# 运行在 http://localhost:3000，自动代理 /api 到后端
```

### 3. 浏览器打开

访问 http://localhost:3000，输入名字（可选），点击"开始攀登"

## 宝塔部署

详见 [宝塔部署指南.md](宝塔部署指南.md)

## 宝塔上传文件清单

只需上传以下 2 个产物到服务器：

| 文件 | 说明 | 来源 |
|------|------|------|
| `text-fighting-game-1.0.0.jar` | 后端 JAR 包 | `cd backend && mvn package -DskipTests` |
| `dist/` 目录 | 前端静态文件 | `cd frontend && npm run build` |

> 后端 JAR 包最终位置示例：`/www/wwwroot/card-game/text-fighting-game-1.0.0.jar`
> 前端 dist 最终位置示例：`/www/wwwroot/card-game/dist/`

## 环境要求

- JDK 17+
- Node.js 18+ (仅本地打包用)
- Maven 3.6+
