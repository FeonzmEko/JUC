import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TestCountDownLatch {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService service = Executors.newFixedThreadPool(10);
        Random r = new Random();
        String[] all = new String[10];
        for (int j = 0; j < 10; j++) {
            int k = j;
            service.submit(()->{
                for (int i = 0; i <= 100; i++) {
                    try {
                        Thread.sleep(r.nextInt(100));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    all[k] = i + "%";
                    System.out.print("\r" + Arrays.toString(all));
                }
                latch.countDown();
            });
        }
        latch.await();
        System.out.println("\n游戏开始");
        service.shutdown();
    }

    private static void test1() {
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(()->{
            log.debug("running...");
            try {
                Thread.sleep(500);
                log.debug("end...");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        service.submit(()->{
            log.debug("running...");
            try {
                Thread.sleep(1500);
                log.debug("end...");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        service.submit(()->{
            log.debug("running...");
            try {
                Thread.sleep(1000);
                log.debug("end...");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        service.submit(()->{
            try {
                log.debug("waiting start...");
                latch.await();
                log.debug("waiting end...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void test() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        new Thread(()->{
            log.debug("running...");
            try {
                Thread.sleep(500);
                log.debug("end...");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            log.debug("running...");
            try {
                Thread.sleep(1000);
                log.debug("end...");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            log.debug("running...");
            try {
                Thread.sleep(1500);
                log.debug("end...");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        latch.await();
        log.debug("main...");
    }
}
