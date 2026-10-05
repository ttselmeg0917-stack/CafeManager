package com.cafe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class MainController {
    @FXML private TabPane tabs;
    @FXML private Label userLabel;
    @FXML private HomeController homeViewController;
    @FXML private OrderController orderViewController;
    @FXML private ProductController productViewController;
    @FXML private HistoryController historyViewController;

    @FXML
    private void initialize() {
        userLabel.setText("Ажилтан: " + Session.fullName);
        tabs.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            switch (newIndex.intValue()) {
                case 0 -> homeViewController.refresh();
                case 1 -> orderViewController.refresh();
                case 2 -> productViewController.refresh();
                case 3 -> historyViewController.refresh();
                default -> { }
            }
        });
    }

    @FXML
    private void onLogout() {
        Session.clear();
        Main.showLogin();
    }
}
