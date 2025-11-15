import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class Test2 {
    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(5);

        System.out.println(i.incrementAndGet());
        System.out.println(i.getAndIncrement());

        i.updateAndGet(value->value*10);
        System.out.println(i.get());

        uppdateAndGet(i,p->p/2);
        System.out.println(i.get());
    }

    public static void uppdateAndGet(AtomicInteger i, IntUnaryOperator operator){
        while (true) {
            int prev = i.get();
            int next = operator.applyAsInt(prev);
            if(i.compareAndSet(prev,next)) {
                break;
            }
        }
    }
}