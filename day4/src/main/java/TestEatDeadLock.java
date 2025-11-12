import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

public class TestEatDeadLock {
    public static void main(String[] args) {
        // 五只筷子
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");

        new Philosopher("Lichen",c1,c2).start();
        new Philosopher("cheny",c2,c3).start();
        new Philosopher("watermelon",c3,c4).start();
        new Philosopher("Seraphim",c4,c5).start();
        new Philosopher("dolphin",c5,c1).start();
    }
}


class Chopstick extends ReentrantLock{
    String name;

    public Chopstick(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "筷子{" + name + "}";
    }
}

@Slf4j
class Philosopher extends Thread{
    Chopstick left;
    Chopstick right;

    public Philosopher(String name,Chopstick left,Chopstick right){
        super(name);
        this.left = left;
        this.right = right;
    }

    @SneakyThrows
    @Override
    public void run(){
        while(true){
            // 尝试获取左手筷子
            if(left.tryLock()){
                try {
                    // 尝试获取右手筷子
                    if(right.tryLock()){
                        try {
                            eat();
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock(); // 释放左手筷子
                }
            }
        }
    }

    private void eat() throws InterruptedException {
        log.debug("吃吃吃");
        Thread.sleep(10);
    }
}
