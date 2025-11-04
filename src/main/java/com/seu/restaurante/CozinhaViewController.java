package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class CozinhaViewController {

    @FXML private Button botaoLogout;
    @FXML private ListView<Pedido> listaPendentes;
    @FXML private Button botaoIniciarPreparo;
    @FXML private ListView<Pedido> listaEmPreparo;
    @FXML private Button botaoPedidoPronto;
    @FXML private Label labelDetalhePedido;
    @FXML private ListView<String> listaItensPedido;
    @FXML private TextArea textAreaObservacaoCozinha;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ObservableList<Pedido> pedidosPendentes = FXCollections.observableArrayList();
    private final ObservableList<Pedido> pedidosEmPreparo = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        botaoLogout.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_OUT));
        botaoIniciarPreparo.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLAY));
        botaoPedidoPronto.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));

        configurarCellFactory(listaPendentes);
        configurarCellFactory(listaEmPreparo);

        listaPendentes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, novo) -> mostrarDetalhes(novo));
        listaEmPreparo.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, novo) -> mostrarDetalhes(novo));

        listaPendentes.setItems(pedidosPendentes);
        listaEmPreparo.setItems(pedidosEmPreparo);

        iniciarAtualizacaoAutomatica();
    }

    private void configurarCellFactory(ListView<Pedido> listView) {
        listView.setCellFactory(param -> new ListCell<Pedido>() {
            @Override
            protected void updateItem(Pedido pedido, boolean empty) {
                super.updateItem(pedido, empty);
                setText(empty || pedido == null ? null : "Pedido #" + pedido.getId() + " (Mesa " + pedido.getIdMesa() + ")");
            }
        });
    }

    private void iniciarAtualizacaoAutomatica() {
        PauseTransition pausa = new PauseTransition(Duration.seconds(10));
        pausa.setOnFinished(event -> {
            System.out.println("Atualizando painel da cozinha...");
            carregarPedidos();
            pausa.playFromStart();
        });
        pausa.play();
        carregarPedidos();
    }

    private void carregarPedidos() {
        Integer idSelecionadoPendente = listaPendentes.getSelectionModel().getSelectedItem() != null ?
                listaPendentes.getSelectionModel().getSelectedItem().getId() : null;
        Integer idSelecionadoEmPreparo = listaEmPreparo.getSelectionModel().getSelectedItem() != null ?
                listaEmPreparo.getSelectionModel().getSelectedItem().getId() : null;

        List<Pedido> pedidos = pedidoDAO.carregarPedidosParaCozinha();

        pedidosPendentes.clear();
        pedidosEmPreparo.clear();
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.ENVIADO) {
                pedidosPendentes.add(p);
            } else if (p.getStatus() == StatusPedido.EM_PREPARO) {
                pedidosEmPreparo.add(p);
            }
        }

        if (idSelecionadoPendente != null) {
            pedidosPendentes.stream()
                    .filter(p -> p.getId() == idSelecionadoPendente)
                    .findFirst()
                    .ifPresent(p -> listaPendentes.getSelectionModel().select(p));
        }
        if (idSelecionadoEmPreparo != null) {
            pedidosEmPreparo.stream()
                    .filter(p -> p.getId() == idSelecionadoEmPreparo)
                    .findFirst()
                    .ifPresent(p -> listaEmPreparo.getSelectionModel().select(p));
        }
    }

    private void mostrarDetalhes(Pedido pedido) {
        if (pedido == null) {
            limparDetalhes();
            return;
        }

        if (listaPendentes.getSelectionModel().getSelectedItem() != pedido) {
            listaPendentes.getSelectionModel().clearSelection();
        }
        if (listaEmPreparo.getSelectionModel().getSelectedItem() != pedido) {
            listaEmPreparo.getSelectionModel().clearSelection();
        }

        labelDetalhePedido.setText("Detalhes do Pedido #" + pedido.getId());
        textAreaObservacaoCozinha.setText(pedido.getObservacao());

        listaItensPedido.getItems().clear();
        for (Map.Entry<ItemCardapio, Integer> entry : pedido.getItens().entrySet()) {
            String nomeItem = entry.getKey().getNome();
            int quantidade = entry.getValue();
            listaItensPedido.getItems().add(String.format("%s (x%d)", nomeItem, quantidade));
        }
    }

    @FXML
    private void iniciarPreparo() {
        Pedido selecionado = listaPendentes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            selecionado.setStatus(StatusPedido.EM_PREPARO);
            pedidoDAO.atualizarStatus(selecionado.getId(), StatusPedido.EM_PREPARO);
            carregarPedidos();
        } else {
            AlertaUtil.mostrarErro("Erro", "Nenhum pedido pendente selecionado.");
        }
    }

    @FXML
    private void pedidoPronto() {
        Pedido selecionado = listaEmPreparo.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            selecionado.setStatus(StatusPedido.PRONTO);
            pedidoDAO.atualizarStatus(selecionado.getId(), StatusPedido.PRONTO);
            carregarPedidos();
            limparDetalhes();
        } else {
            AlertaUtil.mostrarErro("Erro", "Nenhum pedido em preparo selecionado.");
        }
    }

    private void limparDetalhes() {
        listaEmPreparo.getSelectionModel().clearSelection();
        listaPendentes.getSelectionModel().clearSelection();
        labelDetalhePedido.setText("Detalhes do Pedido:");
        listaItensPedido.getItems().clear();
        textAreaObservacaoCozinha.clear();
    }

    @FXML
    private void fazerLogout() {
        App.setUsuarioLogado(null);
        App.trocarDeTela("LoginView.fxml", "AppRestaurante - Login");
    }
}