package code.ponfee.console.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import code.ponfee.commons.data.DataSourceNaming;
import code.ponfee.commons.data.MultipleDataSourceAspect;

@Component
@Aspect
public class MultipleDataSourceChanger extends MultipleDataSourceAspect {

    @Around(value = "execution(public * code.ponfee.console..*..*Impl..*(..)) && @annotation(dsn)", argNames = "pjp,dsn")
    @Override
    public Object doAround(ProceedingJoinPoint pjp, DataSourceNaming dsn) throws Throwable {
        return super.doAround(pjp, dsn);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

}
