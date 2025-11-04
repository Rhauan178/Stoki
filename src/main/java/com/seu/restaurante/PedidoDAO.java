package com.seu.restaurante;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    public void criarTabelas() {
        String sqlPedidos = "CREATE TABLE IF NOT EXISTS pedidos ("
                + " id INT PRIMARY KEY AUTO_INCREMENT,"
                + " id_mesa INT NOT NULL,"
                + " id_funcionario INT NOT NULL,"
                + " status TEXT NOT NULL,"
                + " data_hora TEXT NOT NULL,"
                + " observacao TEXT,"
                + " id_conta_paga INT NULL," // Nova coluna para ligar à conta
                + " FOREIGN KEY (id_funcionario) REFERENCES usuarios(id),"
                + " FOREIGN KEY (id_conta_paga) REFERENCES contas(id)"
                + ");";
        String sqlPedidoItens = "CREATE TABLE IF NOT EXISTS pedido_itens ("
                + " id_pedido INT NOT NULL,"
                + " id_item_cardapio INT NOT NULL,"
                + " quantidade INT NOT NULL,"
                + " FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE,"
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

    public void salvarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos(id_mesa, id_funcionario, status, data_hora, observacao) VALUES(?,?,?,?,?)";
        String sqlItens = "INSERT INTO pedido_itens(id_pedido, id_item_cardapio, quantidade) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, pedido.getIdMesa());
                pstmtPedido.setInt(2, pedido.getIdFuncionario());
                pstmtPedido.setString(3, pedido.getStatus().name());
                pstmtPedido.setString(4, pedido.getDataHora().toString());
                pstmtPedido.setString(5, pedido.getObservacao());
                pstmtPedido.executeUpdate();
                ResultSet generatedKeys = pstmtPedido.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int pedidoIdGerado = generatedKeys.getInt(1);
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

    public void arquivarPedidos(int contaId, List<Pedido> pedidos) {
        String sql = "UPDATE pedidos SET status = ?, id_conta_paga = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Pedido pedido : pedidos) {
                pstmt.setString(1, StatusPedido.ARQUIVADO.name());
                pstmt.setInt(2, contaId);
                pstmt.setInt(3, pedido.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void atualizarStatus(int pedidoId, StatusPedido novoStatus) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, novoStatus.name());
            pstmt.setInt(2, pedidoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void atualizarObservacao(int pedidoId, String observacao) {
        String sql = "UPDATE pedidos SET observacao = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, observacao);
            pstmt.setInt(2, pedidoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void atualizarItensDoPedido(Pedido pedido) {
        if (pedido.getId() == 0) return;
        String sqlDelete = "DELETE FROM pedido_itens WHERE id_pedido = ?";
        String sqlInsert = "INSERT INTO pedido_itens(id_pedido, id_item_cardapio, quantidade) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                pstmtDelete.setInt(1, pedido.getId());
                pstmtDelete.executeUpdate();
            }
            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                for (Map.Entry<ItemCardapio, Integer> entry : pedido.getItens().entrySet()) {
                    pstmtInsert.setInt(1, pedido.getId());
                    pstmtInsert.setInt(2, entry.getKey().getId());
                    pstmtInsert.setInt(3, entry.getValue());
                    pstmtInsert.addBatch();
                }
                pstmtInsert.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void limparPedidosAtivos() {
        String statusAberto = StatusPedido.ABERTO.name();
        String sqlDeleteItens = "DELETE FROM pedido_itens WHERE id_pedido IN (SELECT id FROM pedidos WHERE status = ?)";
        String sqlDeletePedidos = "DELETE FROM pedidos WHERE status = ?";
        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtItens = conn.prepareStatement(sqlDeleteItens);
                 PreparedStatement pstmtPedidos = conn.prepareStatement(sqlDeletePedidos)) {
                pstmtItens.setString(1, statusAberto);
                pstmtItens.executeUpdate();
                pstmtPedidos.setString(1, statusAberto);
                pstmtPedidos.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Pedido> carregarPedidosAtivos() {
        String sql = "SELECT * FROM pedidos WHERE status != 'ARQUIVADO'";
        return carregarPedidos(sql, null, null, null);
    }

    public List<Pedido> carregarPedidosParaCozinha() {
        String sql = "SELECT * FROM pedidos WHERE status = 'ENVIADO' OR status = 'EM_PREPARO'";
        return carregarPedidos(sql, null, null, null);
    }

    private List<Pedido> carregarPedidos(String baseSql, LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        List<Object> params = new ArrayList<>();
        if (dataInicio != null && dataFim != null) {
            sqlBuilder.append(" AND date(data_hora) BETWEEN ? AND ?");
            params.add(dataInicio.toString());
            params.add(dataFim.toString());
        }
        if (funcionarioId != null) {
            sqlBuilder.append(" AND id_funcionario = ?");
            params.add(funcionarioId);
        }
        String sqlFinal = sqlBuilder.toString();
        String sqlItens = "SELECT * FROM pedido_itens WHERE id_pedido = ?";
        List<Pedido> pedidos = new ArrayList<>();
        ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlFinal)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rsPedidos = pstmt.executeQuery();
            while (rsPedidos.next()) {
                int idMesa = rsPedidos.getInt("id_mesa");
                int idFunc = rsPedidos.getInt("id_funcionario");
                Pedido pedido = new Pedido(rsPedidos.getInt("id"), idMesa, idFunc);
                pedido.setStatus(StatusPedido.valueOf(rsPedidos.getString("status")));
                pedido.setDataHora(LocalDateTime.parse(rsPedidos.getString("data_hora")));
                pedido.setObservacao(rsPedidos.getString("observacao"));

                // Lê o novo campo id_conta_paga
                pedido.setIdContaPaga(rsPedidos.getInt("id_conta_paga"));
                if (rsPedidos.wasNull()) {
                    pedido.setIdContaPaga(null);
                }

                try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens)) {
                    pstmtItens.setInt(1, pedido.getId());
                    ResultSet rsItens = pstmtItens.executeQuery();
                    while (rsItens.next()) {
                        int idItem = rsItens.getInt("id_item_cardapio");
                        int quantidade = rsItens.getInt("quantidade");
                        ItemCardapio item = itemCardapioDAO.buscarPorId(idItem);
                        if (item != null) {
                            pedido.getItens().put(item, quantidade);
                        }
                    }
                }
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pedidos;
    }
}