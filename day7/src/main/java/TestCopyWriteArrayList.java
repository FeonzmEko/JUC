import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestCopyWriteArrayList {
    public static void main(String[] args) throws InterruptedException {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        Collections.addAll(list,1,2,3);
        Iterator<Integer> iter = list.iterator();
        new Thread(()->{
            list.remove(0);
            System.out.println(list);
        }).start();

        Thread.sleep(100);
        while(iter.hasNext()){
            System.out.println(iter.next());
        }
    }
}
