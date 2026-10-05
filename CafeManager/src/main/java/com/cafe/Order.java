package com.cafe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final int id;
    private final LocalDateTime date;
    private final double total;
    private final String paymentMethod;
    private final double paid;
    private final String cashier;

    public Order(int id, LocalDateTime date, double total, String paymentMethod, double paid, String cashier) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.paid = paid;
        this.cashier = cashier;
    }

    public int getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public double getTotal() { return total; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getPaid() { return paid; }
    public double getChange() { return paid - total; }
    public String getCashier() { return cashier; }
    public String getDateText() { return date.format(FMT); }
    public String getTotalText() { return Main.money(total); }
}
