package com.zyg.web.admin.service;

import com.zyg.web.admin.annaotation.MyService;

@MyService("MyServiceImpl")
public class HomeServiceImpl implements HomeService {
    @Override
    public String query(String name, String age) {
        return "name=" + name + ";age" + age;
    }
}
