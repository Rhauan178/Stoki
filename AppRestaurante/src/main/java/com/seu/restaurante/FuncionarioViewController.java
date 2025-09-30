package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class FuncionarioViewController {

    @FXML private TableView<Usuario> tabelaFuncionarios;
    @FXML private TableColumn<Usuario, Integer> colunaId;
    @FXML private TableColumn<Usuario, String> colunaNome;
    @FXML private TableColumn<Usuario, String> colunaLogin;
    @FXML private TableColumn<Usuario, String> colunaCargo;

    @FXML private TextField campoNome;
    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private TextField campoCargo;
    @FXML private Button botaoSalvar;

    // --- LINHAS EM FALTA ADICIONADAS AQUI ---
    @FXML private Button botaoRemover;
    @FXML private Button botaoLimpar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioSelecionadoParaEdicao = null;

    @FXML
    public void initialize() {
        // Define o ícone para cada botão
        botaoSalvar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        botaoRemover.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        botaoLimpar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));

        // O resto do seu código no initialize continua o mesmo
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colunaCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));

        tabelaFuncionarios.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> preencherCampos(newValue)
        );

        carregarTabela();
        limparCampos();
    }

    // O resto dos seus métodos (carregarTabela, salvarFuncionario, etc.) continuam os mesmos
    private void carregarTabela() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        ObservableList<Usuario> observableList = FXCollections.observableArrayList(usuarios);
        tabelaFuncionarios.setItems(observableList);
    }

    private void preencherCampos(Usuario usuario) {
        usuarioSelecionadoParaEdicao = usuario;
        if (usuario != null) {
            campoNome.setText(usuario.getNome());
            campoLogin.setText(usuario.getLogin());
            campoSenha.setText(usuario.getSenha());
            campoCargo.setText(usuario.getCargo());
            botaoSalvar.setText("Salvar Alterações");
        } else {
            limparCampos();
        }
    }

    @FXML
    private void salvarFuncionario() {
        String nome = campoNome.getText();
        String login = campoLogin.getText();
        String senha = campoSenha.getText();
        String cargo = campoCargo.getText();

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty() || cargo.isEmpty()) {
            System.err.println("Todos os campos são obrigatórios.");
            return;
        }

        if (usuarioSelecionadoParaEdicao != null) {
            Usuario usuarioAtualizado = new Usuario(usuarioSelecionadoParaEdicao.getId(), nome, login, senha, cargo);
            usuarioDAO.atualizarUsuario(usuarioAtualizado);
        } else {
            Usuario novoUsuario = new Usuario(nome, login, senha, cargo);
            usuarioDAO.adicionarUsuario(novoUsuario);
        }

        carregarTabela();
        limparCampos();
    }

    @FXML
    private void removerFuncionarioSelecionado() {
        if (usuarioSelecionadoParaEdicao != null) {
            Usuario usuarioLogado = App.getUsuarioLogado();
            if (usuarioLogado != null && usuarioLogado.getId() == usuarioSelecionadoParaEdicao.getId()) {
                System.err.println("Não pode remover o seu próprio utilizador enquanto está logado.");
                return;
            }
            usuarioDAO.removerUsuario(usuarioSelecionadoParaEdicao.getId());
            carregarTabela();
            limparCampos();
        } else {
            System.err.println("Nenhum funcionário selecionado para remover.");
        }
    }

    @FXML
    private void limparCampos() {
        tabelaFuncionarios.getSelectionModel().clearSelection();
        usuarioSelecionadoParaEdicao = null;
        campoNome.clear();
        campoLogin.clear();
        campoSenha.clear();
        campoCargo.clear();
        botaoSalvar.setText("Adicionar Novo Funcionário");
    }
}