package code.ponfee.console.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import code.ponfee.commons.mybatis.SqlMapper;

/**
 * MyBatis配置
 * 
 * @author Ponfee
 */
@Configuration
public class MyBatisConfig {

    private static final PathMatchingResourcePatternResolver RESOLVER = 
        new PathMatchingResourcePatternResolver();

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
    @DependsOn("sqlSessionFactory")
    public SqlSessionTemplate sqlSessionTemplate(
        @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("sqlMapper")
    @DependsOn("sqlSessionTemplate")
    public SqlMapper sqlMapper(SqlSessionTemplate sqlSessionTemplate) {
        return new SqlMapper(sqlSessionTemplate);
    }

}
