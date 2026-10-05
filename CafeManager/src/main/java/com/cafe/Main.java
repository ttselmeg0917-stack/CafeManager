package com.cafe;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        try {
            DBConnection.init();
        } catch (Exception e) {
            error("MySQL-тэй холбогдож чадсангүй.\nMySQL ажиллаж байгаа эсэх, DBConnection.java дахь "
                    + "хэрэглэгчийн нэр, нууц үгийг шалгана уу.\n\n" + e.getMessage());
            Platform.exit();
            return;
        }
        showLogin();
        stage.show();
    }

    public static void showLogin() {
        setScene("login.fxml", 400, 520, "Cafe Manager - Нэвтрэх");
    }

    public static void showRegister() {
        setScene("register.fxml", 400, 520, "Cafe Manager - Бүртгүүлэх");
    }

    public static void showMain() {
        setScene("main.fxml", 1000, 680, "Cafe Manager");
    }

    private static void setScene(String fxml, double width, double height, String title) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource(fxml));
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String money(double amount) {
        return String.format("%,.0f₮", amount);
    }

    public static void error(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.setHeaderText("Алдаа");
        a.showAndWait();
    }

    public static void info(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
