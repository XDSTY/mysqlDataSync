package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.constant.Constant;
import com.xdsty.datasync.constant.MySQLCommonSql;
import com.xdsty.datasync.db.init.DBInit;
import com.xdsty.datasync.pojo.Column;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.Index;
import com.xdsty.datasync.pojo.MTable;
import com.xdsty.datasync.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张富华
 * @date 2020/3/20 16:29
 */
@Component
public class MySQLToMySQLSync implements DBSync {

    private static final Logger log = LoggerFactory.getLogger(MySQLToMySQLSync.class);

    private DBInit dbInit;

    @Autowired
    @Qualifier(value = "mySqlInfoInit")
    public void setDbInit(DBInit dbInit) {
        this.dbInit = dbInit;
    }

    @Override
    public void sync(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException, ClassNotFoundException {
        initDBInfo(fromDbInfo, toDbInfo);
        syncStructure(fromDbInfo, toDbInfo);
     //   syncData(fromDbInfo, toDbInfo);
    }

    /**
     * 初始化数据库信息
     *
     * @param fromDbInfo 源db
     * @param toDbInfo   目标db
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void initDBInfo(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException, ClassNotFoundException {
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
            log.error("开始同步数据库结构，源数据库{}, 目标数据库{}, 时间:{}",
                    fromDbInfo.getUrl(), toDbInfo.getUrl(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
            try {
                Map<String, MTable> fromTablesMap = fromDbInfo.getTables().stream().collect(Collectors.toMap(MTable::getTableName, a -> a));
                Map<String, MTable> toTablesMap = toDbInfo.getTables().stream().collect(Collectors.toMap(MTable::getTableName, a -> a));

                MTable table;
                for (MTable fromTable : fromDbInfo.getTables()) {
                    table = toTablesMap.get(fromTable.getTableName());
                    // 新建
                    if (table == null) {
                        executeSql(fromTable.getCreateTableSql(), toDbInfo.getConnection());
                    } else {
                        // 同步表的字符集、引擎、注释
                        syncTableInfo(fromTable, table);
                        // 同步column
                        syncColumns(fromTable, table, toDbInfo.getConnection());
                        // 同步索引
//                        syncIndex(fromTable, table, toDbInfo.getConnection());
                    }
                }
                for (MTable toTable : toDbInfo.getTables()) {
                    table = fromTablesMap.get(toTable.getTableName());
                    if (table == null) {
                        executeSql(MySQLCommonSql.getDropTable(toTable.getTableName()), toDbInfo.getConnection());
                    }
                }
                //同步完后需要更新目标数据库的tables
                toDbInfo.setTables(fromDbInfo.getTables());
            } catch (SQLException e) {
                log.error("数据库结构同步失败，源数据库{}, 目标数据库{}", fromDbInfo.getUrl(), toDbInfo.getUrl(), e);
                throw e;
            }
            log.error("数据库结构同步成功，源数据库{}, 目标数据库{}, 时间: {}",
                    fromDbInfo.getUrl(), toDbInfo.getUrl(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        } catch (SQLException e) {
            log.error("数据库结构同步失败", e);
            throw e;
        }
    }

    /**
     * 同步表的信息 字符集 引擎 注释
     * @param fromTable
     * @param toTable
     */
    private void syncTableInfo(MTable fromTable, MTable toTable) throws SQLException {
        Connection conn = toTable.getDbInfo().getConnection();
        if(!fromTable.getCharset().equals(toTable.getCharset())){
            executeSql(MySQLCommonSql.getTableCharset(fromTable), conn);
        }
        if(!fromTable.getEngine().equals(toTable.getEngine())){
            executeSql(MySQLCommonSql.getTableEngine(fromTable), conn);
        }
        if(!StringUtils.equals(fromTable.getComment(), toTable.getComment())){
            executeSql(MySQLCommonSql.getTableComment(fromTable), conn);
        }
    }

    /**
     * 同步数据
     * @param fromDbInfo
     * @param toDbInfo
     * @throws SQLException
     */
    private void syncData(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException {
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

    /**
     * 设置sql
     * @param fromDbInfo
     * @param toDbInfo
     * @throws SQLException
     */
    private void assembleSql(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException {
        for (int i = 0; i < fromDbInfo.getTables().size(); i++) {
            toDbInfo.getTables().get(i).setInsertSql(assembleTableSql(fromDbInfo.getTables().get(i), fromDbInfo.getConnection()));
        }
    }

    /**
     * 拼接表的数据sql
     *
     * @param table 表
     * @param conn  数据库connection
     * @return 数据sql
     * @throws SQLException
     */
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
            if (firstRow) {
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

    /**
     * 执行更新sql
     * @param toDbInfo 目标db
     * @throws SQLException
     */
    private void executeBatchInsert(DBInfo toDbInfo) throws SQLException {
        Connection conn = toDbInfo.getConnection();
        for (MTable table : toDbInfo.getTables()) {
            if (StringUtils.isEmpty(table.getInsertSql())) {
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

    /**
     * 同步表column
     * @param fromTable 源表
     * @param toTable 目标表
     * @param conn 目标表数据库连接
     */
    private void syncColumns(MTable fromTable, MTable toTable, Connection conn){
        log.error("开始同步表{}，{}", fromTable.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        List<Column> fromColumns = fromTable.getColumns();
        List<Column> toColumns = toTable.getColumns();
        Map<String, Column> fromColumnMap = fromColumns.stream().collect(Collectors.toMap(Column::getColumnName, c -> c));
        Map<String, Column> toColumnMap = toColumns.stream().collect(Collectors.toMap(Column::getColumnName, c -> c));

        fromColumns.forEach(fromColumn -> {
            Column column;
            try {
                // 目标表中不存在该column
                if ((column = toColumnMap.get(fromColumn.getColumnName())) == null) {
                    executeSql(MySQLCommonSql.getAddColumnSql(fromColumn), conn);
                } else {
                    // 对column进行更新
                    if(!fromColumn.equals(column)){
                        executeSql(MySQLCommonSql.getAlterColumn(fromColumn), conn);
                    }
                }
            }catch (SQLException e){
                log.error("列{}更新失败{}", fromColumn, e);
            }
        });

        toColumns.forEach(toColumn -> {
            Column column = fromColumnMap.get(toColumn.getColumnName());
            // 该列已被删除
            if(column == null){
                try {
                    executeSql(MySQLCommonSql.getDropColumn(toColumn), conn);
                } catch (SQLException e) {
                    log.error("删除目标表列{}失败", toColumn.toString(), e);
                }
            }
        });
        log.error("同步表{}完成，{}", fromTable.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
    }

    private void syncIndex(MTable fromTable, MTable toTable, Connection conn) throws SQLException {
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
                executeSql(MySQLCommonSql.getDropIndex(idx), conn);
            }
            executeSql(MySQLCommonSql.getAddIndex(index), conn);
        }
        //找出已删除的索引
        for (Index index : toTable.getIndices()) {
            idx = fromTableIndexMap.get(index.getIndexName());
            if (!index.equals(idx) && !index.isPrimaryKey()) {
                executeSql(MySQLCommonSql.getDropIndex(index), conn);
            }
        }
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
