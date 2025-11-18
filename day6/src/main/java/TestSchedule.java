import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class TestSchedule {
    public static void main(String[] args) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        log.debug("{}",now);
    }
}
