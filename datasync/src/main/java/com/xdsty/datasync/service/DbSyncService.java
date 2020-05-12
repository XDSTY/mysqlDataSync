package com.xdsty.datasync.service;

import com.xdsty.datasync.pojo.DBInfo;
import java.sql.SQLException;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/4/2 15:51
 */
public interface DbSyncService {

    /**
     * 开始同步数据库
     * @param fromDbInfo 源数据库
     * @param toDbInfo 目标数据库
     */
    boolean sync(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException, ClassNotFoundException;

}
