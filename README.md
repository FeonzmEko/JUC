# 进程和线程

## 进程和线程

### 进程

* 程序由指令和数据组成，但这些指令哟啊运行，数据要读写，就必须把指令加载至CPU，数据加载至内存。在指令运行过程中还需要用到磁盘，网络等设备。进程就是用来加载指令，管理内存，管理IO的
* 当一个程序被运行，从磁盘加载这个程序的代码至内存，这时就开启了一个线程
* 进程就可以视为程序的一个实例，大部分程序可以同时运行多个实例进程，有的程序也只能启动一个实例进程。

### 线程

* 一个进程之内可以分为一到多个线程
* 一个进程就是一个指令流，将指令流中的一条条指令以一定的顺序交给CPU执行
* Java中，戏线程作为最小调度单位，进程作为资源分配的最小单位。在windows中进程是不活动的，只是作为线程的容器

### 对比

* 进程基本上是相互独立的，而线程存在于进程内，是进程的一个子集
* 进程拥有共享的资源，如内存空间等，供内部的线程共享
* 进程间通信较为复杂
  * 同一台计算机的进程通信叫IPC
  * 不同计算机之间的进程通信，需要通过网络，并遵循共同的协议
* 线程通信相对简单，因为他们共享进程内的内存，一个例子是多个线程可以访问同一个共享变量
* 线程更轻量，线程上下文切换成本一般比进程上下文切换低



## 并行与并发

单核cpu下，线程实际还是`串行执行`的。操作系统中有一个组件叫做任务调度器，将cpu的时间片（windows下时间片最小约15ms）分给不同的线程使用，只是由于cpu在线程之间的切换非常快，人类觉得是同时运行的。总结一句话**微观串行，宏观并行**

一般会把这种线程轮流使用CPU的方法称为**并发，concurrent**

![image-20251107195334747](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251107195334747.png)

多核cpu下，每个核都可以调度运行线程，这时候线程是可以**并行**的。

## 应用

### 异步调用

从方法调用的角度来讲，如果

* 需要等待结果返回，才能继续运行就是同步
* 不需要等待结果返回，就能继续运行就是异步

注意：同步在多线程中还有另外一层意思，就是让多个线程步调一致

### 效率提升



# Java线程

* 创建和运行线程
* 查看线程
* 线程API
* 线程状态

## 创建和运行线程

方法一，直接使用Thread

方法二，使用Runnable融合Thread

方法三，FutureTask配合Thread



## 观察多个线程同时运行



## 查看线程进程的方法

* tasklist



## 原理之线程运行

### 栈与栈帧

### 线程上下文切换（Thread Context Switch）

因为以下原因导致cpu不再执行当前线程，转而执行另一个线程的代码

* 线程的cpu时间片用完
* 垃圾回收
* 有更高优先级的线程需要运行
* 线程自己调用了sleep,yield,wait,join,park,synchronized,lock等方法

当上下文切换发生时，需要有操作系统保存当前线程的状态，并恢复另一个线程的状态，Java中对应的概念就是程序计数器，作用是记住下一条jvm命令的执行地址，是线程私有的

* 状态包括程序计数器，栈帧信息，如局部变量，操作数栈返回地址等
* 频繁的上下文切换会影响性能

## 常见方法

* start：启动线程，让线程就绪
  * 里面的代码不一定立刻运行（cpu的时间片还没分给他），只能调用一次
* run：线程启动后运行的方法
* join：等待线程运行结束
* set，get方法
* sleep：休眠
* yield：礼让

### start与run

调用start会把当前线程从`NEW`状态进入`RUNNABLE`状态

### sleep与yield

#### sleep

1. 调用sleep会让当前线程从`RUNNABLE`进入`TIMED_WAITING`状态
2. 其他线程可以使用`interrupt`方法打断正在睡眠的线程，这时sleep方法会抛出`InterruptedException`
3. 睡眠结束后的线程未必会立刻得到执行
4. 建议用`TimeUnit`的sleep代替Thread的sleep来获得更好的可读性

#### yield

1. 调用yield会让当前线程从`RUNNING`进入`RUNNABLE`就绪状态，然后调度其它2同优先级的线程。如果这时没有同优先级的线程，那么不能保证让当前线程暂停的效果
2. 具体的实现依赖于os的任务调度器

#### 线程优先级

* 线程优先级会提示（hint）调度器优先调度该线程，但它仅仅是一个提示，调度器可以忽略它
* 如果cpu比较忙，那么优先级高的线程会获得更多的时间片，但cpu闲时，优先级几乎没用。

### join

等待某个线程运行结束

### interrput

#### 打断sleep，wait，join

这三个被打断之后，会清除标记，导致调用`isInterrupted()`方法，会输出false

#### 打断正常运行的线程

#### 两阶段终止模式

**在一个线程中优雅地终止另一个线程，优雅指的是给被中止的线程一个办理后事的机会**

#### 打断park线程

## 主线程与守护线程

主线程结束后，守护线程会被迫结束

## 五种状态

从**操作系统**层面来描述的

![image-20251108155307957](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251108155307957.png)

## 六种状态

从**Java API**层面来描述的

![image-20251108155403421](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251108155403421.png)

16:14:18.757 [main] DEBUG TestState -- t1 state NEW
16:14:18.758 [main] DEBUG TestState -- t2 state RUNNABLE
16:14:18.758 [main] DEBUG TestState -- t3 state TERMINATED
16:14:18.758 [main] DEBUG TestState -- t4 state TIMED_WAITING
16:14:18.758 [main] DEBUG TestState -- t5 state WAITING
16:14:18.758 [main] DEBUG TestState -- t6 state BLOCKED

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestState {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1"){
            @Override
            public void run() {
                log.debug("running...");
            }
        };

        Thread t2 = new Thread("t2"){
            @Override
            public void run() {
                while (true){

                }
            }
        };
        t2.start();

        Thread t3 = new Thread("t3"){
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t3.start();

        Thread t4 = new Thread("t4"){
            @Override
            public void run() {
                synchronized (TestState.class){
                    try {
                        Thread.sleep(10000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t4.start();

        Thread t5 = new Thread("t5"){
            @Override
            public void run() {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t5.start();

        Thread t6 = new Thread("t6"){
            @Override
            public void run() {
                synchronized (TestState.class){
                    try {
                        Thread.sleep(1000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t6.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.debug("t1 state {}",t1.getState());
        log.debug("t2 state {}",t2.getState());
        log.debug("t3 state {}",t3.getState());
        log.debug("t4 state {}",t4.getState());
        log.debug("t5 state {}",t5.getState());
        log.debug("t6 state {}",t6.getState());
    }
}

```

# 共享模型之管程

## 共享问题

### 问题分析

原子性无法得到保证，导致数据脏写脏读

### 临界区 Critical Section

* 一个程序运行多个线程本身是没有问题的
* 问题出现在多个线程访问**共享资源**
  * 多个线程**读**共享资源也没问题
  * 在多个线程对共享资源读写操作时发生指令交错，就会出现问题
* 一段代码块如果存在对**共享资源**的多线程读写操作，就称这段代码为**临界区**

## synchronized

### 应用之互斥

为了避免临界区的竞态条件发生，有多种手段可以达到目的。

* 阻塞式的解决方案：synchronized，Lock
* 非阻塞式的解决方案：原子变量

synchronized，俗称**【对象锁】**，采用互斥的方式让同一时刻至多只有一个线程能持有对象锁，其他线程在想获取这个对象锁就会被阻塞住。这样就能保证用有锁的线程可以安全的执行临界区内的代码，不用担心线程的上下文切换

**要用同一把锁，否则和没加锁没区别**

### 锁的位置

`public synchronized void a()`，锁住的是`this`对象，即调用者本身

`public static synchronized void a()`，锁住的是类的类对象`Myclass.class`

## 线程安全分析

### 成员变量和静态变量

* 如果它们没有共享，则线程安全
* 如果它们被共享了，根据它们的状态是否能够改变，又分两种情况
  * 如果只有读操作，则线程安全
  * 如果有读写操作，则这段代码是临界区，需要考虑线程安全

### 局部变量

* 局部变量是线程安全的
* 但局部变量引用的对象未必
  * 如果该对象没有逃离方法的作用范围，它是线程安全的
  * 如果该对象逃离方法的作用范围，则需要考虑线程安全

### 成员变量

创建一个该类的实例对象，并使用实例对象的method1方法重写多个线程的run方法时，由于`add`方法不是原子性的，所以会导致线程不安全问题发生

```java
class ThreadUnsafe{
    ArrayList<String> list = new ArrayList<>();
    public void method1(int loopNumber){
        for (int i = 0; i < loopNumber; i++) {
            method2();
            method3();
        }
    }

    private void method2() {
        list.add("1");
    }

    private void method3() {
        list.remove(0);
    }
}
```

### 常见线程安全类

* String
* Integer
* StringBuffer
* Random
* Vector
* Hashtable
* java.util.concurrent包下的类

说它们线程安全指的是，多个线程调用它们同一个实例的某个方法时，是线程安全的。也可以理解为：

* 它们的每个方法是原子的
* 但**注意**它们多个方法的组合不是原子的

![image-20251108200555341](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251108200555341.png)

## Monitor

### Java对象头

![image-20251109142606494](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251109142606494.png)

### Monitor

Monitor被翻译为**监视器**或**管程**

每个Java对象都可以关联一个Monitor对象，如果使用synchronized给对象加上锁（重量级）之后，该对象的Mark Word中就被设置指向Monitor对象的指针

Monitor对象结构

![image-20251109143818115](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251109143818115.png)

* 刚开始Monitor中Owner为null
* 当Thread-2执行synchronized后，mointor中的owner变成Thread-2，owner只能有一个
* 其他线程执行到synchronized，发现owner中已经有了线程，就会进入EntryList被阻塞
* 从而保证了多线程并发情况下的原子性操作

## synchronized原理进阶

### 轻量级锁

轻量级锁的使用场景：如果一个对象虽然有多线程访问，但多线程访问的时间是错开的（也就是没有竞争），那么可以用轻量级锁来优化。

轻量级锁对使用者是透明的，即语法仍然是synchronized

假设有两个方法同步块，利用同一个对象枷锁

```java
static final Object obj = new Object();
public static void method1(){
    synchronized(obj){
        // 同步块A
        method2();
    }
}

public static void method2(){
    synchronized(obj){
        // 同步块B
    }
}
```



### 锁膨胀



### 自旋优化

重量级锁竞争的时候，还可以使用自旋来进行优化，如果当前线程自旋成功（即这时候持锁线程已经退出了同步块，释放了锁），这时当前线程就可以避免阻塞



### 偏向锁

#### 偏向状态

**对象头格式**

![image-20251110094617434](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251110094617434.png)

一个对象创建时：

* 如果开启了偏向锁（默认开启），那么对象创建后，markword值为0x05即最后三位为101，这时它的thread，epoch，age都为0
* 偏向锁是默认是延迟的，不会在程序启动时立即生效，如果想避免延迟，可以加VM参数来避免延迟
* 如果没有开启偏向锁，那么对象创建后，markword值为0x01即最后三位为001，这时它的hashcode，age都为0，第一次用到hashcode时才会赋值

#### 撤销-调用对象hashCode

调用了对象的hashCode，但偏向锁的对象MarkWord中存储的是线程id，如果调用hashCode会导致偏向锁被撤销

* 轻量级锁会在锁记录中记录hashCode
* 重量级锁会在Monitor中记录hashCode

#### 撤销-其它线程使用对象

当有其它线程使用偏向锁对象时，会将偏向锁升级为轻量级锁

#### 批量重偏向

如果对象虽然被多个线程访问，但没有竞争，这时偏向了线程t1的对象仍然有机会重新偏向t2，冲偏向会重置对象的ThreadID

当撤销偏向锁阈值超过20次后，jvm会这样觉得，我是不是偏向错了呢，于是会在给这些对象加锁时重新偏向至加锁线程

#### 批量撤销

当撤销偏向锁阈值超过40次后，jvm还会这样觉得，自己确实偏向错了，根本就不该偏向。于是整个类的所有对象都会变成不可偏向的，新建的对象也是不可偏向的。

### 锁消除

JIT对热点代码块去锁

## wait notify

### 为什么需要wait

![image-20251110105605958](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251110105605958.png)

![image-20251110105639204](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251110105639204.png)

**大致理解：**

正在工作的线程需要额外的东西来休息，但无法获得，会调用wait方法从Owner进入WaitSet，此时被阻塞的线程会争抢进入Owner，并给在WaitSet里休息的线程递烟（notify），之后便离开休息室，重新进入竞争队列

### 原理

![image-20251110110307399](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251110110307399.png)

* Owner线程发现条件不满足，调用wait方法，即可计入WaitSet变为WAITING状态
* BLOCKED和WAITING的线程都处于阻塞状态，不占用CPU时间片
* BLOCKED线程会在Owner线程释放锁时唤醒
* WAITING线程会在Owner线程调用notify或notifyAll时唤醒，但唤醒后并不意味着立刻获得锁，仍需进入EntryList重新竞争



### API介绍

* obj.wait()让进入obj监视器的线程到waitSet等待
* obj.notify()在obj上正在waitSet等待的线程挑一个唤醒
* obj.notifyAll()让obj上正在waitSet等待的线程全部唤醒

它们都是线程之间进行协作的手段，都属于Object对象的方法，必须获得此对象的锁，才能调用这几个方法

### 正确用法

**sleep和wait方法的区别**

* sleep是Thread方法，而wait是Object的方法
* sleep不需要强制和synchronized配合使用，但wait需要和synchronized一起用
* sleep在休眠的同时，不会释放对象锁，但wait在等待的时候会释放对象锁

```java
synchronized(lock){
    while(条件不成立){
        lock.wait();
    }
    
    // do something
}

// 另一个线程notifyAll()
synchronized(lock){
    lock.notifyAll();
}
```

## 同步模式之保护性暂停

### 定义

定义：Guarded Suspension，用在一个线程等待另一个线程的执行结果

* 有一个结果在线程间传递，让他们关联同一个GuardedObject
* 如果需要不断传递，可以使用消息队列
* JDK中，join，Future的实现，采用的就是此模式
* 因为需要等待另一方的结果，归类到同步模式

### 原理之join

### 扩展点

如果需要在多个类之间使用GuardedObject对象，作为参数传递不是很方便，因此设计一个用来解耦的中间类，不仅能解耦等待者和生产者，还能同时支持多个任务的管理

![image-20251110170332434](C:\Users\Qingfeng\AppData\Roaming\Typora\typora-user-images\image-20251110170332434.png)

## 异步模式之生产者/消费者

* 与之前的保护性暂停中的GuardedObject不同，不需要产生结果和消费结果的线程一一对应
* 消费队列可以用来平衡生产和消费的线程资源
* 生产者仅负责产生结果数据，不管新数据该如何处理，而消费者专心处理结果数据
* 消息队列是有容量限制的。满时不会再加入数据，空时不会再消耗数据
* JDK中各种阻塞队列，采用的就是这种模式

**代码实现**

```java
class MessageQueue{
    // 双向链表来存储
    private LinkedList<Message> list = new LinkedList<>();
    // 队列容量
    private int capacity;

    public MessageQueue(int capacity){
        this.capacity = capacity;
    }

    // 获取消息
    public Message take(){
        // 监察对象是否为空
        synchronized (list){
            while(list.isEmpty()){
                try {
                    log.debug("Consumer is waiting.");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从队列头部获取消息返回
            Message message = list.removeFirst();
            log.debug("已消费消息 -> {}",message);
            list.notifyAll();
            return message;
        }
    }

    // 存入消息
    public void put(Message message){
        // 队列尾部放入
        synchronized (list){
            while(list.size() == capacity){
                try {
                    log.debug("Queue is already full.");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 在尾部加入消息
            list.addLast(message);
            log.debug("已生产消息 -> {}",message);
            list.notifyAll();
        }
    }
}
```

