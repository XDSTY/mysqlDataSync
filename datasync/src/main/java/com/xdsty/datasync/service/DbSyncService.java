package com.xdsty.datasync.service;

import com.xdsty.datasync.pojo.SyncContext;

import java.sql.SQLException;

/**
 * @author 张富华
 * @date 2020/4/2 15:51
 */
public interface DbSyncService {

    /**
     * 开始同步数据库
     * @param syncContext 源数据库
     */
    boolean sync(SyncContext syncContext) throws SQLException, ClassNotFoundException;

}
