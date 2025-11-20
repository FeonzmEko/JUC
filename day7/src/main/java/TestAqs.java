import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

@Slf4j
public class TestAqs {
    public static void main(String[] args) throws InterruptedException {
        MyLock lock = new MyLock();
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                log.debug("unlocking...,i:{}");
                lock.unlock();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
            } finally {
                log.debug("unlocking...,i:{}");
                lock.unlock();
            }
        }, "t2");

        t1.start();
        t2.start();


    }
}

// 自定义锁，不可重入锁
class MyLock implements Lock{

    // 独占锁,,同步器类？？？？
    class MySync extends AbstractQueuedSynchronizer{
        @Override // 尝试获取独占锁
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0,1)){
                // 加上了锁，并设置owner为当前线程，并返回true
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override //释放锁
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        @Override // 锁是否持有独占锁
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }

    private MySync sync = new MySync();

    @Override // 加锁，不成功会进入等待队列
    public void lock() {
        sync.acquire(1);
    }

    @Override // 加锁，可打断
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override // 尝试加锁，一次
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override // 尝试加锁，带超时时间
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override // 解锁
    public void unlock() {
        sync.release(1);
    }

    @Override // 创建条件变量
    public Condition newCondition() {
        return sync.newCondition();
    }
}
