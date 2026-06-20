package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mbtec.gestaoentradasaida_mbtec.DAO.ClienteDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Cliente;
import mbtec.gestaoentradasaida_mbtec.service.AlertaUtil;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class CadastroClienteController implements Initializable {
    @FXML
    private TableColumn<Cliente, Integer> colunaCodigoCliente;

    @FXML
    private TableColumn<Cliente, String> colunaDescricaoCliente;

    @FXML
    private TableColumn<Cliente, String> colunaEnderecoCliente;

    @FXML
    private TableColumn<Cliente, String> colunaNuitCliente;

    @FXML
    private TableView<Cliente> tableviewCliente;

    @FXML
    private TextField txtEnderecoCliente;

    @FXML
    private TextField txtNomeEmpresa;

    @FXML
    private TextField txtNuit;

    @FXML
    private TextField txtpesquisaFornecedor;

    private Cliente cliente = new Cliente();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private List<Cliente> clienteList;
    private ObservableList<Cliente> clienteObservableList;
    private FilteredList<Cliente> clienteFilteredList;

    @FXML
    void btndeletarCliente(ActionEvent event) {
        cliente = tableviewCliente.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir o cliente");
            alert.setContentText("Tem certeza que deseja excluir " + cliente.getNome() + "?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );

            ButtonType btnSim = new ButtonType("Sim");
            ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnSim, btnNao);

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnSim) {
                clienteDAO.renover(cliente);
                carregarTableViewCliente();
                limparCampos();
            }
        }
    }

    @FXML
    void btneditarCliente(ActionEvent event) {

        cliente = tableviewCliente.getSelectionModel().getSelectedItem();

        if (cliente == null) {
            AlertaUtil.mostrarErro("Seleção necessária", "Por favor, selecione um cliente na tabela.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de atualização");
        alert.setHeaderText("Voce esta prestes a atualizar o cliente");
        alert.setContentText("Tem certeza que deseja atualizar " + cliente.getNome() + "?");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
        );

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isEmpty() || resultado.get() == btnNao) {
            return;
        }

        boolean houveAlteracao =
                !cliente.getNome().equals(txtNomeEmpresa.getText()) ||
                        !cliente.getNuit().equals(txtNuit.getText()) ||
                        !cliente.getEndereco().equals(txtEnderecoCliente.getText());

        if (!houveAlteracao) {
            AlertaUtil.mostrarErro("Erro na atualização", "Nenhuma alteração foi feita.");
            return;
        }

        cliente.setNome(txtNomeEmpresa.getText());
        cliente.setNuit(txtNuit.getText());
        cliente.setEndereco(txtEnderecoCliente.getText());

        clienteDAO.atualizar(cliente);
        carregarTableViewCliente();
        AlertaUtil.mostrarInfo("Atualização", "Cliente " + cliente.getNome() + " atualizado");

    }

    @FXML
    void btnsalvarCliente(ActionEvent event) {
        String nuit = txtNuit.getText().trim();
        if (!validarEntradas()) return;

        if (clienteDAO.existeCliente(nuit)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no Cadastro");
            alert.setHeaderText("Campo invalido");
            alert.setContentText("Cliente ja existe ou Nuit repetido!");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );
            alert.showAndWait();
            return;
        }

        String nomeEmpresa = txtNomeEmpresa.getText().trim();

        String endereco = txtEnderecoCliente.getText().trim();

        cliente = new Cliente();
        cliente.setNome(nomeEmpresa);
        cliente.setNuit(nuit);
        cliente.setEndereco(endereco);
        clienteDAO.inserir(cliente);
        limparCampos();
        carregarTableViewCliente();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableViewCliente();
        listenerTabelaSelecionar();
    }

    private void carregarTableViewCliente() {
        //A forma base para preencher tabela FXML
        colunaCodigoCliente.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        tableviewCliente.getItems().indexOf(cell.getValue()) + 1));
        colunaCodigoCliente.setSortable(false);

        colunaDescricaoCliente.setCellValueFactory(new PropertyValueFactory<>("nome")
        );

        colunaNuitCliente.setCellValueFactory(new PropertyValueFactory<>("nuit")
        );

        colunaEnderecoCliente.setCellValueFactory(new PropertyValueFactory<>("endereco")
        );

        //Carregar a lista a tableview

        clienteList = clienteDAO.listar();
        clienteObservableList = FXCollections.observableArrayList(clienteList);
        clienteFilteredList = new FilteredList<>(clienteObservableList, c -> true);

        tableviewCliente.setItems(clienteFilteredList);


    }

    private boolean validarEntradas() {
        String nomeEmpresa = txtNomeEmpresa.getText().trim();
        String nuit = txtNuit.getText().trim();
        String endereco = txtEnderecoCliente.getText().trim();

        if (nomeEmpresa.isBlank()) {
            AlertaUtil.piscarVermelho(txtNomeEmpresa);
            return false;
        }

        if (nuit.isBlank()) {
            AlertaUtil.piscarVermelho(txtNuit);
            return false;
        }

        if (endereco.isBlank()) {
            AlertaUtil.piscarVermelho(txtEnderecoCliente);
            return false;
        }

        return true;
    }

    private void limparCampos() {
        txtNomeEmpresa.clear();
        txtNuit.clear();
        txtEnderecoCliente.clear();
    }

    private void listenerTabelaSelecionar() {
        tableviewCliente.getSelectionModel().selectedItemProperty().addListener((obs, old, selecionar) ->
        {
            if (selecionar != null) {
                txtNomeEmpresa.setText(selecionar.getNome());
                txtNuit.setText(selecionar.getNuit());
                txtEnderecoCliente.setText(selecionar.getEndereco());
            }
        });
    }
}
