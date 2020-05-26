package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.pojo.SyncContext;

import java.sql.SQLException;

/**
 * 拷贝数据接口
 * @author 张富华
 * @date 2020/3/20 16:15
 */
public interface DBSync {

    /**
     * 同步库结构
     * @param syncContext 上下文信息
     */
    void syncStructure(SyncContext syncContext) throws SQLException;

    /**
     * 同步数据
     * @param syncContext 上下文信息
     * @throws SQLException
     */
    void syncData(SyncContext syncContext) throws SQLException;

    /**
     * 同步数据入口
     * @param syncContext 上下文信息
     */
    void sync(SyncContext syncContext) throws SQLException, ClassNotFoundException;
}
