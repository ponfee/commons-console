package code.ponfee.console.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Datasource transaction management
 * 
 * @author Ponfee
 */
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
@EnableTransactionManagement
public class TransactionManagerConfig {

    @Bean("transactionManager")
    public PlatformTransactionManager transactionManager(
        @Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
