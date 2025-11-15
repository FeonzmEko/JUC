import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Test3 {
    static AtomicReference<String> ref = new AtomicReference<>("A");

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 获取值
        String prev = ref.get();
        Thread.sleep(1000);
        // 尝试更改为C
        log.debug("change A->C {}",ref.compareAndSet(prev,"C"));
    }

}

