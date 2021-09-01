/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务示例
 *
 * @author Ponfee
 */
@Component
public class ScheduleTask {

    protected static Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Scheduled(cron = "0/2 * * * * ?")
    public void test1() {
        logger.debug("task start");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.debug("task end");
    }

}
