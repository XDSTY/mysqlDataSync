package com.xdsty.datasync.service.impl;

import com.xdsty.datasync.db.sync.DBSync;
import com.xdsty.datasync.db.sync.DBSyncFactory;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.service.DbSyncService;
import org.springframework.stereotype.Service;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/4/2 15:53
 */
@Service
public class DbSyncServiceImpl implements DbSyncService {

    @Override
    public boolean sync(DBInfo fromDbInfo, DBInfo toDbInfo) {
        DBSync dbSync = DBSyncFactory.getDBSync(fromDbInfo, toDbInfo);
        if(dbSync == null){
            return false;
        }
        try {
            dbSync.sync(fromDbInfo, toDbInfo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }
}
