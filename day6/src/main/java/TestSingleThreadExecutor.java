import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TestSingleThreadExecutor {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();

        pool.execute(()->{
            log.debug("1");
            int i = 1/0;
        });

        pool.execute(()->{
            log.debug("1");
        });
    }
}
