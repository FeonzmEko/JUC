import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReadWriteLock {
    public static void main(String[] args) throws InterruptedException {
        DataContainer dataContainer = new DataContainer();
        new Thread(()->{
            dataContainer.read();
        },"t1").start();

        Thread.sleep(100);
        new Thread(()->{
            dataContainer.write();
        },"t2").start();
    }
}

@Slf4j
class DataContainer{
    private Object data;
    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public Object read(){
        log.debug("获取读锁...");
        r.lock();
        try {
            log.debug("读取");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.debug("释放读锁...");
            r.unlock();
            return data;
        }
    }

    public Object write(){
        log.debug("获取写锁...");
        w.lock();
        try {
            log.debug("写入");
            return data;
        } finally {
            log.debug("释放写锁...");
            w.unlock();
        }
    }
}
