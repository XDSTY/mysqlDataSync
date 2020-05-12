package com.xdsty.datasync.db;

import com.xdsty.datasync.BaseProjectTest;
import com.xdsty.datasync.pojo.MTable;
import com.xdsty.datasync.db.init.MySqlInfoInit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.SQLException;
import java.util.List;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/3/19 16:04
 */
@SpringBootTest
public class MySqlInfoInitTest extends BaseProjectTest {

    @Autowired
    private MySqlInfoInit mySqlInfoInit;

    @Test
    public void doCreateTable() throws SQLException, ClassNotFoundException {
        mySqlInfoInit.initDbInfo(fromDbInfo);
        List<MTable> tables = fromDbInfo.getTables();
        for(MTable table : tables){
            System.out.println(table.getCreateTableSql());
            table.getColumns().forEach(e -> System.out.println(e));
            table.getIndices().forEach(e -> System.out.println(e));
            System.out.println();
        }
    }

    @Test
    public void testGetAlterColumn(){

    }
}