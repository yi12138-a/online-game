package game.ui;

import game.domain.EnemyCharacter;
import game.domain.HeroCharacter;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FightingGame {

    public  void gameStart(String username) {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("    🎮 "+username+" 欢迎来到文字格斗游戏 🎮   ");
        System.out.println("╚════════════════════════════════╝");


        HeroCharacter player = createPlayerCharacter(username);
        System.out.println("角色创建成功！");
        System.out.println("\uD83C\uDF1F初始属性为："+player.show());
        System.out.println("\uD83C\uDF1F拥有的技能："+player.showSkill());




//  | 敌人名称 | 生命值 | 攻击力 | 防御力 | 技能（变量）                                           |
//  | -------- | ------ | ------ | ------ | ------------------------------------------------------ |
//  | 初级战士 | 80     | 15     | 10     | 猛击（150%伤害）                                       |
//  | 敏捷刺客 | 60     | 20     | 5      | 快速攻击（2次50%伤害）                                 |
//  | 重装坦克 | 120    | 10     | 20     | 防御姿态（下回合伤害减半） buff（ boolean defendding） |
//  | 神秘法师 | 70     | 25     | 8      | 火球术（180%伤害）
     ArrayList<EnemyCharacter> enemyList = new ArrayList<>();
     enemyList.add(new EnemyCharacter("初级战士",80,15,10,"猛击"));
     enemyList.add(new EnemyCharacter("敏捷刺客",60,20,5,"快速攻击"));
     enemyList.add(new EnemyCharacter("重装坦克",120,10,20,"防御姿态"));
     enemyList.add(new EnemyCharacter("神秘法师",70,25,8,"火球术"));



     int count =1;
     int wins =0;
     while(player.isAlive()){
         if (wins!=0) {
             for (int i = 0; i < enemyList.size(); i++){
                 EnemyCharacter c = enemyList.get(i);
                 c.maxHP = c.maxHP+10;
                 c.HP = c.maxHP;
                 c.attack = c.attack+3;
                 c.defense = c.defense+2;
                 c.defending = false;

             }
         }

         Random r = new Random();
         int index = r.nextInt(enemyList.size());
         EnemyCharacter enemy = enemyList.get(index);
         System.out.println(enemy.show());


         System.out.println("═══════════════════════════════════════");
         System.out.println("⚔\uFE0F 第 "+count+" 场战斗开始！对手: "+enemy.name);

         int round =1;
         while (player.isAlive() ) {
             System.out.println("----------------------------------");
             System.out.println("⚔\uFE0F 第 "+round+" 回合开始！");

             System.out.println(getHealthBar(player.name,player.HP,player.maxHP));
             System.out.println(getHealthBar(enemy.name,enemy.HP,enemy.maxHP));

            playerTurn(player,enemy);


            if (!enemy.isAlive()) {
                //🎉 你击败了 敏捷刺客！
                System.out.println("🎉 你击败了 "+enemy.name+"！");
                wins++;
                break;

            }


             enemyTurn(enemy,player);
            if (!player.isAlive()) {
                System.out.println("你被"+enemy.name+"击败了！");

                break;
            }

            round++;



         }

         if(player.isAlive()) {
            int healHP = r.nextInt(21)+20;
            player.heal(healHP);

            //💚 战斗结束！你恢复了 36 点生命值
             //🏆 当前胜场: 1
             System.out.println("💚 战斗结束！你恢复了 "+healHP+" 点生命值");
             System.out.println("🏆 当前胜场: "+wins);
             System.out.println("═══════════════════════════════════════");
        }

         if (player.isAlive()&&wins>0&&wins%3==0){
             System.out.println("🎉 恭喜你，你已获得属性提升！");
             player.attack = player.attack+5;
             player.maxHP = player.maxHP+30;
             player.defense = player.defense+3;
             System.out.println("最大生命值+30， 攻击力+5， 防御力+3");
             System.out.println("当前属性："+player.show());
         }

         if (player.isAlive()){
             System.out.println("是否继续战斗？(y/n)");
             Scanner sc = new Scanner(System.in);
             String choose = sc.next();
             if ("y".equalsIgnoreCase(choose)) {
                 count++;
                 continue;
             } else if ("n".equalsIgnoreCase(choose)){
                 break;
             }else {
                 System.out.println("输入有误！默认游戏继续！");
             }
         }
     }

     System.out.println("═══════════════════════════");
     System.out.println("游戏结束！");
     System.out.println("总胜场："+wins);
     System.out.println("感谢游玩文字版格斗游戏");

     System.exit(0);


    }




    public  String getHealthBar(String name,int HP,int maxHP){
        int barLength = 20;
        int filled = (int) ((HP*1.0/maxHP)*barLength);

        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": [");

        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                sb.append("█");
            } else {
                sb.append(" ");
            }



        }
        sb.append("]").append(HP).append("/").append(maxHP).append(" HP");
        return sb.toString();
    }

    public HeroCharacter createPlayerCharacter(String username) {
        System.out.println("创建你的角色：");
        System.out.println("你的角色名称："+ username);

        int points =20;
        System.out.println("请分配属性点（共20点）：");
        System.out.println("1.生命值（每点+10HP）：");
        System.out.println("2.攻击力（每点+2ATK）：");
        System.out.println("3.防御力（每点+1DEF）：");

        Scanner sc = new Scanner(System.in);

        String[] attributes={"生命值","攻击力","防御力"};
        int[] values = new int[3];

        for (int i = 0; i < attributes.length; i++) {
            System.out.println("分配点数到" + attributes[i] + "（剩余点数：" + points + "）：");
            int input = sc.nextInt();

            if (input < 0) {
                System.out.println("无效输入！默认分配0点");
                input = 0;
            }

            if (input > points) {
                System.out.println("属性点不足！剩余属性点全部分配到：" + attributes[i]);
                input = points;
            }

            points = points - input;
            values[i] = input;
        }


        HeroCharacter player = new HeroCharacter(
                username,
                100+values[0]*10,
                10+values[1]*2,
                0+values[2]*1);

        player.skillList.add("普通攻击");
        player.skillList.add("强力一击");
        player.skillList.add("生命汲取");


        return player;
    }

    public  void playerTurn(HeroCharacter player,EnemyCharacter enemy){
        System.out.println("=====你的回合=====");
        System.out.println("1.普通攻击");
        System.out.println("2.强力一击");
        System.out.println("3.生命汲取");
        System.out.println("选择行动（1-3）：");
        Scanner sc = new Scanner(System.in);
        String choice = sc.next();
        switch (choice) {
            default:
                System.out.println("没有这个操作，默认使用普通攻击");
            case "1":

                int damage1 = caculateDamage(player.attack,enemy.defense);
               //"⚔️" 你对 敏捷刺客 使用了普通攻击，造成 15 点伤害！
                System.out.println("⚔️ 你对 "+enemy.name+" 使用了普通攻击，造成 "+damage1+" 点伤害！");

                enemy.takeDamage(damage1);
                break;
            case "2":

                if (player.HP>10){


                    player.takeDamage(10);
                    int damage2 = caculateDamage((int) (player.attack*1.8),enemy.defense);
                    //💥 消耗10HP，你对 敏捷刺客 使用了强力一击，造成 31 点伤害！
                    System.out.println("💥 消耗10HP，你对 "+enemy.name+" 使用了强力一击，造成 "+damage2+" 点伤害！");

                    enemy.takeDamage(damage2);

                } else {
                    System.out.println("你的生命值不足，无法使用强力一击！");
                }
                break;
            case "3":

                if (player.HP>10){
                    player.takeDamage(10);
                    Random r = new Random();
                    int healHP = r.nextInt(21);
                    player.heal(healHP);
                    System.out.println("💖 消耗10HP，你使用了生命汲取，恢复了 " + healHP + " 点生命值！");
                }
                else {
                    System.out.println("你的生命值不足，无法使用生命汲取！");
                }
                break;


        }
    }

    private void enemyTurn(EnemyCharacter enemy, HeroCharacter player) {
        System.out.println("====="+enemy.name+"的回合 =====");
        String action="普通攻击";
        Random r =new Random();
        int num = r.nextInt(10);
        if (num>=5){

            action= enemy.skill;
        }

        switch ( action){
            case "普通攻击":
                System.out.println("敌人采取了普通攻击");
                int damage1 = caculateDamage(enemy.attack,player.defense);
                System.out.println("⚔️ "+enemy.name+"对你使用了普通攻击，造成 "+damage1+" 点伤害！");
                player.takeDamage(damage1);
                break;
            case "猛击":
                System.out.println("当前战士使用了猛击");
                int damage2 = caculateDamage((int) (enemy.attack*1.5),player.defense);
                System.out.println("⚔\uD83D\uDCA5 "+enemy.name+"对你使用了强力一击，造成 "+damage2+" 点伤害！");
                player.takeDamage(damage2);
                break;
            case "快速攻击":
                System.out.println("当前刺客使用了快速攻击");
                int damage3 = 0;
                for (int i = 0; i < 2; i++) {
                    int temp = caculateDamage(enemy.attack/2,player.defense);
                    damage3 += temp;
                }
                System.out.println("👣 "+enemy.name+"对你使用了快速攻击，造成 "+damage3+" 点伤害！");
                player.takeDamage(damage3);
                break;
            case "防御姿态":
                System.out.println("当前坦克使用了防御姿态");
                enemy.defending=true;
                System.out.println("🛡 "+enemy.name+"摆出了防御姿态！");
                break; 
            case "火球术":
                System.out.println("当前法师使用了火球术");
                int damage4 = caculateDamage((int) (enemy.attack*1.8),player.defense);
                System.out.println("🔥 "+enemy.name+"对你使用了火球术，造成 "+damage4+" 点伤害！");
                player.takeDamage(damage4);
                break;

        }
    }
    public  int caculateDamage(int attack,int defense){
        int damage = attack-defense;
        if (damage<1){
            damage = 1;
        }
        return damage;
    }


}
