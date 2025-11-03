module com.seu.restaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;
    requires com.google.zxing;
    requires java.desktop;
    requires com.google.zxing.javase;
    requires javafx.swing;


    opens com.seu.restaurante to javafx.fxml;
    exports com.seu.restaurante;
}