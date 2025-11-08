import lombok.extern.slf4j.Slf4j;

public class Test3 {
    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();

        Thread.sleep(3500);
        tpt.stop();
    }
}


@Slf4j
class TwoPhaseTermination{
    private Thread monitor;

    // 启动监控线程
    public void start(){
        monitor = new Thread(()->{
            while(true){
                Thread current = Thread.currentThread();
                if(current.isInterrupted()){
                    log.debug("料理后事");
                    break;
                }

                try {
                    Thread.sleep(1000);
                    log.debug("执行监控记录");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 重新设置打断标记
                    current.interrupt();
                }
            }
        });

        monitor.start();
    }

    // 终止监控线程
    public void stop(){
        monitor.interrupt();
    }
}
