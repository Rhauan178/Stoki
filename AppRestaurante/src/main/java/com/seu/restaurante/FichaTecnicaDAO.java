package com.seu.restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FichaTecnicaDAO {

    private final IngredienteDAO ingredienteDAO = new IngredienteDAO();

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS item_ingredientes ("
                + " id_item_cardapio INT NOT NULL,"
                + " id_ingrediente INT NOT NULL,"
                + " quantidade_usada DOUBLE NOT NULL,"
                + " FOREIGN KEY (id_item_cardapio) REFERENCES cardapio(id) ON DELETE CASCADE,"
                + " FOREIGN KEY (id_ingrediente) REFERENCES ingredientes(id) ON DELETE CASCADE,"
                + " PRIMARY KEY (id_item_cardapio, id_ingrediente)"
                + ");";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<FichaTecnicaItem> buscarPorItemId(int itemCardapioId) {
        String sql = "SELECT * FROM item_ingredientes WHERE id_item_cardapio = ?";
        List<FichaTecnicaItem> receita = new ArrayList<>();

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemCardapioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int ingredienteId = rs.getInt("id_ingrediente");
                double quantidade = rs.getDouble("quantidade_usada");

                Ingrediente ingrediente = ingredienteDAO.buscarPorId(ingredienteId);
                if (ingrediente != null) {
                    receita.add(new FichaTecnicaItem(ingrediente, quantidade));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return receita;
    }

    public void salvarFichaTecnica(int itemCardapioId, List<FichaTecnicaItem> fichaTecnica) {
        String sqlDelete = "DELETE FROM item_ingredientes WHERE id_item_cardapio = ?";
        String sqlInsert = "INSERT INTO item_ingredientes(id_item_cardapio, id_ingrediente, quantidade_usada) VALUES(?,?,?)";

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                pstmtDelete.setInt(1, itemCardapioId);
                pstmtDelete.executeUpdate();
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                for (FichaTecnicaItem item : fichaTecnica) {
                    pstmtInsert.setInt(1, itemCardapioId);
                    pstmtInsert.setInt(2, item.getIngrediente().getId());
                    pstmtInsert.setDouble(3, item.getQuantidadeUsada());
                    pstmtInsert.addBatch();
                }
                pstmtInsert.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}