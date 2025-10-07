package com.seu.restaurante;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RelatorioViewController {

    @FXML private TableView<Pedido> tabelaPedidosPagos;
    @FXML private TableColumn<Pedido, Integer> colunaPedidoId;
    @FXML private TableColumn<Pedido, Integer> colunaMesaId;
    @FXML private TableColumn<Pedido, BigDecimal> colunaValorTotal;
    @FXML private TableColumn<Pedido, LocalDateTime> colunaDataHora;
    @FXML private Label labelTotalPedidos;
    @FXML private Label labelFaturamentoTotal;
    @FXML private Label labelTituloRelatorio;
    @FXML private Button botaoArquivar;
    @FXML private Button botaoAlternarVisao;
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private Button botaoFiltrar;
    @FXML private Button botaoLimparFiltro;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private boolean mostrandoArquivados = false;

    @FXML
    public void initialize() {
        configurarTabela();
        carregarDadosDoRelatorio(null, null); // Carrega os dados iniciais sem filtro
    }

    private void configurarTabela() {
        colunaPedidoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaMesaId.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colunaDataHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));

        // Abordagem explícita para a coluna de valor total
        colunaValorTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValorTotal()));

        // Formata a coluna de Valor Total como moeda
        colunaValorTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("R$ %.2f", item));
            }
        });

        // Formata a coluna de Data e Hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        colunaDataHora.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });
    }

    private void carregarDadosDoRelatorio(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos;
        if (mostrandoArquivados) {
            labelTituloRelatorio.setText("Relatório de Vendas Arquivadas");
            pedidos = pedidoDAO.carregarPedidosArquivados(dataInicio, dataFim);
            botaoAlternarVisao.setText("Ver Vendas Atuais");
            botaoArquivar.setVisible(false);
        } else {
            labelTituloRelatorio.setText("Relatório de Vendas Atuais");
            pedidos = pedidoDAO.carregarPedidosPagos(dataInicio, dataFim);
            botaoAlternarVisao.setText("Ver Vendas Arquivadas");
            botaoArquivar.setVisible(true);
        }

        tabelaPedidosPagos.setItems(FXCollections.observableArrayList(pedidos));

        BigDecimal faturamentoTotal = pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        labelTotalPedidos.setText("Total de Pedidos: " + pedidos.size());
        labelFaturamentoTotal.setText(String.format("Faturamento Total: R$ %.2f", faturamentoTotal));
    }

    @FXML
    private void aplicarFiltro() {
        LocalDate inicio = datePickerInicio.getValue();
        LocalDate fim = datePickerFim.getValue();

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Data");
            alert.setHeaderText("A data de início não pode ser posterior à data de fim.");
            alert.showAndWait();
            return;
        }
        carregarDadosDoRelatorio(inicio, fim);
    }

    @FXML
    private void limparFiltro() {
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        carregarDadosDoRelatorio(null, null);
    }

    @FXML
    private void arquivarVendas() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Arquivamento");
        alert.setHeaderText("Tem a certeza que quer arquivar todas as vendas atuais?");
        alert.setContentText("Esta ação irá mover os pedidos 'Pagos' para o arquivo.");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            pedidoDAO.arquivarPedidosPagos();
            carregarDadosDoRelatorio(null, null); // Atualiza a tela
        }
    }

    @FXML
    private void alternarVisao() {
        mostrandoArquivados = !mostrandoArquivados; // Inverte o modo de visualização
        limparFiltro(); // Limpa os filtros ao alternar a visão
    }
}