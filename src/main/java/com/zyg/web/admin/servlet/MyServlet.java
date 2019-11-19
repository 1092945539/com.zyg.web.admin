package com.zyg.web.admin.servlet;


import com.zyg.web.admin.annaotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

@WebServlet(loadOnStartup = 0)
public class MyServlet extends HttpServlet {

    private final List<String> classNames = new ArrayList<>();
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, Object> handlerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        scanPackage("com.zyg.web");
        doInstrance();
        doIoc();
        buildUrlMapping();
    }

    private void buildUrlMapping() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有实例化的类....");
            return;
        }
        beans.forEach((key, value) -> {
            Class<?> clazz = value.getClass();
            if (clazz.isAnnotationPresent(MyController.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                String classPath = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(MyRequestMapping.class)) {
                        MyRequestMapping methodMapping = method.getAnnotation(MyRequestMapping.class);
                        String methodPath = methodMapping.value();
                        handlerMap.put(classPath + methodPath, method);
                    }
                }
            }
        });
    }

    private void doIoc() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有实例化的类....");
            return;
        }

        beans.forEach((key, value) -> {
            Class<?> clazz = value.getClass();
            if (clazz.isAnnotationPresent(MyController.class)) {
                Field[] Fields = clazz.getDeclaredFields();
                for (Field field : Fields) {
                    if (!field.isAnnotationPresent(MyAutowired.class)) {
                        continue;
                    }
                    MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                    String myAutowiredKey = myAutowired.value();
                    field.setAccessible(true);
                    try {
                        field.set(value, beans.get(myAutowiredKey));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void doInstrance() {
        if (classNames.size() <= 0) {
            System.out.println("未扫描到包....");
            return;
        }
        classNames.forEach(className -> {
            String cn = className.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    Object instace = clazz.newInstance();
                    MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    String rmvalue = requestMapping.value();
                    beans.put(rmvalue, instace);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService service = clazz.getAnnotation(MyService.class);
                    Object instace = clazz.newInstance();
                    beans.put(service.value(), instace);
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    private void scanPackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        if (url == null) {
            return;
        }
        String fileStr = url.getFile();
        File file = new File(fileStr);
        String[] filesStr = file.list();
        if (filesStr == null) {
            return;
        }
        Arrays.stream(filesStr).forEach(path -> {
            File filePath = new File(fileStr + File.separator + path);
            if (filePath.isDirectory()) {
                scanPackage(basePackage + "." + path);
            } else {
                classNames.add(basePackage + "." + filePath.getName());
            }
        });
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.replace(contextPath, "");
        Method method = (Method) handlerMap.get(path);
        Object instance = beans.get("/" + path.split("/")[1]);

        Object[] args = hand(req, resp, method);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static Object[] hand(HttpServletRequest req, HttpServletResponse resp, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        int args_i = 0;
        int index = 0;
        for (Class<?> parameterType : parameterTypes) {
            if (ServletRequest.class.isAssignableFrom(parameterType)) {
                args[args_i++] = req;
            }
            if (ServletResponse.class.isAssignableFrom(parameterType)) {
                args[args_i++] = resp;
            }
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if (paramAns.length > 0) {
                for (Annotation paramAn : paramAns) {
                    if (MyRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                        MyRequestParam rp = (MyRequestParam) paramAn;
                        args[args_i++] = req.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }
}
