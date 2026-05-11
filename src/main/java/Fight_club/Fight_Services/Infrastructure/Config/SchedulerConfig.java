package Fight_club.Fight_Services.Infrastructure.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SchedulerConfig {

    @Value("${fight.scheduler.pool-size:0}")
    private int configuredPoolSize;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        int poolSize = configuredPoolSize > 0
                ? configuredPoolSize
                : Math.max(2, Runtime.getRuntime().availableProcessors());
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix("fight-tick-");
        scheduler.initialize();
        return scheduler;
    }
}
