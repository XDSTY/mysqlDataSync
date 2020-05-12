package com.xdsty.datasync.pojo;

import lombok.Data;
import java.util.List;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/18 15:51
 */
@Data
public class MTable {

    private String dbName;

    private String tableName;

    private String createTableSql;

    private String insertSql;

    /**
     * 表对应的数据库
     */
    private DBInfo dbInfo;

    /**
     * 表的列
     */
    private List<Column> columns;

    /**
     * 索引列表
     */
    private List<Index> indices;

}

