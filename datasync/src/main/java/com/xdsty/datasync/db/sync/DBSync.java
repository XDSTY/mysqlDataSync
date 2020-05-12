package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.pojo.DBInfo;
import java.sql.SQLException;

/**
 * 拷贝数据接口
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/20 16:15
 */
public interface DBSync {

    /**
     * 同步数据入口
     * @param fromDbInfo 源数据库
     * @param toDbInfo 目标数据库
     */
    void sync(DBInfo fromDbInfo, DBInfo toDbInfo) throws SQLException, ClassNotFoundException;
}
