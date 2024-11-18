package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/uhms";  // 替换成你的数据库URL
    private static final String USER = "root";  // 数据库用户名
    private static final String PASSWORD = "2317168349";  // 数据库密码

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
