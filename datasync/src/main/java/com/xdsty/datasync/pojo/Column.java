package com.xdsty.datasync.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * column的schema
 * @author 张富华  
 * @date 2020/5/13 15:59
 */
@Data
@EqualsAndHashCode
public class Column {

    public static String TABLE_NAME = "TABLE_NAME";
    public static String COLUMN_NAME = "COLUMN_NAME";
    public static String COLUMN_DEFAULT = "COLUMN_DEFAULT";
    public static String IS_NULLABLE = "IS_NULLABLE";
    public static String DATA_TYPE = "DATA_TYPE";
    public static String CHARACTER_MAXIMUM_LENGTH = "CHARACTER_MAXIMUM_LENGTH";
    public static String CHARACTER_OCTET_LENGTH = "CHARACTER_OCTET_LENGTH";
    public static String NUMERIC_PRECISION = "NUMERIC_PRECISION";
    public static String NUMERIC_SCALE = "NUMERIC_SCALE";
    public static String DATETIME_PRECISION = "DATETIME_PRECISION";
    public static String CHARACTER_SET_NAME = "CHARACTER_SET_NAME";
    public static String COLLATION_NAME = "COLLATION_NAME";
    public static String COLUMN_TYPE = "COLUMN_TYPE";
    public static String COLUMN_KEY = "COLUMN_KEY";
    public static String EXTRA = "EXTRA";
    public static String PRIVILEGES = "PRIVILEGES";
    public static String COLUMN_COMMENT = "COLUMN_COMMENT";
    public static String GENERATION_EXPRESSIC = "GENERATION_EXPRESSIC";

    /**
     * 所属表
     */
    private String tableName;

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 默认值
     */
    private String columnDefault;

    /**
     * 是否可以为空
     */
    private String nullable;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 以字符为单位的最大值
     */
    private Integer characterMaximumLength;

    /**
     * 以字节为单位的最大值
     */
    private Integer characterOctetLength;

    /**
     * 数值精度
     */
    private Integer numericPrecision;

    /**
     * 数值刻度
     */
    private Integer numericScale;

    /**
     * 日期精度
     */
    private Integer datetimePrecision;

    /**
     * 编码
     */
    private String characterSetName;

    /**
     * 编码校验
     */
    private String collationName;

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 字段上的键
     */
    private String columnKey;

    /**
     * 额外的信息 比如auto_increment
     */
    private String extra;

    /**
     * 表权限
     */
    private String privileges;

    /**
     * 字段注释
     */
    private String columnComment;

    /**
     * 表达式
     */
    private String generationExpressic;

}
