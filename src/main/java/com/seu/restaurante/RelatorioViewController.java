package com.seu.restaurante;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioViewController {

    @FXML private TableView<Conta> tabelaContas;
    @FXML private TableColumn<Conta, Integer> colunaContaId;
    @FXML private TableColumn<Conta, Integer> colunaMesaId;
    @FXML private TableColumn<Conta, String> colunaFuncionario;
    @FXML private TableColumn<Conta, BigDecimal> colunaValorTotal;
    @FXML private TableColumn<Conta, String> colunaMetodoPagamento;
    @FXML private TableColumn<Conta, LocalDateTime> colunaDataHora;

    @FXML private Label labelTotalPedidos;
    @FXML private Label labelFaturamentoTotal;
    @FXML private Label labelTituloRelatorio;

    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private ComboBox<Usuario> comboFuncionario;

    private final ContaDAO contaDAO = new ContaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        configurarTabela();
        carregarFuncionarios();
        carregarDadosDoRelatorio(null, null, null);
    }

    private void carregarFuncionarios() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        comboFuncionario.getItems().add(null);
        comboFuncionario.getItems().addAll(usuarios);
    }

    private void configurarTabela() {
        colunaContaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaMesaId.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colunaDataHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colunaFuncionario.setCellValueFactory(cellData -> {
            Usuario f = usuarioDAO.buscarPorId(cellData.getValue().getIdFuncionario());
            return new SimpleStringProperty(f != null ? f.getNome() : "Desconhecido");
        });

        // Coluna do Método de Pagamento
        colunaMetodoPagamento.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMetodoPagamento().toString())
        );

        // Formatadores (código existente)
        colunaValorTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("R$ %.2f", item));
            }
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        colunaDataHora.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });
    }

    private void carregarDadosDoRelatorio(LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        List<Conta> contas = contaDAO.carregarContasPagas(dataInicio, dataFim, funcionarioId);

        tabelaContas.setItems(FXCollections.observableArrayList(contas));

        BigDecimal faturamentoTotal = contas.stream()
                .map(Conta::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        labelTotalPedidos.setText("Total de Contas Pagas: " + contas.size());
        labelFaturamentoTotal.setText(String.format("Faturamento Total: R$ %.2f", faturamentoTotal));
    }

    @FXML
    private void aplicarFiltro() {
        LocalDate inicio = datePickerInicio.getValue();
        LocalDate fim = datePickerFim.getValue();
        Usuario funcionario = comboFuncionario.getValue();
        Integer funcionarioId = (funcionario != null) ? funcionario.getId() : null;

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            AlertaUtil.mostrarErro("Erro de Data", "A data de início não pode ser posterior à data de fim.");
            return;
        }
        carregarDadosDoRelatorio(inicio, fim, funcionarioId);
    }

    @FXML
    private void limparFiltro() {
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        comboFuncionario.setValue(null);
        carregarDadosDoRelatorio(null, null, null);
    }

    @FXML
    private void voltarParaMesas() {
        App.trocarDeTela("MesaView.fxml", "Controle de Mesas");
    }
}