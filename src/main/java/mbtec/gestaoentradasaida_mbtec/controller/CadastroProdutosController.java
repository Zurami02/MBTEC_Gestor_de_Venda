package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import mbtec.gestaoentradasaida_mbtec.DAO.CategoriaDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.ProdutosDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Categoria;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.RelatorioUtil;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class CadastroProdutosController implements Initializable {

    @FXML
    private TableColumn<Produtos, Integer> colunaCodigo;

    @FXML
    private TableColumn<Produtos, String> colunaDescricao;

    @FXML
    private TableColumn<Produtos, Integer> colunaQTD;

    @FXML
    private TableColumn<Produtos, String> colunacategoria;

    @FXML
    private TableColumn<Produtos, Double> colunapreco;

    @FXML
    private TableView<Produtos> tableviewProdutos;

    @FXML
    private ComboBox<Categoria> txtcomboboxCategoriaProduto;

    @FXML
    private TextField txtdescricao;

    @FXML
    private TextField txtpesquisa;

    @FXML
    private TextField txtquantidade;

    @FXML
    private TextField txtpreco;

    @FXML
    private TextField txtcategoria;

    private final ProdutosDAO produtosDAO = new ProdutosDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Produtos produto;
    private List<Produtos> produtosList;
    private ObservableList<Produtos> observableProdutosList;
    private List<Categoria> categoriaList;
    private ObservableList<Categoria> observableCategoriaList;

    @FXML
    void btnPesquisaProduto(ActionEvent event) {
        String pesquisa = txtpesquisa.getText();
        List<Produtos> resultadoencontrado = produtosDAO.buscarPorNome(pesquisa);
        tableviewProdutos.getItems().clear();
        limparCampos();
        if (!resultadoencontrado.isEmpty()) {
            tableviewProdutos.getItems().addAll(resultadoencontrado);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Produto não encontrado");
            alert.setHeaderText(null);
            alert.setContentText("Nenhum produto encontrado com esse nome.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );
            alert.showAndWait();
        }
    }

    @FXML
    void btndeletarProduto(ActionEvent event) {
        produto = tableviewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir o produto");
            alert.setContentText("Tem certeza que deseja excluir " + produto.getDescricao_produto() + "?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                produtosDAO.remover(produto);
                carregarTableViewProdutos();
                limparCampos();
            }
        }else {
            AlertaUtil.mostrarErro("Falha na exclusao de dados","Selecione os dados na tabela");
        }
    }

    @FXML
    void btnInserirCategoria(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/cadastroCategoria.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Cadastro de Categoria");
            stage.centerOnScreen();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace(); // Trate a exceção adequadamente
        }
    }

    @FXML
    void btneditarProduto(ActionEvent event) {
        Produtos produto = tableviewProdutos.getSelectionModel().getSelectedItem();
        if (produto == null) {
            AlertaUtil.mostrarErro("Falha na Atualização", "Selecione um produto na tabela.");
            return;
        }

        int quantidade;
        double preco;
        try {
            quantidade = Integer.parseInt(txtquantidade.getText());
            preco = Double.parseDouble(txtpreco.getText());
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Erro de Entrada", "Quantidade ou preço inválido!");
            return;
        }

        String descricao = txtdescricao.getText();
        Categoria categoriaSelecionada = txtcomboboxCategoriaProduto.getValue();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação de Atualização");
        confirm.setHeaderText("Você está prestes a atualizar o produto");
        confirm.setContentText("Tem certeza que deseja atualizar " + produto.getDescricao_produto() + "?");
        Stage stage = (Stage) confirm.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(
                AlertaUtil.class.getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png"))));

        Optional<ButtonType> resultado = confirm.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) return;

        if (!produto.getDescricao_produto().equals(descricao) && produtosDAO.existeProduto(descricao)) {
            AlertaUtil.mostrarErro("Produto já existe", "Produto com este nome já está cadastrado!");
            return;
        }

        if (!houveAlteracao(produto, descricao, quantidade, preco, categoriaSelecionada)) {
            AlertaUtil.mostrarInfo("Erro na Atualizacao","Nenhuma alteração foi feita no produto.");
            return;
        }

        produto.setDescricao_produto(descricao);
        produto.setQuantidade_produto(quantidade);
        produto.setPreco(preco);
        produto.setCategoria(categoriaSelecionada);

        produtosDAO.editar(produto);
        carregarTableViewProdutos();
        limparCampos();
    }

    private boolean houveAlteracao(Produtos produto, String descricao, int quantidade,
                                   double preco, Categoria categoria) {
        return !produto.getDescricao_produto().equals(descricao)
                || produto.getQuantidade_produto() != quantidade
                || produto.getPreco() != preco
                || !Objects.equals(produto.getCategoria(), categoria);
    }

    @FXML
    void btnsalvarProduto(ActionEvent event) {
        if (validarEntradadedados()) {
            Categoria categoriaSelecionada = txtcomboboxCategoriaProduto.getValue();
            String produtoInserido = txtdescricao.getText();
            if (produtosDAO.existeProduto(produtoInserido)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no Cadastro");
                alert.setHeaderText("Campo descricao invalido");
                alert.setContentText("Produto ja existe!");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(
                        new Image(Objects.requireNonNull(AlertaUtil.class.
                                getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
                );
                alert.show();
                return;
            }
            produto = new Produtos();
            produto.setDescricao_produto(txtdescricao.getText());
            produto.setQuantidade_produto(Integer.parseInt(txtquantidade.getText()));
            produto.setPreco(Double.parseDouble(txtpreco.getText()));
            produto.setCategoria(categoriaSelecionada);
            produtosDAO.inserir(produto);
            limparCampos();
            carregarTableViewProdutos();
        }
    }

    @FXML
    void gerarRelatorioProdutos(ActionEvent event) {
        try {
            InputStream is = getClass().getResourceAsStream("/relatoriosjasper/Relatorio_de_produtos.jasper");
            System.out.println("Arquivo encontrado? " + (is != null));
            ProdutosDAO dao = new ProdutosDAO();
            List<Produtos> dados = dao.listar();
            if (dados.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Nenhum dado encontrado para o período selecionado.").show();
                return;
            }
            // Converter a lista para DataSource do Jasper
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // Sem parâmetros -> apenas mandar null
            Map<String, Object> parametros = new HashMap<>();

            // Adiciona a referência da classe para localizar a imagem
            parametros.put("REPORT_CLASS", RelatorioUtil.class);

            // Preencher relatório
            JasperPrint jp = JasperFillManager.fillReport(is, parametros, dataSource);

            // Mostrar o relatório na tela
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Relatório de Produtos");
            viewer.setVisible(true);

        } catch (
                Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao gerar relatório: " + e.getMessage()).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarCombboxProdutosAutoCompletado();
        carregarTableViewProdutos();
        tableviewProdutos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtdescricao.setText(String.valueOf(newSelection.getDescricao_produto()));
                txtquantidade.setText(String.valueOf(newSelection.getQuantidade_produto()));
                txtpreco.setText(String.valueOf(newSelection.getPreco()));
                txtcomboboxCategoriaProduto.setValue(newSelection.getCategoria());
            }
        });
    }

    /**
     *Validar entrada de Dados no Cadastro
     */
    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (txtdescricao.getText() == null || txtdescricao.getText().isEmpty()) {
            erroMessage += "Plano de conta invalido!\n";
        }
        if (txtquantidade.getText() == null || txtquantidade.getText().isEmpty()) {
            erroMessage += "A quantidade invalida!\n";
        }
        if (txtpreco.getText() == null || txtpreco.getText().isEmpty()) {
            erroMessage += "O preco invalido!\n";
        }
        if (txtcomboboxCategoriaProduto.getValue() == null || txtcomboboxCategoriaProduto.getValue() == null) {
            erroMessage += "A categoria invalida!\n";
        }

        if (erroMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no Cadastro");
            alert.setHeaderText("Campos invalidos, por favor, corrija...");
            alert.setContentText(erroMessage);
            alert.show();
            return false;
        }
    }

    /**
     * carrega Combobox de produtos
     */
    public void carregarCombboxProdutosAutoCompletado() {
        // Converte a lista de produtos em uma lista observável
        categoriaList = new CategoriaDAO().listar(); // ou seu método
        observableCategoriaList = FXCollections.observableArrayList(categoriaList);
        txtcomboboxCategoriaProduto.setItems(observableCategoriaList);

        // Define um filtro dinâmico
        FilteredList<Categoria> categoriaFiltrados = new FilteredList<>(observableCategoriaList, c -> true);

        txtcomboboxCategoriaProduto.setItems(categoriaFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        txtcomboboxCategoriaProduto.setEditable(true);
        txtcomboboxCategoriaProduto.getEditor().textProperty().addListener((obs,
                                                                            oldValue, newValue) -> {
            final String filtro = newValue.toLowerCase();

            // Aplica filtro
            categoriaFiltrados.setPredicate(categoria -> {
                if (filtro == null || filtro.isEmpty()) {
                    return true;
                }
                return categoria.getDescricao_categoria().toLowerCase().contains(filtro);
            });

            // Mostra o menu dropdown automaticamente
            if (!txtcomboboxCategoriaProduto.isShowing()) {
                txtcomboboxCategoriaProduto.show();
            }
        });

        // Corrige o comportamento de seleção para manter o objeto Produtos real
        txtcomboboxCategoriaProduto.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getDescricao_categoria() : "";
            }

            @Override
            public Categoria fromString(String string) {
                return observableCategoriaList.stream()
                        .filter(c -> c.getDescricao_categoria().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void carregarTableViewProdutos() {
        colunaCodigo.setCellValueFactory(new PropertyValueFactory<>("idproduto"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao_produto"));
        colunaQTD.setCellValueFactory(new PropertyValueFactory<>("quantidade_produto"));
        colunapreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        //colunacategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunacategoria.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoria().getDescricao_categoria()));


        //Listener para txtProcuraNome
        txtpesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewProdutos();
            }
        });

        produtosList = produtosDAO.listar();

        observableProdutosList = FXCollections.observableArrayList(produtosList);
        tableviewProdutos.setItems(observableProdutosList);
    }

    private void limparCampos() {
        txtdescricao.clear();
        txtquantidade.clear();
        txtpreco.clear();
        txtcomboboxCategoriaProduto.setValue(null);
    }
}
