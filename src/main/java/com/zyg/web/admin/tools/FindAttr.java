package com.zyg.web.admin.tools;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ZhangYaguang on 2020-02-15.
 */
public class FindAttr {
    public static Map<String, Map<String,String>> allMap(){
        File file = new File("C:\\git\\com.qs.web.gdtexAdmin\\qs_etrade\\QsTech.ETrade\\Modules");
        Map<String, Map<String,String>> map = new HashMap<>();
        files(file, map);
        return map;
    }

    public static String allZhushi(Map<String, Map<String,String>> map, String p){
        List<String> stringList = new ArrayList<>();
        map.forEach((ks,vs)-> vs.forEach((k, s)->{
            if (ObjectUtils.nullSafeEquals(k,p) && !StringUtils.isEmpty(s)){
                stringList.add(s);
            }
        }));
        final List<String> collect = stringList.stream().distinct().collect(Collectors.toList());
        if (collect.size() == 1){
            return collect.get(0);
        }else if (collect.size() == 0){
            return "";
        }
        System.out.println("当前clss："+AddAttr.entityNow);
        System.out.println("当前字段："+p);
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < collect.size(); i++) {
            stringBuilder.append(i+1 + ":" + collect.get(i) + ";");
        }
        System.out.println("候选值：[" + stringBuilder.toString() + "]");
        Scanner scanner = new Scanner(System.in);
        String scan = scanner.nextLine();
        if (scan.length() == 1){
            try {
                int i = Integer.parseInt(scan);
                return stringList.get(i-1);
            }catch (Exception e){
                return scan;
            }
        }
        return scan;
    }

    private static void files(File file, Map<String, Map<String,String>> map){
        if (file.isFile()) {
            AnalyticFile(file, map);
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File file2 : listFiles) {
                    if (file2.isFile()) {
                        AnalyticFile(file2, map);
                    } else {
                        files(file2, map);
                    }
                }
            }
        }
    }

    private static void AnalyticFile(File file, Map<String, Map<String,String>> map){
        String fileName = file.getName();
        if (!".cs".equals(fileName.substring(fileName.length() - 3, fileName.length()))) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))){
            String zhushi = "";
            String s = "";
            String className = "";
            Map<String,String> m = new HashMap<>();
            while ((s = br.readLine()) != null) {
                if (s.contains("public") && (s.contains("class") || s.contains("enum")) && !s.contains("ClassMapping")) {
                    if (m.size() > 0) {
                        map.put(className, m);
                    }
                    className = s.split(":")[0].substring(s.indexOf("class") + 5).trim();
                    m = new HashMap<>();
                    zhushi = "";
                    continue;
                }
                if (s.contains("///") && !s.contains("summary")) {
                    zhushi += s.replaceAll("///", "").trim();
                    continue;
                }
                if (s.contains("public") && s.contains("get") && s.contains("set")) {
                    if (!s.trim().startsWith("//")) {
                        String properties = AnalyticProperties(s);
                        m.put(properties, zhushi);
                        zhushi = "";
                    }
                }
            }
            map.put(className, m);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String AnalyticProperties(String s){
        if (StringUtils.isEmpty(s)) {
            return "";
        }
        // 解析至public virtual string AAAA
        String all = s.substring(0, s.lastIndexOf("{")).trim();
        int lastLength = all.lastIndexOf(" ");
        if (lastLength == -1) {
            return "";
        }
        return all.substring(lastLength).trim();
    }
}
