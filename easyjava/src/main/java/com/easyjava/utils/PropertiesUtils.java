package com.easyjava.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取application.properties中的信息.
 */
public class PropertiesUtils {
    private static Properties props = new Properties();
    private static Map<String, String> PROPER_MAP =  new ConcurrentHashMap();

    static {
        InputStream is = null;

        try{
            // 从项目的类路径中加载资源文件。
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            // 使用props.load(is)加载application.properties文件的内容到Properties对象（props）。可以从属性文件中读取键值对
            props.load(is);

            Iterator<Object> iterator = props.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                PROPER_MAP.put(key, props.getProperty(key));
            }

        }catch (Exception e) {

        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String getString(String key) {
        return PROPER_MAP.get(key);
    }

    public static void main(String[] args) {
        System.out.println(getString("db.url"));
    }

}
