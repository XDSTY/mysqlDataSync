package com.xdsty.datasync.enums;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/4/2 10:42
 */
public enum DbTypeEnum {
    /**
     * MySQL
     */
    MYSQL(1, "MYSQL", "com.mysql.cj.jdbc.Driver"),
    /**
     * SQLSERVER
     */
    SQLSERVER(2, "SQLSERVER", ""),

    /**
     * ORACLE
     */
    ORACLE(3, "ORACLE", "");

    private Integer value;

    private String desc;

    private String driver;

    DbTypeEnum(Integer value, String desc, String driver) {
        this.value = value;
        this.desc = desc;
        this.driver = driver;
    }

    public static String getDriverByType(Integer dbType){
        for(DbTypeEnum dbTypeEnum : values()){
            if(dbTypeEnum.value.equals(dbType)){
                return dbTypeEnum.driver;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getDriver() {
        return driver;
    }}
