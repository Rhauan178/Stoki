package com.seu.restaurante;

// Novas importações necessárias para a caixa de diálogo
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class MesaViewController {

    @FXML private GridPane gridMesas;
    @FXML private Button botaoGerirCardapio;
    private List<Mesa> listaDeMesas;

    @FXML
    public void initialize() {
        this.listaDeMesas = App.getListaDeMesas();
        popularGridDeMesas();
        configurarVisibilidadeBotoes();
    }

    private void configurarVisibilidadeBotoes() {
        Usuario usuarioLogado = App.getUsuarioLogado();
        if (usuarioLogado != null && "Gerente".equalsIgnoreCase(usuarioLogado.getCargo())) {
            botaoGerirCardapio.setVisible(true);
        } else {
            botaoGerirCardapio.setVisible(false);
        }
    }

    private void popularGridDeMesas() {
        gridMesas.getChildren().clear();
        this.listaDeMesas = App.getListaDeMesas();
        int mesaIndex = 0;
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 4; coluna++) {
                if (mesaIndex < listaDeMesas.size()) {
                    final Mesa mesa = listaDeMesas.get(mesaIndex);
                    Button botaoMesa = new Button("Mesa " + mesa.getNumero());
                    botaoMesa.setPrefSize(100, 100);
                    atualizarEstiloBotao(botaoMesa, mesa);

                    // --- LÓGICA DE CLIQUE ATUALIZADA ---
                    botaoMesa.setOnAction(event -> {
                        switch (mesa.getStatus()) {
                            case OCUPADA:
                                abrirJanelaDePedido(mesa);
                                break;
                            case RESERVADA:
                                System.out.println("Mesa " + mesa.getNumero() + " (Reservada) foi ocupada.");
                                ocuparMesa(mesa);
                                atualizarEstiloBotao(botaoMesa, mesa);
                                break;
                            case LIVRE:
                                mostrarOpcoesMesaLivre(mesa, botaoMesa);
                                break;
                        }
                    });

                    gridMesas.add(botaoMesa, coluna, linha);
                    mesaIndex++;
                }
            }
        }
    }

    private void mostrarOpcoesMesaLivre(Mesa mesa, Button botaoMesa) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ação para Mesa Livre");
        alert.setHeaderText("A mesa " + mesa.getNumero() + " está livre.");
        alert.setContentText("O que deseja fazer?");

        ButtonType botaoOcupar = new ButtonType("Ocupar Mesa");
        ButtonType botaoReservar = new ButtonType("Reservar Mesa");
        ButtonType botaoCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botaoOcupar, botaoReservar, botaoCancelar);

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent()) {
            if (resultado.get() == botaoOcupar) {
                System.out.println("Mesa " + mesa.getNumero() + " (Livre) foi ocupada.");
                ocuparMesa(mesa);
            } else if (resultado.get() == botaoReservar) {
                System.out.println("Mesa " + mesa.getNumero() + " (Livre) foi reservada.");
                mesa.setStatus(StatusMesa.RESERVADA);
            }
            atualizarEstiloBotao(botaoMesa, mesa);
        }
    }

    private void ocuparMesa(Mesa mesa) {
        mesa.setStatus(StatusMesa.OCUPADA);
        // Garante que a mesa ocupada tenha um pedido (mesmo que seja um novo e vazio)
        if (mesa.getPedidoAtual() == null) {
            Pedido novoPedido = new Pedido(0, mesa.getNumero());
            mesa.setPedidoAtual(novoPedido);
        }
    }

    private void atualizarEstiloBotao(Button botao, Mesa mesa) {
        botao.getStyleClass().removeAll("botao-livre", "botao-ocupada", "botao-reservada");
        switch (mesa.getStatus()) {
            case LIVRE:
                botao.getStyleClass().add("botao-livre");
                break;
            case OCUPADA:
                botao.getStyleClass().add("botao-ocupada");
                break;
            case RESERVADA:
                botao.getStyleClass().add("botao-reservada");
                break;
        }
    }

    // O resto da sua classe (abrirGerenciamentoCardapio, fazerLogout, abrirJanelaDePedido) continua igual
    @FXML
    private void abrirGerenciamentoCardapio() throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/seu/restaurante/GerenteView.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Gerenciamento de Cardápio");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        System.out.println("Janela de gerenciamento fechada.");
    }

    @FXML
    private void fazerLogout() {
        System.out.println("Usuário a fazer logout... a voltar para a tela de login.");
        App.setUsuarioLogado(null);
        App.trocarDeTela("LoginView.fxml", "AppRestaurante - Login");
    }

    private void abrirJanelaDePedido(Mesa mesa) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/seu/restaurante/PedidoView.fxml"));
            Parent root = loader.load();
            PedidoViewController controller = loader.getController();
            controller.carregarDadosDaMesa(mesa);
            controller.setOnCloseCallback(this::popularGridDeMesas);
            Stage stage = new Stage();
            stage.setTitle("Detalhes do Pedido");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}