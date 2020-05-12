package com.xdsty.datasync.enums;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/23 9:44
 */
public enum IndexUniqueTypeEnum {

    /**
     * 唯一索引
     */
    UNIQUE(0, "UNIQUE" ,"唯一索引"),

    /**
     * 普通索引
     */
    NO_UNIQUE(1,"" , "普通索引");

    private Integer value;

    private String key;

    private String name;

    IndexUniqueTypeEnum(Integer value, String key, String name) {
        this.value = value;
        this.key = key;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
