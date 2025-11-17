import lombok.Data;
import lombok.SneakyThrows;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class TestUnsafe {
    @SneakyThrows
    public static void main(String[] args) throws NoSuchFieldException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        System.out.println(unsafe);

        // 获取域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));

        Teacher t = new Teacher();
        // 执行cas操作
        unsafe.compareAndSwapInt(t,idOffset,0,1);
        unsafe.compareAndSwapObject(t,nameOffset,null,"张三");

        System.out.println(t);
    }
}

@Data
class Teacher{
    volatile int id;
    volatile String name;
}