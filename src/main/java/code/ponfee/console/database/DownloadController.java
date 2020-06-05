/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2020, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console.database;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import code.ponfee.commons.web.WebUtils;

/**
 * 
 * 
 * @author Ponfee
 */
@Controller
public class DownloadController {

    @GetMapping("download1")
    public void download1(HttpServletResponse resp, 
                          @RequestParam("isGzip") boolean isGzip, 
                          @RequestParam("withBom") boolean withBom) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator('\n').withHeader(new String[] {"列名1","列名2","列名3","列名4","列名5","列名6","列名7","列名8","列名9"});
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(output); 
             CSVPrinter printer = format.print(writer)
        ) {
            for (int i = 0; i < 99999; i++) {
                printer.printRecord("数据" + i + "_1", "数据" + i + "_2", "数据" + i + "_3", "数据" + i + "_4", "数据" + i + "_5", "数据" + i + "_6", "数据" + i + "_7", "数据" + i + "_8", "数据" + i + "_9");
            }
            printer.flush();
            writer.flush();
            String filename = "测试文件下载1-" + isGzip + "-" + withBom + ".csv";
            WebUtils.download(resp, output.toByteArray(), filename, "UTF-8", isGzip, withBom);
        }
    }

    @GetMapping("download2")
    public void download2(HttpServletResponse resp, 
                          @RequestParam("isGzip") boolean isGzip, 
                          @RequestParam("withBom") boolean withBom) throws IOException {
        String filename = "测试文件下载2-" + isGzip + "-" + withBom + ".csv";
        WebUtils.download(resp, new FileInputStream("d:/测试文件下载-false-false.csv"), filename, "UTF-8", isGzip, withBom);
    }
}
