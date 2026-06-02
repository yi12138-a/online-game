package game.ui;

import game.domain.User;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static sun.management.MemoryNotifInfoCompositeData.getCount;

public class Login {
    public void start() {
        System.out.println("游戏的登录注册页面打开了~");

        ArrayList<User> list = new ArrayList<>();

        while (true) {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("    🎮 欢迎来到文字格斗游戏 🎮   ");
        System.out.println("╚════════════════════════════════╝");
        System.out.println("请选择操作：1登录 2注册 3退出");

        Scanner sc = new Scanner(System.in);
        String choose = sc.next();

        switch (choose) {
            case "1":
                login(list);
                break;

            case "2":
               register(list);
                break;

            case "3":
                System.out.println("用户选择了退出操作~");
                System.exit(0);
                break;

            default:
                System.out.println("输入有误，请重新输入~");
                break;
        }}

    }

    public void register( ArrayList<User> list) {
        System.out.println("用户选择了注册操作~");

        User u=new User();
        Scanner sc=new Scanner(System.in);
      while (true){
        System.out.println("请输入用户名：");
        String username=sc.next();

        if (!checkLen(3,16,username)) {
            System.out.println("用户名长度必须在3-16之间");
            continue;
        }
        if (!checkUsername(username)) {
            System.out.println("用户名只能包含字母和数字, 不能是纯数字");
            continue;
        }
        if (contains(list,username)) {
            System.out.println("用户名已存在,请重新输入~");
            continue;
        }

        u.setUsername( username);
        break;
    }



      while (true){
      System.out.println("请输入密码：");
      String password1=sc.next();
      System.out.println("请再次输入密码：");
      String password2=sc.next();
      if (!checkLen(3,8,password1)){
          System.out.println("密码长度必须在3-8之间");
          continue;
      }
      if(!checkPassword(password1)){
          System.out.println("密码只能包含字母和数字,不能有其他字符");
          continue;
      }
      if (!password1.equals(password2)) {
          System.out.println("两次输入的密码不一致,请重新输入~");
          continue;
      }
      u.setPassword(password1);
      break;
      }
      list.add(u);
      System.out.println("用户"+u.getUsername()+"注册成功~");
    }

    public void login( ArrayList<User> list) {
        System.out.println("用户选择了登录操作~");


        Scanner sc=new Scanner(System.in);
        System.out.println("请输入用户名：");
        String username=sc.next();


        if (!contains(list,username)){
            System.out.println("用户名"+username+"未注册，请先注册再登录~");
            return;
        }

        int index = findIndex(list, username);
        User u = list.get(index);
        if (!u.isStatus()){
            System.out.println("用户"+username+"已禁用，请联系客服xxxxxxxx ~");
            return;
        }


        String rightPassword = u.getPassword();

        for (int i = 0; i < 3; i++) {
            System.out.println("请输入密码：");
            String password = sc.next();

            while (true) {

                String rightCode = getCode();
                System.out.println("验证码是：" + rightCode);

                System.out.println("请输入验证码：");
                String code = sc.next();


                if (rightCode.equalsIgnoreCase(code)) {
                    System.out.println("验证码输入正确");
                    break ;
                } else {
                    System.out.println("验证码输入错误");
                    continue;
                }
            }

            if (password.equals(rightPassword)) {
                System.out.println("登录成功，游戏启动~");
                FightingGame fg = new FightingGame();
                fg.gameStart(username);
                break;
            } else {
                System.out.println("登录失败，密码输入错误~");
                if(i==2){
                    u.setStatus( false);
                System.out.println("当前账户"+username+"已锁定，请联系客服xxxxxxxx ~");
                return;
            }else {
                System.out.println("密码错误，还剩下"+(2-i)+"次机会");
            }
            }
        }

    }


    public  int[] getCount(String uesrInfo){
        int charCount=0;
        int numCount=0;
        int otherCount=0;
        for (int i = 0; i < uesrInfo.length(); i++) {
            char c=uesrInfo.charAt(i);
            if (c>='a'&&c<='z'||c>='A'&&c<='Z') {
                charCount++;

            }
            else if (c>='0'&&c<='9') {
                numCount++;
            }
            else {
                otherCount++;
            }
        }
        return new int[]{charCount,numCount,otherCount};
    }


    public  int findIndex(ArrayList<User> list,String username){
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            if (u.getUsername().equals(username)){
                return i;
            }}
               return -1;

    }

    public boolean contains(ArrayList<User> list,String username){
        for (int i = 0; i < list.size(); i++){
            User u= list.get(i);
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    public boolean checkUsername(String username){
       int[] arr= getCount(username);
        return arr[0]>0&&arr[1]>=0&&arr[2]==0;
    }

    public boolean checkPassword(String password){
        int[] arr= getCount(password);
        return arr[0]>0&&arr[1]>0&&arr[2]==0;
    }
    public boolean checkLen(int minLen,int maxLen,String str){
        return  str.length()>=minLen && str.length()<=maxLen;
    }

//验证码
    public static  String getCode(){
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            list.add((char)('a'+i));
            list.add((char)('A'+i));
        }
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            int index = r.nextInt(list.size());
            char c = list.get(index);
            sb.append(c);
        }
        sb.append(r.nextInt(10));

        char[] arr = sb.toString().toCharArray();
        int i= r.nextInt(arr.length);
        char temp = arr[i];
        arr[i] = arr[arr.length-1];
        arr[arr.length-1] = temp;
        String code = new String(arr);
        return code;
    }
}
