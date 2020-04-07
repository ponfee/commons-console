package code.ponfee.console.redis;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import code.ponfee.commons.exception.Throwables;
import code.ponfee.commons.json.Jsons;
import code.ponfee.commons.math.Numbers;
import code.ponfee.commons.model.ExtendedHashMap;
import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageBoundsResolver;
import code.ponfee.commons.model.PageBoundsResolver.PageBounds;
import code.ponfee.commons.model.PageHandler;
import code.ponfee.commons.model.PageRequestParams;
import code.ponfee.commons.util.Enums;

/**
 * Redis manager service implementation
 * 
 * @author Ponfee
 */
public abstract class AbstractRedisManagerService implements RedisManagerService {

    protected static Logger logger = LoggerFactory.getLogger(AbstractRedisManagerService.class);
    private static final int MAX_VALUE_LENGTH = 200;

    protected @Resource RedisTemplate<String, Object> redis;

    protected abstract List<RedisKey> query4list(PageRequestParams params);

    protected abstract void onAddOrUpdate(RedisKey redisKey);

    protected abstract void onDelete(List<String> keys);

    @Override
    public Page<RedisKey> query4page(PageRequestParams params) {
        List<RedisKey> list = query4list(params);

        if (CollectionUtils.isEmpty(list)) {
            return Page.of(Collections.emptyList());
        }

        PageBounds bounds = PageBoundsResolver.resolve(
            params.getPageNum(), params.getPageSize(), list.size()
        );
        List<RedisKey> data = list.subList(
            (int) bounds.getOffset(), (int) bounds.getOffset() + bounds.getLimit()
        );
        com.github.pagehelper.Page<RedisKey> page = new com.github.pagehelper.Page<>(
            PageHandler.computePageNum(bounds.getOffset(), params.getPageSize()), params.getPageSize()
        );
        page.addAll(data);
        page.setPages(PageHandler.computeTotalPages(bounds.getTotal(), params.getPageSize()));
        page.setTotal(bounds.getTotal());
        page.setStartRow((int) bounds.getOffset());
        page.setEndRow((int) bounds.getOffset() + bounds.getLimit());
        return Page.of(page);
    }

    @Override
    public void addOrUpdateRedisEntry(Map<String, Object> params) {
        ExtendedHashMap<String, Object> map = new ExtendedHashMap<>(params);
        String key = map.getRequireString("key");
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Redis key cannot be null.");
        }
        key = key.trim();

        Object value = parseValue(
            map.getString("value"), map.getRequireString("valueType"), redis.getValueSerializer()
        );
        long expire = Numbers.toLong(map.get("expire"), -1);
        DataType type = parseDataType(key, map.getRequireString("dataType"));
        switch (type) {
            case STRING:
                if (expire > 0) {
                    redis.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
                } else {
                    redis.opsForValue().set(key, value);
                }
                break;
            case LIST:
                redis.opsForList().rightPush(key, value);
                if (expire > 0) {
                    redis.expire(key, expire, TimeUnit.SECONDS);
                }
                break;
            case SET:
                redis.opsForSet().add(key, value);
                if (expire > 0) {
                    redis.expire(key, expire, TimeUnit.SECONDS);
                }
                break;
            case ZSET:
                redis.opsForZSet().add(key, value, map.getRequireDouble("score"));
                if (expire > 0) {
                    redis.expire(key, expire, TimeUnit.SECONDS);
                }
                break;
            case HASH:
                redis.opsForHash().put(key, map.getRequireString("hashKey"), value);
                if (expire > 0) {
                    redis.expire(key, expire, TimeUnit.SECONDS);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported redis type: " + type.name());
        }

        onAddOrUpdate(new RedisKey(key, type, toString(value), expire));
    }

    @Override
    public void delete(String... redisKeys) {
        if (ArrayUtils.isEmpty(redisKeys)) {
            return ;
        }

        List<String> deletes = new ArrayList<>(redisKeys.length);
        for (String key : redisKeys) {
            if (key.contains("*")) {
                Set<String> keys = redis.keys(key);
                redis.delete(keys);
                deletes.addAll(keys);
            } else {
                redis.delete(key);
                deletes.add(key);
            }
        }

        onDelete(deletes);
    }

    @Override
    public void flushAll() {
        redis.execute((RedisCallback<Void>) action -> {
            action.flushAll();
            return null;
        });
    }

    // ------------------------------------------------------------------others methods
    public RedisKey getAsString(RedisConnection conn, byte[] key) {
        return getAsString(key, conn.type(key), conn.ttl(key));
    }

    public RedisKey getAsString(byte[] key, DataType dataType, long ttl) {
        String value = null;
        switch (dataType) {
            case NONE:
                value = "[KEY NOT EXISTS]";
                ttl = -2;
                break;
            case STRING:
                try {
                    value = redis.execute(
                        (RedisCallback<String>) c -> deserializeAsString(redis.getValueSerializer(), c.get(key))
                    );
                    if (value != null && value.length() > MAX_VALUE_LENGTH) {
                        value = value.substring(0, MAX_VALUE_LENGTH) + "...";
                    }
                } catch (Exception ex) {
                    logger.error("Get redis key value occur error.", ex);
                    value = Optional.ofNullable(value).orElse("[ERROR: " + ex.getMessage() + "]");
                }
                break;
            default:
                value = "[NOT STRING TYPE: " + dataType.name() + "]";
                break;
        }
        return new RedisKey(
            deserializeAsString(redis.getKeySerializer(), key), dataType, value, ttl
        );
    }

    private String deserializeAsString(RedisSerializer<?> serializer, byte[] data) {
        if (data == null) {
            return null;
        }
        if (serializer == null) {
            return new String(data);
        }
        try {
            return toString(serializer.deserialize(data));
        } catch (Exception ignored) {
            return new String(data);
        }
    }

    private DataType parseDataType(String key, String dataType) {
        DataType actual = Optional.ofNullable(redis.type(key))
                                  .filter(this::isNotNull)
                                  .orElse(DataType.STRING);
        if (StringUtils.isEmpty(dataType)) {
            return actual;
        }
        DataType expect = null;
        try {
            expect = DataType.fromCode(dataType);
        } catch (Exception ignored) {
            Throwables.ignore(ignored);
        }
        return Optional.ofNullable(expect)
                       .filter(this::isNotNull)
                       .orElse(actual);
    }

    private boolean isNotNull(DataType type) {
        return type != null && type != DataType.NONE;
    }

    private static Object parseValue(String value, String valueType, RedisSerializer<?> serializer) {
        return Enums.ofIgnoreCase(ValueType.class, valueType, ValueType.RAW)
                    .parse(value, serializer);
    }

    public enum MatchMode {
        LIKE {
            @Override
            public String build(String keyWildcard) {
                return StringUtils.isEmpty(keyWildcard)
                     ? ASTERISK
                     : ASTERISK + keyWildcard + ASTERISK;
            }
        },
        EQUAL {
            @Override
            public String build(String keyWildcard) {
                return keyWildcard;
            }
        },
        HEAD {
            @Override
            public String build(String keyWildcard) {
                return keyWildcard + ASTERISK;
            }
        },
        TAIL {
            @Override
            public String build(String keyWildcard) {
                return ASTERISK + keyWildcard;
            }
        };

        private static final String ASTERISK = "*";

        public abstract String build(String keyWildcard);
    }

    private enum ValueType {
        RAW, //
        B64() {
            @Override
            public Object parse(String value, RedisSerializer<?> serializer) {
                if (value == null) {
                    return null;
                }
                if (value.isEmpty()) {
                    return ArrayUtils.EMPTY_BYTE_ARRAY;
                }
                try {
                    return serializer.deserialize(
                        Base64.getUrlDecoder().decode(value)
                    );
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (SerializationException e) {
                    throw new IllegalArgumentException(
                        "Deserialize base64 data '" + value + "' failed!", e
                    );
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Parse base64 value '" + value + "' occur error.", e
                    );
                }
            }
        };

        public Object parse(String value, RedisSerializer<?> serializer) {
            return value;
        }
    }

    private static String toString(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof CharSequence) {
            return o.toString();
        }
        if (o instanceof byte[]) {
            return new String((byte[]) o);
        }
        if (o instanceof Byte[]) {
            return new String(ArrayUtils.toPrimitive((Byte[]) o));
        }
        return Jsons.toJson(o);
    }

}
