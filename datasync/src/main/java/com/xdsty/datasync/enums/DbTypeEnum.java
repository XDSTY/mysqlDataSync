package com.xdsty.datasync.enums;

/**
 * @author 张富华
 * @date 2020/4/2 10:42
 */
public enum DbTypeEnum {
    /**
     * MySQL
     */
    MYSQL("mysql", "MYSQL", "com.mysql.cj.jdbc.Driver"),
    /**
     * SQLSERVER
     */
    SQLSERVER("sqlserver", "SQLSERVER", ""),

    /**
     * ORACLE
     */
    ORACLE("oracle", "ORACLE", "");

    private String value;

    private String desc;

    private String driver;

    DbTypeEnum(String value, String desc, String driver) {
        this.value = value;
        this.desc = desc;
        this.driver = driver;
    }

    public static String getDriverByType(String dbType){
        for(DbTypeEnum dbTypeEnum : values()){
            if(dbTypeEnum.value.equals(dbType)){
                return dbTypeEnum.driver;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getDriver() {
        return driver;
    }}
