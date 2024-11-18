package controller;

import dao.HealthDataDAO;
import dao.HealthDataDAOImpl;
import dao.UserDAO;
import dao.UserDAOImpl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import main.Main;
import model.HealthData;
import model.User;
import service.HealthDataService;
import service.UserService;
import util.AlertHelper;
import util.DatabaseHelper;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static controller.MainController.getCurrentAccount;
import static controller.MainController.getCurrentuserid;

public class HealthDataController {

    // FXML 注入的组件
    @FXML private TableColumn<HealthData, Void> editColumn;
    @FXML private TableColumn<HealthData, Boolean> selectColumn;
    @FXML private Label weightLabel;
    @FXML private Label bodyFatLabel;
    @FXML private Label weightChangeLabel;
    @FXML private LineChart<String, Number> weightChart;
    @FXML private ChoiceBox<String> findFieldChoiceBox;
    @FXML private TextField minValue;
    @FXML private TextField maxValue;
    @FXML private Button searchButton;
    @FXML private Button resetSearchButton;
//    @FXML private ChoiceBox<String> modifyFieldChoiceBox;
//    @FXML private TextField modifyValue;
//    @FXML private Button modifyButton;
//    @FXML private Button resetModifyButton;
    @FXML private TextField weightInput;
    @FXML private TextField heightInput;
    @FXML private TextField stepsInput;
    @FXML private TextField bloodSugarInput;
    @FXML private DatePicker datePicker;
    @FXML private Button addButton;
    @FXML private Button resetButton;
//    @FXML private TextField deleteIdInput;
//    @FXML private Button deleteButton;
//    @FXML private Button resetDeleteButton;
    @FXML private TableView<HealthData> healthDataTable;
    @FXML private TableColumn<HealthData, Integer> idColumn;
    @FXML private TableColumn<HealthData, Double> weightColumn;
    @FXML private TableColumn<HealthData, Double> heightColumn;
    @FXML private TableColumn<HealthData, Double> bloodSugarColumn;
    @FXML private TableColumn<HealthData, Integer> stepsColumn;
    @FXML private TableColumn<HealthData, java.sql.Timestamp> dateColumn;
    @FXML private Pagination pagination;
    @FXML private Label timeLabel;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Button refreshChartButton;
    @FXML private VBox chartPane;
    @FXML private Button logoutButton;
    @FXML private Button quitButton;
    @FXML private VBox settingsContent;



    private final HealthDataService healthDataService;

    HealthData healthdata = new HealthData();
    UserDAO userDAO = new UserDAOImpl();
    UserService userService = new UserService(userDAO);
    HealthDataDAO healthdataDAO = new HealthDataDAOImpl();

    int currentuserid = getCurrentuserid();


    public HealthDataController() {
        healthDataService = new HealthDataService();  // 初始化Service层
    }

    // 初始化方法
    // 初始化方法
    @FXML
    public void initialize() {
        // 在加载表格数据之前，先检查并确保 HealthData 表中有数据
        ensureHealthDataExists(currentuserid);

        loadHealthDataTable(currentuserid);  // 刷新表格数据

        // 主页面初始化
        showRealTimeClock(timeLabel); // 启动时间显示
        displayCurrentWeight(); // 体重显示
        displayWeightChange(); // 与上一次体重的差值
        loadWeightChart(currentuserid);
        refreshChartButton.setOnAction(this::handleRefreshChart);

        // 初始化增删改查事件
        searchButton.setOnAction(this::handleSearch);
        resetSearchButton.setOnAction(this::handleResetSearch);
        addButton.setOnAction(this::handleAdd);
        resetButton.setOnAction(this::handleResetAdd);
//        deleteButton.setOnAction(this::handleDelete);
//        resetDeleteButton.setOnAction(this::handleResetDelete);
        logoutButton.setOnAction(this::logout);
        quitButton.setOnAction(this::showMainMenu);

        // 初始化配置表格列
        // 设置时间列的格式化
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt")); // 确保绑定到 recordedAt 字段
        dateColumn.setCellFactory(col -> new TableCell<HealthData, Timestamp>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item.toLocalDateTime().toLocalDate()));
                }
            }
        });

        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        bloodSugarColumn.setCellValueFactory(new PropertyValueFactory<>("bloodSugar"));
        stepsColumn.setCellValueFactory(new PropertyValueFactory<>("steps"));

        // 设置编辑按钮列
        editColumn.setCellFactory(col -> new TableCell<HealthData, Void>() {
            private final Button editButton = new Button("编辑");

            {
                editButton.setOnAction(e -> {
                    HealthData selectedTransaction = (HealthData) getTableRow().getItem();
                    if (selectedTransaction != null) {
                        openEditDialog(selectedTransaction);
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // 初始化查找字段
        findFieldChoiceBox.getItems().addAll("height", "weight", "blood_sugar", "steps");
        findFieldChoiceBox.setValue("height");

//        // 设置修改字段的选项
//        modifyFieldChoiceBox.getItems().addAll("height", "weight", "blood_sugar", "steps");
//        modifyFieldChoiceBox.setValue("height");

        // 初始化分页功能
    }

    // 确保 HealthData 表中有数据
    private void ensureHealthDataExists(int userId) {
        // 查询 HealthData 表是否有数据
        String checkQuery = "SELECT COUNT(*) FROM HealthData WHERE user_id = ?";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ps = connection.prepareStatement(checkQuery)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    // 如果没有数据，插入一些默认数据
                    insertDefaultHealthData(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 插入默认数据到 HealthData 表
    private void insertDefaultHealthData(int userId) {
        String insertQuery = "INSERT INTO HealthData (user_id, weight, height, blood_sugar, steps, recorded_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            // 假设插入一些默认数据，日期可以根据当前日期生成
            ps.setInt(1, userId);
            ps.setDouble(2, 70.0);  // 默认体重
            ps.setDouble(3, 175.0); // 默认身高
            ps.setDouble(4, 5.5);   // 默认血糖
            ps.setInt(5, 1000);     // 默认步数
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now())); // 当前时间

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("默认数据插入成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 登录成功后触发数据加载
    public void onLoginSuccess(int currentuserid) {
        loadHealthDataTable(currentuserid);  // 在登录成功后加载健康数据
    }

    // 加载健康数据到表格
    private void loadHealthDataTable(int currentuserid) {
        // 假设这个方法会根据账户加载健康数据
        if (currentuserid > 0) {
            List<HealthData> healthDataList = healthdataDAO.getAllHealthData(currentuserid);
            // 打印加载的数据，确认 `date` 是否有值
            //healthDataList.forEach(data -> System.out.println("Date: " + data.getRecordedAt()));
            // 清空现有的表格数据并添加新的数据
            healthDataTable.getItems().clear();
            healthDataTable.getItems().addAll(healthDataList);

            // 如果需要，添加日志确认数据加载
            System.out.println("Health data loaded for account: " + currentuserid);
        }
        System.out.println("Health data loaded for account: " + currentuserid);

    }


    //主页面功能分区---------------------------------------------------------------------

    //分区一：实时显示时间
    public void showRealTimeClock(Label timeLabel) {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // 每秒更新一次时间
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            timeLabel.setText(currentTime.toString());
        }));
        clock.setCycleCount(Timeline.INDEFINITE); // 无限循环
        clock.play(); // 开始运行

    }

    private void displayCurrentWeight() {
        // 获取当前用户最新的体重记录
        HealthData latestData = healthdataDAO.getLatestHealthData(currentuserid);

        // 如果获取到的最新数据为 null，表示用户没有健康数据
        if (latestData != null) {
            // 确保体重字段存在并且有效
            if (latestData.getWeight() != 0.0) {
                // 显示最新体重，格式化为保留两位小数的字符串
                weightLabel.setText(String.format("%.2f kg", latestData.getWeight()));
            } else {
                // 如果体重值为 null，设置为默认提示
                weightLabel.setText("体重数据缺失");
            }
        } else {
            // 如果最新数据为空，说明用户没有任何体重记录，显示默认提示
            weightLabel.setText("---");
        }
    }


    private void displayWeightChange() {
        // 获取最新和上一个体重数据
        HealthData latestData = healthdataDAO.getLatestHealthData(currentuserid);
        HealthData previousData = healthdataDAO.getPreviousHealthData(currentuserid);

        if (latestData != null && previousData != null) {
            double weightChange = latestData.getWeight() - previousData.getWeight();
            if (weightChange > 0) {
                weightChangeLabel.setText(String.format("重" + "%.2f kg", weightChange));
            }
            else{
                weightChangeLabel.setText(String.format("轻" + "%.2f kg", weightChange));
            }
        } else {
            weightChangeLabel.setText("---");
        }
    }

    private void loadWeightChart(int userId) {
        // 获取健康数据
        List<HealthData> healthDataList = healthdataDAO.getHealthDataForUser(userId);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("体重变化");

        // 将数据加入到 series 中
        for (HealthData healthData : healthDataList) {
            String date = healthData.getRecordedAt().toString();  // 格式化日期
            double weight = healthData.getWeight();

            // 使用字符串日期作为横坐标
            series.getData().add(new XYChart.Data<>(date, weight));
        }

        // 清空已有数据，并将新的数据添加到 chart 中
        weightChart.getData().clear();
        weightChart.getData().add(series);  // 添加到图表的数据序列
    }

    @FXML
    public void handleRefreshChart(ActionEvent event) {

        System.out.println("111");
        // 清空之前的图表数据
        weightChart.getData().clear();

        // 获取当前用户的健康数据列表
        List<HealthData> healthDataList = healthdataDAO.getHealthDataForUser(currentuserid);

        // 创建一个新的系列来保存更新后的数据，横坐标使用字符串（日期），纵坐标使用数字（体重）
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("体重变化");

        // 将新的数据添加到系列
        for (HealthData healthData : healthDataList) {
            Timestamp recordedAt = healthData.getRecordedAt();
            double weight = healthData.getWeight();

            // 将时间戳转换为字符串格式的日期，作为横坐标
            String date = recordedAt.toString(); // 将时间戳转换为字符串

            // 将日期和体重数据添加到系列中
            series.getData().add(new XYChart.Data<>(date, weight));
        }

        // 将新的数据系列添加到图表
        weightChart.getData().add(series);
    }



    //增删查改功能分区----------------------------------------------------------------------

    // 查找按钮事件处理
    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            String field = findFieldChoiceBox.getValue();
            double min = Double.parseDouble(minValue.getText());
            double max = Double.parseDouble(maxValue.getText());

            // 查找数据并显示在表格中
            List<HealthData> searchResults = healthDataService.searchHealthData(field, min, max);
            if (searchResults.isEmpty()) {
                AlertHelper.showInfoAlert("没有结果", "没有找到符合条件的健康数据！");
            } else {
                healthDataTable.getItems().clear();
                healthDataTable.getItems().addAll(searchResults);
            }

        } catch (NumberFormatException e) {
            AlertHelper.showErrorAlert("输入错误", "请输入有效的数字范围！");
        }
    }

    // 重置查找表单
    @FXML
    private void handleResetSearch(ActionEvent event) {
        minValue.clear();
        maxValue.clear();
        loadHealthDataTable(currentuserid);  // 刷新表格数据

    }


    private void openEditDialog(HealthData healthData) {
        // 使用 final 修饰 dialogStage，确保它不会被修改
        final Stage dialogStage = new Stage();

        // 获取交易记录的 family_id
        int familyId = healthData.getFamilyId();
        System.out.println("打开编辑对话框，交易记录 ID: " + familyId);  // 添加调试信息

        // 创建标签
        Label heightLabel = new Label("身高 (cm):");
        Label weightLabel = new Label("体重 (kg):");
        Label stepsLabel = new Label("步数:");
        Label bloodSugarLabel = new Label("血糖 (mg/dL):");

        // 打开编辑对话框
        TextField heightField = new TextField(String.valueOf(healthData.getHeight()));
        TextField weightField = new TextField(String.valueOf(healthData.getWeight()));
        TextField stepsField = new TextField(String.valueOf(healthData.getSteps()));
        TextField bloodSugarField = new TextField(String.valueOf(healthData.getBloodSugar()));

        // 设置输入框宽度
        heightField.setPrefWidth(200);
        weightField.setPrefWidth(200);
        stepsField.setPrefWidth(200);
        bloodSugarField.setPrefWidth(200);

        // 创建保存按钮
        Button saveButton = new Button("保存");
        saveButton.setOnAction(e -> {
            healthData.setHeight(Integer.parseInt(heightField.getText()));
            healthData.setWeight(Integer.parseInt(weightField.getText()));
            healthData.setSteps(Integer.parseInt(stepsField.getText()));
            healthData.setBloodSugar(Double.parseDouble(bloodSugarField.getText()));

            // 更新数据库
            boolean success = healthdataDAO.updateHealthData(healthData);

            if (success) {
                AlertHelper.showInfoAlert("保存成功", "交易记录已成功更新！");
                loadHealthDataTable(currentuserid);  // 刷新表格数据
                displayCurrentWeight();  // 体重显示
                displayWeightChange();  // 体重差值显示
                dialogStage.close();  // 保存成功后关闭对话框
            } else {
                AlertHelper.showInfoAlert("操作失败", "更新交易记录失败，请重试！");
            }
        });

        // 创建删除按钮
        Button deleteButton = new Button("删除");
        deleteButton.setOnAction(e -> {
            System.out.println("删除按钮点击，familyID: " + familyId);  // 打印 ID

            // 删除交易记录
            boolean success = healthdataDAO.deleteHealthData(familyId);
            if (success) {
                AlertHelper.showInfoAlert("删除成功", "交易记录已成功删除！");
                loadHealthDataTable(currentuserid);  // 刷新表格数据
                dialogStage.close();  // 删除成功后关闭对话框
            } else {
                AlertHelper.showInfoAlert("操作失败", "删除交易记录失败，请重试！");
            }
        });

        // 创建布局
        VBox dialogLayout = new VBox(10);
        dialogLayout.setAlignment(Pos.CENTER);

        // 使用 GridPane 布局，将标签和输入框对齐
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // 将标签和文本框放入 GridPane
        gridPane.add(heightLabel, 0, 0);
        gridPane.add(heightField, 1, 0);
        gridPane.add(weightLabel, 0, 1);
        gridPane.add(weightField, 1, 1);
        gridPane.add(stepsLabel, 0, 2);
        gridPane.add(stepsField, 1, 2);
        gridPane.add(bloodSugarLabel, 0, 3);
        gridPane.add(bloodSugarField, 1, 3);

        // 按钮放入 HBox
        HBox buttonLayout = new HBox(15, deleteButton, saveButton);
        buttonLayout.setAlignment(Pos.CENTER);

        // 组合布局
        dialogLayout.getChildren().addAll(gridPane, buttonLayout);

        // 设置场景和舞台
        Scene dialogScene = new Scene(dialogLayout, 400, 300);
        dialogStage.setTitle("编辑健康数据");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }


//            // 修改按钮事件处理
//    @FXML
//    private void handleModify() {
//        String field = modifyFieldChoiceBox.getValue();
//        String value = modifyValue.getText();
//        try {
//            System.out.println("111");
//            // 根据选择的字段修改数据
//            //boolean success = healthDataService.modifyHealthData(field, value);
////            if (success) {
////                showAlert("修改成功", "数据修改成功！");
////                loadHealthDataTable();  // 刷新表格数据
////            } else {
////                showAlert("修改失败", "数据修改失败，请检查输入！");
////            }
//        } catch (Exception e) {
//            AlertHelper.showErrorAlert("错误", "修改过程中发生了错误：" + e.getMessage());
//        }
//    }

//    // 重置修改表单
//    @FXML
//    private void handleResetModify(ActionEvent event) {
//        modifyValue.clear();
//    }

    // 添加增加按钮事件处理
    @FXML
    private void handleAdd(ActionEvent event) {
        try {

            //获取familyid
            healthdata = healthdataDAO.getHealthDataById(currentuserid);
            System.out.println("healid" + healthdata.getFamilyId());
            int currentfamilyid = healthdata.getFamilyId();
            System.out.println("currentuserid" + currentuserid);
            System.out.println("currentfamilyid" + currentuserid);

            // 获取选择的日期
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                LocalDate today = LocalDate.now();
                Timestamp timestamp;

                // 判断选中的日期是否是今天
                if (selectedDate.isEqual(today)) {
                    // 如果是今天，则设置为当前时间
                    LocalDateTime currentTime = LocalDateTime.now();
                    timestamp = Timestamp.valueOf(currentTime);
                } else {
                    // 如果不是今天，则设置为当天的00:00:00
                    timestamp = Timestamp.valueOf(selectedDate.atStartOfDay());
                }

                healthdata.setRecordedAt(timestamp); // 将设置的时间戳存储到 `HealthData`
            }


            double weight = Double.parseDouble(weightInput.getText());
            int height = Integer.parseInt(heightInput.getText());
            int steps = Integer.parseInt(stepsInput.getText());
            double bloodSugar = Double.parseDouble(bloodSugarInput.getText());
            //Timestamp date = new Timestamp(System.currentTimeMillis()); //获取当前时间（不利于改时间）
            Timestamp date = healthdata.getRecordedAt();
            // 添加新健康数据
            boolean success = healthDataService.addHealthData(currentfamilyid,currentuserid, height, weight,  bloodSugar,steps, date);
            if (success) {
                AlertHelper.showInfoAlert("添加成功", "健康数据已成功添加！");
                loadHealthDataTable(currentuserid);  // 刷新表格数据
                displayCurrentWeight();//体重显示
                displayWeightChange();//体重差值显示

            } else {
                AlertHelper.showErrorAlert("添加失败", "数据添加失败，请检查输入！");
            }
        } catch (NumberFormatException e) {
            AlertHelper.showErrorAlert("输入错误", "请输入有效的数值！");
        }
    }

    // 添加按钮重置添加表单
    @FXML
    private void handleResetAdd(ActionEvent event) {
        weightInput.clear();
        heightInput.clear();
        stepsInput.clear();
        bloodSugarInput.clear();
        datePicker.setValue(null);
    }

//    // 删除按钮事件处理
//    @FXML
//    private void handleDelete(ActionEvent event) {
//        try {
//            int id = Integer.parseInt(deleteIdInput.getText());
//            System.out.println("id: " + id);
//            boolean success = healthDataService.deleteHealthData(id);
//            if (success) {
//                AlertHelper.showInfoAlert("删除成功", "健康数据已成功删除！");
//                loadHealthDataTable(currentuserid);  // 刷新表格数据
//            } else {
//                AlertHelper.showErrorAlert("删除失败", "没有找到指定的健康数据ID！");
//            }
//        } catch (NumberFormatException e) {
//            AlertHelper.showErrorAlert("输入错误", "请输入有效的ID！");
//        }
//    }
//
//    // 重置删除表单
//    @FXML
//    private void handleResetDelete(ActionEvent event) {
//        deleteIdInput.clear();
//    }

    // 设置分区内容
    private void logout(ActionEvent event) {
        System.out.println("注销");
        // 处理注销逻辑，显示确认对话框等
        System.out.println("account" + currentuserid);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("注销");
        alert.setHeaderText(null);
        alert.setContentText("是否确认注销");

        // 创建“确定”和“取消”按钮
        ButtonType okButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);

        // 添加按钮到 Alert 中
        alert.getButtonTypes().setAll(okButton, cancelButton);

        // 显示弹窗并等待用户响应
        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                userDAO.deleteUser(currentuserid);
                //返回主菜单
                try {
                    Stage stage = Main.getPrimaryStage(); // 从 main.Main 类中获取 Stage

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
                    Parent mainView = loader.load();


                    Scene scene = new Scene(mainView, 500, 400);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 执行注销操作
            } else {
                System.out.println("用户选择了取消");
                // 不执行任何操作
            }
        });
    }

    private void showMainMenu(ActionEvent event) {
        System.out.println("返回主菜单");
        // 返回主菜单逻辑
        try {
            Stage stage = Main.getPrimaryStage(); // 从 main.Main 类中获取 Stage

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent mainView = loader.load();


            Scene scene = new Scene(mainView, 850, 650);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}