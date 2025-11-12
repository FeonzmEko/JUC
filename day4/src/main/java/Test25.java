import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test25 {
    static final Object lock = new Object();
    static boolean t2runned = false;
    static final ReentrantLock LOCK = new ReentrantLock();
    static Condition condition = LOCK.newCondition();
    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            LOCK.lock();
            try {
                while (!t2runned){
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("1");
            } finally {
                LOCK.unlock();
            }
        },"t1");

        Thread t2 = new Thread(()->{
           LOCK.lock();
           try {
               log.debug("2");
               t2runned = true;
               condition.signal();
           } finally {
               LOCK.unlock();
           }
        },"t2");

        t1.start();
        t2.start();
    }
}
