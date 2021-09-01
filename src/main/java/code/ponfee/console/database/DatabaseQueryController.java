package code.ponfee.console.database;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Throwables;

import code.ponfee.commons.collect.Collects;
import code.ponfee.commons.data.lookup.MultipleDataSourceContext;
import code.ponfee.commons.export.HtmlExporter;
import code.ponfee.commons.export.Table;
import code.ponfee.commons.export.Thead;
import code.ponfee.commons.export.Tmeta;
import code.ponfee.commons.export.Tmeta.Align;
import code.ponfee.commons.export.Tmeta.Type;
import code.ponfee.commons.http.ContentType;
import code.ponfee.commons.io.Files;
import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageParameter;
import code.ponfee.commons.model.PaginationHtmlBuilder;
import code.ponfee.commons.model.Result;
import code.ponfee.commons.model.ResultCode;
import code.ponfee.commons.tree.BaseNode;
import code.ponfee.commons.web.WebUtils;

/**
 * Database query http api
 * 
 * http://localhost:8100/database/query/view
 *   default  : select * from t_user
 *   secondary: select * from t_word_count
 * 
 * @author Ponfee
 */
@RequestMapping("database/query")
@RestController
public class DatabaseQueryController implements InitializingBean {

    private @Resource DatabaseQueryService service;

    @Override
    public void afterPropertiesSet() throws Exception {
        /*DruidDataSource datasource = new DruidDataSource();
        datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        datasource.setUrl("");
        datasource.setUsername("");
        datasource.setPassword("");

        MultipletCachedDataSource mcds = SpringContextHolder.getBean(MultipletCachedDataSource.class);
        mcds.addIfAbsent("test-dynamic-add", datasource);*/
    }

    @GetMapping("page")
    public Result<Page<Object[]>> query4page(PageParameter params) {
        if (StringUtils.isAnyEmpty(params.getString("datasource"), params.getString("sql"))) {
            return Result.failure(ResultCode.NOT_FOUND.getCode(), null, Page.empty());
        }
        try {
            Result<Page<LinkedHashMap<String, Object>>> result = service.query4page(params);
            return result.isFailure() ? result.copy() : result.map(p -> p.map(Collects::map2array));
        } catch (Exception e) {
            return Result.failure(ResultCode.BAD_REQUEST, Throwables.getRootCause(e).getMessage());
        }
    }

    // Oracle: select table_name from tabs
    //  Mysql: select table_name from INFORMATION_SCHEMA.TABLES
    @GetMapping("view")
    public void query4view(PageParameter params, HttpServletRequest req, HttpServletResponse resp) {
        Page<LinkedHashMap<String, Object>> page;
        String errorMsg;
        Stream<String> head;
        if (StringUtils.isAnyEmpty(params.getString("datasource"), params.getString("sql"))) {
            page = Page.empty();
            errorMsg = null;
            head = Arrays.stream(new String[] { "Tips" });
        } else {
            try {
                Result<Page<LinkedHashMap<String, Object>>> result = service.query4page(params);
                page = result.getData() == null ? Page.empty() : result.getData();
                errorMsg = result.isSuccess() ? null : result.getMsg();
            } catch (Exception e) {
                page = Page.empty();
                errorMsg = Throwables.getRootCause(e).getMessage(); // Throwables.getStackTraceAsString(e);
            }
            head = page.isEmpty() ? Arrays.stream(new String[] { "Tips" }) : page.getRows().get(0).keySet().stream();
        }

        AtomicInteger id = new AtomicInteger(1);
        List<BaseNode<Integer, Thead>> heads = head.map(h -> {
            Tmeta tmeta = new Tmeta(Type.CHAR, null, Align.LEFT, true, null);
            return new BaseNode<>(id.getAndIncrement(), 0, new Thead(h, tmeta, null));
        }).collect(Collectors.toList());

        Table<Object[]> table = new Table<>(heads);
        table.setComment(errorMsg);
        page.forEach(row -> table.addRow(Collects.map2array(row)));
        table.toEnd();

        try (HtmlExporter exporter = new HtmlExporter()) {
            exporter.build(table);
            PaginationHtmlBuilder builder = PaginationHtmlBuilder.newBuilder(
                "Database Query", WebUtils.getContextPath(req) + "/database/query/view", page
            );
            builder.table(exporter.body())
                   .scripts(PaginationHtmlBuilder.CDN_JQUERY + "\n" + PaginationHtmlBuilder.CDN_BASE64)
                   .form(buildForm(params))
                   .foot(buildFoot(WebUtils.getContextPath(req)))
                   .params(params);

            WebUtils.response(resp, ContentType.TEXT_HTML, builder.build(), Files.UTF_8);
        } 
    }

    // ------------------------------------------------------------------------private methods
    private String buildForm(PageParameter params) {
        StringBuilder builder = new StringBuilder(2048)
            .append("<select name=\"datasource\">\n");
        for (String datasource : MultipleDataSourceContext.listDataSourceNames()) {
            builder.append("<option value=\"").append(datasource).append("\"")
                   .append(datasource(params.getString("datasource", ""), datasource))
                   .append(">").append(datasource).append("</option>\n");
        }
        return builder.append("</select>\n")
            .append("<input type=\"submit\" value=\"Search\" onclick=\"doQuery(); return false;\" disabled=\"disabled\"/><br/>\n")
            .append("<textarea name=\"sql\" rows=\"8\" cols=\"100\">")
            .append(params.getString("sql", "")).append("</textarea>")
            .toString();
    }

    private String buildFoot(String contextPath) {
        return new StringBuilder(1024)
            .append("<script>\n")
            .append("String.prototype.rightPad = function(len, pad) {\n")
            .append("  return this + new Array(len - this.length + 1).join(pad, '');\n")
            .append("}\n")
            .append("var textarea, submit;\n")
            .append("$(document).ready(function() {\n")
            .append("  var value = (textarea = $(\"textarea[name='sql']\")).val();\n")
            .append("  if(value) {\n")
            .append("    value = value.replace(/\\-/g, '+').replace(/\\_/g, '/');\n")
            .append("    value += ''.rightPad((4 - value.length) & 0x3, '='); // (4 - (value.length & 0x3)) & 0x3 \n")
            .append("    console.log(value);\n")
            .append("    textarea.val(atob(value));\n")
            .append("  }\n")
            .append("  (submit = $(\"input[type='submit']\")).removeAttr('disabled');\n")
            .append("});\n")
            .append("function doQuery() {\n")
            .append("  submit.attr('disabled', 'disabled');\n")
            .append("  var value = $.trim(textarea.val());\n")
            .append("  if(value) {\n")
            .append("    value = btoa(value);\n")
            .append("    console.log(value);\n")
            .append("    textarea.val(value.replace(/\\+/g, '-').replace(/\\//g, '_').replace(/\\=/g, ''));\n")
            .append("    document.forms['search_form'].submit();\n")
            .append("  } else {\n")
            .append("    submit.removeAttr('disabled');\n")
            .append("  }\n")
            .append("}\n")
            .append("</script>\n")
            .toString();
    }

    private String datasource(String actual, String expect) {
        return expect.equals(actual) ? " selected=\"selected\"" : "";
    }

}
