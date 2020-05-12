package com.xdsty.datasync;

import com.xdsty.datasync.pojo.DBInfo;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/19 14:54
 */
@SpringBootTest
public abstract class BaseProjectTest {

    protected DBInfo fromDbInfo;

    protected DBInfo toDbInfo;

    public BaseProjectTest(){
        fromDbInfo = new DBInfo();
        fromDbInfo.setDriver("com.mysql.cj.jdbc.Driver");
        fromDbInfo.setUrl("jdbc:mysql://119.23.240.184:3306/test");
        fromDbInfo.setUsername("root");
        fromDbInfo.setPassword("Zhangfuhua123!");

        toDbInfo = new DBInfo();
        toDbInfo.setDriver("com.mysql.cj.jdbc.Driver");
        toDbInfo.setUrl("jdbc:mysql://47.107.99.226:3306/test");
        toDbInfo.setUsername("root");
        toDbInfo.setPassword("njord1996");
    }

}
