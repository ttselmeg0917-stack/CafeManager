package com.cafe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Receipt {
    private static final String LINE = "----------------------------------------\n";

    public static String build(Order order, List<OrderItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("              CAFE MANAGER\n");
        sb.append("            Төлбөрийн баримт\n");
        sb.append(LINE);
        sb.append("Захиалга №: ").append(order.getId()).append('\n');
        sb.append("Огноо:      ").append(order.getDateText()).append('\n');
        sb.append("Ажилтан:    ").append(order.getCashier()).append('\n');
        sb.append(LINE);
        sb.append(String.format("%-20s %4s %13s%n", "Бүтээгдэхүүн", "Тоо", "Дүн"));
        for (OrderItem item : items) {
            sb.append(String.format("%-20s %4d %13s%n", shorten(item.getName()),
                    item.getQuantity(), item.getSubtotalText()));
        }
        sb.append(LINE);
        sb.append(String.format("%-20s %18s%n", "НИЙТ ТӨЛБӨР", Main.money(order.getTotal())));
        sb.append(String.format("%-20s %18s%n", "Төлбөрийн хэлбэр", order.getPaymentMethod()));
        sb.append(String.format("%-20s %18s%n", "Төлсөн", Main.money(order.getPaid())));
        sb.append(String.format("%-20s %18s%n", "Хариулт", Main.money(order.getChange())));
        sb.append(LINE);
        sb.append("         Манайхаар үйлчлүүлсэнд\n              баярлалаа!\n");
        return sb.toString();
    }

    private static String shorten(String name) {
        return name.length() <= 20 ? name : name.substring(0, 19) + ".";
    }

    public static void show(Order order, List<OrderItem> items) {
        String text = build(order, items);
        Stage dialog = new Stage();
        dialog.initOwner(Main.stage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Баримт №" + order.getId());

        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setFont(Font.font("Consolas", 14));
        area.setPrefSize(420, 420);

        Button print = new Button("Хэвлэх");
        print.setOnAction(e -> print(text, dialog));
        Button save = new Button("Файлд хадгалах");
        save.setOnAction(e -> save(text, order.getId(), dialog));
        Button close = new Button("Хаах");
        close.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, print, save, close);
        VBox root = new VBox(10, area, buttons);
        root.setPadding(new Insets(14));
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    private static void print(String text, Stage owner) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            Main.error("Хэвлэгч олдсонгүй.");
            return;
        }
        if (job.showPrintDialog(owner)) {
            Text node = new Text(text);
            node.setFont(Font.font("Consolas", 11));
            if (job.printPage(node)) job.endJob();
            else Main.error("Хэвлэж чадсангүй.");
        }
    }

    private static void save(String text, int orderId, Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Баримт хадгалах");
        chooser.setInitialFileName("barimt_" + orderId + ".txt");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текст файл", "*.txt"));
        File file = chooser.showSaveDialog(owner);
        if (file == null) return;
        try {
            Files.writeString(file.toPath(), text);
            Main.info("Баримт хадгалагдлаа:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            Main.error("Файл хадгалагдсангүй: " + e.getMessage());
        }
    }
}
