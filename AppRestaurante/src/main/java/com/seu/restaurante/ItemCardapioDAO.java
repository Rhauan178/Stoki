package com.seu.restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemCardapioDAO {

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS cardapio ("
                + " id INT PRIMARY KEY AUTO_INCREMENT,"
                + " nome text NOT NULL,"
                + " descricao text,"
                + " preco DECIMAL(10, 2) NOT NULL,"
                + " categoria text,"
                + " disponivel boolean NOT NULL"
                + ");";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int adicionarNovoItem(ItemCardapio item) {
        String sql = "INSERT INTO cardapio(nome, descricao, preco, categoria, disponivel) VALUES(?,?,?,?,?)";
        int idGerado = -1;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, item.getNome());
            pstmt.setString(2, item.getDescricao());
            pstmt.setBigDecimal(3, item.getPreco());
            pstmt.setString(4, item.getCategoria());
            pstmt.setBoolean(5, item.isDisponivel());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idGerado = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return idGerado;
    }

    public void adicionarItemComId(ItemCardapio item) {
        String sql = "INSERT INTO cardapio(id, nome, descricao, preco, categoria, disponivel) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, item.getId());
            pstmt.setString(2, item.getNome());
            pstmt.setString(3, item.getDescricao());
            pstmt.setBigDecimal(4, item.getPreco());
            pstmt.setString(5, item.getCategoria());
            pstmt.setBoolean(6, item.isDisponivel());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Ignora o erro se o item já existir
            if (!e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println(e.getMessage());
            }
        }
    }

    public List<ItemCardapio> listarTodos() {
        String sql = "SELECT * FROM cardapio";
        List<ItemCardapio> itens = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ItemCardapio item = new ItemCardapio(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("preco"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponivel")
                );
                itens.add(item);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return itens;
    }

    public ItemCardapio buscarPorId(int id) {
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

    public void atualizarItem(ItemCardapio item) {
        String sql = "UPDATE cardapio SET nome = ?, descricao = ?, preco = ?, categoria = ?, disponivel = ? "
                + "WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getNome());
            pstmt.setString(2, item.getDescricao());
            pstmt.setBigDecimal(3, item.getPreco());
            pstmt.setString(4, item.getCategoria());
            pstmt.setBoolean(5, item.isDisponivel());
            pstmt.setInt(6, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removerItem(int id) {
        String sql = "DELETE FROM cardapio WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}