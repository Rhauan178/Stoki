package com.seu.restaurante;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS contas ("
                + " id INT PRIMARY KEY AUTO_INCREMENT,"
                + " id_mesa INT NOT NULL,"
                + " id_funcionario INT NOT NULL,"
                + " valor_total DECIMAL(10, 2) NOT NULL,"
                + " metodo_pagamento VARCHAR(100) NOT NULL,"
                + " data_hora VARCHAR(100) NOT NULL,"
                + " status VARCHAR(100) NOT NULL,"
                + " FOREIGN KEY (id_funcionario) REFERENCES usuarios(id)"
                + ");";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int salvarConta(Conta conta) {
        String sql = "INSERT INTO contas(id_mesa, id_funcionario, valor_total, metodo_pagamento, data_hora) VALUES(?,?,?,?,?)";
        int idGerado = 0;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, conta.getIdMesa());
            pstmt.setInt(2, conta.getIdFuncionario());
            pstmt.setBigDecimal(3, conta.getValorTotal());
            pstmt.setString(4, conta.getMetodoPagamento().name());
            pstmt.setString(5, conta.getDataHora().toString());
            pstmt.setString(6, conta.getStatus().name());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                idGerado = generatedKeys.getInt(1);
                conta.setId(idGerado);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return idGerado;
    }

    public void arquivarContasPagas() {
        String sql = "UPDATE contas SET status = ? WHERE status = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, StatusConta.ARQUIVADA.name());
            pstmt.setString(2, StatusConta.PAGA.name());
            int affectedRows = pstmt.executeUpdate();
            System.out.println(affectedRows + " contas foram arquivadas.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Conta> carregarContasPagas(LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        String sql = "SELECT * FROM contas WHERE status = 'PAGA'";
        return carregarContas(sql, dataInicio, dataFim, funcionarioId);
    }

    public List<Conta> carregarContasArquivadas(LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
        String sql = "SELECT * FROM contas WHERE status = 'ARQUIVADA'";
        return carregarContas(sql, dataInicio, dataFim, funcionarioId);
    }

    private List<Conta> carregarContas(String baseSql, LocalDate dataInicio, LocalDate dataFim, Integer funcionarioId) {
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
        List<Conta> contas = new ArrayList<>();

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlFinal)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                contas.add(new Conta(
                        rs.getInt("id"),
                        rs.getInt("id_mesa"),
                        rs.getInt("id_funcionario"),
                        rs.getBigDecimal("valor_total"),
                        MetodoPagamento.valueOf(rs.getString("metodo_pagamento")),
                        LocalDateTime.parse(rs.getString("data_hora")),
                        StatusConta.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return contas;
    }
}