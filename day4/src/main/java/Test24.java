import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Test24 {
    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;
    static ReentrantLock ROOM = new ReentrantLock();
    static Condition waitCigarette = ROOM.newCondition();
    static Condition waitTakeout = ROOM.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            ROOM.lock();
            try {
                log.debug("有烟没？[{}]",hasCigarette);
                while(!hasCigarette){
                    log.debug("没烟，先歇会儿！");
                    try {
                        waitCigarette.await();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]",hasCigarette);
                if(hasCigarette){
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            } finally {
                ROOM.unlock();
            }
        },"小南").start();

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("外卖到了吗？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("还没到，先等等吧！");
                    try {
                        waitTakeout.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖到了吗？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }finally {
                ROOM.unlock();
            }
        }, "小女").start();

        Thread.sleep(1000);
        new Thread(()->{
            ROOM.lock();
            try {
                hasTakeout = true;
                waitTakeout.signal();
            } finally {
                ROOM.unlock();
            }
        },"送外卖的").start();

        Thread.sleep(1000);
        new Thread(()->{
            ROOM.lock();
            try {
                hasCigarette = true;
                waitCigarette.signal();
            } finally {
                ROOM.unlock();
            }
        },"送烟的").start();

    }
}
