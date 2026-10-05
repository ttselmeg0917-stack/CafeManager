package com.cafe;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class OrderController {
    private static final String ALL = "Бүгд";
    private static final String CASH = "Бэлэн";

    @FXML private ComboBox<String> categoryBox;
    @FXML private FlowPane productPane;
    @FXML private TableView<OrderItem> cartTable;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> paymentBox;
    @FXML private TextField paidField;
    @FXML private Label changeLabel;

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ObservableList<OrderItem> cart = FXCollections.observableArrayList();
    private List<Product> products = new ArrayList<>();

    @FXML
    private void initialize() {
        cartTable.setItems(cart);
        paymentBox.setItems(FXCollections.observableArrayList(CASH, "Карт", "QPay"));
        paymentBox.setValue(CASH);
        paymentBox.valueProperty().addListener((obs, old, value) -> {
            paidField.setDisable(!CASH.equals(value));
            updateTotal();
        });
        paidField.textProperty().addListener((obs, old, value) -> updateChange());
        categoryBox.valueProperty().addListener((obs, old, value) -> showProducts());
        refresh();
        updateTotal();
    }

    public void refresh() {
        try {
            products = productDAO.getAll();
        } catch (SQLException e) {
            Main.error(e.getMessage());
            return;
        }
        String selected = categoryBox.getValue();
        List<String> categories = new ArrayList<>();
        categories.add(ALL);
        for (Product p : products) {
            if (!categories.contains(p.getCategory())) categories.add(p.getCategory());
        }
        categoryBox.setItems(FXCollections.observableArrayList(categories));
        categoryBox.setValue(categories.contains(selected) ? selected : ALL);
        showProducts();
    }

    private void showProducts() {
        String category = categoryBox.getValue();
        productPane.getChildren().clear();
        for (Product p : products) {
            if (category != null && !ALL.equals(category) && !category.equals(p.getCategory())) continue;
            Button card = new Button(p.getName() + "\n" + p.getPriceText());
            card.getStyleClass().add("product-card");
            card.setOnAction(e -> addToCart(p));
            productPane.getChildren().add(card);
        }
    }

    private void addToCart(Product p) {
        for (OrderItem item : cart) {
            if (item.getProductId() == p.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                cartTable.refresh();
                updateTotal();
                return;
            }
        }
        cart.add(new OrderItem(p.getId(), p.getName(), p.getPrice(), 1));
        updateTotal();
    }

    @FXML
    private void onPlus() {
        changeQuantity(1);
    }

    @FXML
    private void onMinus() {
        changeQuantity(-1);
    }

    private void changeQuantity(int delta) {
        OrderItem item = cartTable.getSelectionModel().getSelectedItem();
        if (item == null) {
            Main.error("Жагсаалтаас мөр сонгоно уу.");
            return;
        }
        int quantity = item.getQuantity() + delta;
        if (quantity <= 0) cart.remove(item);
        else item.setQuantity(quantity);
        cartTable.refresh();
        updateTotal();
    }

    @FXML
    private void onRemove() {
        OrderItem item = cartTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            cart.remove(item);
            updateTotal();
        }
    }

    @FXML
    private void onClear() {
        cart.clear();
        paidField.clear();
        updateTotal();
    }

    @FXML
    private void onSave() {
        if (cart.isEmpty()) {
            Main.error("Захиалгын жагсаалт хоосон байна.");
            return;
        }
        double total = total();
        String method = paymentBox.getValue();
        double paid = total;
        if (CASH.equals(method) && !paidField.getText().isBlank()) {
            Double value = parsePaid();
            if (value == null) {
                Main.error("Төлсөн дүн тоо байх ёстой.");
                return;
            }
            if (value < total) {
                Main.error("Төлсөн дүн нийт төлбөрөөс бага байна.");
                return;
            }
            paid = value;
        }
        try {
            int id = orderDAO.save(cart, total, method, paid);
            Order order = new Order(id, LocalDateTime.now(), total, method, paid, Session.fullName);
            List<OrderItem> items = new ArrayList<>(cart);
            onClear();
            Receipt.show(order, items);
        } catch (SQLException e) {
            Main.error("Захиалга хадгалагдсангүй: " + e.getMessage());
        }
    }

    private Double parsePaid() {
        try {
            return Double.parseDouble(paidField.getText().trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double total() {
        double sum = 0;
        for (OrderItem item : cart) sum += item.getSubtotal();
        return sum;
    }

    private void updateTotal() {
        totalLabel.setText(Main.money(total()));
        updateChange();
    }

    private void updateChange() {
        Double paid = parsePaid();
        if (!CASH.equals(paymentBox.getValue()) || paid == null || paid < total()) {
            changeLabel.setText(Main.money(0));
        } else {
            changeLabel.setText(Main.money(paid - total()));
        }
    }
}
