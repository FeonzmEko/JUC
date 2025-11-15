public class Test1 {
    volatile static  int x;
    static int y;
    public static void main(String[] args) {

        new Thread(()->{
            y = 10;
            x = 20;
        },"t1").start();

        new Thread(()->{
            System.out.println(x);
        },"t2").start();
    }
}
