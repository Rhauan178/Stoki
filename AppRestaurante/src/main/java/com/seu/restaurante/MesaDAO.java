package com.seu.restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS mesas ("
                + " numero integer PRIMARY KEY,"
                + " status text NOT NULL"
                + ");";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // --- CREATE ---
    public void adicionarMesa(Mesa mesa) {
        String sql = "INSERT INTO mesas(numero, status) VALUES(?,?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mesa.getNumero());
            pstmt.setString(2, mesa.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // --- READ ---
    public List<Mesa> carregarTodasAsMesas() {
        String sql = "SELECT * FROM mesas ORDER BY numero";
        List<Mesa> mesas = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mesa mesa = new Mesa(
                        rs.getInt("numero"),
                        StatusMesa.valueOf(rs.getString("status"))
                );
                mesas.add(mesa);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return mesas;
    }

    // --- UPDATE ---
    public void atualizarStatusMesa(Mesa mesa) {
        String sql = "UPDATE mesas SET status = ? WHERE numero = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mesa.getStatus().name());
            pstmt.setInt(2, mesa.getNumero());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // --- DELETE ---
    public void removerMesa(int numeroMesa) {
        // Num sistema real, verificaríamos se a mesa tem pedidos abertos antes de apagar.
        // Por agora, vamos remover a mesa diretamente.
        String sql = "DELETE FROM mesas WHERE numero = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}