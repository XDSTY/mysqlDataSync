package com.xdsty.datasync.pojo;

import lombok.Data;
import java.util.List;

/**
 * @author 张富华
 * @date 2020/3/18 15:51
 */
@Data
public class MTable {

    private String dbName;

    private String tableName;

    private String createTableSql;

    private String insertSql;

    /**
     * 字符集
     */
    private String charset;

    /**
     * 引擎
     */
    private String engine;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 表对应的数据库
     */
    private DBInfo dbInfo;

    /**
     * 表的列
     */
    private List<Column> columns;


    private List<ColumnSchema> columnSchemas;

    /**
     * 索引列表
     */
    private List<Index> indices;

    /**
     * 根据建表语句初始化字符集和引擎
     * @param createTableSql 建表语句
     */
    public void initTable(String createTableSql){
        this.createTableSql = createTableSql;
        String tableDesc = createTableSql.substring(createTableSql.lastIndexOf(')'));
        String []descs = tableDesc.split(" ");
        for(String desc : descs){
            if(desc.startsWith("ENGINE")){
                this.engine = desc.substring(7);
            }
            if(desc.startsWith("CHARSET")){
                this.charset = desc.substring(8);
            }
            if(desc.startsWith("COMMENT")){
                this.comment = desc.substring(9, desc.length() - 1);
            }
        }
    }

}

