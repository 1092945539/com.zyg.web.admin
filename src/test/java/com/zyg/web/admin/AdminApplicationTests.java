package com.zyg.web.admin;

import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminApplicationTests {

    private static final String RESULT_FRONT = "<result>";
    private static final String RESULT_LATER = "</result>";

    private static final String SIGN_FRONT = "<sign>";
    private static final String SIGN_LATER = "</sign>";
    @Test
    public void contextLoads() throws IOException {
        byte[] bytes = "<body><lists name=\"acctList\"><list><acctNo>952A9997220008092</acctNo></list></lists></body>".getBytes();
        InputStream in = send(bytes, "INFOSEC_SIGN/1.0", "http://192.168.64.99:5449");
        String str = inputStreamToString(in);
        //System.out.printf(str);
        //String str = "<html><head><title>签名结果</title><result>0</result></head><body><sign>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</sign></body></html>";
        if (str.contains(RESULT_FRONT) && str.contains(RESULT_LATER)) {
            String result = str.substring(str.indexOf(RESULT_FRONT) + RESULT_FRONT.length(),str.indexOf(RESULT_LATER));
            if ("0".equals(result)){
                String sign = str.substring(str.indexOf(SIGN_FRONT) + SIGN_FRONT.length(),str.indexOf(SIGN_LATER));
                if (!StringUtils.isEmpty(sign)){
                    String head = "<head>"+"<transCode>4402</transCode>"+"<signFlag>1</signFlag>"
                            +"<masterID>2000040752</masterID>"+"<packetID>20000407522000040752</packetID>"
                            +"<timeStamp>2019-03-21 16:18:57</timeStamp>"+"</head>";
                    String body = "<body><signature>"+sign+"</signature></body>";

                    String xml = "<?xml version='1.0' encoding='GB2312'?><packet>"+head+body+"</packet>";

                    String request = String.format("%06d", xml.length()) + xml;
                    System.out.println(request);

                    String res = inputStreamToString(send(request.getBytes(),null,"http://192.168.64.99:5775"));
                    System.out.println(res);
                }
            }
        }

    }

    private static String inputStreamToString(InputStream inputStream) {
        StringBuffer str = new StringBuffer();
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GB2312");
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String value;
            while ((value = bufferedReader.readLine()) != null) {
                str.append(value);
            }
        } catch (IOException e) {
            //
        }
        return str.toString();
    }

    /**
     * @param content----     <body>开头</body>结束的交易报文体
     * @param contentType---- INFOSEC_SIGN/1.0
     * @param url----         http://ip:port
     * @return
     * @throws IOException
     */
    public static InputStream send(byte[] content, String contentType, String url)
            throws IOException {
        URL urll = new URL(url);
        HttpURLConnection con1 = (HttpURLConnection) urll.openConnection();
        con1.setDoInput(true);
        con1.setDoOutput(true);
        con1.setRequestMethod("POST");
        if (contentType != null){
            con1.setRequestProperty("Content-Type", contentType);
        }
        con1.setRequestProperty("Content-Length", String.valueOf(content.length));
        con1.connect();
        con1.getOutputStream().write(content);
        con1.getOutputStream().flush();
        return con1.getInputStream();
    }


}
