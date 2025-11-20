import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
public class TestSemaphore {
    public static void main(String[] args) {
        // 创建semaphore对象
        Semaphore semaphore = new Semaphore(3);
        // 10个线程同时运行
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    log.debug("running...");
                    Thread.sleep(1000);
                    log.debug("end...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
