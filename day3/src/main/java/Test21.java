import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j(topic = "c.Test21")
public class Test21 {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(()->{
                queue.put(new Message(id,"值"+id));
            },"Producer - " + i).start();
        }

        new Thread(()->{
           while(true){
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               queue.take();
           }
        },"Consumer").start();
    }
}

// 消息队列类，java线程之间通信
@Slf4j
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


@Data
@AllArgsConstructor
@NoArgsConstructor
final class Message{
    private int id;
    private Object value;
}
