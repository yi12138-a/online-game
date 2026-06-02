import game.ui.FightingGame;
import game.ui.Login;

public class App {
    public static void main(String[] args) {


        /*Login l = new Login();
        l.start();*/
        FightingGame fg = new FightingGame();
        fg.gameStart("zhangsan");

    }
}
