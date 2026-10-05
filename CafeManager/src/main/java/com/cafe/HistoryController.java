package com.cafe;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/** Захиалгын түүх ба өдрийн борлуулалт. */
public class HistoryController {
    @FXML private Label countLabel;
    @FXML private Label revenueLabel;
    @FXML private TableView<Order> orderTable;
    @FXML private TableView<OrderItem> itemTable;

    private final OrderDAO dao = new OrderDAO();

    @FXML
    private void initialize() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, old, order) -> {
            try {
                itemTable.setItems(order == null
                        ? FXCollections.observableArrayList()
                        : FXCollections.observableArrayList(dao.getItems(order.getId())));
            } catch (SQLException e) {
                Main.error(e.getMessage());
            }
        });
        refresh();
    }

    @FXML
    private void onReceipt() {
        Order order = orderTable.getSelectionModel().getSelectedItem();
        if (order == null) {
            Main.error("Захиалга сонгоно уу.");
            return;
        }
        try {
            Receipt.show(order, dao.getItems(order.getId()));
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    public void refresh() {
        try {
            orderTable.setItems(FXCollections.observableArrayList(dao.getAll()));
            double[] today = dao.getTodaySales();
            countLabel.setText(String.valueOf((int) today[0]));
            revenueLabel.setText(Main.money(today[1]));
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }
}
