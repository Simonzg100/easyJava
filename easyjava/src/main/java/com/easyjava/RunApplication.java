package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.*;
import com.easyjava.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class RunApplication {
    private static final Logger logger = LoggerFactory.getLogger(RunApplication.class);
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        logger.info("开始生成代码......");
        try {
            //构建基础类
            BuildBase.execute();

            List<TableInfo> tableInfoList = new BuildTable().getTables();
            for (TableInfo tableInfo : tableInfoList) {
                //构建bean
                BuildBeanPo.execute(tableInfo);
                //构建query对象
                BuildBeanQuery.execute(tableInfo);

                BuildMapper.execute(tableInfo);
            }
            logger.info("生成代码成功，代码在->" + PropertiesUtils.getString("path.base"));
        } catch (Exception e) {
            logger.error("生成代码失败，错误信息:", e);
        }

    }
}
