package controller;

import dao.UserDAO;
import dao.UserDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.Main;
import model.User;
import service.UserService;
import util.AlertHelper;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField accountField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button exitButton;

    UserDAO userDAO = new UserDAOImpl();
    private UserService userService = new UserService(userDAO);  // UserService 实例化



    private static String currentAccount;
    private static int currentuserid;


    public static void setCurrentAccount(String account) {
        currentAccount = account;
    }

    public static String getCurrentAccount() {
        return currentAccount;
    }

    public static void setCurrentuserid(int userid) {
        currentuserid = userid;
    }

    public static int getCurrentuserid() {
        return currentuserid;
    }


    // 登录功能
    @FXML
    private void handleLogin(ActionEvent actionEvent) {


        System.out.println("Login button pressed");

        // 获取输入的用户名和密码
        String account = accountField.getText();
        String password = passwordField.getText();

        // 验证输入是否为空
        if (account.isEmpty() || password.isEmpty()) {
            AlertHelper.showInfoAlert("提示", "账号和密码不能为空！");
            return;
        }

        // 调用 UserService 进行登录验证
        User user = userService.loginUser(account, password);
        if (user != null) {
            setCurrentAccount(account);
            setCurrentuserid(user.getUserId());

            System.out.println("userid" + user.getUserId());
            // 登录成功后
            System.out.println("Login success: " + user.getAccount());

            // 进入健康数据页面
            try {
                Stage stage = Main.getPrimaryStage(); // 获取主界面 Stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/health.fxml"));
                Parent mainView = loader.load();

                Scene scene = new Scene(mainView);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                AlertHelper.showErrorAlert("错误", "加载健康数据页面失败！");
            }
        } else {
            // 登录失败
            AlertHelper.showErrorAlert("失败", "账号或密码错误！");
        }
    }

    // 注册功能
    @FXML
    private void handleCount(ActionEvent actionEvent) {
        System.out.println("Register button pressed");
        try {
            Stage stage = Main.getPrimaryStage();
            // 加载注册页面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/registration.fxml"));

            Parent registrationPage = loader.load();
            Scene scene = new Scene(registrationPage);
            stage.setScene(scene);
            stage.show();
            stage.setTitle("注册");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 退出应用
    @FXML
    private void handleExit(ActionEvent actionEvent) {
        Stage stage = Main.getPrimaryStage();
        // 关闭应用程序
        stage.close();
    }
}
