package code.ponfee.console.config;

import code.ponfee.commons.data.NamedDataSource;
import code.ponfee.commons.data.lookup.MultipleCachedDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * Druid监控配置
 * 
 * http://localhost:8100/druid/login.html
 *
 * @author Ponfee
 */
@Configuration
public class DataSourceConfig {

    @Bean("dataSource")
    public DataSource dataSource() {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource("application-jdbc.yml"));
        factory.afterPropertiesSet();
        NamedDataSource[] ds = NamedDataSource.build("datasource", factory.getObject());
        return new MultipleCachedDataSource(1800, ds);
    }

    /**
     * 配置Druid监控启动页面
     */
    @Bean
    public ServletRegistrationBean druidStartViewServlet() {
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean srb = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        //白名单：
        srb.addInitParameter("allow", "127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        srb.addInitParameter("deny", "192.168.1.100");

        //登录查看信息的账号密码.
        srb.addInitParameter("loginUsername", "admin");
        srb.addInitParameter("loginPassword", "123456");

        //是否能够重置数据.
        srb.addInitParameter("resetEnable", "false");
        return srb;
    }

    /**
     * Druid监控过滤器配置规则
     */
    @Bean
    public FilterRegistrationBean druidStartFilter() {
        FilterRegistrationBean frb = new FilterRegistrationBean(new WebStatFilter());

        //添加过滤规则.
        frb.addUrlPatterns("/*");

        //添加不需要忽略的格式信息.
        frb.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");

        return frb;
    }

}
