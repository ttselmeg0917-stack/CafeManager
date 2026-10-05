package com.cafe;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label messageLabel;

    private final UserDAO dao = new UserDAO();

    @FXML
    private void onRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Бүх талбарыг бөглөнө үү.");
            return;
        }
        if (password.length() < 4) {
            messageLabel.setText("Нууц үг дор хаяж 4 тэмдэгт байна.");
            return;
        }
        if (!password.equals(confirmField.getText())) {
            messageLabel.setText("Нууц үг давталттайгаа таарахгүй байна.");
            return;
        }
        try {
            dao.add(username, password, fullName);
            Main.info("Бүртгэл амжилттай. Одоо нэвтэрнэ үү.");
            Main.showLogin();
        } catch (SQLIntegrityConstraintViolationException e) {
            messageLabel.setText("Энэ хэрэглэгчийн нэр бүртгэлтэй байна.");
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        Main.showLogin();
    }
}
