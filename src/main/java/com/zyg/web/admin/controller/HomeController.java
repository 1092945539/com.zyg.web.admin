package com.zyg.web.admin.controller;

import com.zyg.web.admin.annaotation.MyAutowired;
import com.zyg.web.admin.annaotation.MyController;
import com.zyg.web.admin.annaotation.MyRequestMapping;
import com.zyg.web.admin.annaotation.MyRequestParam;
import com.zyg.web.admin.service.HomeService;
import lombok.val;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@MyController
@MyRequestMapping("/home")
public class HomeController {

    @MyAutowired("MyServiceImpl")
    private HomeService homeService;

    @MyRequestMapping("/index")
//    @ResponseBody
    public void index(HttpServletRequest request, HttpServletResponse response,
                      @MyRequestParam("name") String name, @MyRequestParam("age") String age) {
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            String str = homeService.query(name, age);
            pw.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:/Users/ZhangYaguang/AppData/Local/Temp/fundsrc.txt");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader reader = new BufferedReader(isr);
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("INSERT INTO [dbo].[DIC_FUND_SRC]([ID], [SOURCE_VALUE], [NAME], [SOURCE_PARENT_VALUE], [LEVEL]) VALUES (" + line + "," + tempString + ");");
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
