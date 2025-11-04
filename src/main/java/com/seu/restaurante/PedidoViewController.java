package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PedidoViewController {

    @FXML private Label labelTitulo;
    @FXML private ListView<Map.Entry<ItemCardapio, Integer>> listaItensPedido;
    @FXML private ComboBox<ItemCardapio> comboCardapio;
    @FXML private Button botaoAdicionar;
    @FXML private Button botaoRemover;
    @FXML private TextArea textAreaObservacao;
    @FXML private Button botaoEnviarCozinha;
    @FXML private Button botaoCancelar;

    private Mesa mesaAtual;
    private int idFuncionarioLogado;
    private Pedido novoPedido;

    private final ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ObservableList<Map.Entry<ItemCardapio, Integer>> itensDoEnvio = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        botaoAdicionar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        botaoRemover.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS));
        botaoEnviarCozinha.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SEND));

        Tooltip.install(botaoAdicionar, new Tooltip("Adicionar item selecionado ao envio"));
        Tooltip.install(botaoRemover, new Tooltip("Remover item selecionado do envio"));
        Tooltip.install(botaoEnviarCozinha, new Tooltip("Enviar este conjunto de itens para a cozinha"));
        Tooltip.install(botaoCancelar, new Tooltip("Cancelar e fechar esta janela"));

        carregarCardapioDoBanco();
        configurarCellFactory();

        listaItensPedido.setItems(itensDoEnvio);
    }

    public void prepararNovoPedido(Mesa mesa, int idFuncionario) {
        this.mesaAtual = mesa;
        this.idFuncionarioLogado = idFuncionario;
        this.novoPedido = new Pedido(0, mesa.getNumero(), idFuncionarioLogado);

        labelTitulo.setText("Novo Envio para Mesa " + mesa.getNumero());
        atualizarVisualizacaoPedido();
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

    @FXML
    private void enviarParaCozinha() {
        if (novoPedido.getItens().isEmpty()) {
            AlertaUtil.mostrarErro("Envio Vazio", "Não pode enviar um pedido vazio. Adicione pelo menos um item.");
            return;
        }

        novoPedido.setObservacao(textAreaObservacao.getText());
        novoPedido.setStatus(StatusPedido.ENVIADO);
        pedidoDAO.salvarPedido(novoPedido);
        mesaAtual.adicionarPedido(novoPedido);

        AlertaUtil.mostrarInformacao("Sucesso", "Envio realizado para a cozinha!");
        fecharJanela();
    }

    @FXML
    private void cancelarEnvio() {
        fecharJanela();
    }

    @FXML
    private void adicionarItemAoPedido() {
        ItemCardapio itemSelecionado = comboCardapio.getSelectionModel().getSelectedItem();
        if (itemSelecionado != null) {
            novoPedido.adicionarItem(itemSelecionado);
            atualizarVisualizacaoPedido();
        }
    }

    @FXML
    private void removerItemDoPedido() {
        Map.Entry<ItemCardapio, Integer> itemSelecionadoEntry = listaItensPedido.getSelectionModel().getSelectedItem();
        if (itemSelecionadoEntry != null) {
            ItemCardapio itemParaRemover = itemSelecionadoEntry.getKey();
            novoPedido.removerItem(itemParaRemover);
            atualizarVisualizacaoPedido();
        }
    }

    private void atualizarVisualizacaoPedido() {
        itensDoEnvio.setAll(new ArrayList<>(novoPedido.getItens().entrySet()));
    }

    private void fecharJanela() {
        Stage stage = (Stage) botaoCancelar.getScene().getWindow();
        stage.close();
    }
}