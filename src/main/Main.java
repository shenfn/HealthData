package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage; // 定义全局 primaryStage

    @Override
    public void start(Stage primaryStage) throws IOException {
        Main.primaryStage = primaryStage; // 设置全局 primaryStage
        // 加载主界面布局文件
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();

        // 设置窗口标题
        primaryStage.setTitle("家庭管理系统");

        // 创建和设置场景
        primaryStage.setScene(new Scene(root, 850, 650));
        // 显示窗口
        primaryStage.show();
    }

    // 提供获取 primaryStage 的静态方法
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args); // 启动JavaFX应用
    }
}
