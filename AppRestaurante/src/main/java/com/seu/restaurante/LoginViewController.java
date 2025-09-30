package com.seu.restaurante;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoSenha;

    @FXML
    private Label labelErro;

    // 1. Criamos uma instância do nosso DAO de usuário.
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void processarLogin() {
        String login = campoUsuario.getText();
        String senha = campoSenha.getText();

        // 2. Buscamos na base de dados um usuário com o login inserido.
        Usuario usuarioDoBanco = usuarioDAO.buscarPorLogin(login);

        // 3. Verificamos se o usuário existe e se a senha corresponde.
        if (usuarioDoBanco != null && usuarioDoBanco.getSenha().equals(senha)) {
            // Login bem-sucedido
            System.out.println("Login bem-sucedido para o usuário: " + usuarioDoBanco.getNome());
            App.setUsuarioLogado(usuarioDoBanco); // Guarda o usuário que acabou de logar
            labelErro.setText("");

            // Troca para a tela principal
            App.trocarDeTela("MesaView.fxml", "Controle de Mesas");

        } else {
            // Se o usuário não existe ou a senha está errada, mostra a mesma mensagem.
            labelErro.setText("Usuário ou senha inválidos.");
        }
    }
}