package code.ponfee.console.database;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import code.ponfee.commons.data.DataSourceNaming;
import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageRequestParams;
import code.ponfee.commons.model.Result;
import code.ponfee.commons.model.ResultCode;
import code.ponfee.commons.mybatis.SqlMapper;
import code.ponfee.commons.util.Base64UrlSafe;
import code.ponfee.commons.util.SqlUtils;

/**
 * Database dynamic query service implementation
 * 
 * @author Ponfee
 */
@Service
public class DatabaseQueryServiceImpl implements DatabaseQueryService {

    private @Resource SqlMapper sqlMapper;

    @SuppressWarnings("unchecked")
    @DataSourceNaming("#root[0].getRequireString('datasource')") // 指定数据源：@DataSourceNaming("'default'")
    @Override
    public Result<Page<LinkedHashMap<String, Object>>> query4page(PageRequestParams params) {
        String sql;
        if (StringUtils.isBlank(sql = params.getString("sql"))) {
            return Result.failure(ResultCode.BAD_REQUEST, "Sql cannot be blank.");
        }

        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        sql = new String(Base64UrlSafe.decode(sql.trim()));
        sql = SqlUtils.trim(sql);
        List<?> list = sqlMapper.selectList(sql, LinkedHashMap.class);
        Page<LinkedHashMap<String, Object>> page = CollectionUtils.isEmpty(list) || (list.size() == 1 && list.get(0) == null)
                                                 ? null : Page.of((List<LinkedHashMap<String, Object>>) list);
        return Result.success(page);
    }

}
