import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestTimer {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

        log.debug("start...");
        // 循环计时任务
        pool.scheduleAtFixedRate(()->{
            log.debug("running...");
        },1,1,TimeUnit.SECONDS);

    }
}
