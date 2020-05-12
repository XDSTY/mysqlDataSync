package com.xdsty.datasync.pojo;


import com.xdsty.datasync.enums.IndexTypeEnum;
import com.xdsty.datasync.enums.IndexUniqueTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * 表索引
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/21 16:51
 */
@Data
@ToString
public class Index {

    /**
     * 是否是唯一索引
     */
    public static final String NON_UNIQUE = "Non_unique";

    public static final String KEY_NAME = "Key_name";

    public static final String COLUMN_NAME = "Column_name";

    public static final String NULL = "Null";

    /**
     * 索引类型  BTREE/FULLTEXT
     */
    public static final String INDEX_TYPE = "Index_type";

    /**
     * 主键的Key_name
     */
    public static final String PRIMARY = "PRIMARY";

    /**
     * 表名
     */
    private String tableName;

    /**
     * 索引名
     */
    private String indexName;

    /**
     * 是否是唯一索引
     */
    private Integer idxUniqueType;

    /**
     * 列名 可能是复合索引 name, age
     */
    private String column;

    /**
     * 是否可以为空
     */
    private Boolean canBeNull;

    /**
     * 索引类型  普通索引/全文索引
     */
    private Integer indexType;

    /**
     * 默认值
     */
    private String defaultVal;

    /**
     * 是否是主键
     */
    public boolean isPrimaryKey(){
        return PRIMARY.equals(indexName);
    }

    /**
     * 是否是唯一索引
     */
    public boolean isUniqueKey(){
        return IndexUniqueTypeEnum.UNIQUE.getValue().equals(idxUniqueType);
    }

    /**
     * 是否是全文索引
     */
    public boolean isFullTextKey(){
        return IndexTypeEnum.FULLTEXT.getValue().equals(indexType);
    }

}
