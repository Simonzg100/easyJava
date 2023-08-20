package com.easyjava.builder;

import com.easyjava.bean.Constants;
import com.easyjava.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;


public class BuildComment {
    public static void createClassComment(BufferedWriter bw, String classComment) throws IOException {
        /**
         * @Description:
         * @date: 2023/9/10
         */
        bw.write("/**");
        bw.newLine();
        bw.write(" * @Description:" + classComment);
        bw.newLine();
        bw.write(" * @author:" + Constants.AUTHOR_COMMENT);
        bw.newLine();
        bw.write(" * @date:" + DateUtils.format(new Date(), DateUtils._YYYYMMDD));
        bw.newLine();
        bw.write("*/");
        bw.newLine();
    }


    public static void createFieldComment(BufferedWriter bw, String fieldComment) throws IOException {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + (fieldComment == null ? " ": fieldComment) );
        bw.newLine();
        bw.write("\t */");
        bw.newLine();
    }

    public static void createMethodComment() {

    }
}
