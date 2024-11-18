package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {

    // 登录失败提示
    public static void showLoginFailedAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("登录失败");
        alert.setHeaderText(null);
        alert.setContentText("账号或密码错误，请重新输入！");
        alert.showAndWait();
    }

    // 注册成功提示
    public static void showRegisterSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("注册成功");
        alert.setHeaderText(null);
        alert.setContentText("恭喜您，注册成功！");
        alert.showAndWait();
    }

    // 注册失败提示
    public static void showRegisterFailedAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("注册失败");
        alert.setHeaderText(null);
        alert.setContentText("注册失败，请检查信息或稍后重试！");
        alert.showAndWait();
    }

    // 注销成功提示
    public static void showLogoutSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("注销成功");
        alert.setHeaderText(null);
        alert.setContentText("您已成功注销。");
        alert.showAndWait();
    }

    // 记账成功提示
    public static void showTransactionSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("记账成功");
        alert.setHeaderText(null);
        alert.setContentText("您的记账记录已成功保存！");
        alert.showAndWait();
    }

    // 记账失败提示
    public static void showTransactionFailedAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("记账失败");
        alert.setHeaderText(null);
        alert.setContentText("记账失败，请检查信息或稍后重试！");
        alert.showAndWait();
    }

    // 通用信息提示
    public static void showInfoAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 通用错误提示
    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
