package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.DateUtils;
import com.easyjava.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

public class BuildPo {
    private static final Logger logger = LoggerFactory.getLogger(BuildPo.class);
    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_PO);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        File poFile = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;

        try{
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out, "utf8");
            bw = new BufferedWriter(outw);

            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import java.io.Serializable;");
            bw.newLine();

            if (tableInfo.getHaveDate() || tableInfo.getHaveDateTime()) {
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write(Constants.BEAN_DATE_UNFORMAT_CLASS);
                bw.newLine();
            }
            // 忽略属性
            boolean haveIgnoreBean = false;
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","), fieldInfo.getPropertyName())) {
                    haveIgnoreBean = true;
                    break;
                }
            }
            if (haveIgnoreBean) {
                bw.write(Constants.IGNORE_BEAN_TOJSON_CLASS);
                bw.newLine();
            }


            if (tableInfo.getHaveBigDecimal()) {
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
                bw.newLine();
            }
            bw.newLine();

            //create class comment
            BuildComment.createClassComment(bw,tableInfo.getComment());
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();

            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
//                /**
//                 * 创建日期
//                 */
//                @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//                @DateTimeFormat(pattern = "yyyy-MM-dd")
//                private Date createDate;
                // 1. write explain
                BuildComment.createFieldComment(bw,fieldInfo.getComment() == null? " ": fieldInfo.getComment());

                // 2. write annotation
                if (ArrayUtils.contains(Constants.SQL_DATE_TIME_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t" +String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                    bw.write("\t" +String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }

                if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\t" +String.format(Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                    bw.write("\t" +String.format(Constants.BEAN_DATE_UNFORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                }

                if (ArrayUtils.contains(Constants.IGNORE_BEAN_TOJSON_FIELD.split(","), fieldInfo.getPropertyName())) {
                    bw.write("\t" +Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    bw.newLine();
                }

                // 3. write -- private...
                bw.write( "\tprivate" + " " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }
            //4. write get/set method
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                String tempField = StringUtils.upperCaseFirstLetter(fieldInfo.getPropertyName());
                bw.write("\tpublic void set" + tempField + "(" +fieldInfo.getJavaType()+" " + fieldInfo.getPropertyName()+") {" );
                bw.newLine();

                bw.write("\t\tthis." +fieldInfo.getPropertyName() + " = " +fieldInfo.getPropertyName() + ";");
                bw.newLine();

                bw.write("\t}");
                bw.newLine();

                bw.write("\tpublic " + fieldInfo.getJavaType() + " get" + tempField + "() {" );
                bw.newLine();

                bw.write("\t\treturn this." + fieldInfo.getPropertyName() + ";");
                bw.newLine();

                bw.write("\t}");
                bw.newLine();
            }
            StringBuffer toString = new StringBuffer();
            //5.重写toString方法
            for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
                toString.append(fieldInfo.getComment() + ":\" + (" + fieldInfo.getPropertyName() + " == null ? \"空\" : "
                + fieldInfo.getPropertyName() + ")");
                toString.append(" + \",");
            }
            String toStringstr = toString.toString();
            toStringstr = "\"" + toStringstr;

            toStringstr = toStringstr.substring(0, toStringstr.lastIndexOf("+"));

            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic String toString() {");
            bw.newLine();
            bw.write("\t\treturn " + toStringstr + ";");
            bw.newLine();
            bw.write("\t}");



            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            logger.info("fail to create po", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (outw != null) {
                try {
                    outw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }



}
