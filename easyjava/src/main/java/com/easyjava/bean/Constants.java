package com.easyjava.bean;

import com.easyjava.utils.PropertiesUtils;

public class Constants {

    public static Boolean IGNORE_TABLE_PREFIX;

    public static String SUFFIX_BEAN_PARAM;

    public static String PATH_BASE;

    public static String PACKAGE_BASE;

    public static String PATH_JAVA = "java";

    public static String PATH_RESOURCES = "resources";
    public static String PATH_PO;
    public static String PACKAGE_PO;

    public static String PATH_PARAM;

    public static String AUTHOR_COMMENT;

    //需要忽略的属性
    public static String IGNORE_BEAN_TOJSON_FIELD;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    // 日期序列化， 反序列化
    public static String BEAN_DATE_FORMAT_EXPRESSION;
    public static String BEAN_DATE_FORMAT_CLASS;
    public static String BEAN_DATE_UNFORMAT_EXPRESSION;
    public static String BEAN_DATE_UNFORMAT_CLASS;


    static {
        AUTHOR_COMMENT = PropertiesUtils.getString("author.comment");

        IGNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getString("ignore.bean.tojson.field");
        IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getString("ignore.bean.tojson.expression");
        IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getString("ignore.bean.tojson.class");
        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.format.expression");
        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getString("bean.date.format.class");
        BEAN_DATE_UNFORMAT_EXPRESSION = PropertiesUtils.getString("bean.date.unformat.expression");
        BEAN_DATE_UNFORMAT_CLASS = PropertiesUtils.getString("bean.date.unformat.class");


        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARAM = PropertiesUtils.getString("suffix.bean.param");
        PACKAGE_BASE = PropertiesUtils.getString("package.base");
        PATH_BASE = (PropertiesUtils.getString("path.base") + PATH_JAVA + "/" +PACKAGE_BASE).replace(".", "/");
        PATH_PO = PATH_BASE + "/" + PropertiesUtils.getString("package.po").replace(".", "/");
        PACKAGE_PO = PACKAGE_BASE + "." +PropertiesUtils.getString("package.po");

    }

    public static String[] SQL_DATE_TIME_TYPES = new String[]{"datetime", "timestamp"};
    public static String[] SQL_DATE_TYPES = new String[]{"date"};
    public static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "double", "float"};
    public static String[] SQL_STRING_TYPE = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};
    public static String[] SQL_INTEGER_TYPE = new String[]{"int", "tinyint"};
    public static String[] SQL_LONG_TYPE = new String[]{"bigint"};


    public static void main(String[] args) {
        System.out.println(PATH_BASE);
        System.out.println(PACKAGE_PO);
        System.out.println(PACKAGE_BASE);
        System.out.println(PATH_PO);
    }
}
