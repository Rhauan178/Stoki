package com.seu.restaurante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private int id;
    private String nome;
    private String login;
    private String senha;
    private String cargo; // Ex: "Gerente", "Garcom"

    public Usuario(int id, String nome, String login, String senha, String cargo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.cargo = cargo;
    }

    public Usuario(String nome, String login, String senha, String cargo) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.cargo = cargo;
    }

    // Getters para acessar os dados
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getCargo() { return cargo; }
}