package code.ponfee.console.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis扫描配置
 * 
 * @author Ponfee
 */
@Configuration
@AutoConfigureAfter(MyBatisConfig.class)
public class MyBatisScannerConfig {

    @Bean
    public static MapperScannerConfigurer tkMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("code.ponfee.console.dao.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }

}
