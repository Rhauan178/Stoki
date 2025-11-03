package com.seu.restaurante;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Tooltip;

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
    @FXML private TableColumn<Pedido, String> colunaFuncionario;
    @FXML private TableColumn<Pedido, BigDecimal> colunaValorTotal;
    @FXML private TableColumn<Pedido, LocalDateTime> colunaDataHora;
    @FXML private Label labelTotalPedidos;
    @FXML private Label labelFaturamentoTotal;
    @FXML private Label labelTituloRelatorio;
    @FXML private Button botaoArquivar;
    @FXML private Button botaoAlternarVisao;
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private ComboBox<Usuario> comboFuncionario;
    @FXML private Button botaoFiltrar;
    @FXML private Button botaoLimparFiltro;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private boolean mostrandoArquivados = false;

    @FXML
    public void initialize() {
        Tooltip.install(botaoFiltrar, new Tooltip("Aplicar os filtros de data e funcionário selecionados"));
        Tooltip.install(botaoLimparFiltro, new Tooltip("Remover todos os filtros aplicados"));
        Tooltip.install(botaoArquivar, new Tooltip("Mover os pedidos pagos atuais para o arquivo"));
        Tooltip.install(botaoAlternarVisao, new Tooltip("Alternar a visualização entre vendas atuais e vendas arquivadas"));

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
        colunaPedidoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaMesaId.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colunaDataHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colunaValorTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValorTotal()));

        colunaFuncionario.setCellValueFactory(cellData -> {
            Pedido pedido = cellData.getValue();
            Usuario funcionario = usuarioDAO.buscarPorId(pedido.getIdFuncionario());
            if (funcionario != null) {
                return new SimpleStringProperty(funcionario.getNome());
            } else {
                return new SimpleStringProperty("ID " + pedido.getIdFuncionario() + " (Apagado)");
            }
        });

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
        List<Pedido> pedidos;
        if (mostrandoArquivados) {
            labelTituloRelatorio.setText("Relatório de Vendas Arquivadas");
            pedidos = pedidoDAO.carregarPedidosArquivados(dataInicio, dataFim, funcionarioId);
            botaoAlternarVisao.setText("Ver Vendas Atuais");
            botaoArquivar.setVisible(false);
        } else {
            labelTituloRelatorio.setText("Relatório de Vendas Atuais");
            pedidos = pedidoDAO.carregarPedidosPagos(dataInicio, dataFim, funcionarioId);
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
    private void arquivarVendas() {
        boolean confirmado = AlertaUtil.mostrarConfirmacao("Confirmar Arquivamento",
                "Tem a certeza que quer arquivar todas as vendas atuais exibidas?");
        if (confirmado) {
            pedidoDAO.arquivarPedidosPagos();
            carregarDadosDoRelatorio(null, null, null);
        }
    }

    @FXML
    private void alternarVisao() {
        mostrandoArquivados = !mostrandoArquivados;
        limparFiltro();
    }

    @FXML
    private void voltarParaMesas() {
        App.trocarDeTela("MesaView.fxml", "Controle de Mesas");
    }
}