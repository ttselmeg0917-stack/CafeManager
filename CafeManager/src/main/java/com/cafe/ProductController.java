package com.cafe;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductController {
    @FXML private TableView<Product> table;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField priceField;

    private final ProductDAO dao = new ProductDAO();

    @FXML
    private void initialize() {
        categoryBox.setItems(FXCollections.observableArrayList("Кофе", "Цай", "Ундаа", "Амттан"));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) {
                nameField.setText(p.getName());
                categoryBox.setValue(p.getCategory());
                priceField.setText(String.format("%.0f", p.getPrice()));
            }
        });
        refresh();
    }

    public void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(dao.getAll()));
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        Double price = readForm();
        if (price == null) return;
        try {
            dao.add(nameField.getText().trim(), categoryBox.getValue().trim(), price);
            onClear();
            refresh();
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Product p = table.getSelectionModel().getSelectedItem();
        if (p == null) {
            Main.error("Засах бүтээгдэхүүнээ жагсаалтаас сонгоно уу.");
            return;
        }
        Double price = readForm();
        if (price == null) return;
        try {
            p.setName(nameField.getText().trim());
            p.setCategory(categoryBox.getValue().trim());
            p.setPrice(price);
            dao.update(p);
            onClear();
            refresh();
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Product p = table.getSelectionModel().getSelectedItem();
        if (p == null) {
            Main.error("Устгах бүтээгдэхүүнээ жагсаалтаас сонгоно уу.");
            return;
        }
        try {
            dao.delete(p.getId());
            onClear();
            refresh();
        } catch (SQLIntegrityConstraintViolationException e) {
            Main.error("Энэ бүтээгдэхүүн өмнөх захиалгад орсон тул устгах боломжгүй.");
        } catch (SQLException e) {
            Main.error(e.getMessage());
        }
    }

    @FXML
    private void onClear() {
        nameField.clear();
        priceField.clear();
        categoryBox.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private Double readForm() {
        String category = categoryBox.getValue();
        if (nameField.getText().isBlank() || category == null || category.isBlank()) {
            Main.error("Нэр болон төрлийг оруулна уу.");
            return null;
        }
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) throw new NumberFormatException();
            return price;
        } catch (NumberFormatException e) {
            Main.error("Үнэ эерэг тоо байх ёстой.");
            return null;
        }
    }
}
