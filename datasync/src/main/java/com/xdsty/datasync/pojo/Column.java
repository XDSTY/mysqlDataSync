package com.xdsty.datasync.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * 表字段
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/21 16:51
 */
@Data
@ToString
public class Column {

    public static final String CAN_NULL = "YES";

    public static final String CAN_NOT_NULL = "NO";

    public static final String FIELD = "Field";

    public static final String TYPE = "Type";

    public static final String NULL = "Null";

    public static final String KEY = "Key";

    public static final String DEFAULT = "Default";

    public static final String EXTRA = "Extra";

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表名
     */
    private String columnName;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 是否null
     */
    private Boolean canBeNull;

    /**
     * 初始值
     */
    private String defaultVal;

}
