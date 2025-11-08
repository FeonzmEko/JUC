import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{

            try {
                log.debug("sleep...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        },"t1");
        t1.start();
        t1.interrupt();
        log.debug("interrupt");
        Thread.sleep(1000);
        log.debug("打断标记：{}",t1.isInterrupted());
    }
}
