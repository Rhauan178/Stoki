package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Tooltip;

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
    @FXML private Button botaoRemover;
    @FXML private Button botaoLimpar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioSelecionadoParaEdicao = null;

    @FXML
    public void initialize() {
        botaoSalvar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        botaoRemover.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        botaoLimpar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));

        Tooltip.install(botaoSalvar, new Tooltip("Salvar funcionário novo ou alterações"));
        Tooltip.install(botaoRemover, new Tooltip("Remover o funcionário selecionado"));
        Tooltip.install(botaoLimpar, new Tooltip("Limpar campos / Cancelar edição"));

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
        String nome = campoNome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText().trim();
        String cargo = campoCargo.getText().trim();

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty() || cargo.isEmpty()) {
            AlertaUtil.mostrarErro("Campos Obrigatórios", "Todos os campos devem ser preenchidos.");
            return;
        }

        if (usuarioSelecionadoParaEdicao != null) {
            Usuario usuarioAtualizado = new Usuario(usuarioSelecionadoParaEdicao.getId(), nome, login, senha, cargo);
            usuarioDAO.atualizarUsuario(usuarioAtualizado);
            AlertaUtil.mostrarInformacao("Sucesso", "Funcionário '" + nome + "' atualizado com sucesso!");
        } else {
            Usuario novoUsuario = new Usuario(nome, login, senha, cargo);
            usuarioDAO.adicionarUsuario(novoUsuario);
            AlertaUtil.mostrarInformacao("Sucesso", "Novo funcionário '" + nome + "' adicionado com sucesso!");
        }

        carregarTabela();
        limparCampos();
    }

    @FXML
    private void removerFuncionarioSelecionado() {
        if (usuarioSelecionadoParaEdicao != null) {
            Usuario usuarioLogado = App.getUsuarioLogado();

            if (usuarioLogado != null && usuarioLogado.getId() == usuarioSelecionadoParaEdicao.getId()) {
                AlertaUtil.mostrarErro("Ação Inválida", "Você não pode remover o seu próprio utilizador enquanto está logado.");
                return;
            }

            boolean confirmado = AlertaUtil.mostrarConfirmacao("Confirmar Remoção",
                    "Tem a certeza que quer remover o funcionário '" + usuarioSelecionadoParaEdicao.getNome() + "'?");

            if (confirmado) {
                usuarioDAO.removerUsuario(usuarioSelecionadoParaEdicao.getId());
                carregarTabela();
                limparCampos();
                AlertaUtil.mostrarInformacao("Sucesso", "Funcionário removido.");
            }
        } else {
            AlertaUtil.mostrarErro("Nenhum Funcionário Selecionado", "Por favor, selecione um funcionário na tabela para remover.");
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

    @FXML
    private void voltarParaMesas() {
        App.trocarDeTela("MesaView.fxml", "Controle de Mesas");
    }
}