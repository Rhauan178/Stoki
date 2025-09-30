package com.seu.restaurante;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PedidoViewController {

    @FXML private Label labelTitulo;
    @FXML private Label labelValorTotal;
    @FXML private ListView<Map.Entry<ItemCardapio, Integer>> listaItensPedido;
    @FXML private ComboBox<ItemCardapio> comboCardapio;
    @FXML private Button botaoAdicionar;
    @FXML private Button botaoRemover;
    @FXML private Button botaoFecharConta;

    private Mesa mesaAtual;
    private final ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private Runnable refreshCallback;

    @FXML
    public void initialize() {
        itemCardapioDAO.criarTabela();
        carregarCardapioDoBanco();
        configurarCellFactory();
    }

    // Método para configurar como a lista é exibida
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

        System.out.println("\n--- [DEBUG] A EXIBIR JANELA DE DETALHES PARA MESA " + mesa.getNumero() + " ---");
        if (mesa.getPedidoAtual() != null) {
            System.out.println("[DEBUG] O pedido contém " + mesa.getPedidoAtual().getItens().size() + " tipos de itens.");
            mesa.getPedidoAtual().getItens().forEach((item, qtd) ->
                    System.out.println("  -> A exibir: " + item.getNome() + ", Qtd: " + qtd)
            );
        } else {
            System.out.println("[DEBUG] A mesa não tem pedido atual.");
        }
        System.out.println("--- [DEBUG] FIM DA ANÁLISE DA JANELA DE DETALHES ---\n");

        atualizarVisualizacaoPedido();
    }

    public void setOnCloseCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void fecharConta() {
        Pedido pedido = mesaAtual.getPedidoAtual();
        if (pedido != null) {
            // --- INÍCIO DA CORREÇÃO ---
            // Se o pedido tem ID 0, significa que é novo e nunca foi salvo na base de dados.
            // Portanto, precisamos de o salvar primeiro para que ele obtenha um ID real.
            if (pedido.getId() == 0) {
                System.out.println("[DEBUG] Pedido novo, a salvá-lo pela primeira vez antes de o fechar.");
                pedidoDAO.salvarPedido(pedido);
            }
            // --- FIM DA CORREÇÃO ---

            // Agora, com a certeza de que o pedido existe na base de dados e tem o ID correto,
            // podemos atualizar o seu status para PAGO.
            pedidoDAO.atualizarStatus(pedido.getId(), StatusPedido.PAGO);

            // O resto da lógica para liberar a mesa e fechar a janela
            mesaAtual.setStatus(StatusMesa.LIVRE);
            mesaAtual.setPedidoAtual(null);

            System.out.println("Conta da Mesa " + mesaAtual.getNumero() + " fechada.");

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            Stage stage = (Stage) botaoFecharConta.getScene().getWindow();
            stage.close();
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