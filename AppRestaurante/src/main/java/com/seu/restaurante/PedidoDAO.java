package com.seu.restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    public void criarTabelas() {
        String sqlPedidos = "CREATE TABLE IF NOT EXISTS pedidos ("
                + " id integer PRIMARY KEY AUTOINCREMENT,"
                + " id_mesa integer NOT NULL,"
                + " status text NOT NULL,"
                + " data_hora text NOT NULL"
                + ");";

        // 1. Adicionamos a coluna "quantidade" à tabela de ligação
        String sqlPedidoItens = "CREATE TABLE IF NOT EXISTS pedido_itens ("
                + " id_pedido integer NOT NULL,"
                + " id_item_cardapio integer NOT NULL,"
                + " quantidade integer NOT NULL," // <-- NOVA COLUNA
                + " FOREIGN KEY (id_pedido) REFERENCES pedidos(id),"
                + " FOREIGN KEY (id_item_cardapio) REFERENCES cardapio(id)"
                + ");";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPedidos);
            stmt.execute(sqlPedidoItens);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Substitua o seu método salvarPedido por este
    public void salvarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos(id_mesa, status, data_hora) VALUES(?,?,?)";
        String sqlItens = "INSERT INTO pedido_itens(id_pedido, id_item_cardapio, quantidade) VALUES(?,?,?)";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, pedido.getIdMesa());
                pstmtPedido.setString(2, pedido.getStatus().name());
                pstmtPedido.setString(3, pedido.getDataHora().toString());
                pstmtPedido.executeUpdate();

                ResultSet generatedKeys = pstmtPedido.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int pedidoIdGerado = generatedKeys.getInt(1);
                    // AQUI ESTÁ A CORREÇÃO CRÍTICA:
                    // Atualizamos o objeto original com o ID correto do banco de dados
                    pedido.setId(pedidoIdGerado);

                    try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens)) {
                        for (Map.Entry<ItemCardapio, Integer> entry : pedido.getItens().entrySet()) {
                            pstmtItens.setInt(1, pedidoIdGerado);
                            pstmtItens.setInt(2, entry.getKey().getId());
                            pstmtItens.setInt(3, entry.getValue());
                            pstmtItens.addBatch();
                        }
                        pstmtItens.executeBatch();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Adicione este método à sua classe ItemCardapioDAO
    public ItemCardapio buscarPorId(int id) {
        return getItemCardapio(id);
    }

    static ItemCardapio getItemCardapio(int id) {
        String sql = "SELECT * FROM cardapio WHERE id = ?";
        ItemCardapio item = null;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                item = new ItemCardapio(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("preco"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponivel")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return item;
    }

    // Adicione este método à sua classe PedidoDAO
    public void atualizarStatus(int pedidoId, StatusPedido novoStatus) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define os parâmetros da query
            pstmt.setString(1, novoStatus.name()); // ex: "PAGO"
            pstmt.setInt(2, pedidoId);

            // Executa a atualização
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Adicione este método à sua classe PedidoDAO
    public void limparPedidosAtivos() {
        // Apaga todos os pedidos e os seus itens que não estão com status "PAGO"
        String sqlDeleteItens = "DELETE FROM pedido_itens WHERE id_pedido IN (SELECT id FROM pedidos WHERE status != 'PAGO')";
        String sqlDeletePedidos = "DELETE FROM pedidos WHERE status != 'PAGO'";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlDeleteItens);
            stmt.execute(sqlDeletePedidos);
            System.out.println("Pedidos ativos antigos foram limpos da base de dados.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Substitua o seu método carregarPedidosAtivos por este
    public List<Pedido> carregarPedidosAtivos() {
        String sqlPedidos = "SELECT * FROM pedidos WHERE status != 'PAGO'";
        String sqlItens = "SELECT * FROM pedido_itens WHERE id_pedido = ?";

        List<Pedido> pedidosAtivos = new ArrayList<>();
        ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();

        try (Connection conn = DatabaseConnector.connect();
             Statement stmtPedidos = conn.createStatement();
             ResultSet rsPedidos = stmtPedidos.executeQuery(sqlPedidos)) {

            while (rsPedidos.next()) {
                int idMesa = rsPedidos.getInt("id_mesa");
                Pedido pedido = new Pedido(rsPedidos.getInt("id"), idMesa);
                pedido.setStatus(StatusPedido.valueOf(rsPedidos.getString("status")));

                try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens)) {
                    pstmtItens.setInt(1, pedido.getId());
                    ResultSet rsItens = pstmtItens.executeQuery();

                    while (rsItens.next()) {
                        int idItem = rsItens.getInt("id_item_cardapio");
                        int quantidade = rsItens.getInt("quantidade");

                        // LÓGICA CORRIGIDA E EFICIENTE
                        ItemCardapio item = itemCardapioDAO.buscarPorId(idItem);
                        if (item != null) {
                            // Adiciona o item e a sua quantidade diretamente ao Map do pedido
                            pedido.getItens().put(item, quantidade);
                        }
                    }
                }
                pedidosAtivos.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pedidosAtivos;
    }

    public List<Pedido> carregarPedidosPagos() {
        // A única diferença é a cláusula WHERE: buscamos por status 'PAGO'
        String sqlPedidos = "SELECT * FROM pedidos WHERE status = 'PAGO'";
        String sqlItens = "SELECT * FROM pedido_itens WHERE id_pedido = ?";

        List<Pedido> pedidosPagos = new ArrayList<>();
        ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();

        try (Connection conn = DatabaseConnector.connect();
             Statement stmtPedidos = conn.createStatement();
             ResultSet rsPedidos = stmtPedidos.executeQuery(sqlPedidos)) {

            while (rsPedidos.next()) {
                int idMesa = rsPedidos.getInt("id_mesa");
                Pedido pedido = new Pedido(rsPedidos.getInt("id"), idMesa);
                pedido.setStatus(StatusPedido.valueOf(rsPedidos.getString("status")));

                // A lógica para carregar os itens de cada pedido é a mesma de antes
                try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens)) {
                    pstmtItens.setInt(1, pedido.getId());
                    ResultSet rsItens = pstmtItens.executeQuery();

                    while(rsItens.next()) {
                        int idItem = rsItens.getInt("id_item_cardapio");
                        int quantidade = rsItens.getInt("quantidade");

                        ItemCardapio item = itemCardapioDAO.buscarPorId(idItem);
                        if (item != null) {
                            pedido.getItens().put(item, quantidade);
                        }
                    }
                }
                pedidosPagos.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pedidosPagos;
    }
}