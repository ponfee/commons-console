package code.ponfee.console.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import code.ponfee.commons.data.DataSourceNaming;
import code.ponfee.commons.model.Result;
import code.ponfee.commons.mybatis.SqlMapper;

@Service
public class TestServiceImpl implements TestService {

    private @Resource SqlMapper sqlMapper;

    /**
     * CREATE TABLE `test_tx_primary_1` (
     *   `name` varchar(255) NOT NULL
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     * 
     * CREATE TABLE `test_tx_primary_2` (
     *   `name` varchar(255) NOT NULL
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    @Override @Transactional @DataSourceNaming("#root[0]") // #root=arg[]
    public void testTransaction(String datasource, boolean bool) {
        sqlMapper.delete("DELETE FROM test_tx_${datasource}_1", datasource);
        if (bool) {
            throw new RuntimeException("Transaction testing...");
        }
        sqlMapper.delete("DELETE FROM test_tx_${datasource}_2", datasource);
    }

    @Override @DataSourceNaming("'primary'")
    public Result<Long> selectScroll() {
        String sql = 
            "<script>"
          + "  SELECT * FROM t_sched_log WHERE 1=1"
          + "  <if test='id!=null'>AND id>#{id}</if> "
          + "  ORDER BY id ASC LIMIT 7"
          + "</script>";
        AtomicLong count = new AtomicLong(0);
        sqlMapper.selectScroll(sql, new HashMap<>(), Map.class, (param, list) -> {
            param.put("id", list.get(list.size() - 1).get("id"));
            count.addAndGet(list.size());
            return param;
        });
        return Result.success(count.get());
    }

}
