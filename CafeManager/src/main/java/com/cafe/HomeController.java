package com.cafe;

import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class HomeController {
    @FXML private Label welcomeLabel;
    @FXML private Label productCountLabel;
    @FXML private Label orderCountLabel;
    @FXML private Label revenueLabel;
    @FXML private TableView<Order> recentTable;

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @FXML
    private void initialize() {
        welcomeLabel.setText("Сайн байна уу, " + Session.fullName + "!");
        refresh();
    }

    public void refresh() {
        try {
            productCountLabel.setText(String.valueOf(productDAO.getAll().size()));
            double[] today = orderDAO.getTodaySales();
            orderCountLabel.setText(String.valueOf((int) today[0]));
            revenueLabel.setText(Main.money(today[1]));
            List<Order> orders = orderDAO.getAll();
            recentTable.setItems(FXCollections.observableArrayList(
                    orders.subList(0, Math.min(8, orders.size()))));
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }
}
