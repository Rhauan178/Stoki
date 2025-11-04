package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MesaViewController {

    @FXML private BorderPane painelPrincipal;
    @FXML private GridPane gridMesas;
    @FXML private Button botaoAdicionarMesa;
    @FXML private Button botaoRemoverMesa;
    @FXML private Button botaoLogout;
    @FXML private VBox painelAdmin;
    @FXML private Button botaoGerirCardapio;
    @FXML private Button botaoGerirEstoque;
    @FXML private Button botaoGerirFuncionarios;
    @FXML private Button botaoVerRelatorios;
    @FXML private Button botaoVerConta;

    private List<Mesa> listaDeMesas;
    private Mesa mesaSelecionada = null;
    private Button botaoMesaSelecionado = null;
    private final MesaDAO mesaDAO = new MesaDAO();

    @FXML
    public void initialize() {
        botaoAdicionarMesa.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE));
        botaoRemoverMesa.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS_SQUARE));
        botaoLogout.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SIGN_OUT));
        botaoGerirCardapio.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BOOK));
        botaoGerirEstoque.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARCHIVE));
        botaoGerirFuncionarios.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.USERS));
        botaoVerRelatorios.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LINE_CHART));
        botaoVerConta.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MONEY));

        Tooltip.install(botaoAdicionarMesa, new Tooltip("Adicionar uma nova mesa ao salão"));
        Tooltip.install(botaoRemoverMesa, new Tooltip("Remover a mesa selecionada (deve estar livre)"));
        Tooltip.install(botaoLogout, new Tooltip("Sair do sistema e voltar para a tela de login"));
        Tooltip.install(botaoVerConta, new Tooltip("Ver/Fechar a conta da mesa selecionada"));
        Tooltip.install(botaoGerirCardapio, new Tooltip("Abrir o módulo de gerenciamento do cardápio"));
        Tooltip.install(botaoGerirEstoque, new Tooltip("Abrir o módulo de gerenciamento de estoque"));
        Tooltip.install(botaoGerirFuncionarios, new Tooltip("Abrir o módulo de gerenciamento de funcionários"));
        Tooltip.install(botaoVerRelatorios, new Tooltip("Abrir o módulo de relatórios de vendas"));

        this.listaDeMesas = App.getListaDeMesas();
        popularGridDeMesas();
        configurarVisibilidadeAdmin();

        botaoVerConta.setDisable(true);
    }

    private void configurarVisibilidadeAdmin() {
        Usuario usuarioLogado = App.getUsuarioLogado();
        boolean isGerente = usuarioLogado != null && "Gerente".equalsIgnoreCase(usuarioLogado.getCargo());
        boolean isGarcom = usuarioLogado != null && "Garçom".equalsIgnoreCase(usuarioLogado.getCargo());

        boolean podeAtender = isGerente || isGarcom;
        botaoVerConta.setVisible(podeAtender);
        botaoVerConta.setManaged(podeAtender);

        painelAdmin.setVisible(isGerente);
        painelAdmin.setManaged(isGerente);
        botaoAdicionarMesa.setVisible(isGerente);
        botaoAdicionarMesa.setManaged(isGerente);
        botaoRemoverMesa.setVisible(isGerente);
        botaoRemoverMesa.setManaged(isGerente);
        botaoRemoverMesa.setDisable(true);
    }

    private void popularGridDeMesas() {
        gridMesas.getChildren().clear();
        this.listaDeMesas = App.getListaDeMesas();
        this.listaDeMesas.sort(Comparator.comparing(Mesa::getNumero));

        int coluna = 0;
        int linha = 0;
        int maxColunas = 4;

        for (Mesa mesa : listaDeMesas) {
            Button botaoMesa = new Button("Mesa " + mesa.getNumero());
            botaoMesa.setPrefSize(100, 100);
            atualizarEstiloBotao(botaoMesa, mesa);

            botaoMesa.setOnAction(event -> selecionarMesa(mesa, botaoMesa));

            botaoMesa.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    acaoDuploClique(mesa, botaoMesa);
                }
            });

            gridMesas.add(botaoMesa, coluna, linha);
            coluna++;
            if (coluna >= maxColunas) {
                coluna = 0;
                linha++;
            }
        }
    }

    private void selecionarMesa(Mesa mesa, Button botaoClicado) {
        if (botaoMesaSelecionado != null) {
            botaoMesaSelecionado.getStyleClass().remove("mesa-selecionada");
        }
        mesaSelecionada = mesa;
        botaoMesaSelecionado = botaoClicado;
        botaoMesaSelecionado.getStyleClass().add("mesa-selecionada");
        botaoRemoverMesa.setDisable(false);
        botaoVerConta.setDisable(mesa.getStatus() == StatusMesa.LIVRE);
    }

    private void acaoDuploClique(Mesa mesa, Button botaoMesa) {
        if (mesa.getStatus() == StatusMesa.OCUPADA || mesa.getStatus() == StatusMesa.RESERVADA) {
            abrirJanelaDePedido(mesa);
        } else {
            mostrarOpcoesMesaLivre(mesa, botaoMesa);
        }
    }

    @FXML
    private void adicionarMesa() {
        int proximoNumero = listaDeMesas.stream()
                .mapToInt(Mesa::getNumero)
                .max()
                .orElse(0) + 1;
        Mesa novaMesa = new Mesa(proximoNumero, StatusMesa.LIVRE);
        mesaDAO.adicionarMesa(novaMesa);
        listaDeMesas.add(novaMesa);
        popularGridDeMesas();
        AlertaUtil.mostrarInformacao("Sucesso", "Mesa " + proximoNumero + " adicionada com sucesso.");
    }

    @FXML
    private void removerMesa() {
        if (mesaSelecionada == null) {
            AlertaUtil.mostrarErro("Erro", "Nenhuma mesa selecionada para remover.");
            return;
        }
        if (mesaSelecionada.getStatus() != StatusMesa.LIVRE) {
            AlertaUtil.mostrarErro("Ação Inválida", "Só é possível remover mesas que estão livres.");
            return;
        }
        boolean confirmado = AlertaUtil.mostrarConfirmacao("Confirmar Remoção",
                "Tem a certeza que quer remover permanentemente a Mesa " + mesaSelecionada.getNumero() + "?");
        if (confirmado) {
            mesaDAO.removerMesa(mesaSelecionada.getNumero());
            listaDeMesas.remove(mesaSelecionada);
            mesaSelecionada = null;
            botaoMesaSelecionado = null;
            popularGridDeMesas();
            AlertaUtil.mostrarInformacao("Sucesso", "Mesa removida com sucesso.");
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
                ocuparMesa(mesa);
            } else if (resultado.get() == botaoReservar) {
                mesa.setStatus(StatusMesa.RESERVADA);
            }
            atualizarEstiloBotao(botaoMesa, mesa);
        }
    }

    private void ocuparMesa(Mesa mesa) {
        mesa.setStatus(StatusMesa.OCUPADA);
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

    @FXML
    private void fazerLogout() {
        App.setUsuarioLogado(null);
        App.trocarDeTela("LoginView.fxml", "AppRestaurante - Login");
    }

    private void abrirJanelaDePedido(Mesa mesa) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/seu/restaurante/PedidoView.fxml"));
            Parent root = loader.load();
            PedidoViewController controller = loader.getController();

            controller.prepararNovoPedido(mesa, App.getUsuarioLogado().getId());

            Stage stage = new Stage();
            stage.setTitle("Novo Envio para Mesa " + mesa.getNumero());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            popularGridDeMesas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirContaDaMesa() {
        if (mesaSelecionada == null || mesaSelecionada.getStatus() == StatusMesa.LIVRE) {
            AlertaUtil.mostrarErro("Erro", "Selecione uma mesa ocupada ou reservada para ver a conta.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/seu/restaurante/ContaView.fxml"));
            Parent root = loader.load();
            ContaViewController controller = loader.getController();
            controller.carregarConta(mesaSelecionada, this::popularGridDeMesas);

            Stage stage = new Stage();
            stage.setTitle("Conta da Mesa " + mesaSelecionada.getNumero());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGerenciamentoCardapio() {
        try {
            Parent painelGerente = FXMLLoader.load(getClass().getResource("/com/seu/restaurante/GerenteView.fxml"));
            painelPrincipal.setCenter(painelGerente);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGerenciamentoEstoque() {
        try {
            Parent painelEstoque = FXMLLoader.load(getClass().getResource("/com/seu/restaurante/EstoqueView.fxml"));
            painelPrincipal.setCenter(painelEstoque);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGerenciamentoFuncionarios() {
        try {
            Parent painelFuncionarios = FXMLLoader.load(getClass().getResource("/com/seu/restaurante/FuncionarioView.fxml"));
            painelPrincipal.setCenter(painelFuncionarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirRelatorios() {
        try {
            Parent painelRelatorios = FXMLLoader.load(getClass().getResource("/com/seu/restaurante/RelatorioView.fxml"));
            painelPrincipal.setCenter(painelRelatorios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}