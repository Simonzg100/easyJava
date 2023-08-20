package com.easyjava;

import com.easyjava.bean.TableInfo;
import com.easyjava.builder.BuildBase;
import com.easyjava.builder.BuildBeanPo;
import com.easyjava.builder.BuildTable;

import java.sql.SQLException;
import java.util.List;

public class RunApplication {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        BuildBase.execute();
        List<TableInfo> tableInfoList = new BuildTable().getTables();
        for (TableInfo tableInfo : tableInfoList) {
            BuildBeanPo.execute(tableInfo);
        }

    }
}
