import lombok.extern.slf4j.Slf4j;

public class TestMultiLock {
    public static void main(String[] args) {
        BigRoom bigRoom = new BigRoom();
        new Thread(()->{
            try {
                bigRoom.study();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"小南").start();
        new Thread(()->{
            try {
                bigRoom.sleep();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"小女").start();
    }
}


@Slf4j
class BigRoom{
    private final Object studyRoom = new Object();
    private final Object sleepRoom = new Object();
    public void sleep() throws InterruptedException {
        synchronized (sleepRoom){
            log.debug("sleeping 2 hours...");
            Thread.sleep(2000);
        }
    }

    public void study() throws InterruptedException {
        synchronized (studyRoom){
            log.debug("study 1 hour...");
            Thread.sleep(1000);
        }
    }
}
