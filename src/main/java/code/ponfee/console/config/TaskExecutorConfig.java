/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console.config;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import code.ponfee.commons.concurrent.NamedThreadFactory;
import code.ponfee.commons.concurrent.ThreadPoolExecutors;

/**
 * TaskExecutorConfig
 * 
 * @author Ponfee
 */
@Configuration
@EnableScheduling
public class TaskExecutorConfig implements SchedulingConfigurer {

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(60);
        pool.setCorePoolSize(4);
        pool.setMaxPoolSize(32);
        pool.setQueueCapacity(0);
        pool.setRejectedExecutionHandler(ThreadPoolExecutors.CALLER_RUN);
        return pool;
    }

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
