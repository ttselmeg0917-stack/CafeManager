package com.cafe;

public class OrderItem {
    private final int productId;
    private final String name;
    private final double unitPrice;
    private int quantity;

    public OrderItem(int productId, String name, double unitPrice, int quantity) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return unitPrice * quantity; }

    public String getUnitPriceText() { return Main.money(unitPrice); }
    public String getSubtotalText() { return Main.money(getSubtotal()); }
}
