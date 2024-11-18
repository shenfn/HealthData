package dao;

import model.HealthData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static util.DatabaseHelper.getConnection;

public class HealthDataDAOImpl implements HealthDataDAO {


    // 获取所有健康数据
    @Override
    public List<HealthData> getAllHealthData(int userid) {
        List<HealthData> healthDataList = new ArrayList<>();
        String sql = "SELECT * FROM HealthData WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                healthDataList.add(new HealthData(
                        rs.getInt("family_id"),
                        rs.getInt("user_id"),
                        rs.getInt("height"),
                        rs.getDouble("weight"),
                        rs.getDouble("blood_sugar"),
                        rs.getInt("steps"),
                        rs.getTimestamp("recorded_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return healthDataList;
    }

    // 根据查询条件获取健康数据
    @Override
    public List<HealthData> searchHealthData(String field, Object minValue, Object maxValue) {
        List<HealthData> healthDataList = new ArrayList<>();
        String sql = "SELECT * FROM HealthData WHERE " + field + " BETWEEN ? AND ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, minValue);
            stmt.setObject(2, maxValue);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                healthDataList.add(new HealthData(
                        rs.getInt("family_id"),
                        rs.getInt("user_id"),
                        rs.getInt("height"),
                        rs.getDouble("weight"),
                        rs.getDouble("blood_sugar"),
                        rs.getInt("steps"),
                        rs.getTimestamp("recorded_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return healthDataList;
    }

    // 增加一条健康数据
    @Override
    public boolean addHealthData(HealthData healthData) {
        System.out.println("add" + healthData.getFamilyId());
        System.out.println("add" + healthData.getRecordedAt());

        String sql = "INSERT INTO HealthData ( user_id, height, weight, blood_sugar, steps, recorded_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, healthData.getUserId());
            stmt.setInt(2, healthData.getHeight());
            stmt.setDouble(3, healthData.getWeight());
            stmt.setDouble(4, healthData.getBloodSugar());
            stmt.setInt(5, healthData.getSteps());
            stmt.setTimestamp(6, healthData.getRecordedAt());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 更新健康数据
    @Override
    public boolean updateHealthData(HealthData healthData) {

        String sql = "UPDATE HealthData SET height = ?, weight = ?, blood_sugar = ?, steps = ? WHERE family_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, healthData.getHeight());
            stmt.setDouble(2, healthData.getWeight());
            stmt.setDouble(3, healthData.getBloodSugar());
            stmt.setInt(4, healthData.getSteps());
            stmt.setInt(5, healthData.getFamilyId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 删除健康数据
    @Override
    public boolean deleteHealthData(int familyId) {
        String sql = "DELETE FROM HealthData WHERE family_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, familyId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 根据ID获取健康数据
    @Override
    public HealthData getHealthDataById(int userid) {
        String sql = "SELECT * FROM HealthData WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new HealthData(
                        rs.getInt("family_id"),
                        rs.getInt("user_id"),
                        rs.getInt("height"),
                        rs.getDouble("weight"),
                        rs.getDouble("blood_sugar"),
                        rs.getInt("steps"),
                        rs.getTimestamp("recorded_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 在 HealthDataDAO 类中创建这个方法
    @Override
    public int getTotalCount(int userId) {
        int totalCount = 0;
        String sql = "SELECT COUNT(*) FROM HealthData WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 这里可以使用日志记录来代替打印堆栈
        }
        return totalCount;
    }

    @Override
    public HealthData getLatestHealthData(int userId) {
        String sql = "SELECT * FROM HealthData WHERE user_id = ? ORDER BY recorded_at DESC LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToHealthData(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 可以改为更合适的日志记录
        }
        return null;
    }

    @Override
    public HealthData getPreviousHealthData(int userId) {
        String sql = "SELECT * FROM HealthData WHERE user_id = ? ORDER BY recorded_at DESC LIMIT 1 OFFSET 1";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToHealthData(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 可以改为更合适的日志记录
        }
        return null;
    }

    private HealthData mapResultSetToHealthData(ResultSet resultSet) throws SQLException {
        HealthData healthData = new HealthData();
        healthData.setFamilyId(resultSet.getInt("family_id"));
        healthData.setUserId(resultSet.getInt("user_id"));
        healthData.setHeight(resultSet.getInt("height"));
        healthData.setWeight(resultSet.getDouble("weight"));
        healthData.setBloodSugar(resultSet.getDouble("blood_sugar"));
        healthData.setSteps(resultSet.getInt("steps"));
        healthData.setRecordedAt(resultSet.getTimestamp("recorded_at"));
        return healthData;
    }


    // 获取用户的健康数据
    @Override
    public List<HealthData> getHealthDataForUser(int userId) {
        List<HealthData> healthDataList = new ArrayList<>();
        String sql = "SELECT * FROM HealthData WHERE user_id = ? ORDER BY recorded_at DESC";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                HealthData healthData = new HealthData();
                healthData.setRecordedAt(resultSet.getTimestamp("recorded_at"));
                healthData.setWeight(resultSet.getDouble("weight"));
                healthDataList.add(healthData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return healthDataList;
    }

}
