package com.xdsty.datasync.db.init;

import com.xdsty.datasync.pojo.DBInfo;
import java.sql.SQLException;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/25 16:15
 */
public interface DBInit {

    /**
     * 初始化数据库信息
     * @param dbInfo 数据库
     */
    void initDbInfo(DBInfo dbInfo) throws SQLException, ClassNotFoundException;

}
