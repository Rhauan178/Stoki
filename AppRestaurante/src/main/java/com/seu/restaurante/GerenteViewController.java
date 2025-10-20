package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class GerenteViewController {

    // --- Componentes da Gestão de Cardápio (Esquerda) ---
    @FXML private TableView<ItemCardapio> tabelaCardapio;
    @FXML private TableColumn<ItemCardapio, Integer> colunaId;
    @FXML private TableColumn<ItemCardapio, String> colunaNome;
    @FXML private TableColumn<ItemCardapio, BigDecimal> colunaPreco;
    @FXML private TextField campoId;
    @FXML private TextField campoNome;
    @FXML private TextField campoPreco;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoCategoria;
    @FXML private Button botaoAdicionarSalvar;
    @FXML private Button botaoRemover;
    @FXML private Button botaoLimpar;

    // --- Componentes da Ficha Técnica (Direita) ---
    @FXML private Label labelFichaTecnicaTitulo;
    @FXML private ListView<FichaTecnicaItem> listaIngredientesDaReceita;
    @FXML private ComboBox<Ingrediente> comboIngredientesDisponiveis;
    @FXML private TextField campoQuantidadeUsada;
    @FXML private Button botaoAdicionarIngrediente;
    @FXML private Button botaoRemoverIngrediente;
    @FXML private Button botaoSalvarFicha;

    // --- DAOs e Variáveis de Estado ---
    private final ItemCardapioDAO itemCardapioDAO = new ItemCardapioDAO();
    private final IngredienteDAO ingredienteDAO = new IngredienteDAO();
    private final FichaTecnicaDAO fichaTecnicaDAO = new FichaTecnicaDAO();
    private ItemCardapio itemSelecionadoParaEdicao = null;
    private final ObservableList<FichaTecnicaItem> fichaTecnicaAtual = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // --- APARÊNCIA ---
        botaoAdicionarSalvar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        botaoRemover.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        botaoLimpar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        botaoAdicionarIngrediente.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        botaoRemoverIngrediente.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MINUS));
        botaoSalvarFicha.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_SQUARE));

        Tooltip.install(botaoAdicionarSalvar, new Tooltip("Salvar item novo ou alterações"));
        Tooltip.install(botaoRemover, new Tooltip("Remover o item do cardápio selecionado"));
        Tooltip.install(botaoLimpar, new Tooltip("Limpar campos para um novo item"));
        Tooltip.install(botaoAdicionarIngrediente, new Tooltip("Adicionar ingrediente à receita"));
        Tooltip.install(botaoRemoverIngrediente, new Tooltip("Remover ingrediente da receita"));
        Tooltip.install(botaoSalvarFicha, new Tooltip("Salvar a ficha técnica"));

        // --- COMPORTAMENTO ---
        configurarTabelaCardapio();
        listaIngredientesDaReceita.setItems(fichaTecnicaAtual);

        // --- DADOS ---
        itemCardapioDAO.criarTabela();
        ingredienteDAO.criarTabela();
        fichaTecnicaDAO.criarTabela();
        carregarIngredientesDisponiveis();
        carregarTabelaCardapio();
        limparCampos();
    }

    private void configurarTabelaCardapio() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        tabelaCardapio.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    preencherCamposItemCardapio(newValue);
                    carregarFichaTecnica(newValue);
                }
        );
    }

    private void carregarTabelaCardapio() {
        List<ItemCardapio> itens = itemCardapioDAO.listarTodos();
        tabelaCardapio.setItems(FXCollections.observableArrayList(itens));
    }

    private void carregarIngredientesDisponiveis() {
        List<Ingrediente> ingredientes = ingredienteDAO.listarTodos();
        comboIngredientesDisponiveis.setItems(FXCollections.observableArrayList(ingredientes));
    }

    private void preencherCamposItemCardapio(ItemCardapio item) {
        itemSelecionadoParaEdicao = item;
        if (item != null) {
            campoId.setText(String.valueOf(item.getId()));
            campoId.setDisable(true);
            campoNome.setText(item.getNome());
            campoPreco.setText(item.getPreco().toPlainString());
            campoDescricao.setText(item.getDescricao());
            campoCategoria.setText(item.getCategoria());
            botaoAdicionarSalvar.setText("Salvar Alterações");
        } else {
            limparCampos();
        }
    }

    private void carregarFichaTecnica(ItemCardapio item) {
        fichaTecnicaAtual.clear();
        if (item != null) {
            labelFichaTecnicaTitulo.setText("Ficha Técnica para: " + item.getNome());
            List<FichaTecnicaItem> receita = fichaTecnicaDAO.buscarPorItemId(item.getId());
            fichaTecnicaAtual.addAll(receita);
        } else {
            labelFichaTecnicaTitulo.setText("Ficha Técnica");
        }
    }

    @FXML
    private void adicionarIngredienteNaReceita() {
        Ingrediente ingrediente = comboIngredientesDisponiveis.getValue();
        try {
            double quantidade = Double.parseDouble(campoQuantidadeUsada.getText());
            if (ingrediente != null && quantidade > 0 && itemSelecionadoParaEdicao != null) {
                boolean jaExiste = fichaTecnicaAtual.stream().anyMatch(item -> item.getIngrediente().getId() == ingrediente.getId());
                if (!jaExiste) {
                    fichaTecnicaAtual.add(new FichaTecnicaItem(ingrediente, quantidade));
                    comboIngredientesDisponiveis.setValue(null);
                    campoQuantidadeUsada.clear();
                } else {
                    AlertaUtil.mostrarErro("Erro", "Este ingrediente já está na receita.");
                }
            }
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Erro de Formato", "A quantidade deve ser um número.");
        }
    }

    @FXML
    private void removerIngredienteDaReceita() {
        FichaTecnicaItem selecionado = listaIngredientesDaReceita.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            fichaTecnicaAtual.remove(selecionado);
        }
    }

    @FXML
    private void salvarFichaTecnica() {
        if (itemSelecionadoParaEdicao != null) {
            fichaTecnicaDAO.salvarFichaTecnica(itemSelecionadoParaEdicao.getId(), fichaTecnicaAtual);
            AlertaUtil.mostrarInformacao("Sucesso", "Ficha técnica para '" + itemSelecionadoParaEdicao.getNome() + "' salva com sucesso!");
        } else {
            AlertaUtil.mostrarErro("Erro", "Nenhum item do cardápio selecionado.");
        }
    }

    @FXML
    private void salvarItem() {
        try {
            String nome = campoNome.getText();
            BigDecimal preco = new BigDecimal(campoPreco.getText());
            String descricao = campoDescricao.getText();
            String categoria = campoCategoria.getText();

            if (nome.isEmpty() || categoria.isEmpty()) {
                AlertaUtil.mostrarErro("Campos Obrigatórios", "Nome e categoria são obrigatórios.");
                return;
            }

            if (itemSelecionadoParaEdicao != null) {
                ItemCardapio itemAtualizado = new ItemCardapio(itemSelecionadoParaEdicao.getId(), nome, descricao, preco, categoria, true);
                itemCardapioDAO.atualizarItem(itemAtualizado);
            } else {
                ItemCardapio novoItem = new ItemCardapio(nome, descricao, preco, categoria, true);
                int novoId = itemCardapioDAO.adicionarNovoItem(novoItem);
                novoItem.setId(novoId);
            }
            carregarTabelaCardapio();
            limparCampos();
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Erro de Formato", "ID e Preço devem ser números válidos.");
        }
    }

    @FXML
    private void removerItemSelecionado() {
        if (itemSelecionadoParaEdicao != null) {
            boolean confirmado = AlertaUtil.mostrarConfirmacao("Confirmar Remoção", "Tem a certeza que quer remover o item '" + itemSelecionadoParaEdicao.getNome() + "' do cardápio?");
            if (confirmado) {
                itemCardapioDAO.removerItem(itemSelecionadoParaEdicao.getId());
                carregarTabelaCardapio();
                limparCampos();
            }
        } else {
            AlertaUtil.mostrarErro("Nenhum Item Selecionado", "Por favor, selecione um item na tabela para remover.");
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
    private void voltarParaMesas() {
        App.trocarDeTela("MesaView.fxml", "Controle de Mesas");
    }

    @FXML
    private void abrirRelatorios() {
        abrirNovaJanela("/com/seu/restaurante/RelatorioView.fxml", "Relatório de Vendas");
    }

    @FXML
    private void abrirGerenciamentoFuncionarios() {
        abrirNovaJanela("/com/seu/restaurante/FuncionarioView.fxml", "Gestão de Funcionários");
    }

    @FXML
    private void abrirGerenciamentoEstoque() {
        abrirNovaJanela("/com/seu/restaurante/EstoqueView.fxml", "Gestão de Estoque");
    }

    private void abrirNovaJanela(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/com/seu/restaurante/styles.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}