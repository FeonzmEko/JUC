import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test23 {
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            log.debug("尝试获得锁");
            try {
                if(!lock.tryLock(2, TimeUnit.SECONDS)){
                    log.debug("获得锁失败");
                    return;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                log.debug("获得锁成功");
            } finally {
                lock.unlock();
            }
        },"t1");

        lock.lock();
        log.debug("获得锁成功");
        t1.start();
        Thread.sleep(1000);
        log.debug("释放锁成功");
        lock.unlock();
    }
}
