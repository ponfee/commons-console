/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console.config;

import code.ponfee.commons.concurrent.ThreadPoolExecutors;
import code.ponfee.commons.util.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * InitializerConfig
 * 
 * @author Ponfee
 */
@Configuration
public class CommonConfig {

    @Bean("springContextHolder")
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(60);
        pool.setCorePoolSize(4);
        pool.setMaxPoolSize(32);
        pool.setQueueCapacity(0);
        pool.setRejectedExecutionHandler(ThreadPoolExecutors.CALLER_RUN);
        return pool;
    }
}
