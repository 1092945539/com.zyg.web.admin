package com.zyg.web.admin;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Data;

import javax.script.*;

public class TestClass {
    public static void main(String[] args) throws ScriptException {
//        testCompilable();
        try {
            testInvocable();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Data
    static class PltAccount{
        private Integer id;
        private String name;
    }

    private static void testInvocable() throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        String str = "var configData = {data:function(){return pltAccount;}," +
                "codeShowFn:function(){return pltAccount.id;}, " +
                "nameShowFn:function(){return pltAccount.name}}";
        engine.eval(str);

        final PltAccount pltAccount = new PltAccount();
        pltAccount.setId(0);
        pltAccount.setName("zyg");
        engine.put("pltAccount",pltAccount);

        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) engine.get("configData");
        scriptObjectMirror.forEach((k, v) -> {
            if (v != null && v.toString().contains("function")) {
                String str2 = "configData." + k + "()";
                try {
                    System.out.println(engine.eval(str2));
                } catch (ScriptException e) {
                    System.out.println("错误:"+e.getMessage());
                }
            }
        });
    }

    private static void testCompilable() throws ScriptException {
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("javascript");
        engine.put("num", 2);
        if (engine instanceof Compilable){
            Compilable compilable = (Compilable)engine;
            CompiledScript script = compilable.compile("function count(){num = num + 1;return num;} count();");
            System.out.println(script.eval() + "");
            System.out.println(script.eval() + "");
        }
        engine.eval("function count(){num = num + 1;return num;} count();");
        Integer data = (Integer)engine.get("num");
        System.out.println(data);
    }
}
