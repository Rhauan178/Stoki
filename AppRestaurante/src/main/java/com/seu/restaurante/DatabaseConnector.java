package com.seu.restaurante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // --- NOVAS INFORMAÇÕES DE CONEXÃO PARA O MYSQL ---

    // A URL agora aponta para o servidor MySQL local (localhost), na porta padrão (3306),
    // e para a base de dados que criámos (restaurante_db).
    // A parte "?serverTimezone=UTC" é importante para evitar problemas com fuso horário.
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante_db?serverTimezone=UTC";

    // Utilizador padrão do XAMPP
    private static final String USER = "root";

    // Senha padrão do XAMPP (vazia)
    private static final String PASSWORD = "";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar à base de dados MySQL: " + e.getMessage());
            // É uma boa ideia mostrar um alerta de erro aqui numa aplicação real
            // AlertaUtil.mostrarErro("Erro de Conexão", "Não foi possível conectar à base de dados. Verifique se o XAMPP está a funcionar.");
        }
        return conn;
    }
}