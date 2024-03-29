package code.ponfee.console.redis;

import java.util.Map;

import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageParameter;

/**
 * Redis manager service interface
 * 
 * @author Ponfee
 */
public interface RedisManagerService {

    Page<RedisKey> query4page(PageParameter params);

    void addOrUpdateRedisEntry(Map<String, Object> params);

    void delete(String... keys);

    void refresh();

    void refreshForce();

    void flushAll();
}
