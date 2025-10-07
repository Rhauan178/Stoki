module com.seu.restaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;


    opens com.seu.restaurante to javafx.fxml;
    exports com.seu.restaurante;
}