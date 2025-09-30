package com.seu.restaurante;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import java.math.BigDecimal;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class GerenteViewController {

    @FXML private TableView<ItemCardapio> tabelaCardapio;
    @FXML private TableColumn<ItemCardapio, Integer> colunaId;
    @FXML private TableColumn<ItemCardapio, String> colunaNome;
    @FXML private TableColumn<ItemCardapio, BigDecimal> colunaPreco;

    @FXML private TextField campoId;
    @FXML private TextField campoNome;
    @FXML private TextField campoPreco;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoCategoria;

    // Adicionamos referências aos botões para podermos alterar o texto
    @FXML private Button botaoAdicionarSalvar;
    @FXML private Button botaoLimpar;

    private final ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
    private ItemCardapio itemSelecionadoParaEdicao = null;

    @FXML
    public void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        // 1. ADICIONAMOS UM "OUVINTE" À TABELA
        // Este código é executado sempre que um item na tabela é selecionado
        tabelaCardapio.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> preencherCampos(newValue)
        );

        carregarTabela();
        limparCampos(); // Garante que a tela comece no modo "novo item"
    }

    // 2. NOVO MÉTODO para preencher os campos com os dados de um item
    private void preencherCampos(ItemCardapio item) {
        itemSelecionadoParaEdicao = item;
        if (item != null) {
            // Modo Edição: preenche os campos e muda o texto do botão
            campoId.setText(String.valueOf(item.getId()));
            campoId.setDisable(true); // Impede que o ID seja editado
            campoNome.setText(item.getNome());
            campoPreco.setText(item.getPreco().toPlainString());
            campoDescricao.setText(item.getDescricao());
            campoCategoria.setText(item.getCategoria());
            botaoAdicionarSalvar.setText("Salvar Alterações");
        } else {
            // Modo Novo Item: limpa os campos
            limparCampos();
        }
    }

    private void carregarTabela() {
        List<ItemCardapio> itens = itemCardapioDAO.listarTodos();
        ObservableList<ItemCardapio> observableList = FXCollections.observableArrayList(itens);
        tabelaCardapio.setItems(observableList);
    }

    // 3. O antigo "adicionarNovoItem" agora é "salvarItem" e está mais inteligente
    @FXML
    private void salvarItem() {
        try {
            String nome = campoNome.getText();
            BigDecimal preco = new BigDecimal(campoPreco.getText());
            String descricao = campoDescricao.getText();
            String categoria = campoCategoria.getText();

            if (nome.isEmpty() || categoria.isEmpty()) {
                System.err.println("Nome e categoria são obrigatórios.");
                return;
            }

            if (itemSelecionadoParaEdicao != null) {
                // UPDATE: A lógica de atualização continua a mesma
                ItemCardapio itemAtualizado = new ItemCardapio(itemSelecionadoParaEdicao.getId(), nome, descricao, preco, categoria, true);
                itemCardapioDAO.atualizarItem(itemAtualizado);
            } else {
                // INSERT: Agora usamos o novo construtor sem ID
                ItemCardapio novoItem = new ItemCardapio(nome, descricao, preco, categoria, true);

                // O DAO agora retorna o ‘ID’ gerado, e nós usamo-lo para atualizar o nosso objeto
                int novoId = itemCardapioDAO.adicionarItem(novoItem);
                novoItem.setId(novoId);
                System.out.println("Novo item adicionado com ID: " + novoId);
            }

            carregarTabela();
            limparCampos();
        } catch (NumberFormatException e) {
            System.err.println("Erro: O preço deve ser um número válido.");
        }
    }

    @FXML
    private void removerItemSelecionado() {
        if (itemSelecionadoParaEdicao != null) {
            itemCardapioDAO.removerItem(itemSelecionadoParaEdicao.getId());
            carregarTabela();
            limparCampos();
        } else {
            System.err.println("Nenhum item selecionado para remover.");
        }
    }

    @FXML
    private void limparCampos() {
        tabelaCardapio.getSelectionModel().clearSelection();
        itemSelecionadoParaEdicao = null;
        campoId.clear();
        campoId.setDisable(false);
        campoNome.clear();
        campoPreco.clear();

        campoDescricao.clear();
        campoCategoria.clear();
        botaoAdicionarSalvar.setText("Adicionar Novo Item");
    }

    @FXML
    private void abrirRelatorios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/seu/restaurante/RelatorioView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("/com/seu/restaurante/styles.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Relatório de Vendas");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGerenciamentoFuncionarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/seu/restaurante/FuncionarioView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("/com/seu/restaurante/styles.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Gestão de Funcionários");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}