package dao;

import util.DatabaseHelper;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class UserDAOImpl implements UserDAO {

    // 登录用户
    @Override
    public boolean loginUser(String account, String password) {
        String query = "SELECT * FROM User WHERE account = ? AND password = ?";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, account);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();  // 如果找到了结果，说明登录成功
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // 注册用户
    @Override
    public boolean registerUser(User user) {
        String query = "INSERT INTO User (account, password, email, age) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getAccount());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getAge());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;  // 如果插入成功，返回 true
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取用户信息
    @Override
    public User getUserInfo(String account) {
        String query = "SELECT * FROM User WHERE account = ?";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setAccount(rs.getString("account"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setAge(rs.getInt("age"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;  // 返回查询到的用户对象
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // 没找到用户，返回 null
    }

    @Override
    public boolean deleteUser(int userid) {
        // 由于有外键关系，需要先删除 HealthData 表中的数据，再删除 User 表中的数据
        String deleteHealthDataQuery = "DELETE FROM HealthData WHERE user_id = ?";
        String deleteUserQuery = "DELETE FROM User WHERE user_id = ?";

        Connection connection = null;
        try {
            connection = DatabaseHelper.getConnection();
            // 开启事务
            connection.setAutoCommit(false);

            // 先删除 HealthData 表中的数据
            try (PreparedStatement psHealth = connection.prepareStatement(deleteHealthDataQuery)) {
                psHealth.setInt(1, userid);
                psHealth.executeUpdate();
            }

            // 再删除 User 表中的数据
            try (PreparedStatement psUser = connection.prepareStatement(deleteUserQuery)) {
                psUser.setInt(1, userid);
                int rowsAffected = psUser.executeUpdate();

                // 提交事务
                connection.commit();
                return rowsAffected > 0;  // 如果删除成功，返回 true
            }

        } catch (SQLException e) {
            // 发生异常时回滚事务
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    // 恢复自动提交
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // 根据账户查找用户
    @Override
    public User findUserByAccount(String account) {
        String query = "SELECT * FROM User WHERE account = ?";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, account);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setAccount(rs.getString("account"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setAge(rs.getInt("age"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // 没找到用户
    }
}
