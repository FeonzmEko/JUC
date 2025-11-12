import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class Test22 {
    private static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            try{
                log.debug("尝试获取锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁，返回");
                return;
            }

            try{
                log.debug("获取到锁");
            } finally {
                lock.unlock();
            }
        },"t1");

        lock.lock();
        t1.start();

        Thread.sleep(1000);
        log.debug("打断t1");
        t1.interrupt();
    }
}
