package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.constant.Constant;
import com.xdsty.datasync.constant.MySQLCommonSql;
import com.xdsty.datasync.db.init.DBInit;
import com.xdsty.datasync.pojo.Column;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.Index;
import com.xdsty.datasync.pojo.MTable;
import com.xdsty.datasync.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/20 16:29
 */
@Component
public class MySQLToMySQLSync extends AbstractDBSync implements DBSync {

    private static final Logger log = LoggerFactory.getLogger(MySQLToMySQLSync.class);

    @Autowired
    @Qualifier(value = "mySqlInfoInit")
    private DBInit dbInit;

    @Override
    public void sync(DBInfo fromDbInfo, DBInfo toDbInfo) {
        try {
            initDBInfo(fromDbInfo, toDbInfo);
            syncStructure(fromDbInfo, toDbInfo);
            syncData(fromDbInfo, toDbInfo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void initDBInfo(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException, ClassNotFoundException {
        try {
            dbInit.initDbInfo(fromDbInfo);
            dbInit.initDbInfo(toDbInfo);
        } catch (SQLException | ClassNotFoundException e) {
            log.error("数据初始化失败", e);
            throw e;
        }
    }

    @Override
    public void syncStructure(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException {
        try {
            syncTableInfo(fromDbInfo, toDbInfo);
        } catch (SQLException e) {
            log.error("数据库结构同步失败", e);
            throw e;
        }
    }

    @Override
    public void syncData(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException {
        try {
            assembleSql(fromDbInfo, toDbInfo);
            executeBatchInsert(toDbInfo);
            fromDbInfo.destory();
            toDbInfo.destory();
        } catch (SQLException e) {
            log.error("数据同步失败", e);
            throw e;
        }
    }

    @Override
    public void assembleSql(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException {
        for (int i = 0; i < fromDbInfo.getTables().size(); i++) {
            toDbInfo.getTables().get(i).setInsertSql(assembleTableSql(fromDbInfo.getTables().get(i), fromDbInfo.getConnection()));
        }
    }

    private String assembleTableSql(MTable table, Connection conn) throws SQLException {
        log.error("开始复制{},数据,{}", table.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        //根据表的列拼接处查询sql
        StringBuilder selectSql = new StringBuilder("SELECT ");
        List<String> columns = table.getColumns().stream().map(Column::getColumnName).collect(Collectors.toList());
        for (int i = 0; i < columns.size() - 1; i++) {
            selectSql.append(columns.get(i)).append(Constant.COMMA);
        }
        selectSql.append(columns.get(columns.size() - 1)).append(" ");
        selectSql.append("FROM ").append(table.getTableName());

        StringBuilder insertSql = new StringBuilder("INSERT INTO ");
        insertSql.append(table.getTableName()).append("(");
        for (int i = 0; i < columns.size() - 1; i++) {
            insertSql.append(columns.get(i)).append(Constant.COMMA);
        }
        insertSql.append(columns.get(columns.size() - 1)).append(") VALUES");

        try {
            ResultSet set = conn.prepareStatement(selectSql.toString()).executeQuery();
            boolean firstRow = true;
            while (set.next()) {
                insertSql.append(firstRow ? "(" : ",(");
                firstRow = false;
                for (int i = 0; i < columns.size() - 1; i++) {
                    insertSql.append("'").append(set.getString(columns.get(i))).append("'").append(Constant.COMMA);
                }
                insertSql.append("'").append(set.getString(columns.get(columns.size() - 1))).append("')");
            }
            // 没有数据
            if(firstRow){
                return null;
            }
            insertSql.append(" ON DUPLICATE KEY UPDATE ");
            for (String column : columns) {
                insertSql.append(column).append(" = VALUES(").append(column).append("),");
            }
            insertSql.deleteCharAt(insertSql.length() - 1);
            set.close();
        } catch (SQLException e) {
            log.error("拷贝失败", e);
            throw e;
        }
        return insertSql.toString();
    }

    @Override
    public void executeBatchInsert(DBInfo toDbInfo) throws SQLException {
        Connection conn = toDbInfo.getConnection();
        for (MTable table : toDbInfo.getTables()) {
            if(StringUtils.isEmpty(table.getInsertSql())){
                continue;
            }
            try {
                PreparedStatement statement = conn.prepareStatement(table.getInsertSql());
                statement.execute();
                conn.commit();
                statement.close();
            } catch (SQLException e) {
                log.error("目标数据库数据同步失败,表名:{}", table.getTableName());
                throw e;
            }
        }
    }

    @Override
    public void createTargetTable(MTable table, Connection conn) throws SQLException {
        try {
            executeSql(table.getCreateTableSql(), conn);
        } catch (SQLException e) {
            log.error("创建表失败", e);
            throw e;
        }
    }

    @Override
    void deleteTable(MTable table, Connection conn) throws SQLException {
        executeSql(MySQLCommonSql.getDropTable(table.getTableName()), conn);
    }

    @Override
    public void syncColumn(MTable fromTable, MTable toTable, Connection conn) throws SQLException {
        List<Column> fromColumns = fromTable.getColumns();
        List<Column> toColumns = toTable.getColumns();
        Map<String, Column> fromColumnMap = fromColumns.stream().collect(Collectors.toMap(Column::getColumnName, a -> a));
        Map<String, Column> toColumnMap = toColumns.stream().collect(Collectors.toMap(Column::getColumnName, a -> a));
        Column column;
        // 找到新建和修改的column
        for (Column col : fromColumns) {
            column = toColumnMap.get(col.getColumnName());
            // 新增
            if (column == null) {
                createColumn(col, conn);
            } else if (!column.equals(col)) {
                //修改
                modifyColumn(col, conn);
            }
        }
        // 找到删除的
        for (Column col : toColumns) {
            if (!fromColumnMap.containsKey(col.getColumnName())) {
                deleteColumn(col, conn);
            }
        }
    }

    /**
     * 创建列
     *
     * @param column 列信息
     * @param conn   数据库链接
     */
    private void createColumn(Column column, Connection conn) throws SQLException {
        executeSql(MySQLCommonSql.getAddColumnSql(column), conn);
    }

    /**
     * 修改column
     *
     * @param column 字段信息
     * @param conn   连接
     * @throws SQLException
     */
    private void modifyColumn(Column column, Connection conn) throws SQLException {
        executeSql(MySQLCommonSql.getAlterColumn(column), conn);
    }

    /**
     * 删除字段
     *
     * @param column 字段
     * @param conn   连接
     * @throws SQLException
     */
    private void deleteColumn(Column column, Connection conn) throws SQLException {
        executeSql(MySQLCommonSql.getDropColumn(column), conn);
    }

    @Override
    public void syncIndex(MTable fromTable, MTable toTable, Connection conn) throws SQLException {
        Map<String, Index> fromTableIndexMap = fromTable.getIndices().stream().collect(Collectors.toMap(Index::getIndexName, a -> a));
        Map<String, Index> toTableIndexMap = toTable.getIndices().stream().collect(Collectors.toMap(Index::getIndexName, a -> a));
        Index idx;
        //找出添加的索引
        for (Index index : fromTable.getIndices()) {
            idx = toTableIndexMap.get(index.getIndexName());
            if (idx != null && idx.isPrimaryKey()) {
                continue;
            }
            if (idx != null) {
                dropIndex(idx, conn);
            }
            addIndex(index, conn);
        }
        //找出已删除的索引
        for (Index index : toTable.getIndices()) {
            idx = fromTableIndexMap.get(index.getIndexName());
            if (!index.equals(idx) && !index.isPrimaryKey()) {
                dropIndex(index, conn);
            }
        }
    }

    private void dropIndex(Index index, Connection conn) throws SQLException {
        executeSql(MySQLCommonSql.getDropIndex(index), conn);
    }

    private void addIndex(Index index, Connection conn) throws SQLException {
        executeSql(MySQLCommonSql.getAddIndex(index), conn);
    }

    private static void executeSql(String sql, Connection conn) throws SQLException {
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement(sql);
            statement.execute();
            conn.commit();
            statement.close();
        } catch (SQLException e) {
            log.error("sql执行失败, sql: {}, ", sql, e);
            throw e;
        }
    }
}
