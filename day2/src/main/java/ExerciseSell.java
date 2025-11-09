import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

@Slf4j
public class ExerciseSell {
    public static void main(String[] args) throws InterruptedException {
        // 模拟多人买票
        TicketWindow window = new TicketWindow(1000);

        // 卖出的票数统计
        List<Integer> amountList = new Vector<>();

        // 所有线程的集合
        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < 2000; i++) {
            Thread thread = new Thread(()->{
                int amount = window.sell(randomAmount());
                amountList.add(amount);
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        // 统计结果
        log.debug("余票：{}",window.getCount());
        log.debug("卖出的票数：{}",amountList.stream().mapToInt(i->i).sum());
    }

    // Random为线程安全
    static Random random = new Random();

    // 随机1-5
    public static int randomAmount(){
        return random.nextInt(5)+1;
    }
}

// 售票窗口
@Data
@AllArgsConstructor
class TicketWindow{
    private int count;
    // 售票
    public synchronized int sell(int amount){
        if(this.count >= amount){
            this.count -= amount;
            return amount;
        } else {
            return 0;
        }
    }
}
