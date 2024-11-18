package dao;
import model.HealthData;

import java.util.List;

public interface HealthDataDAO {
    //获取所有的健康数据（应该可以进行表格的填充和分页操作的进行）
    List<HealthData> getAllHealthData(int userid);

    //查找，根据choicebox获取字段信息，进行区间查找（如查找身高在150到170的所有数据）
    List<HealthData> searchHealthData(String field, Object minValue, Object maxValue);

    //增加健康数据包括（身高，体重，血糖，步数，时间）
    boolean addHealthData(HealthData healthData);

    //修改，在表后面添加修改按钮，点击修改按钮跳出弹窗进行修改操作；或者根据choicebox在其中选择要修改的字段进行对应的修改
    boolean updateHealthData(HealthData healthData);

    //删除，获取family_id进行对应记录删除，
    boolean deleteHealthData(int familyId);


    // 根据ID获取健康数据
    HealthData getHealthDataById(int id);

    // 在 HealthDataDAO 类中创建这个方法
    int getTotalCount(int userId);

    HealthData getLatestHealthData(int userId);

    HealthData getPreviousHealthData(int userId);

    // 获取用户的健康数据
    List<HealthData> getHealthDataForUser(int userId);
}
