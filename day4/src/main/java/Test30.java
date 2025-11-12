import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Test30 {
    public static void main(String[] args) {
        WaitNotify_2 waitNotify = new WaitNotify_2(1,5);
        new Thread(()->{
            waitNotify.print("a",1,2);
        }).start();
        new Thread(()->{
            waitNotify.print("b",2,3);
        }).start();
        new Thread(()->{
            waitNotify.print("c",3,1);
        }).start();
    }
}

class WaitNotify_2 {
    private int loopNumber;
    private int flag;
    static ReentrantLock lock = new ReentrantLock();
    static Condition[] conditions = new Condition[3];

    public WaitNotify_2(int flag,int loopNumber){
        this.flag = flag;
        this.loopNumber = loopNumber;
        for (int i = 0; i < 3; i++) {
            conditions[i] = lock.newCondition();
        }
    }

    public void print(String s,int now,int next){
        for (int i = 0; i < loopNumber; i++) {
            lock.lock();
            try {
                while(flag!=now){
                    try{
                        conditions[now-1].await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(s);
                flag = next;
                conditions[next-1].signal();
            } finally {
                lock.unlock();
            }
        }
    }
}