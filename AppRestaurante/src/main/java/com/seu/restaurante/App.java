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

    public static void trocarDeTela(String fxml, String titulo) {
        try {
            Parent novaCenaRoot = FXMLLoader.load(App.class.getResource("/com/seu/restaurante/" + fxml));
            Scene novaCena = new Scene(novaCenaRoot);
            novaCena.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
            stagePrincipal.setScene(novaCena);
            stagePrincipal.setTitle(titulo);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela: " + fxml);
            e.printStackTrace();
        }
    }

    private void inicializarDados() {
        // Inicializa todos os DAOs e cria as tabelas se não existirem
        new UsuarioDAO().criarTabela();
        new ItemCardapioDAO().criarTabela();
        new PedidoDAO().criarTabelas();
        mesaDAO.criarTabela();

        // Garante que o usuário admin existe
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.existemUsuarios()) {
            usuarioDAO.adicionarUsuario(new Usuario("Administrador", "admin", "1234", "Gerente"));
        }

        // Carrega as mesas que existem na base de dados
        listaDeMesas = mesaDAO.carregarTodasAsMesas();

        // Se a base de dados estiver vazia, cria 12 mesas padrão e as salva
        if (listaDeMesas.isEmpty()) {
            System.out.println("Nenhuma mesa encontrada. A criar 12 mesas padrão.");
            for (int i = 1; i <= 12; i++) {
                Mesa novaMesa = new Mesa(i, StatusMesa.LIVRE);
                listaDeMesas.add(novaMesa); // Adiciona à lista em memória
                mesaDAO.adicionarMesa(novaMesa); // Salva na base de dados
            }
        }

        // Carrega os pedidos ativos e liga eles às mesas
        PedidoDAO pedidoDAO = new PedidoDAO();
        List<Pedido> pedidosAtivos = pedidoDAO.carregarPedidosAtivos();
        for (Pedido pedido : pedidosAtivos) {
            listaDeMesas.stream()
                    .filter(mesa -> mesa.getNumero() == pedido.getIdMesa())
                    .findFirst()
                    .ifPresent(mesa -> {
                        mesa.setStatus(StatusMesa.OCUPADA);
                        mesa.setPedidoAtual(pedido);
                    });
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Aplicação a fechar... a salvar estado atual.");
        for (Mesa mesa : listaDeMesas) {
            mesaDAO.atualizarStatusMesa(mesa);
        }
        System.out.println("Estado das mesas salvo.");

        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.limparPedidosAtivos();
        for (Mesa mesa : listaDeMesas) {
            if (mesa.getStatus() == StatusMesa.OCUPADA && mesa.getPedidoAtual() != null) {
                pedidoDAO.salvarPedido(mesa.getPedidoAtual());
            }
        }
    }

    public static List<Mesa> getListaDeMesas() {
        return listaDeMesas;
    }
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }
    public static void main(String[] args) {
        launch();
    }
}