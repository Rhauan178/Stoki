package com.seu.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static Stage stagePrincipal;
    private static Usuario usuarioLogado;
    private static List<Mesa> listaDeMesas = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        inicializarDados(); // Método que centraliza a inicialização
        stagePrincipal = stage;

        Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/seu/restaurante/styles.css").toExternalForm());

        stage.setTitle("AppRestaurante - Login");
        stage.setScene(scene);
        stage.show();
    }

    // Em App.java
    private void inicializarDados() {
        // Código de inicialização que você já tem...
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.criarTabela();
        if (!usuarioDAO.existemUsuarios()) {
            usuarioDAO.adicionarUsuario(new Usuario(1, "Administrador", "admin", "1234", "Gerente"));
        }
        ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
        itemCardapioDAO.criarTabela();
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.criarTabelas();
        criarMesasIniciais();

        System.out.println("\n--- [DEBUG] A CARREGAR ESTADO ANTERIOR ---");
        List<Pedido> pedidosAtivos = pedidoDAO.carregarPedidosAtivos();
        System.out.println("[DEBUG] Encontrados " + pedidosAtivos.size() + " pedidos ativos na base de dados.");

        for (Pedido pedido : pedidosAtivos) {
            System.out.println("[DEBUG] A processar pedido ID " + pedido.getId() + " para Mesa " + pedido.getIdMesa() + " com " + pedido.getItens().size() + " tipos de itens.");
            pedido.getItens().forEach((item, qtd) ->
                    System.out.println("  -> Item carregado: " + item.getNome() + ", Qtd: " + qtd)
            );
            listaDeMesas.stream()
                    .filter(mesa -> mesa.getNumero() == pedido.getIdMesa())
                    .findFirst()
                    .ifPresent(mesa -> {
                        mesa.setStatus(StatusMesa.OCUPADA);
                        mesa.setPedidoAtual(pedido);
                    });
        }
        System.out.println("--- [DEBUG] FIM DO CARREGAMENTO ---\n");
    }

    // O resto da classe (criarMesasIniciais, stop, getListaDeMesas, etc.) continua igual.

    @Override
    public void stop() throws Exception {
        System.out.println("\n--- [DEBUG] APLICAÇÃO A FECHAR ---");
        PedidoDAO pedidoDAO = new PedidoDAO();
        pedidoDAO.limparPedidosAtivos();

        for (Mesa mesa : listaDeMesas) {
            if (mesa.getStatus() == StatusMesa.OCUPADA && mesa.getPedidoAtual() != null) {
                System.out.println("[DEBUG] A salvar pedido para Mesa " + mesa.getNumero() + " com " + mesa.getPedidoAtual().getItens().size() + " tipos de itens.");
                // Imprime cada item e quantidade
                mesa.getPedidoAtual().getItens().forEach((item, qtd) ->
                        System.out.println("  -> Item: " + item.getNome() + ", Qtd: " + qtd)
                );
                pedidoDAO.salvarPedido(mesa.getPedidoAtual());
            }
        }
        System.out.println("--- [DEBUG] FIM DO PROCESSO DE SALVAR ---\n");
    }

    public static void criarMesasIniciais() {
        if (listaDeMesas.isEmpty()) {
            for (int i = 1; i <= 12; i++) {
                listaDeMesas.add(new Mesa(i, StatusMesa.LIVRE));
            }
        }
    }

    public static List<Mesa> getListaDeMesas() { return listaDeMesas; }
    public static Usuario getUsuarioLogado() { return usuarioLogado; }
    public static void setUsuarioLogado(Usuario usuario) { usuarioLogado = usuario; }

    public static void trocarDeTela(String fxml, String titulo) {
        try {
            Parent novaCenaRoot = FXMLLoader.load(App.class.getResource(fxml));
            Scene novaCena = new Scene(novaCenaRoot);
            novaCena.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
            stagePrincipal.setScene(novaCena);
            stagePrincipal.setTitle(titulo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}