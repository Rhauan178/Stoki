package com.seu.restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAO {

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS ingredientes ("
                + " id INT PRIMARY KEY AUTO_INCREMENT,"
                + " nome text NOT NULL UNIQUE,"
                + " estoque_atual DOUBLE NOT NULL,"
                + " unidade_medida text NOT NULL,"
                + " estoque_minimo DOUBLE NOT NULL"
                + ");";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int adicionarIngrediente(Ingrediente ingrediente) {
        String sql = "INSERT INTO ingredientes(nome, estoque_atual, unidade_medida, estoque_minimo) VALUES(?,?,?,?)";
        int idGerado = -1;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ingrediente.getNome());
            pstmt.setDouble(2, ingrediente.getEstoqueAtual());
            pstmt.setString(3, ingrediente.getUnidadeMedida());
            pstmt.setDouble(4, ingrediente.getEstoqueMinimo());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                idGerado = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return idGerado;
    }

    public List<Ingrediente> listarTodos() {
        String sql = "SELECT * FROM ingredientes ORDER BY nome";
        List<Ingrediente> ingredientes = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredientes.add(new Ingrediente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("estoque_atual"),
                        rs.getString("unidade_medida"),
                        rs.getDouble("estoque_minimo")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ingredientes;
    }

    public Ingrediente buscarPorId(int id) {
        String sql = "SELECT * FROM ingredientes WHERE id = ?";
        Ingrediente ingrediente = null;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ingrediente = new Ingrediente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("estoque_atual"),
                        rs.getString("unidade_medida"),
                        rs.getDouble("estoque_minimo")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ingrediente;
    }

    public void atualizarIngrediente(Ingrediente ingrediente) {
        String sql = "UPDATE ingredientes SET nome = ?, estoque_atual = ?, unidade_medida = ?, estoque_minimo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ingrediente.getNome());
            pstmt.setDouble(2, ingrediente.getEstoqueAtual());
            pstmt.setString(3, ingrediente.getUnidadeMedida());
            pstmt.setDouble(4, ingrediente.getEstoqueMinimo());
            pstmt.setInt(5, ingrediente.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void darBaixaEstoque(int ingredienteId, double quantidadeParaSubtrair) {
        String sql = "UPDATE ingredientes SET estoque_atual = estoque_atual - ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, quantidadeParaSubtrair);
            pstmt.setInt(2, ingredienteId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao dar baixa no estoque para o ingrediente ID " + ingredienteId + ": " + e.getMessage());
        }
    }

    public void removerIngrediente(int id) {
        String sql = "DELETE FROM ingredientes WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}