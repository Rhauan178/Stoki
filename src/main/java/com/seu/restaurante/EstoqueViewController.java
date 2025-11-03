package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class EstoqueViewController {

    @FXML private TableView<Ingrediente> tabelaIngredientes;
    @FXML private TableColumn<Ingrediente, Integer> colunaId;
    @FXML private TableColumn<Ingrediente, String> colunaNome;
    @FXML private TableColumn<Ingrediente, Double> colunaEstoqueAtual;
    @FXML private TableColumn<Ingrediente, String> colunaUnidade;
    @FXML private TableColumn<Ingrediente, Double> colunaEstoqueMinimo;

    @FXML private TextField campoNome;
    @FXML private TextField campoEstoqueAtual;
    @FXML private TextField campoUnidade;
    @FXML private TextField campoEstoqueMinimo;

    @FXML private Button botaoSalvar;
    @FXML private Button botaoRemover;
    @FXML private Button botaoLimpar;

    private final IngredienteDAO ingredienteDAO = new IngredienteDAO();
    private Ingrediente ingredienteSelecionadoParaEdicao = null;

    @FXML
    public void initialize() {
        // Configura os ícones dos botões
        botaoSalvar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        botaoRemover.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        botaoLimpar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaEstoqueAtual.setCellValueFactory(new PropertyValueFactory<>("estoqueAtual"));
        colunaUnidade.setCellValueFactory(new PropertyValueFactory<>("unidadeMedida"));
        colunaEstoqueMinimo.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));
        tabelaIngredientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> preencherCampos(newValue)
        );

        ingredienteDAO.criarTabela();
        carregarTabela();
        limparCampos();
    }

    private void carregarTabela() {
        List<Ingrediente> ingredientes = ingredienteDAO.listarTodos();
        tabelaIngredientes.setItems(FXCollections.observableArrayList(ingredientes));
    }

    private void preencherCampos(Ingrediente ingrediente) {
        ingredienteSelecionadoParaEdicao = ingrediente;
        if (ingrediente != null) {
            campoNome.setText(ingrediente.getNome());
            campoEstoqueAtual.setText(String.valueOf(ingrediente.getEstoqueAtual()));
            campoUnidade.setText(ingrediente.getUnidadeMedida());
            campoEstoqueMinimo.setText(String.valueOf(ingrediente.getEstoqueMinimo()));
            botaoSalvar.setText("Salvar Alterações");
        } else {
            limparCampos();
        }
    }

    @FXML
    private void salvarIngrediente() {
        try {
            String nome = campoNome.getText().trim();
            double estoqueAtual = Double.parseDouble(campoEstoqueAtual.getText().trim());
            String unidade = campoUnidade.getText().trim();
            double estoqueMinimo = Double.parseDouble(campoEstoqueMinimo.getText().trim());

            if (nome.isEmpty() || unidade.isEmpty()) {
                AlertaUtil.mostrarErro("Campos Obrigatórios", "Nome e Unidade são obrigatórios.");
                return;
            }

            if (ingredienteSelecionadoParaEdicao != null) {
                ingredienteSelecionadoParaEdicao.setNome(nome);
                ingredienteSelecionadoParaEdicao.setEstoqueAtual(estoqueAtual);
                ingredienteSelecionadoParaEdicao.setUnidadeMedida(unidade);
                ingredienteSelecionadoParaEdicao.setEstoqueMinimo(estoqueMinimo);
                ingredienteDAO.atualizarIngrediente(ingredienteSelecionadoParaEdicao);
                AlertaUtil.mostrarInformacao("Sucesso", "Ingrediente atualizado com sucesso!");
            } else {
                Ingrediente novoIngrediente = new Ingrediente(nome, estoqueAtual, unidade, estoqueMinimo);
                ingredienteDAO.adicionarIngrediente(novoIngrediente);
                AlertaUtil.mostrarInformacao("Sucesso", "Novo ingrediente adicionado com sucesso!");
            }
            carregarTabela();
            limparCampos();
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Erro de Formato", "Os campos de quantidade devem ser números válidos.");
        }
    }

    @FXML
    private void removerIngredienteSelecionado() {
        if (ingredienteSelecionadoParaEdicao != null) {
            boolean confirmado = AlertaUtil.mostrarConfirmacao("Confirmar Remoção",
                    "Tem a certeza que quer remover o ingrediente '" + ingredienteSelecionadoParaEdicao.getNome() + "'?");
            if (confirmado) {
                ingredienteDAO.removerIngrediente(ingredienteSelecionadoParaEdicao.getId());
                carregarTabela();
                limparCampos();
            }
        } else {
            AlertaUtil.mostrarErro("Nenhum Ingrediente Selecionado", "Por favor, selecione um ingrediente na tabela para remover.");
        }
    }

    @FXML
    private void limparCampos() {
        tabelaIngredientes.getSelectionModel().clearSelection();
        ingredienteSelecionadoParaEdicao = null;
        campoNome.clear();
        campoEstoqueAtual.clear();
        campoUnidade.clear();
        campoEstoqueMinimo.clear();
        botaoSalvar.setText("Adicionar Novo Ingrediente");
    }

    @FXML
    private void voltarParaMesas() {
        App.trocarDeTela("MesaView.fxml", "Controle de Mesas");
    }
}