package com.pwhxbdk.utils;

import com.intellij.psi.PsiType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pwhxbdk
 * @date 2020/4/6
 */
public class CommentUtils {


    public static String getDataType(String dataType, PsiType psiType) {
        String typeName = BaseTypeEnum.findByName(dataType);
        if (StringUtils.isNotEmpty(typeName)) {
            return typeName;
        }
        if (BaseTypeEnum.isName(dataType)) {
            return dataType;
        }
        String multipartFileText = "org.springframework.web.multipart.MultipartFile";
        String javaFileText = "java.io.File";
        if (psiType.getCanonicalText().equals(multipartFileText)
                || psiType.getCanonicalText().equals(javaFileText)) {
            return "file";
        }
        // 查找是否实现自File类
        for (PsiType superType : psiType.getSuperTypes()) {
            if (superType.getCanonicalText().equals(multipartFileText)
                    || superType.getCanonicalText().equals(javaFileText)) {
                return "file";
            }
        }
        return null;
    }


    /**
     * 获取注解说明  不写/@desc/@describe/@description
     * @param comment 所有注释
     * @return String
     */
    public static String getCommentDesc(String comment) {
        String[] strings = comment.split("\n");
        if (strings.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            String row = StringUtils.deleteWhitespace(string);
            if (StringUtils.isEmpty(row) || StringUtils.startsWith(row,"/**")) {
                continue;
            }
            if (StringUtils.startsWithIgnoreCase(row,"*@desc")
                    && !StringUtils.startsWithIgnoreCase(row,"*@describe")
                    && !StringUtils.startsWithIgnoreCase(row,"*@description")) {
                appendComment(string, stringBuilder, 5);
            }
            if (StringUtils.startsWithIgnoreCase(row,"*@description")) {
                appendComment(string, stringBuilder, 12);
            }
            if (StringUtils.startsWithIgnoreCase(row,"*@describe")) {
                appendComment(string, stringBuilder, 9);
            }
            if (StringUtils.startsWith(row,"*@") || StringUtils.startsWith(row,"*/")) {
                continue;
            }
            int descIndex = StringUtils.ordinalIndexOf(string,"*",1);
            if (descIndex == -1) {
                descIndex = StringUtils.ordinalIndexOf(string,"//",1);
                descIndex += 1;
            }
            String desc = string.substring(descIndex + 1);
            stringBuilder.append(desc);
        }
        return StringUtils.trim(stringBuilder.toString());
    }


    private static void appendComment(String string, StringBuilder stringBuilder, int index) {
        String lowerCaseStr = string.toLowerCase();
        int descIndex = StringUtils.ordinalIndexOf(lowerCaseStr,"@",1);
        descIndex += index;
        String desc = string.substring(descIndex);
        stringBuilder.append(desc);
    }

    /**
     * dfdfdf
     * @param args
     */
    public static void main(String[] args) {

//        System.out.println(getCommentDesc("/**\n" +
//                " * @describe pwhxbdk\n" +
//                " * @author pwhxbdk\n" +
//                " * @date 2020/4/6\n" +
//                " */"));
//        System.out.println(StringUtils.ordinalIndexOf("*@desc fdfdfdfdf","c",1));
    }


}
