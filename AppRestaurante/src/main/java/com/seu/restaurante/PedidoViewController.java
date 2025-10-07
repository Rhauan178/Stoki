package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PedidoViewController {

    @FXML private Label labelTitulo;
    @FXML private Label labelValorTotal;
    @FXML private ListView<Map.Entry<ItemCardapio, Integer>> listaItensPedido;
    @FXML private ComboBox<ItemCardapio> comboCardapio;
    @FXML private Button botaoAdicionar;
    @FXML private Button botaoRemover;
    @FXML private Button botaoFecharConta;
    @FXML private TextArea textAreaObservacao;
    @FXML private Button botaoSalvarObservacao;

    private Mesa mesaAtual;
    private final ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final FichaTecnicaDAO fichaTecnicaDAO = new FichaTecnicaDAO();
    private final IngredienteDAO ingredienteDAO = new IngredienteDAO();
    private Runnable refreshCallback;

    @FXML
    public void initialize() {
        botaoAdicionar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        botaoRemover.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS));
        botaoFecharConta.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
        botaoSalvarObservacao.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));

        itemCardapioDAO.criarTabela();
        carregarCardapioDoBanco();
        configurarCellFactory();
    }

    private void configurarCellFactory() {
        listaItensPedido.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Map.Entry<ItemCardapio, Integer> entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                } else {
                    ItemCardapio item = entry.getKey();
                    Integer quantidade = entry.getValue();
                    setText(String.format("%s (x%d) - R$ %.2f",
                            item.getNome(),
                            quantidade,
                            item.getPreco().multiply(new BigDecimal(quantidade))));
                }
            }
        });
    }

    private void carregarCardapioDoBanco() {
        List<ItemCardapio> cardapio = itemCardapioDAO.listarTodos();
        comboCardapio.getItems().clear();
        comboCardapio.getItems().addAll(cardapio);
    }

    public void carregarDadosDaMesa(Mesa mesa) {
        this.mesaAtual = mesa;
        labelTitulo.setText("Detalhes do Pedido - Mesa " + mesa.getNumero());
        if (mesa.getPedidoAtual() != null) {
            textAreaObservacao.setText(mesa.getPedidoAtual().getObservacao());
        }
        atualizarVisualizacaoPedido();
    }

    public void setOnCloseCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void fecharConta() {
        Pedido pedido = mesaAtual.getPedidoAtual();
        if (pedido != null) {
            if (pedido.getId() == 0) {
                pedidoDAO.salvarPedido(pedido);
            }

            System.out.println("A dar baixa no estoque para o Pedido ID: " + pedido.getId());
            for (Map.Entry<ItemCardapio, Integer> itemDoPedido : pedido.getItens().entrySet()) {
                ItemCardapio itemCardapio = itemDoPedido.getKey();
                Integer quantidadeVendida = itemDoPedido.getValue();

                List<FichaTecnicaItem> receita = fichaTecnicaDAO.buscarPorItemId(itemCardapio.getId());

                for (FichaTecnicaItem itemDaReceita : receita) {
                    Ingrediente ingrediente = itemDaReceita.getIngrediente();
                    double totalADeduzir = itemDaReceita.getQuantidadeUsada() * quantidadeVendida;

                    System.out.println("  -> Baixa de " + totalADeduzir + " " + ingrediente.getUnidadeMedida() + " de " + ingrediente.getNome());
                    ingredienteDAO.darBaixaEstoque(ingrediente.getId(), totalADeduzir);
                }
            }

            pedidoDAO.marcarComoPago(pedido.getId());
            mesaAtual.setStatus(StatusMesa.LIVRE);
            mesaAtual.setPedidoAtual(null);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            Stage stage = (Stage) botaoFecharConta.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void salvarObservacao() {
        Pedido pedido = mesaAtual.getPedidoAtual();
        if (pedido != null) {
            String observacao = textAreaObservacao.getText();
            pedido.setObservacao(observacao);
            if (pedido.getId() == 0) {
                pedidoDAO.salvarPedido(pedido);
            } else {
                pedidoDAO.atualizarObservacao(pedido.getId(), observacao);
            }
            AlertaUtil.mostrarInformacao("Sucesso", "Observação salva com sucesso!");
        } else {
            AlertaUtil.mostrarErro("Erro", "Não há um pedido ativo para esta mesa.");
        }
    }

    @FXML
    private void adicionarItemAoPedido() {
        ItemCardapio itemSelecionado = comboCardapio.getSelectionModel().getSelectedItem();
        if (itemSelecionado != null && mesaAtual != null && mesaAtual.getPedidoAtual() != null) {
            mesaAtual.getPedidoAtual().adicionarItem(itemSelecionado);
            atualizarVisualizacaoPedido();
        }
    }

    @FXML
    private void removerItemDoPedido() {
        Map.Entry<ItemCardapio, Integer> itemSelecionadoEntry = listaItensPedido.getSelectionModel().getSelectedItem();
        if (itemSelecionadoEntry != null && mesaAtual != null && mesaAtual.getPedidoAtual() != null) {
            ItemCardapio itemParaRemover = itemSelecionadoEntry.getKey();
            mesaAtual.getPedidoAtual().removerItem(itemParaRemover);
            atualizarVisualizacaoPedido();
        }
    }

    private void atualizarVisualizacaoPedido() {
        if (mesaAtual == null) return;
        Pedido pedido = mesaAtual.getPedidoAtual();
        listaItensPedido.getItems().clear();
        if (pedido != null) {
            listaItensPedido.getItems().setAll(new ArrayList<>(pedido.getItens().entrySet()));
            BigDecimal total = pedido.getValorTotal();
            labelValorTotal.setText(String.format("Valor Total: R$ %.2f", total));
        } else {
            labelValorTotal.setText("Valor Total: R$ 0.00");
        }
    }
}