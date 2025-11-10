import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j(topic = "c.Test20")
public class Test20 {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }

        Thread.sleep(1000);
        for (Integer id : Mailboxes.getIds()) {
            new Postman(id,"内容" + id).start();
        }

    }
}

@Slf4j(topic = "c.People")
class People extends Thread{
    @Override
    public void run() {
        // 收信
        GuardedObject guardedObject = Mailboxes.createGuardedObject();
        log.debug("开始收信 id:{}",guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到回信 id:{} 内容:{}",guardedObject.getId(),mail);
    }
}

@Slf4j(topic = "c.Postman")
class Postman extends Thread{
    private int id;
    private String mail;

    public Postman(int id,String mail){
        this.id = id;
        this.mail = mail;
    }
    @Override
    public void run() {
        GuardedObject guardedObject = Mailboxes.getGuardedObject(id);
        // 送信
        log.debug("送信 id:{} 内容:{}",id,mail);
        guardedObject.complete(mail);
    }
}

class Mailboxes{
    private static Map<Integer,GuardedObject> boxes = new Hashtable<>();

    private static int id = 1;
    // 产生唯一id
    private static synchronized int generateId(){
        return id++;
    }

    public static GuardedObject getGuardedObject(int id){
        return boxes.remove(id);
    }

    // 产生GuardedObject方法
    public static GuardedObject createGuardedObject(){
        GuardedObject go = new GuardedObject(generateId());
        boxes.put(go.getId(),go);
        return go;
    }

    public static Set<Integer> getIds(){
        return boxes.keySet();
    }
}

class GuardedObject{
    // 标识id
    private int id;

    public GuardedObject(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    // 结果
    private Object response;

    // 获取结果
    public Object get(long timeout){

        synchronized (this){
            // 开始时间
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while(response == null){
                // 这一次应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间时，退出循环
                if(waitTime <= 0){
                    break;
                }

                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经历时间
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }

    // 产生结果
    public void complete(Object response){
        synchronized (this){
            this.response = response;
            this.notifyAll();
        }
    }
}
