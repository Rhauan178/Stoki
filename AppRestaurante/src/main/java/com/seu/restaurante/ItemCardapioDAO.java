package com.seu.restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.seu.restaurante.PedidoDAO.getItemCardapio;

public class ItemCardapioDAO {

    // Método para criar a tabela do cardápio se ela não existir
    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS cardapio ("
                + " id integer PRIMARY KEY,"
                + " nome text NOT NULL,"
                + " descricao text,"
                + " preco real NOT NULL,"
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

    // Método para adicionar um novo item ao cardápio
    public int adicionarItem(ItemCardapio item) {
        // A query agora não inclui o campo 'id'
        String sql = "INSERT INTO cardapio(nome, descricao, preco, categoria, disponivel) VALUES(?,?,?,?,?)";
        int idGerado = -1; // Valor padrão em caso de falha

        try (Connection conn = DatabaseConnector.connect();
             // Pedimos ao PreparedStatement para nos retornar as chaves geradas
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.getNome());
            pstmt.setString(2, item.getDescricao());
            pstmt.setBigDecimal(3, item.getPreco());
            pstmt.setString(4, item.getCategoria());
            pstmt.setBoolean(5, item.isDisponivel());

            int affectedRows = pstmt.executeUpdate();

            // Se a inserção funcionou, capturamos o ‘ID’ gerado
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

        // O método agora retorna o ID que foi gerado pela base de dados
        return idGerado;
    }

    // Adicione este método à sua classe ItemCardapioDAO
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

    // Adicione este método à sua classe ItemCardapioDAO
    public void atualizarItem(ItemCardapio item) {
        String sql = "UPDATE cardapio SET nome = ?, descricao = ?, preco = ?, categoria = ?, disponivel = ? "
                + "WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define os parâmetros para a secção SET
            pstmt.setString(1, item.getNome());
            pstmt.setString(2, item.getDescricao());
            pstmt.setBigDecimal(3, item.getPreco());
            pstmt.setString(4, item.getCategoria());
            pstmt.setBoolean(5, item.isDisponivel());
            // Define o parâmetro para a secção WHERE
            pstmt.setInt(6, item.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para listar todos os itens do cardápio
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

    // Adicione este método à sua classe ItemCardapioDAO
    public ItemCardapio buscarPorId(int id) {
        return getItemCardapio(id);
    }
}