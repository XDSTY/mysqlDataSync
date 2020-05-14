package com.xdsty.datasync.db.init;

import com.xdsty.datasync.constant.MySQLCommonSql;
import com.xdsty.datasync.enums.IndexTypeEnum;
import com.xdsty.datasync.pojo.Column;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.Index;
import com.xdsty.datasync.pojo.MTable;
import com.xdsty.datasync.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 张富华
 * @date 2020/3/17 16:06
 */
@Component
public class MySqlInfoInit implements DBInit {

    private static final Logger log = LoggerFactory.getLogger(MySqlInfoInit.class);

    @Override
    public void initDbInfo(DBInfo dbInfo) throws SQLException, ClassNotFoundException {
        dbInfo.init();

        // 获取所有表
        initTableInfo(dbInfo);
        initTableColumnWithSchema(dbInfo);
//        initTableIndex(dbInfo);
    }

    /**
     * 初始化表信息
     * @param dbInfo 数据库信息
     */
    private void initTableInfo(DBInfo dbInfo) throws SQLException {
        Connection conn = dbInfo.getConnection();
        List<String> tableNames = getTableNames(conn);
        List<MTable> tables = new LinkedList<>();
        for(String tableName : tableNames){
            MTable table = new MTable();
            ResultSet set = conn.prepareStatement(MySQLCommonSql.getShowCreateTable(tableName)).executeQuery();
            if (set.next()) {
                table.initTable(set.getString(2));
            }
            table.setTableName(tableName);
            table.setDbInfo(dbInfo);
            tables.add(table);
        }
        dbInfo.setTables(tables);
    }

    /**
     * 获取所有表 并按照表名排序
     *
     * @return 表名列表
     */
    private List<String> getTableNames(Connection conn) throws SQLException {
        log.error("开始获取源数据库表，{}", DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        PreparedStatement statement;
        List<String> list = new LinkedList<>();
        try {
            statement = conn.prepareStatement(MySQLCommonSql.getSelectTableSql());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                list.add(set.getString(1));
            }
        } catch (SQLException e) {
            log.error("获取数据库表失败，{}", e);
            throw e;
        }
        log.error("获取源数据库表成功，{}", DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        return list.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
    }

    /**
     * 根据schema初始化表信息
     * @param dbInfo 数据库
     */
    private void initTableColumnWithSchema(DBInfo dbInfo) throws SQLException {
        log.error("开始初始化columns信息，{}", DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        if(CollectionUtils.isEmpty(dbInfo.getTables())){
            return;
        }
        Connection conn = dbInfo.getConnection();
        for(MTable table : dbInfo.getTables()){
            ResultSet set = conn.prepareStatement(MySQLCommonSql.getColumnSchema(dbInfo.getDbName(), table.getTableName())).executeQuery();
            List<Column> columns = new LinkedList<>();
            while (set.next()){
                Column column = new Column();
                column.setTableName(set.getString(Column.TABLE_NAME));
                column.setColumnName(set.getString(Column.COLUMN_NAME));
                column.setColumnDefault(set.getString(Column.COLUMN_DEFAULT));
                column.setNullable(set.getString(Column.IS_NULLABLE));
                column.setDataType(set.getString(Column.DATA_TYPE));
                column.setCharacterMaximumLength(set.getInt(Column.CHARACTER_MAXIMUM_LENGTH));
                column.setCharacterOctetLength(set.getInt(Column.CHARACTER_OCTET_LENGTH));
                column.setNumericPrecision(set.getInt(Column.NUMERIC_PRECISION));
                column.setNumericScale(set.getInt(Column.NUMERIC_SCALE));
                column.setDatetimePrecision(set.getInt(Column.DATETIME_PRECISION));
                column.setCharacterSetName(set.getString(Column.CHARACTER_SET_NAME));
                column.setCollationName(set.getString(Column.COLLATION_NAME));
                column.setColumnType(set.getString(Column.COLUMN_TYPE));
                column.setColumnKey(set.getString(Column.COLUMN_KEY));
                column.setExtra(set.getString(Column.EXTRA));
                column.setPrivileges(set.getString(Column.PRIVILEGES));
                column.setColumnComment(set.getString(Column.COLUMN_COMMENT));
                columns.add(column);
            }
            table.setColumns(columns.stream().sorted(Comparator.comparing(Column::getColumnName)).collect(Collectors.toList()));
        }
        log.error("初始化columns信息完成，{}", DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
    }

    /**
     * 初始化表的索引信息
     * @param dbInfo 数据库信息
     */
    private void initTableIndex(DBInfo dbInfo) throws SQLException {
        if(CollectionUtils.isEmpty(dbInfo.getTables())){
            return;
        }
        Connection conn = dbInfo.getConnection();
        List<MTable> tables = dbInfo.getTables();
        for(MTable table : tables){
            ResultSet set = conn.prepareStatement(MySQLCommonSql.getIndexSchema(dbInfo.getDbName(), table.getTableName())).executeQuery();
            LinkedList<Index> indices = new LinkedList<>();
            while (set.next()){
                // 复合索引
                if(CollectionUtils.isEmpty(indices) && indices.getLast().getIndexName().equals(set.getString(Index.INDEX_NAME))){
                    Index index = indices.getLast();
                    index.setColumnName(index.getColumnName() + "," + set.getString(Index.COLUMN_NAME));
                } else {
                    Index index = new Index();
                    index.setTableName(set.getString(Index.TABLE_NAME));
                    index.setNonUnique(set.getInt(Index.NON_UNIQUE));
                    index.setIndexSchema(set.getString(Index.INDEX_SCHEMA));
                    index.setIndexName(set.getString(Index.INDEX_NAME));
                    index.setSeqInIndex(set.getInt(Index.SEQ_IN_INDEX));
                    index.setColumnName(set.getString(Index.COLUMN_NAME));
                    index.setNullable(set.getString(Index.NULLABLE));
                    index.setIndexType(set.getString(Index.INDEX_TYPE));
                    index.setIndexComment(set.getString(Index.INDEX_COMMENT));
                    indices.add(index);
                }
            }
            table.setIndices(indices.stream().sorted(Comparator.comparing(Index::getIndexName)).collect(Collectors.toList()));
        }
    }
}
