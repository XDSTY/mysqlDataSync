package com.xdsty.datasync.constant;

import com.xdsty.datasync.enums.IndexTypeEnum;
import com.xdsty.datasync.enums.IndexUniqueTypeEnum;
import com.xdsty.datasync.pojo.Column;
import com.xdsty.datasync.pojo.Index;
import com.xdsty.datasync.pojo.MTable;
import org.apache.commons.lang.StringUtils;

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
     * 查询表的索引
     */
    private static final String SHOW_INDEX = "SHOW INDEX FROM ";

    /**
     * 修改列的类型或者默认值
     * {0} tableName
     * {1} columnName
     * {2} columnType
     * {3} character set
     * {4} defaultValue
     * {5} null or not null
     * {6} auto_increment
     * {7} comment
     */
    private static final String ALTER_COLUMN = "ALTER TABLE {0} MODIFY COLUMN {1} {2} {3} {4} {5} {6} {7}";

    /**
     * 添加新的列
     * {0} tableName
     * {1} columnName
     * {2} columnType
     * {3} character set
     * {4} defaultValue
     * {5} null or not null
     * {6} auto_increment
     * {7} comment
     */
    private static final String ADD_COLUMN = "ALTER TABLE {0} ADD COLUMN {1} {2} {3} {4} {5} {6} {7}";

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
    private static final String COLUMN_SCHEMA = "SELECT * from information_schema.columns WHERE TABLE_SCHEMA = \"{0}\" AND table_name = \"{1}\"";

    /**
     * 修改表字符集
     */
    private static final String TABLE_CHARSET = "ALTER TABLE {0} DEFAULT CHARACTER SET {1}";

    /**
     * 修改表的注释
     */
    private static final String TABLE_COMMENT = "ALTER TABLE {0} COMMENT \"{1}\"";

    /**
     * 修改表引擎
     */
    private static final String TABLE_ENGINE = "ALTER TABLE {0} ENGINE = {1}";


    private static final String TABLE_INDEX = "SELECT * from information_schema.STATISTICS WHERE TABLE_SCHEMA = \"{0}\" AND TABLE_NAME = \"{1}\"";

    public static String getSelectTableSql() {
        return SELECT_TABLES;
    }

    public static String getShowCreateTable(String tableName) {
        return SHOW_CREATE_TABLE + tableName;
    }

    public static String getDropTable(String tableName) {
        return DROP_TABLE + tableName;
    }

    public static String getShowIndex(String tableName) {
        return SHOW_INDEX + tableName;
    }

    public static String getAlterColumn(Column column) {
        return MessageFormat.format(ALTER_COLUMN,
                column.getTableName(),
                column.getColumnName(),
                column.getColumnType(),
                StringUtils.isNotEmpty(column.getCharacterSetName()) ? "CHARACTER SET " + column.getCharacterSetName() : "",
                column.getColumnDefault() != null ? "DEFAULT " + column.getColumnDefault() : "",
                "NO".equals(column.getNullable()) ? "NOT NULL" : "",
                "auto_increment".equals(column.getExtra()) ? "auto_increment" : "",
                StringUtils.isNotEmpty(column.getColumnComment()) ? "comment \"" + column.getColumnComment() + "\"" : "");
    }

    public static String getAddColumnSql(Column column) {
        return MessageFormat.format(ADD_COLUMN,
                column.getTableName(),
                column.getColumnName(),
                column.getColumnType(),
                StringUtils.isNotEmpty(column.getCharacterSetName()) ? "CHARACTER SET " + column.getCharacterSetName() : "",
                column.getColumnDefault() != null ? "DEFAULT " + column.getColumnDefault() : "",
                "NO".equals(column.getNullable()) ? "NOT NULL" : "",
                "auto_increment".equals(column.getExtra()) ? "auto_increment" : "",
                StringUtils.isNotEmpty(column.getColumnComment()) ? "comment \"" + column.getColumnComment() + "\"" : "");
    }

    public static String getDropColumn(Column column) {
        return MessageFormat.format(DROP_COLUMN, column.getTableName(), column.getColumnName());
    }

    public static String getAddIndex(Index index) {
        String keyPre = IndexUniqueTypeEnum.UNIQUE.getValue().equals(index.getIdxUniqueType()) ?
                IndexUniqueTypeEnum.UNIQUE.getKey() :
                (IndexTypeEnum.FULLTEXT.getValue().equals(index.getIndexType()) ? IndexTypeEnum.FULLTEXT.getName() : "");
        return MessageFormat.format(ADD_INDEX,
                index.getTableName(),
                keyPre,
                index.getIndexName(),
                index.getColumn());
    }

    public static String getDropIndex(Index index) {
        return MessageFormat.format(DROP_INDEX, index.getTableName(), index.getIndexName());
    }

    public static String getColumnSchema(String dbName, String tableName) {
        return MessageFormat.format(COLUMN_SCHEMA, dbName, tableName);
    }

    public static String getTableCharset(MTable table){
        return MessageFormat.format(TABLE_CHARSET, table.getTableName(), table.getCharset());
    }

    public static String getTableEngine(MTable table){
        return MessageFormat.format(TABLE_ENGINE, table.getTableName(), table.getEngine());
    }

    public static String getTableComment(MTable table){
        return MessageFormat.format(TABLE_COMMENT, table.getTableName(), table.getComment());
    }

    public static String getIndexSchema(String dbName, String tableName){
        return MessageFormat.format(TABLE_INDEX, dbName, tableName);
    }
}
