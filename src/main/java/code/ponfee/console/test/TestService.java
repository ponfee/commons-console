package code.ponfee.console.test;

import code.ponfee.commons.model.Result;

public interface TestService {

    void testTransaction(String datasource, boolean bool);

    Result<Long> selectScroll();
}
