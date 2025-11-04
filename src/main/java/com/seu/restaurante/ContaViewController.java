package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ContaViewController {

    @FXML private Label labelTituloConta;
    @FXML private ListView<Map.Entry<ItemCardapio, Integer>> listaItensDaConta;
    @FXML private Label labelValorTotalConta;
    @FXML private Button botaoCancelar;
    @FXML private Button botaoPagar;

    private Mesa mesaAtual;
    private Runnable refreshCallback;
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ContaDAO contaDAO = new ContaDAO();
    private final FichaTecnicaDAO fichaTecnicaDAO = new FichaTecnicaDAO();
    private final IngredienteDAO ingredienteDAO = new IngredienteDAO();

    private final ObservableList<Map.Entry<ItemCardapio, Integer>> itensAgregados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        botaoPagar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
        botaoCancelar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));

        Tooltip.install(botaoPagar, new Tooltip("Ir para a tela de seleção de pagamento"));
        Tooltip.install(botaoCancelar, new Tooltip("Voltar para a tela de mesas"));

        listaItensDaConta.setItems(itensAgregados);
        configurarCellFactory();
    }

    public void carregarConta(Mesa mesa, Runnable callback) {
        this.mesaAtual = mesa;
        this.refreshCallback = callback;
        labelTituloConta.setText("Conta da Mesa " + mesa.getNumero());
        Map<ItemCardapio, Integer> totalItens = new HashMap<>();
        for (Pedido pedido : mesa.getPedidosAtivos()) {
            for (Map.Entry<ItemCardapio, Integer> entry : pedido.getItens().entrySet()) {
                totalItens.put(entry.getKey(), totalItens.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        itensAgregados.setAll(new ArrayList<>(totalItens.entrySet()));
        labelValorTotalConta.setText(String.format("Valor Total: R$ %.2f", mesa.getValorTotalDaConta()));
    }

    private void configurarCellFactory() {
        listaItensDaConta.setCellFactory(param -> new ListCell<>() {
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

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    @FXML
    private void irParaPagamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/seu/restaurante/PagamentoView.fxml"));
            Parent root = loader.load();
            PagamentoViewController pagamentoController = loader.getController();

            Consumer<MetodoPagamento> callbackPagamentoSucesso = (metodoEscolhido) -> {
                Usuario funcionario = App.getUsuarioLogado();
                if (funcionario == null) {
                    AlertaUtil.mostrarErro("Erro Crítico", "Nenhum funcionário está logado. Impossível fechar a conta.");
                    return;
                }

                Conta novaConta = new Conta(
                        mesaAtual.getNumero(),
                        funcionario.getId(),
                        mesaAtual.getValorTotalDaConta(),
                        metodoEscolhido,
                        LocalDateTime.now()
                );

                int contaId = contaDAO.salvarConta(novaConta);

                for (Pedido pedido : mesaAtual.getPedidosAtivos()) {
                    darBaixaEstoque(pedido);
                }

                pedidoDAO.arquivarPedidos(contaId, mesaAtual.getPedidosAtivos());

                mesaAtual.limparPedidos();
                mesaAtual.setStatus(StatusMesa.LIVRE);

                if (refreshCallback != null) {
                    refreshCallback.run();
                }

                fecharJanela();
            };

            pagamentoController.carregarDados(mesaAtual.getValorTotalDaConta(), callbackPagamentoSucesso);

            Stage stage = new Stage();
            stage.setTitle("Finalizar Pagamento");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lógica de baixa de estoque.
     */
    private void darBaixaEstoque(Pedido pedido) {
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
    }

    private void fecharJanela() {
        Stage stage = (Stage) botaoCancelar.getScene().getWindow();
        stage.close();
    }
}