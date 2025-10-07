package com.seu.restaurante;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {
    @FXML private TextField campoUsuario;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelErro;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void processarLogin() {
        String login = campoUsuario.getText().trim();
        String senha = campoSenha.getText().trim();
        Usuario usuarioDoBanco = usuarioDAO.buscarPorLogin(login);

        if (usuarioDoBanco != null && usuarioDoBanco.getSenha().equals(senha)) {
            System.out.println("Login bem-sucedido para o usuário: " + usuarioDoBanco.getNome());
            App.setUsuarioLogado(usuarioDoBanco); // Guarda o usuário que acabou de logar
            labelErro.setText("");
            // Troca para a tela principal
            App.trocarDeTela("MesaView.fxml", "Controle de Mesas");
        } else {
            // Se o usuário não existe ou a senha está errada, mostra a mensagem.
            labelErro.setText("Usuário ou senha inválidos.");
        }
    }
}