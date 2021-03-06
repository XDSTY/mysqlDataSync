package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.constant.Constant;
import com.xdsty.datasync.constant.MySQLCommonSql;
import com.xdsty.datasync.db.init.DBInit;
import com.xdsty.datasync.pojo.*;
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
    public void sync(SyncContext syncContext) throws SQLException, ClassNotFoundException {
        initDBInfo(syncContext.getFromDb(), syncContext.getDestDb());
        syncStructure(syncContext);
        // 同步数据
        if (syncContext.getDataSync() != null && "TRUE".equalsIgnoreCase(syncContext.getDataSync().getFlag())) {
            syncData(syncContext);
        }
        afterSync(syncContext);
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
    public void syncStructure(SyncContext syncContext) throws SQLException {
        DBInfo fromDbInfo = syncContext.getFromDb();
        DBInfo destDbInfo = syncContext.getDestDb();
        log.info("开始同步数据库结构, 时间:{}",
                fromDbInfo.getUrl(), destDbInfo.getUrl(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        try {
            Map<String, MTable> fromTablesMap = fromDbInfo.getTables().stream().collect(Collectors.toMap(MTable::getTableName, a -> a));
            Map<String, MTable> toTablesMap = destDbInfo.getTables().stream().collect(Collectors.toMap(MTable::getTableName, a -> a));

            MTable table;
            for (MTable fromTable : fromDbInfo.getTables()) {
                table = toTablesMap.get(fromTable.getTableName());
                // 新建
                if (table == null) {
                    executeSql(fromTable.getCreateTableSql(), destDbInfo.getConnection());
                } else {
                    // 同步表的字符集、引擎、注释
                    syncTableInfo(fromTable, table);
                    // 同步column
                    syncColumns(fromTable, table, destDbInfo.getConnection());
                    // 同步索引
                    syncIndex(fromTable, table, destDbInfo.getConnection());
                    afterIndexSync(fromTable, table, destDbInfo.getConnection());
                }
            }
            for (MTable toTable : destDbInfo.getTables()) {
                table = fromTablesMap.get(toTable.getTableName());
                if (table == null) {
                    executeSql(MySQLCommonSql.getDropTable(toTable.getTableName()), destDbInfo.getConnection());
                }
            }
            //同步完后需要更新目标数据库的tables
            destDbInfo.setTables(fromDbInfo.getTables());
        } catch (SQLException e) {
            log.error("数据库结构同步失败，源数据库{}, 目标数据库{}", fromDbInfo.getUrl(), destDbInfo.getUrl(), e);
            throw e;
        }
        log.info("数据库结构同步成功 时间: {}",
                fromDbInfo.getUrl(), destDbInfo.getUrl(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
    }

    @Override
    public void syncData(SyncContext syncContext) throws SQLException {
        DBInfo fromDb = syncContext.getFromDb(), destDb = syncContext.getDestDb();
        fromDb.getTables().forEach(table -> {
            try {
                syncTable(syncContext.getDataSync(), table, fromDb.getConnection(), destDb.getConnection());
            } catch (SQLException e) {
                log.error("数据同步失败{}", table, e);
            }
        });
        try {
            fromDb.destory();
            destDb.destory();
        } catch (SQLException e) {
            log.error("数据库连接销毁失败", e);
            throw e;
        }
    }

    /**
     * 同步表的信息 字符集 引擎 注释
     *
     * @param fromTable
     * @param toTable
     */
    private void syncTableInfo(MTable fromTable, MTable toTable) throws SQLException {
        Connection conn = toTable.getDbInfo().getConnection();
        if (!fromTable.getCharset().equals(toTable.getCharset())) {
            executeSql(MySQLCommonSql.getTableCharset(fromTable), conn);
        }
        if (!fromTable.getEngine().equals(toTable.getEngine())) {
            executeSql(MySQLCommonSql.getTableEngine(fromTable), conn);
        }
        if (!StringUtils.equals(fromTable.getComment(), toTable.getComment())) {
            executeSql(MySQLCommonSql.getTableComment(fromTable), conn);
        }
    }

    /**
     * 同步当前表数据
     *
     * @param syncInfo  同步信息
     * @param fromTable 源表信息
     * @param fromConn  源数据库conn
     * @param toConn    目标数据库conn
     * @throws SQLException
     */
    private void syncTable(DataSyncInfo syncInfo, MTable fromTable, Connection fromConn, Connection toConn) throws SQLException {
        log.info("开始同步表{}数据, {}", fromTable.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_SECOND_PATTERN));
        LimitPage page = syncInfo == null ? null : new LimitPage();
        for (int i = 0; ; i++) {
            if (page != null) {
                page.setStartLimit(i * syncInfo.getLimit());
                page.setEndLimit(page.getStartLimit() + syncInfo.getLimit());
            }
            String insertSql = assembleTableSql(fromTable, fromConn, page);
            // 没有数据了
            if (StringUtils.isEmpty(insertSql)) {
                break;
            }
            executeSql(insertSql, toConn);
        }
        log.info("同步表{}数据完成, {}", fromTable.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_SECOND_PATTERN));
    }

    /**
     * 拼接表的数据sql
     *
     * @param table 表
     * @param conn  数据库connection
     * @return 数据sql
     * @throws SQLException
     */
    private String assembleTableSql(MTable table, Connection conn, LimitPage page) throws SQLException {
        //根据表的列拼接处查询sql
        StringBuilder selectSql = new StringBuilder("SELECT ");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size() - 1; i++) {
            selectSql.append(columns.get(i).getColumnName()).append(Constant.COMMA);
        }
        selectSql.append(columns.get(columns.size() - 1).getColumnName()).append(" ");
        selectSql.append("FROM ").append(table.getTableName());
        if (page != null) {
            selectSql.append(" LIMIT ").append(page.getStartLimit()).append(", ").append(page.getEndLimit());
        }

        StringBuilder insertSql = new StringBuilder("INSERT INTO ");
        insertSql.append(table.getTableName()).append("(");
        for (int i = 0; i < columns.size() - 1; i++) {
            insertSql.append(columns.get(i).getColumnName()).append(Constant.COMMA);
        }
        insertSql.append(columns.get(columns.size() - 1).getColumnName()).append(") VALUES");

        try {
            ResultSet set = conn.prepareStatement(selectSql.toString()).executeQuery();
            boolean firstRow = true;
            while (set.next()) {
                insertSql.append(firstRow ? "(" : ",(");
                firstRow = false;
                for (int i = 0; i < columns.size() - 1; i++) {
                    appendInsertSql(insertSql, set, columns.get(i));
                    insertSql.append(Constant.COMMA);
                }
                appendInsertSql(insertSql, set, columns.get(columns.size() - 1));
                insertSql.append(")");
            }
            // 没有数据
            if (firstRow) {
                return null;
            }
            insertSql.append(" ON DUPLICATE KEY UPDATE ");
            for (Column column : columns) {
                insertSql.append(column.getColumnName()).append(" = VALUES(").append(column.getColumnName()).append("),");
            }
            insertSql.deleteCharAt(insertSql.length() - 1);
            set.close();
        } catch (SQLException e) {
            log.error("拷贝失败", e);
            throw e;
        }
        return insertSql.toString();
    }

    private void appendInsertSql(StringBuilder insertSql, ResultSet set, Column column) throws SQLException {
        String val = set.getString(column.getColumnName());
        if (column.isNumeric()) {
            insertSql.append(StringUtils.isEmpty(val) ? "null" : Long.parseLong(val));
        } else {
            if (StringUtils.isEmpty(val)) {
                insertSql.append("null");
            } else {
                insertSql.append("'").append(set.getString(column.getColumnName())).append("'");
            }
        }
    }

    /**
     * 同步表column
     *
     * @param fromTable 源表
     * @param toTable   目标表
     * @param conn      目标表数据库连接
     */
    private void syncColumns(MTable fromTable, MTable toTable, Connection conn) {
        log.info("开始同步表{}，{}", fromTable.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_SECOND_PATTERN));
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
                    if (!fromColumn.equals(column)) {
                        executeSql(MySQLCommonSql.getAlterColumn(fromColumn), conn);
                    }
                }
            } catch (SQLException e) {
                log.error("列{}更新失败{}", fromColumn, e);
            }
        });

        toColumns.forEach(toColumn -> {
            Column column = fromColumnMap.get(toColumn.getColumnName());
            // 该列已被删除
            if (column == null) {
                try {
                    executeSql(MySQLCommonSql.getDropColumn(toColumn), conn);
                } catch (SQLException e) {
                    log.error("删除目标表列{}失败", toColumn.toString(), e);
                }
            }
        });
        log.info("同步表{}完成，{}", fromTable.getTableName(), DateUtil.date2String(new Date(), DateUtil.DATE_SECOND_PATTERN));
    }

    private void syncIndex(MTable fromTable, MTable toTable, Connection conn) {
        Map<String, Index> fromTableIndexMap = fromTable.getIndices().stream().collect(Collectors.toMap(Index::getIndexName, a -> a));
        Map<String, Index> toTableIndexMap = toTable.getIndices().stream().collect(Collectors.toMap(Index::getIndexName, a -> a));

        //删除fromTable中不存在的
        toTable.getIndices().forEach(toIdx -> {
            if (fromTableIndexMap.get(toIdx.getIndexName()) == null) {
                try {
                    executeSql(MySQLCommonSql.getDropIndex(toIdx), conn);
                } catch (SQLException e) {
                    log.error("索引{}删除失败, {}", toIdx, e);
                }
            }
        });

        //找到新建的和修改的
        fromTable.getIndices().forEach(fromIdx -> {
            Index idx = toTableIndexMap.get(fromIdx.getIndexName());
            try {
                if (idx == null) {
                    //判断是否是主键索引
                    if (fromIdx.isPrimaryKey()) {
                        executeSql(MySQLCommonSql.getAddPrimaryKey(fromIdx), conn);
                    } else {
                        executeSql(MySQLCommonSql.getAddIndex(fromIdx), conn);
                    }
                } else if (!fromIdx.equals(idx)) {
                    // 索引有改动 删除旧索引 添加新索引
                    executeSql(MySQLCommonSql.getDropIndex(idx), conn);
                    executeSql(MySQLCommonSql.getAddIndex(fromIdx), conn);
                }
            } catch (SQLException e) {
                log.error("索引{}同步失败, {}", fromIdx, e);
            }
        });
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

    /**
     * 同步完索引后置处理
     *
     * @param fromTable 源表
     * @param toTable   目标表
     * @param conn      目标表连接
     */
    private void afterIndexSync(MTable fromTable, MTable toTable, Connection conn) {
        //索引同步完成后检查fromTable是否有extra字段不为空的column，主要是auto_increment必须建立在为key的column上
        List<Column> extraColumns = fromTable.getColumns().stream().filter(e -> StringUtils.isNotEmpty(e.getExtra())).collect(Collectors.toList());
        extraColumns.forEach(column -> {
            try {
                executeSql(MySQLCommonSql.getAlterColumnWithExtra(column), conn);
            } catch (SQLException e) {
                log.error("Column{}修改失败", column, e);
            }
        });
    }

    /**
     * 同步工作完成之后
     *
     * @param syncContext 同步上下文
     */
    private void afterSync(SyncContext syncContext) {

    }
}
