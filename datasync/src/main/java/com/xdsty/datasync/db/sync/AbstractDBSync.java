package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.MTable;
import com.xdsty.datasync.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/24 15:52
 */
@Component
abstract class AbstractDBSync implements DBSync {

    private static final Logger log = LoggerFactory.getLogger(AbstractDBSync.class);

    /**
     * 目标表不存在则创建表
     * @param fromDbInfo 源数据库
     * @param toDbInfo 目标数据库
     */
    void syncTableInfo(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException {
        log.error("开始同步数据库结构，源数据库{}, 目标数据库{}, 时间:{}",
                fromDbInfo.getUrl(), toDbInfo.getUrl(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
        try {
            Map<String, MTable> fromTablesMap = fromDbInfo.getTables().stream().collect(Collectors.toMap(MTable::getTableName, a -> a));
            Map<String, MTable> toTablesMap = toDbInfo.getTables().stream().collect(Collectors.toMap(MTable::getTableName, a -> a));

            MTable table;
            for(MTable fromTable : fromDbInfo.getTables()){
                table = toTablesMap.get(fromTable.getTableName());
                // 新建
                if(table == null){
                    createTargetTable(fromTable, toDbInfo.getConnection());
                }else{
                    // 同步结构
                    syncColumn(fromTable, table, toDbInfo.getConnection());
                    syncIndex(fromTable, table, toDbInfo.getConnection());
                }
            }
            for(MTable toTable : toDbInfo.getTables()){
                table = fromTablesMap.get(toTable.getTableName());
                if(table == null){
                    deleteTable(toTable, toDbInfo.getConnection());
                }
            }
            //同步完后需要更新目标数据库的tables
            toDbInfo.setTables(fromDbInfo.getTables());
        }catch (SQLException e){
            log.error("数据库结构同步失败，源数据库{}, 目标数据库{}", fromDbInfo.getUrl(), toDbInfo.getUrl(), e);
            throw e;
        }
        log.error("数据库结构同步成功，源数据库{}, 目标数据库{}, 时间: {}",
                fromDbInfo.getUrl(), toDbInfo.getUrl(), DateUtil.date2String(new Date(), DateUtil.DATE_TIME_PATTERN));
    }

    /**
     * 初始化数据库数据，包括字段、索引
     * @param fromDbInfo
     * @param toDbInfo
     */
    abstract void initDBInfo(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException, ClassNotFoundException;

    /**
     * 同步表结构 目标表不存在则创建新的
     * 添加字段
     * 修改字段名（无法同步）
     * 修改字段类型
     * 修改字段是否为NULL
     * 添加索引
     * 删除索引
     * @param fromDbInfo 源数据库
     * @param toDbInfo 目标数据库
     */
    abstract void syncStructure(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException;

    /**
     * 同步数据
     */
    abstract void syncData(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException;

    /**
     * 组装sql
     * @param fromDbInfo 源数据库
     * @return
     */
    abstract void assembleSql(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException;

    /**
     * 执行插入sql
     * @param toDbInfo 目标数据库连接
     */
    abstract void executeBatchInsert(DBInfo toDbInfo) throws SQLException;

    /**
     * 创建表
     * @param table 表信息
     */
    abstract void createTargetTable(MTable table, Connection conn) throws SQLException;

    /**
     * 删除表
     * @param table
     * @param conn
     */
    abstract void deleteTable(MTable table, Connection conn) throws SQLException;

    /**
     * 同步字段
     * @param fromTable 源表
     * @param toTable 目标表
     */
    abstract void syncColumn(MTable fromTable, MTable toTable, Connection conn) throws SQLException;

    /**
     * 同步索引
     * @param fromTable 源表
     * @param toTable 目标表
     */
    abstract void syncIndex(MTable fromTable, MTable toTable, Connection conn) throws SQLException;
}
