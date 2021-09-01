package code.ponfee.console.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import code.ponfee.commons.model.PageParameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;

import code.ponfee.commons.io.Closeables;
import code.ponfee.commons.util.Enums;

/**
 * Lightweight redis manager service implementation
 * 
 * @author Ponfee
 */
//@Service("lightweightRedisManagerService")
public class LightweightRedisManagerServiceImpl extends AbstractRedisManagerService {

    @Override
    public List<RedisKey> query4list(PageParameter params) {
        MatchMode matchMode = Enums.ofIgnoreCase(
            MatchMode.class, params.getString("matchmode"), MatchMode.LIKE
        );
        String key = matchMode.build(params.getString("keyword"));
        switch (matchMode) {
            case EQUAL:
                if (StringUtils.isEmpty(key)) {
                    return Collections.emptyList();
                }
                return normalRedis.execute((RedisCallback<List<RedisKey>>) conn -> {
                    RedisKey rk = getAsString(conn, key.getBytes());
                    return rk.getType() == DataType.NONE ? null : Collections.singletonList(rk);
                });
            default:
                int pageSize = params.getPageSize();
                return normalRedis.execute(
                    (RedisCallback<List<RedisKey>>) (conn -> {
                        Cursor<byte[]> cursor = conn.scan(
                            ScanOptions.scanOptions().match(key).count(pageSize).build()
                        );
                        List<RedisKey> res = new ArrayList<>(pageSize);
                        while (cursor.hasNext()) {
                            res.add(getAsString(conn, cursor.next()));
                            if (res.size() >= pageSize) {
                                break;
                            }
                        }
                        Closeables.log(cursor);
                        return res;
                    })
                );
        }
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshForce() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onAddOrUpdate(RedisKey redisKey) {}

    @Override
    protected void onDelete(List<String> keys) {}

}
