package com.xdsty.datasync.constant;

import com.xdsty.datasync.enums.IndexTypeEnum;
import com.xdsty.datasync.enums.IndexUniqueTypeEnum;
import com.xdsty.datasync.pojo.Column;
import com.xdsty.datasync.pojo.Index;
import java.text.MessageFormat;

/**
 * @author 张富华
 * @date 2020/3/17 16:21
 */
public class MySQLCommonSql {

    /**
     * 查询数据库的表列表
     */
    private static final String SELECT_TABLES = "SHOW TABLES";

    /**
     * 查询建表语句
     */
    private static final String SHOW_CREATE_TABLE = "SHOW CREATE TABLE ";

    /**
     * 删除表
     */
    private static final String DROP_TABLE = "DROP TABLE ";

    /**
     * 查询表的columns
     */
    private static final String SHOW_COLUMNS = "SHOW COLUMNS FROM ";

    /**
     * 查询表的索引
     */
    private static final String SHOW_INDEX = "SHOW INDEX FROM ";

    /**
     * 修改列的类型或者默认值
     * {0} tableName
     * {1} columnName
     * {2} columnType
     * {3} defaultValue
     * {4} null or not null
     */
    private static final String ALTER_COLUMN = "ALTER TABLE {0} MODIFY COLUMN {1} {2} {3} {4}";

    /**
     * 添加新的列
     */
    private static final String ADD_COLUMN = "ALTER TABLE {0} ADD COLUMN {1} {2} DEFAULT {3}";

    /**
     * 添加主键
     */
    private static final String ADD_PRIMARY_KEY = "ALTER TABLE {0} ADD PRIMARY KEY ({1})";

    /**
     * 添加普通索引
     */
    private static final String ADD_INDEX = "ALTER TABLE {0} ADD {1} KEY {2} ({3})";

    private static final String DROP_INDEX = "ALTER TABLE {0} DROP KEY {1}";

    /**
     * 删除列
     */
    private static final String DROP_COLUMN = "ALTER TABLE {0} DROP COLUMN {1}";

    /**
     * 表的字段信息
     */
    private static final String COLUMN_SCHEMA = "SELECT * from information_schema.columns WHERE table_name = '{0}'";

    public static String getSelectTableSql(){
        return SELECT_TABLES;
    }

    public static String getShowCreateTable(String tableName){
        return SHOW_CREATE_TABLE + tableName;
    }

    public static String getDropTable(String tableName){
        return DROP_TABLE + tableName;
    }

    public static String getSelectColumns(String tableName){
        return SHOW_COLUMNS + tableName;
    }

    public static String getShowIndex(String tableName){
        return SHOW_INDEX + tableName;
    }

    public static String getAlterColumn(Column column){
        return MessageFormat.format(ALTER_COLUMN,
                column.getTableName(),
                column.getColumnName(),
                column.getType(),
                column.getDefaultVal() != null ? "DEFAULT " + column.getDefaultVal() : "",
                column.getCanBeNull() ? "" : "NOT NULL");
    }

    public static String getAddColumnSql(Column column){
        return MessageFormat.format(ADD_COLUMN, column.getTableName(), column.getColumnName(), column.getType(), column.getDefaultVal());
    }

    public static String getDropColumn(Column column){
        return MessageFormat.format(DROP_COLUMN, column.getTableName(), column.getColumnName());
    }

    public static String getAddIndex(Index index){
        String keyPre = IndexUniqueTypeEnum.UNIQUE.getValue().equals(index.getIdxUniqueType()) ?
                IndexUniqueTypeEnum.UNIQUE.getKey() :
                (IndexTypeEnum.FULLTEXT.getValue().equals(index.getIndexType()) ? IndexTypeEnum.FULLTEXT.getName() : "");
        return MessageFormat.format(ADD_INDEX,
                index.getTableName(),
                keyPre,
                index.getIndexName(),
                index.getColumn());
    }

    public static String getDropIndex(Index index){
        return MessageFormat.format(DROP_INDEX, index.getTableName(), index.getIndexName());
    }

    public static String getColumnSchema(String tableName){
        return MessageFormat.format(COLUMN_SCHEMA, tableName);
    }
}
