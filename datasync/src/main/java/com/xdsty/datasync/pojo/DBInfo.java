package com.xdsty.datasync.pojo;

import com.xdsty.datasync.connect.ConnUtil;
import lombok.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *  数据库信息
 * @author 张富华
 * @date 2020/3/17 11:22
 */
@Data
public class DBInfo {

    public static final String MYSQL = "MySQL";

    public static final String SQLSERVER = "SQLSERVER";

    public static final String ORACLE = "ORACLE";

    /**
     * 数据库类型
     * MYSQL SQLServer ORACLE
     */
    private String dbType;

    private String driver;

    private String url;

    private String username;

    private String password;

    private String dbName;

    /**
     * 连接到数据库的connection
     */
    private Connection connection;

    /**
     * 数据库的表
     */
    private List<MTable> tables;

    public void init() throws SQLException, ClassNotFoundException {
        connection = ConnUtil.getConnection(this);
        dbName = url.substring(url.lastIndexOf('/') + 1);
    }

    public void destory() throws SQLException {
        connection.close();
    }
}
