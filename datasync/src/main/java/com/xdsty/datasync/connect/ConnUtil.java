package com.xdsty.datasync.connect;

import com.xdsty.datasync.pojo.DBInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author 张富华
 * @date 2020/3/16 14:07
 */
public class ConnUtil {

    private static final Logger log = LoggerFactory.getLogger(ConnUtil.class);

    /**
     * 创建连接
     * @param dbInfo 数据库连接信息
     * @return 新创建的connection
     */
    public static Connection getConnection(DBInfo dbInfo) throws SQLException, ClassNotFoundException {
        try {
            Class.forName(dbInfo.getDriver());
            Connection connection = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUsername(), dbInfo.getPassword());
            connection.setAutoCommit(false);
            return connection;
        }
        catch (ClassNotFoundException | SQLException e) {
            log.error("获取connection失败", e);
            throw e;
        }
    }

    /**
     * 销毁连接
     * @param connection 待销毁的connection
     */
    public static void destoryConnection(Connection connection){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
