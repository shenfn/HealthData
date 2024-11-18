package model;

import java.sql.Timestamp;

public class HealthData {
    private int familyId;
    private int userId;
    private int height;
    private double weight;
    private double bloodSugar;
    private int steps;
    private Timestamp recordedAt;

    // 构造方法
    public HealthData(int familyId, int userId, int height, double weight, double bloodSugar, int steps, Timestamp recordedAt) {
        this.familyId = familyId;
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.bloodSugar = bloodSugar;
        this.steps = steps;
        this.recordedAt = recordedAt;
    }

    // 默认构造方法
    public HealthData() {}

    public HealthData(int familyId, int userId, int height, double weight, double bloodSugar, int steps) {
        this.familyId = familyId;
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.bloodSugar = bloodSugar;
        this.steps = steps;

    }

    public HealthData(int userId, int height, double weight, double bloodSugar, int steps) {
        this.userId = userId;
        this.height = height;

        this.weight = weight;
        this.bloodSugar = bloodSugar;
        this.steps = steps;

    }


    // Getter 和 Setter 方法
    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(double bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public Timestamp getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Timestamp recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public String toString() {
        return "HealthData{" +
                "familyId=" + familyId +
                ", userId=" + userId +
                ", height=" + height +
                ", weight=" + weight +
                ", bloodSugar=" + bloodSugar +
                ", steps=" + steps +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
