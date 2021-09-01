/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console.config;

import code.ponfee.commons.concurrent.NamedThreadFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * TaskExecutorConfig
 * 
 * @author Ponfee
 */
@Configuration
@EnableScheduling // To support @Scheduled(cron = "0/2 * * * * ?") annotation
public class TaskExecutorConfig implements SchedulingConfigurer {

    /**
     * 配置定时任务线程池大小
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(
            10, new NamedThreadFactory("task-sched-exec")
        );
        taskRegistrar.setScheduler(scheduler);
    }
}
