package com.xdsty.datasync.enums;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/23 10:05
 */
public enum IndexTypeEnum {

    /**
     * 普通索引
     */
    BTREE(0, ""),

    /**
     * 全文索引
     */
    FULLTEXT(1, "FULLTEXT");

    private Integer value;

    private String name;

    IndexTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Integer getIndexType(String name){
        for(IndexTypeEnum indexTypeEnum : values()){
            if(indexTypeEnum.name.equals(name)){
                return indexTypeEnum.value;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }}