package com.pwhxbdk.utils;

import java.util.Objects;

/**
 * @author pwhxbdk
 * @date 2020/4/12
 */
public enum BaseTypeEnum {
    /**
     * 基本数据类型 包装类
     */
    BYTE("byte", "java.lang.Byte"),
    CHAR("char", "java.lang.Character"),
    DOUBLE("double", "java.lang.Double"),
    FLOAT("float", "java.lang.Float"),
    INT("int", "java.lang.Integer"),
    LONG("long", "java.lang.Long"),
    SHORT("short", "java.lang.Short"),
    BOOLEAN("boolean", "java.lang.Boolean"),

    STRING("string", "java.lang.String"),

    ;

    private String name;
    private String boxedTypeName;

    BaseTypeEnum(String name, String boxedTypeName) {
        this.name = name;
        this.boxedTypeName = boxedTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoxedTypeName() {
        return boxedTypeName;
    }

    public void setBoxedTypeName(String boxedTypeName) {
        this.boxedTypeName = boxedTypeName;
    }

    public static String findByName(String boxedTypeName) {
        for (BaseTypeEnum type : values()) {
            if (Objects.equals(type.getBoxedTypeName(), boxedTypeName)) {
                return type.getName();
            }
        }
        return "";
    }

    public static boolean isName(String name) {
        for (BaseTypeEnum type : values()) {
            if (Objects.equals(type.getName(), name)) {
                return true;
            }
        }
        return false;
    }
}
