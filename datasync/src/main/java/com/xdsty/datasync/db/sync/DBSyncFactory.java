package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.enums.DbTypeEnum;
import com.xdsty.datasync.pojo.DBInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 根据db类型获取对应的数据库同步类
 * 单例模式
 * @author 张富华
 * @date 2020/3/27 10:21
 */
@Component
public class DBSyncFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DBSyncFactory.applicationContext = applicationContext;
    }

    private DBSyncFactory() {}

    public static DBSync getDBSync(DBInfo fromDbInfo, DBInfo toDbInfo){
        if(DbTypeEnum.MYSQL.getValue().equals(fromDbInfo.getDbType())
        && DbTypeEnum.MYSQL.getValue().equals(toDbInfo.getDbType())){
            return (DBSync) applicationContext.getBean("mySQLToMySQLSync");
        }
        return null;
    }
}
