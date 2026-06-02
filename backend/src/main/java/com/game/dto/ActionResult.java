package com.game.dto;

public class ActionResult {
    public boolean playerAlive;
    public boolean enemyAlive;
    public boolean battleOver;
    public boolean playerWin;

    public String playerName;
    public int playerHP;
    public int playerMaxHP;

    public String enemyName;
    public int enemyHP;
    public int enemyMaxHP;

    public String playerActionLog;
    public String enemyActionLog;

    public int wins;
    public boolean gameOver;
    public boolean levelUp;
    public int healAmount;
    public String levelUpMessage;
}
