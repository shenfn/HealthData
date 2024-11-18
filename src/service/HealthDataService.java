package service;

import dao.HealthDataDAO;
import dao.HealthDataDAOImpl;
import model.HealthData;

import java.sql.Timestamp;
import java.util.List;

public class HealthDataService {

    private HealthDataDAO healthDataDAO;

    public HealthDataService() {
        this.healthDataDAO = new HealthDataDAOImpl(); // 初始化DAO层
    }

    // 获取指定家庭的所有健康数据
    public List<HealthData> getAllHealthData(int userid) {
        return healthDataDAO.getAllHealthData(userid);
    }

    // 根据字段范围查找健康数据（例如身高、体重等字段的区间查找）
    public List<HealthData> searchHealthData(String field, Object minValue, Object maxValue) {
        return healthDataDAO.searchHealthData(field, minValue, maxValue);
    }

    // 添加健康数据
    public boolean addHealthData(int familyid, int userId, int height, double weight, double bloodSugar, int steps, Timestamp data) {
        HealthData healthData = new HealthData(familyid, userId, height, weight, bloodSugar, steps, data);
        return healthDataDAO.addHealthData(healthData);
    }

    // 更新健康数据
    public boolean updateHealthData(int id, String field, Object newValue) {
        HealthData healthData = healthDataDAO.getHealthDataById(id); // 假设有方法根据ID获取健康数据
        if (healthData == null) {
            return false;
        }

        // 根据字段更新对应的值
        switch (field) {
            case "height":
                healthData.setHeight((Integer) newValue);
                break;
            case "weight":
                healthData.setWeight((Double) newValue);
                break;
            case "bloodSugar":
                healthData.setBloodSugar((Double) newValue);
                break;
            case "steps":
                healthData.setSteps((Integer) newValue);
                break;
            case "recordedAt":
                healthData.setRecordedAt((Timestamp) newValue);
                break;
            default:
                return false;
        }

        return healthDataDAO.updateHealthData(healthData);
    }

    // 删除健康数据
    public boolean deleteHealthData(int familyId) {
        return healthDataDAO.deleteHealthData(familyId);
    }
}
