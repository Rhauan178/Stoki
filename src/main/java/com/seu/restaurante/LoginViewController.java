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
            App.setUsuarioLogado(usuarioDoBanco);
            labelErro.setText("");

            String cargo = usuarioDoBanco.getCargo();

            if ("Gerente".equalsIgnoreCase(cargo) || "Garçom".equalsIgnoreCase(cargo)) {
                App.trocarDeTela("MesaView.fxml", "Controle de Mesas");

            } else if ("Cozinheiro".equalsIgnoreCase(cargo)) {
                App.trocarDeTela("CozinhaView.fxml", "Painel da Cozinha");

            } else {
                AlertaUtil.mostrarErro("Erro de Permissão", "O seu cargo não tem uma tela de acesso definida.");
            }
        } else {
            labelErro.setText("Usuário ou senha inválidos.");
        }
    }
}