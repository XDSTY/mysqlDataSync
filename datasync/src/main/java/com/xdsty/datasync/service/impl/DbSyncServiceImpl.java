package com.xdsty.datasync.service.impl;

import com.xdsty.datasync.db.sync.DBSync;
import com.xdsty.datasync.db.sync.DBSyncFactory;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.service.DbSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/4/2 15:53
 */
@Service
public class DbSyncServiceImpl implements DbSyncService {

    private static final Logger log = LoggerFactory.getLogger(DbSyncServiceImpl.class);

    @Override
    public boolean sync(DBInfo fromDbInfo, DBInfo toDbInfo) {
        DBSync dbSync = DBSyncFactory.getDBSync(fromDbInfo, toDbInfo);
        if(dbSync == null){
            return false;
        }
        try {
            dbSync.sync(fromDbInfo, toDbInfo);
        } catch (Exception e) {
            log.error("数据库同步失败", e);
        }
        return true;
    }
}
