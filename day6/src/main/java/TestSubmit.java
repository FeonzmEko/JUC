import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;

@Slf4j
public class TestSubmit {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<String> future = pool.submit(()->{
            log.debug("running");
            Thread.sleep(1000);
            log.debug("finish");
            return "ok";
        });

        Future<String> future1 = pool.submit(()->{
            log.debug("running");
            Thread.sleep(1000);
            log.debug("finish");
            return "ok";
        });
        pool.shutdownNow();



        Future<String> future2 = pool.submit(()->{
            log.debug("running");
            Thread.sleep(1000);
            log.debug("finish");
            return "ok";
        });


        /*List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.debug("begin");
                    Thread.sleep(1000);
                    return "1";
                },

                () -> {
                    log.debug("middle");
                    Thread.sleep(1000);
                    return "2";
                },

                () -> {
                    log.debug("end");
                    Thread.sleep(2000);
                    return "3";
                }

        ));*/

        /*futures.forEach(f -> {
            try {
                log.debug("{}", f.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });*/


    }
}
