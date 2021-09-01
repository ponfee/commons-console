package code.ponfee.console.config;

import code.ponfee.commons.mybatis.SqlMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis配置
 * 
 * @author Ponfee
 */
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
public class MyBatisConfig {

    private static final PathMatchingResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();

    @Bean("sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage("code.ponfee.console.model");
        sessionFactory.setConfigLocation(
            RESOLVER.getResource("classpath:mybatis-conf.xml")
        );
        sessionFactory.setMapperLocations(
            RESOLVER.getResources("classpath*:code/ponfee/**/dao/mapping/*.xml")
        );
        return sessionFactory;
    }

    @Bean("sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("sqlMapper")
    public SqlMapper sqlMapper(SqlSessionTemplate sqlSessionTemplate) {
        return new SqlMapper(sqlSessionTemplate);
    }

    /*@Bean("mapperScannerConfigurer")
    @DependsOn("sqlMapper")
    public MapperScannerConfigurer tkMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("code.ponfee.console.dao.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }*/
}
