package com.cafe;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final UserDAO dao = new UserDAO();

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Нэр, нууц үгээ оруулна уу.");
            return;
        }
        try {
            if (dao.login(username, password)) {
                Main.showMain();
            } else {
                messageLabel.setText("Нэр эсвэл нууц үг буруу байна.");
                passwordField.clear();
            }
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        Main.showRegister();
    }
}
