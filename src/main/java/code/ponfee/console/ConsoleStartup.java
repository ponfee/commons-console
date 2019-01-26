package code.ponfee.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * SpringBoot启动类
 *
 * @author Ponfee
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
})
public class ConsoleStartup {

    public static void main(String[] args) {
        SpringApplication.run(ConsoleStartup.class, args);
    }

}
