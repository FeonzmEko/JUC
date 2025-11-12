import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test27 {
    static final Object lock = new Object();
    static int flag = 1;
    public static void main(String[] args) {
        Thread a = new Thread(()->{
            synchronized (lock){
                int count = 5;
                while(count-- > 0){
                    while (flag != 1) {  // 使用while循环避免虚假唤醒
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("a");
                    flag = 2;
                    lock.notifyAll();
                }
            }
        },"a");
        a.start();

        Thread b = new Thread(()->{
            synchronized (lock){
                int count = 5;
                while(count-- > 0){
                    while (flag != 2) {  // 使用while循环避免虚假唤醒
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("b");
                    flag = 3;
                    lock.notifyAll();
                }
            }
        },"b");
        b.start();

        Thread c = new Thread(()->{
            synchronized (lock){
                int count = 5;
                while(count-- > 0){
                    while (flag != 3) {  // 使用while循环避免虚假唤醒
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("c");
                    flag = 1;
                    lock.notifyAll();
                }
            }
        },"c");
        c.start();
    }
}
