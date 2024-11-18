package controller;

import dao.UserDAO;
import dao.UserDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import service.UserService;

import java.io.IOException;

public class RegistrationController {

    @FXML
    private TextField accountField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;


    @FXML
    private TextField ageFiled;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;
    UserDAO userDAO = new UserDAOImpl();
    private final UserService userService = new UserService(userDAO); // 假设 userService 已被实例化

    // 处理注册按钮点击事件
    @FXML
    private void handleRegister() {
        User user = new User();
        String account = accountField.getText();
        user.setAccount(account);
        String password = passwordField.getText();
        user.setPassword(passwordField.getText());
        String email = emailField.getText();
        user.setEmail(emailField.getText());

        try {
            String ageText = ageFiled.getText();
            int age = Integer.parseInt(ageText);

            if (age < 0) {
                System.out.println("年龄不能为负数！");
            } else {
                user.setAge(age);
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的年龄数字！");
        }



        // 验证输入是否为空
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert("提示", "所有字段均为必填项！");
            return;
        }

        try {

            boolean success = userService.register(user);

            if (success) {
                showAlert("成功", "注册成功！");
                // 注册成功后跳转到主菜单界面
                Thread.sleep(100); // 100毫秒的延迟

                loadMainMenu();
            } else {
                showAlert("失败", "注册失败，请稍后再试。");
            }
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的余额数字！");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 处理返回按钮点击事件
    @FXML
    private void handleBack() {
        // 返回逻辑，例如返回上一页面
        System.out.println("返回到上一页面");
        loadMainMenu();
        //showAlert("提示", "返回到上一页面");
    }

    // 显示弹窗的帮助方法
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 加载主菜单界面
    private void loadMainMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow(); // 获取当前窗口
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("错误", "无法加载主菜单界面！");
            e.printStackTrace();
        }
    }
}
