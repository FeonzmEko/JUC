import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

public class TestStampedLock {
    public static void main(String[] args) throws InterruptedException {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        new Thread(()->{
            try {
                dataContainer.read(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();
        Thread.sleep(100);
        new Thread(()->{
            dataContainer.write(0);
        },"t2").start();
    }
}

@Slf4j
class DataContainerStamped{
    private int data;
    private final StampedLock lock = new StampedLock();

    public DataContainerStamped(int data){
        this.data = data;
    }

    public int read(int readTime) throws InterruptedException {
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}",stamp);
        Thread.sleep(readTime);
        if(lock.validate(stamp)){
            log.debug("read finish...{}",stamp);
            return data;
        }
        // 锁从乐观锁升级为读锁
        log.debug("updating to read lock...{}",stamp);
        try {
            stamp = lock.readLock();
            log.debug("read lock {}",stamp);
            Thread.sleep(readTime);
            log.debug("read finish...{}",stamp);
            return data;
        } finally {
            log.debug("read unlock {}",stamp);
            lock.unlock(stamp);
        }
    }

    public void write(int newData){
        long stamp = lock.writeLock();
        log.debug("write lock {}",stamp);
        try {
            Thread.sleep(2000);
            this.data = newData;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.debug("write unlock {}",stamp);
            lock.unlockWrite(stamp);
        }
    }
}
