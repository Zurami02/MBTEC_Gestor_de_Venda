package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mbtec.gestaoentradasaida_mbtec.domain.Fornecedores;
import mbtec.gestaoentradasaida_mbtec.domain.FornecedoresDAO;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class CadastroFornecedorController implements Initializable {
    @FXML
    private TableColumn<Fornecedores, Integer> colunaCodigoFornecedor;

    @FXML
    private TableColumn<Fornecedores, String> colunaDescricaoFornecedor;

    @FXML
    private TableColumn<Fornecedores, String> colunaFornecedor;

    @FXML
    private TableColumn<Fornecedores, Integer> colunaquantidadeFornecedor;

    @FXML
    private TableColumn<Fornecedores, Double> colunaprecoFornecedor;

    @FXML
    private TableView<Fornecedores> tableviewFornecedor;

    @FXML
    private TextField txtFornecedor;

    @FXML
    private TextField txtquantidadeDPFornecedor;

    @FXML
    private TextField txtdescricaoFornecedor;

    @FXML
    private TextField txtpesquisaFornecedor;

    @FXML
    private TextField txtprecoFornecedor;

    private final FornecedoresDAO fornecedoresDAO = new FornecedoresDAO();
    private Fornecedores fornecedores;
    private List<Fornecedores> fornecedoresList;
    private ObservableList<Fornecedores> observableFornecedoresList;

    @FXML
    void btnPesquisaFornecedor(ActionEvent event) {
        String pesquisa = txtpesquisaFornecedor.getText();
        List<Fornecedores> resultadoencontrado = fornecedoresDAO.buscarPorNome(pesquisa);
        tableviewFornecedor.getItems().clear();
        limparCampos();
        if (!resultadoencontrado.isEmpty()) {
            tableviewFornecedor.getItems().addAll(resultadoencontrado);
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
    void btndeletarFornecedor(ActionEvent event) {
        fornecedores = tableviewFornecedor.getSelectionModel().getSelectedItem();
        if (fornecedores != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir o fornecedor");
            alert.setContentText("Tem certeza que deseja excluir " + fornecedores.getFornecedor() + "?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                fornecedoresDAO.remover(fornecedores);
                carregarTableViewFornecedores();
                limparCampos();
            }
        } else {
            AlertaUtil.mostrarErro("Exclusao de dados", "Selecione dados na Tabela para excluir!");
        }
    }

    @FXML
    void btneditarFornecedor(ActionEvent event) {
        fornecedores = tableviewFornecedor.getSelectionModel().getSelectedItem();
        if (fornecedores == null) {
            AlertaUtil.mostrarErro("Atualizacao de dados", "Selecione dados na Tabela para editar!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Atualização");
        alert.setHeaderText("Você está prestes a atualizar o fornecedor");
        alert.setContentText("Tem certeza que deseja atualizar o fornecedor: " + fornecedores.getFornecedor() + "?");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
        );

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Dados atuais
            int idAtual = fornecedores.getIdfornecedor();
            String fornecedorAtual = fornecedores.getFornecedor();
            String dProdutoAtual = fornecedores.getDescricaoProduto();

            // Dados novos do formulário
            String fornecedorNovo = txtFornecedor.getText().trim();
            String dProdutoNovo = txtdescricaoFornecedor.getText().trim();
            int quantidadeNova = Integer.parseInt(txtquantidadeDPFornecedor.getText());
            double precoNovo = Double.parseDouble(txtprecoFornecedor.getText());

            // Verifica se já existe outro registro com mesma combinação fornecedor + descrição
            if (fornecedoresDAO.existeOutroCombinacao(fornecedorNovo, dProdutoNovo, idAtual)) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro de Duplicação");
                erro.setHeaderText("Fornecedor e descrição de produto já cadastrados");
                erro.setContentText("Já existe outro fornecedor com essa mesma descrição de produto.");
                stage = (Stage) erro.getDialogPane().getScene().getWindow();
                stage.getIcons().add(
                        new Image(Objects.requireNonNull(AlertaUtil.class.
                                getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
                );
                erro.show();
                return;
            }

            // Verifica se houve alteração
            boolean houveAlteracao =
                    !fornecedorAtual.equals(fornecedorNovo) ||
                            !dProdutoAtual.equals(dProdutoNovo) ||
                            fornecedores.getQuantidade() != quantidadeNova ||
                            fornecedores.getPreco() != precoNovo;

            if (houveAlteracao) {
                // Atualiza o objeto
                fornecedores.setFornecedor(fornecedorNovo);
                fornecedores.setDescricaoProduto(dProdutoNovo);
                fornecedores.setQuantidade(quantidadeNova);
                fornecedores.setPreco(precoNovo);

                // Salva no banco
                fornecedoresDAO.editar(fornecedores);
                carregarTableViewFornecedores();
                limparCampos();
            } else {
                // Nenhuma alteração detectada
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Nenhuma Alteração");
                info.setHeaderText(null);
                info.setContentText("Nenhuma alteração foi feita no fornecedor.");
                stage = (Stage) info.getDialogPane().getScene().getWindow();
                stage.getIcons().add(
                        new Image(Objects.requireNonNull(AlertaUtil.class.
                                getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
                );
                info.show();
            }
        }

    }

    @FXML
    void btnsalvarFornecedor(ActionEvent event) {
        if (validarEntradadedados()) {
            // Dados novos do formulário
            String fornecedorNovo = txtFornecedor.getText().trim();
            String dProdutoNovo = txtdescricaoFornecedor.getText().trim();

            // Verifica se já existe outro registro com mesma combinação fornecedor + descrição
            if (fornecedoresDAO.existeFornecedor(fornecedorNovo, dProdutoNovo)) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro de Duplicação");
                erro.setHeaderText("Fornecedor e descrição de produto já cadastrados");
                erro.setContentText("O fornecedor " + txtFornecedor.getText() + " Já existe com o mesmo produto.");
                Stage stage = (Stage) erro.getDialogPane().getScene().getWindow();
                stage.getIcons().add(
                        new Image(Objects.requireNonNull(AlertaUtil.class.
                                getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
                );
                erro.show();
                return;
            }
            fornecedores = new Fornecedores();
            fornecedores.setDescricaoProduto(txtdescricaoFornecedor.getText());
            fornecedores.setQuantidade(Integer.parseInt(txtquantidadeDPFornecedor.getText()));
            fornecedores.setFornecedor((txtFornecedor.getText()));
            fornecedores.setPreco(Double.parseDouble(txtprecoFornecedor.getText()));
            fornecedoresDAO.inserir(fornecedores);
            limparCampos();
            carregarTableViewFornecedores();
        }
    }

    private void limparCampos() {
        txtdescricaoFornecedor.clear();
        txtquantidadeDPFornecedor.clear();
        txtFornecedor.clear();
        txtprecoFornecedor.clear();
    }

    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (txtdescricaoFornecedor.getText() == null || txtdescricaoFornecedor.getText().isEmpty()) {
            erroMessage += "O produto invalido!\n";
        }
        if (txtquantidadeDPFornecedor.getText() == null || txtquantidadeDPFornecedor.getText().isEmpty()) {
            erroMessage += "A quantidade invalida!\n";
        }
        if (txtFornecedor.getText() == null || txtFornecedor.getText().isEmpty()) {
            erroMessage += "Fornecedor invalido!\n";
        }
        if (txtprecoFornecedor.getText() == null || txtprecoFornecedor.getText().isEmpty()) {
            erroMessage += "O preco invalido!\n";
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

    private void carregarTableViewFornecedores() {
        colunaCodigoFornecedor.setCellValueFactory(new PropertyValueFactory<>("idfornecedor"));
        colunaDescricaoFornecedor.setCellValueFactory(new PropertyValueFactory<>("descricaoProduto"));
        colunaquantidadeFornecedor.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaFornecedor.setCellValueFactory(new PropertyValueFactory<>("fornecedor"));
        colunaprecoFornecedor.setCellValueFactory(new PropertyValueFactory<>("preco"));

        //Listener para txtProcuraNome
        txtpesquisaFornecedor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewFornecedores();
            }
        });

        fornecedoresList = fornecedoresDAO.listar();

        observableFornecedoresList = FXCollections.observableArrayList(fornecedoresList);
        tableviewFornecedor.setItems(observableFornecedoresList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableViewFornecedores();
        tableviewFornecedor.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtdescricaoFornecedor.setText(String.valueOf(newSelection.getDescricaoProduto()));
                txtquantidadeDPFornecedor.setText(String.valueOf(newSelection.getQuantidade()));
                txtFornecedor.setText(String.valueOf(newSelection.getFornecedor()));
                txtprecoFornecedor.setText(String.valueOf(newSelection.getPreco()));
            }
        });

    }
}
