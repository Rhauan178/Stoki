package com.seu.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static Stage stagePrincipal;
    private static Usuario usuarioLogado;
    private static List<Mesa> listaDeMesas = new ArrayList<>();
    private final MesaDAO mesaDAO = new MesaDAO();

    @Override
    public void start(Stage stage) throws IOException {
        inicializarDados();
        stagePrincipal = stage;

        Parent root = FXMLLoader.load(getClass().getResource("/com/seu/restaurante/LoginView.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/seu/restaurante/styles.css").toExternalForm());

        stage.setTitle("AppRestaurante - Login");
        stage.setScene(scene);
        stage.show();
    }

    private void inicializarDados() {
        new UsuarioDAO().criarTabela();
        new ItemCardapioDAO().criarTabela();
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.criarTabelas();
        mesaDAO.criarTabela();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.existemUsuarios()) {
            usuarioDAO.adicionarUsuario(new Usuario("Administrador", "admin", "1234", "Gerente"));
        }

        listaDeMesas = mesaDAO.carregarTodasAsMesas();

        if (listaDeMesas.isEmpty()) {
            System.out.println("Nenhuma mesa encontrada. A criar 12 mesas padrão.");
            for (int i = 1; i <= 12; i++) {
                Mesa novaMesa = new Mesa(i, StatusMesa.LIVRE);
                listaDeMesas.add(novaMesa);
                mesaDAO.adicionarMesa(novaMesa);
            }
        }

        System.out.println("A carregar pedidos ativos...");
        List<Pedido> pedidosAtivos = pedidoDAO.carregarPedidosAtivos();
        for (Pedido pedido : pedidosAtivos) {
            for (Mesa mesa : listaDeMesas) {
                if (mesa.getNumero() == pedido.getIdMesa()) {
                    mesa.setPedidoAtual(pedido);
                    mesa.setStatus(StatusMesa.OCUPADA);
                    System.out.println("Pedido ID " + pedido.getId() + " associado à Mesa " + mesa.getNumero());
                }
            }
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Aplicação a fechar... a salvar estado atual.");

        // Salva o status atual de TODAS as mesas
        for (Mesa mesa : listaDeMesas) {
            mesaDAO.atualizarStatusMesa(mesa);
        }
        System.out.println("Estado das mesas salvo.");

        // Limpa e salva os pedidos que estão realmente ativos
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.limparPedidosAtivos();
        for (Mesa mesa : listaDeMesas) {
            if (mesa.getStatus() == StatusMesa.OCUPADA && mesa.getPedidoAtual() != null) {
                pedidoDAO.salvarPedido(mesa.getPedidoAtual());
            }
        }
    }

    public static List<Mesa> getListaDeMesas() { return listaDeMesas; }
    public static Usuario getUsuarioLogado() { return usuarioLogado; }
    public static void setUsuarioLogado(Usuario usuario) { usuarioLogado = usuario; }

    public static void trocarDeTela(String fxml, String titulo) {
        try {
            Parent novaCenaRoot = FXMLLoader.load(App.class.getResource("/com/seu/restaurante/" + fxml));
            if (stagePrincipal.getScene() != null) {
                stagePrincipal.getScene().setRoot(novaCenaRoot);
            } else {
                Scene novaCena = new Scene(novaCenaRoot);
                novaCena.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
                stagePrincipal.setScene(novaCena);
            }
            stagePrincipal.setTitle(titulo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}