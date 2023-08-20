package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.JsonUtils;
import com.easyjava.utils.PropertiesUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 吧数据库里面的信息全部写入到tableinfo中。
 */
public class BuildTable {
    // use jdbc to read database
    private static final Logger logger = LoggerFactory.getLogger(BuildTable.class);
    private static Connection conn = null;

    private static String SQL_SHOW_TABLE_STATUS = "show table status"; // get the table and comment
    private static String SQL_SHOW_TABLE_FIELDS = "show full fields from %s"; // get the table and comment
    private static String SQL_SHOW_TABLE_INDEX = "show index from %s"; // get the table and comment



    static {
        String driverName = PropertiesUtils.getString("db.driver.name");
        String url = PropertiesUtils.getString("db.url");
        String user = PropertiesUtils.getString("db.username");
        String password = PropertiesUtils.getString("db.password");
        try {
            Class.forName(driverName);
            conn = DriverManager.getConnection(url,user,password);
        } catch (Exception e) {
            logger.error("fail to connect database" + e);
        }
    }

    public static List<TableInfo> getTables(){
        PreparedStatement ps = null;
        ResultSet tableResult = null;

        List<TableInfo> tableInfoList = new ArrayList<TableInfo>();

        try {
            ps = conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
            tableResult = ps.executeQuery();
            while (tableResult.next()) {
                String tableName = tableResult.getString("name");
                String comment = tableResult.getString("comment");
//                logger.info("tableName: {}, comment:{}", tableName, comment);

                String beanName = tableName; // tb_product_info

                //  delete "_"
                if (Constants.IGNORE_TABLE_PREFIX){
                    beanName = tableName.substring(beanName.indexOf("_") + 1);
                }

                // upper first letter
                beanName = processField(beanName,true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setBeanName(beanName);
                tableInfo.setComment(comment);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);

                readFieldInfo(tableInfo);
                //转换为 json格式去查看 不然就只是地址
//                logger.info("表: {}", JsonUtils.convertObj2Json(tableInfo));
//                logger.info("字段: {}", JsonUtils.convertObj2Json(fieldInfoList));
                //解析唯一索引
                getKeyIndexInfo(tableInfo);
//                logger.info("table: {}", JsonUtils.convertObj2Json(tableInfo));
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            logger.error("fail to read table");
        }finally {
            if(tableResult != null) {
                try {
                    tableResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return tableInfoList;
    }

    private static String processField(String field, Boolean upperCaseFirstLetter){
        StringBuffer sb =  new StringBuffer();
        String[] fields = field.split("_");
        sb.append(upperCaseFirstLetter? StringUtils.upperCaseFirstLetter(fields[0]):fields[0]);

        for(int i = 1, len = fields.length; i < len; i++) {
            sb.append(StringUtils.upperCaseFirstLetter(fields[i]));
        }
        return  sb.toString();
    }

    private static void readFieldInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
        Boolean haveDateTime = false;
        Boolean haveDate = false;
        Boolean haveBigDecimal = false;
        try {
            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_FIELDS, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String field = fieldResult.getString("field");

                String type = fieldResult.getString("type");
                String extra = fieldResult.getString("extra");
                String comment = fieldResult.getString("comment");
                 if(type.contains("(")){
                     type = type.substring(0, type.indexOf("("));
                 }
                 String propertyName = processField(field,false);

                 FieldInfo fieldInfo = new FieldInfo();

                 fieldInfo.setFieldName(field);
                 fieldInfo.setComment(comment);
                 fieldInfo.setSqlType(type);
                 fieldInfo.setAutoIncrement("auto_increment".equalsIgnoreCase(extra));
                 fieldInfo.setPropertyName(propertyName);
                 fieldInfo.setJavaType(processJavaType(type));

                 fieldInfoList.add(fieldInfo);
//                logger.info("field: {}, propertyName: {}, type: {}, extra: {}, comment: {}", field, propertyName,type,extra,comment);
//                logger.info("javatype: {}", fieldInfo.getJavaType());
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, type)) {
                    haveDateTime = true;
                }
                if (ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, type)) {
                    haveBigDecimal = true;
                }
                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, type)) {
                    haveDate = true;
                }

            }
            tableInfo.setHaveDate(haveDate);
            tableInfo.setHaveBigDecimal(haveBigDecimal);
            tableInfo.setHaveDateTime(haveDateTime);
            tableInfo.setFieldList(fieldInfoList);
        } catch (Exception e) {
            logger.error("fail to read table");
        }finally {
            if(fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private static void getKeyIndexInfo(TableInfo tableInfo) {
        PreparedStatement ps = null;
        ResultSet fieldResult = null;
        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();

        try {

            Map<String, FieldInfo> tempMap = new HashMap();
            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                tempMap.put(fieldInfo.getFieldName(),fieldInfo);
            }


            ps = conn.prepareStatement(String.format(SQL_SHOW_TABLE_INDEX, tableInfo.getTableName()));
            fieldResult = ps.executeQuery();
            while (fieldResult.next()) {
                String keyName = fieldResult.getString("key_name");
                Integer nonUnique = Integer.valueOf(fieldResult.getString("non_unique"));
                String columnName = fieldResult.getString("column_name");
                if (nonUnique == 1) {
                    continue;
                }

                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().get(keyName);
                if (keyFieldList == null) {
                    keyFieldList = new ArrayList<FieldInfo>();
                    tableInfo.getKeyIndexMap().put(keyName,keyFieldList);
                }

                keyFieldList.add(tempMap.get(columnName));
            }
        } catch (Exception e) {
            logger.error("fail to read index table");
        }finally {
            if(fieldResult != null) {
                try {
                    fieldResult.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private static String processJavaType(String type) {
        if(ArrayUtils.contains(Constants.SQL_INTEGER_TYPE,type)){
            return "Integer";
        } else if(ArrayUtils.contains(Constants.SQL_LONG_TYPE,type)) {
            return "Long";
        }else if(ArrayUtils.contains(Constants.SQL_STRING_TYPE,type)) {
            return "String";
        }else if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES,type)) {
            return "BigDecimal";
        }else if(ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES,type) || ArrayUtils.contains(Constants.SQL_DATE_TYPES,type)) {
            return "Date";
        } else {
            throw new RuntimeException("无法识别的类型: " + type);
        }
    }

}
