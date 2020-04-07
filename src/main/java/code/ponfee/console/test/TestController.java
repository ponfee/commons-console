package code.ponfee.console.test;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import code.ponfee.commons.model.Result;

@RestController
@RequestMapping("test")
public class TestController {

    private @Resource TestService service;

    /**
     * http://localhost:8100/test/transaction?datasource=primary&bool=true
     * 
     * 
     * http://localhost:8100/test/transaction?datasource=secondary&bool=true
     * 
     * @param datasource
     * @param bool
     * @return
     */
    @GetMapping("transaction")
    public Result<Void> transaction(@RequestParam("datasource") String datasource, @RequestParam("bool") boolean bool) {
        service.testTransaction(datasource, bool);
        return Result.SUCCESS;
    }

    /**
     * http://localhost:8100/test/scroll
     * 
     * @return
     */
    @GetMapping("scroll")
    public Result<Long> scroll() {
        return service.selectScroll();
    }

}
