package com.xdsty.datasync.db.sync;

import com.xdsty.datasync.BaseProjectTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/27 15:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MySQLToMySQLSyncTest extends BaseProjectTest {

    @Autowired
    private DBSync dbSync;

    @Test
    public void syncStructure() throws SQLException, ClassNotFoundException {
        System.out.println(null + "");
    }

}