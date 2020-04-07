package code.ponfee.console.database;

import java.util.LinkedHashMap;

import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageRequestParams;
import code.ponfee.commons.model.Result;

/**
 * Database dynamic query service interface
 * 
 * @author Ponfee
 */
public interface DatabaseQueryService {

    Result<Page<LinkedHashMap<String, Object>>> query4page(PageRequestParams params);

}
