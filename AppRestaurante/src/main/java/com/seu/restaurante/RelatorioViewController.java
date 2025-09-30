package com.seu.restaurante;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioViewController {

    @FXML private TableView<Pedido> tabelaPedidosPagos;
    @FXML private TableColumn<Pedido, Integer> colunaPedidoId;
    @FXML private TableColumn<Pedido, Integer> colunaMesaId;
    @FXML private TableColumn<Pedido, BigDecimal> colunaValorTotal;
    @FXML private TableColumn<Pedido, LocalDateTime> colunaDataHora;
    @FXML private Label labelTotalPedidos;
    @FXML private Label labelFaturamentoTotal;

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @FXML
    public void initialize() {
        // 1. Configura as colunas da tabela para saberem de onde puxar os dados de um objeto Pedido
        colunaPedidoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaMesaId.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colunaDataHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));

        // Formata a data e hora para um formato mais legível
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        colunaDataHora.setCellFactory(column -> new TableCell<Pedido, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // 2. Carrega os dados da base de dados
        carregarDadosDoRelatorio();
    }

    private void carregarDadosDoRelatorio() {
        List<Pedido> pedidosPagos = pedidoDAO.carregarPedidosPagos();

        // 3. Preenche a tabela
        tabelaPedidosPagos.setItems(FXCollections.observableArrayList(pedidosPagos));

        // 4. Calcula e exibe os totais
        int totalPedidos = pedidosPagos.size();
        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        for (Pedido pedido : pedidosPagos) {
            faturamentoTotal = faturamentoTotal.add(pedido.getValorTotal());
        }

        labelTotalPedidos.setText("Total de Pedidos Pagos: " + totalPedidos);
        labelFaturamentoTotal.setText(String.format("Faturamento Total: R$ %.2f", faturamentoTotal));
    }
}