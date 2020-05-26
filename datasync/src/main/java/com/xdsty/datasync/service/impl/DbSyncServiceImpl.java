package com.xdsty.datasync.service.impl;

import com.xdsty.datasync.db.sync.DBSync;
import com.xdsty.datasync.db.sync.DBSyncFactory;
import com.xdsty.datasync.pojo.SyncContext;
import com.xdsty.datasync.service.DbSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 张富华
 * @date 2020/4/2 15:53
 */
@Service
public class DbSyncServiceImpl implements DbSyncService {

    private static final Logger log = LoggerFactory.getLogger(DbSyncServiceImpl.class);

    @Override
    public boolean sync(SyncContext syncContext) {
        DBSync dbSync = DBSyncFactory.getDBSync(syncContext.getFromDb(), syncContext.getDestDb());
        if(dbSync == null){
            return false;
        }
        try {
            dbSync.sync(syncContext);
        } catch (Exception e) {
            log.error("数据库同步失败", e);
        }
        return true;
    }
}
