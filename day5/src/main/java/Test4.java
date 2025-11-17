import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicStampedReference;

@Slf4j
public class Test4 {
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A",0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 取值
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("{}",stamp);
        other();
        Thread.sleep(1000);
        log.debug("change A->C {}",ref.compareAndSet(ref.getReference(),"C",stamp,stamp+1));
    }

    private static void other() {
        new Thread(()->{
            int stamp = ref.getStamp();
            log.debug("change A->B {}",ref.compareAndSet(ref.getReference(),"B",stamp,stamp+1));
        },"t1").start();

        new Thread(()->{
            int stamp = ref.getStamp();
            log.debug("change B->A {}",ref.compareAndSet(ref.getReference(),"B",stamp,stamp+1));
        },"t2").start();

    }
}
