import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestLiveLock {
    static volatile int count = 10;
    static final Object Lock = new Object();
    public static void main(String[] args) {
        new Thread(()->{
            // 期望减到0退出循环
            while(count > 0){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count--;
                log.debug("count:{}",count);
            }
        },"t1").start();

        new Thread(()->{
            // 期望减到0退出循环
            while(count < 20){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                log.debug("count:{}",count);
            }
        },"t2").start();
    }
}
