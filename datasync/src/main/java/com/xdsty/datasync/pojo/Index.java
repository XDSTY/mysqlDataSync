package com.xdsty.datasync.pojo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

/**
 * 表索引
 * @author 张富华
 * @date 2020/3/21 16:51
 */
@Data
@EqualsAndHashCode
public class Index {

    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String NON_UNIQUE = "NON_UNIQUE";
    public static final String INDEX_SCHEMA = "INDEX_SCHEMA";
    public static final String INDEX_NAME = "INDEX_NAME";
    public static final String SEQ_IN_INDEX = "SEQ_IN_INDEX";
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String NULLABLE = "NULLABLE";
    public static final String INDEX_TYPE = "INDEX_TYPE";
    public static final String INDEX_COMMENT = "INDEX_COMMENT";

    /**
     * 表名
     */
    private String tableName;

    /**
     * 唯一索引标识  0:唯一索引  1:非唯一索引
     */
    private Integer nonUnique;

    /**
     * 索引所在schema
     */
    private String indexSchema;

    /**
     * 索引名
     */
    private String indexName;

    /**
     * 索引字段排序 复合索引中每个字段按照数字升序排序
     * (a, b, c)  a的seqInIndex为1, b的为2, c的为3
     */
    private Integer seqInIndex;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 是否可以为空
     */
    private String nullable;

    /**
     * 索引类型
     */
    private String indexType;

    /**
     * 索引注释
     */
    private String indexComment;

    public boolean isPrimaryKey(){
        return StringUtils.equals("PRIMARY", indexName);
    }
}
