package com.seu.restaurante;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private GridPane gridMesas;

    private static final int NUM_MESAS = 12;
    private List<Button> mesas = new ArrayList<>();

    @FXML
    public void initialize() {
        for (int i = 0; i < NUM_MESAS; i++) {
            Button mesa = new Button("Mesa " + (i + 1));
            mesa.setPrefSize(100, 100);
            mesa.setStyle("-fx-background-color: #A9A9A9; -fx-text-fill: white; -fx-font-weight: bold;");

            int rowIndex = i / 4;
            int colIndex = i % 4;

            gridMesas.add(mesa, colIndex, rowIndex);
            mesas.add(mesa);
        }
    }
}