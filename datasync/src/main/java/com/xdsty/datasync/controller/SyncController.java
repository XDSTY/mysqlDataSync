package com.xdsty.datasync.controller;

import com.xdsty.datasync.enums.DbTypeEnum;
import com.xdsty.datasync.pojo.DBInfo;
import com.xdsty.datasync.pojo.SyncParam;
import com.xdsty.datasync.service.DbSyncService;
import com.xdsty.datasync.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/4/2 11:14
 */
@RestController
public class SyncController {

    @Autowired
    private DbSyncService dbSyncService;

    @PostMapping("sync")
    public Result sync(@RequestBody SyncParam syncParam){
        DBInfo fromDbInfo = assemblyFromDbInfo(syncParam);
        DBInfo toDbInfo = assemblyToDbInfo(syncParam);
        try {
            boolean res = dbSyncService.sync(fromDbInfo, toDbInfo);
            return res ? Result.createSuccess(null, "同步成功") : Result.createFailure("暂不支持该类型数据库");
        } catch (Exception e) {
            return Result.createFailure("同步失败" + e.getMessage());
        }
    }

    private DBInfo assemblyFromDbInfo(SyncParam param){
        DBInfo dbInfo = new DBInfo();
        dbInfo.setUrl(param.getFromDbUrl());
        dbInfo.setUsername(param.getFromDbUser());
        dbInfo.setPassword(param.getFromDbPassword());
        dbInfo.setDriver(DbTypeEnum.getDriverByType(param.getFromDbType()));
        dbInfo.setDbType(param.getFromDbType());
        return dbInfo;
    }

    private DBInfo assemblyToDbInfo(SyncParam param){
        DBInfo dbInfo = new DBInfo();
        dbInfo.setUrl(param.getToDbUrl());
        dbInfo.setUsername(param.getToDbUser());
        dbInfo.setPassword(param.getToDbPassword());
        dbInfo.setDriver(DbTypeEnum.getDriverByType(param.getToDbType()));
        dbInfo.setDbType(param.getToDbType());
        return dbInfo;
    }

}
