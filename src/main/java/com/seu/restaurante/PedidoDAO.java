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
                + " metodo_pagamento TEXT,"
                + " FOREIGN KEY (id_funcionario) REFERENCES usuarios(id)"
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
        String sqlPedido = "INSERT INTO pedidos(id_mesa, id_funcionario, status, data_hora, observacao, metodo_pagamento) VALUES(?,?,?,?,?,?)";
        String sqlItens = "INSERT INTO pedido_itens(id_pedido, id_item_cardapio, quantidade) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, pedido.getIdMesa());
                pstmtPedido.setInt(2, pedido.getIdFuncionario());
                pstmtPedido.setString(3, pedido.getStatus().name());
                pstmtPedido.setString(4, pedido.getDataHora().toString());
                pstmtPedido.setString(5, pedido.getObservacao());
                pstmtPedido.setString(6, pedido.getMetodoPagamento() != null ? pedido.getMetodoPagamento().name() : null);
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

    public void marcarComoPago(int pedidoId, MetodoPagamento metodo) {
        String sql = "UPDATE pedidos SET status = ?, data_hora = ?, metodo_pagamento = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, StatusPedido.PAGO.name());
            pstmt.setString(2, LocalDateTime.now().toString());
            pstmt.setString(3, metodo.name());
            pstmt.setInt(4, pedidoId);
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

    public void arquivarPedidosPagos() {
        String sql = "UPDATE pedidos SET status = ? WHERE status = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, StatusPedido.ARQUIVADO.name());
            pstmt.setString(2, StatusPedido.PAGO.name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Pedido> carregarPedidosAtivos() { return carregarPedidosAtivos(null, null, null); }
    public List<Pedido> carregarPedidosPagos() { return carregarPedidosPagos(null, null, null); }
    public List<Pedido> carregarPedidosArquivados() { return carregarPedidosArquivados(null, null, null); }

    public List<Pedido> carregarPedidosAtivos(LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        String sql = "SELECT * FROM pedidos WHERE status != 'PAGO' AND status != 'ARQUIVADO'";
        return carregarPedidos(sql, dataInicio, dataFim, funcionarioId);
    }
    public List<Pedido> carregarPedidosPagos(LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        String sql = "SELECT * FROM pedidos WHERE status = 'PAGO'";
        return carregarPedidos(sql, dataInicio, dataFim, funcionarioId);
    }
    public List<Pedido> carregarPedidosArquivados(LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        String sql = "SELECT * FROM pedidos WHERE status = 'ARQUIVADO'";
        return carregarPedidos(sql, dataInicio, dataFim, funcionarioId);
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
                String metodoPagamentoStr = rsPedidos.getString("metodo_pagamento");
                if (metodoPagamentoStr != null) {
                    pedido.setMetodoPagamento(MetodoPagamento.valueOf(metodoPagamentoStr));
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