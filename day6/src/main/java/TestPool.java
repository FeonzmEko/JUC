import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestPool {
    public static void main(String[] args) {
        // 创建线程池，传入线程数量，超时时间，消息队列容量，拒绝策略
        ThreadPool threadPool = new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1,(queue,task)->{
            // 1.死等
            //queue.put(task);
            // 2.带超时等待
            //queue.offer(task,1500,TimeUnit.MILLISECONDS);
            // 3.让调用者放弃任务执行
            //log.debug("放弃{}",task);
            // 4.让调用者抛出异常
            //throw new RuntimeException("任务执行失败");
            // 5.让调用者自己执行任务
            task.run();
        });

        // 执行任务
        for (int i = 0; i < 4; i++) {
            int j = i;
            // 调用线程池执行任务API
            threadPool.execute(()->{
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}",j);
            });
        }
    }
}

// 线程池
@Slf4j
class ThreadPool{
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;
    // 线程集合
    private HashSet<Worker> workers = new HashSet<>();
    // 核心线程数
    private int coreSize;
    // 获取任务的超时时间
    private long timeout;

    private TimeUnit timeUnit;
    // 拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit,int queueCapacity,RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    // 执行任务
    public void execute(Runnable task){
        // 当任务数没有超过coreSize时，直接交给worker对象执行
        // 如果任务超过coeSize，直接加入任务队列暂存
        synchronized (workers) {
            // 线程数量不超过线程池最大数量-1
            if (workers.size() < coreSize) {
                // 创建线程对象，并给出任务task
                Worker worker = new Worker(task);
                // 打印提示语句
                log.debug("新增worker,{}",worker);
                // 把创建的线程加入线程集合当中去
                workers.add(worker);
                // 启动线程
                worker.start();
            } else {
                // 如果正在运行线程已满，消息加入队列
                taskQueue.tryPut(rejectPolicy,task);
            }
        }
    }

    class Worker extends Thread{
        private Runnable task;
        public Worker(Runnable task){
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1) 当task不为空，执行任务
            // 2) 当task为空，从任务队列获取任务
            while (task != null || (task = taskQueue.poll(timeout,timeUnit)) != null) {
                try {
                    log.debug("执行,{}",task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            synchronized (workers){
                log.debug("worker 被移除{}",this);
                workers.remove(this);
            }
        }
    }
}

// 任务队列
@Slf4j
class BlockingQueue<T>{
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();
    // 2.锁
    private ReentrantLock lock = new ReentrantLock();
    // 3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();
    // 4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();
    // 5.容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 带超时的阻塞获取
    public T poll(long timeout, TimeUnit unit){
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            // 使用while增强代码健壮性来避免虚假唤醒
            while(queue.isEmpty()){
                if(nanos < 0){
                    return null;
                }
                try {
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take(){
        lock.lock();
        try {
            while(queue.isEmpty()){
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞添加
    public void put(T element){
        lock.lock();
        try {
            while (queue.size() == capacity){
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    // 超时放入
    public boolean offer(T task,long timeout,TimeUnit timeUnit){
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity){
                try {
                    log.debug("等待加任务队列 {}...",task);
                    if(nanos <= 0){
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    // 获取大小
    public int size(){
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    // 拒绝策略
    public void tryPut(RejectPolicy<T> rejectPolicy,T task){
        // 加锁，保证高并发情况下的原子性
        lock.lock();
        try {
            // 判断队列是否已满
            if (queue.size() == capacity) {
                // 队列已满，执行拒绝策略
                rejectPolicy.reject(this,task);
            } else { // 有空闲,消息入队列
                log.debug("加入任务队列 {}",task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}

@FunctionalInterface
interface RejectPolicy<T>{
    public void reject(BlockingQueue<T> tBlockingQueue, T task);
}
