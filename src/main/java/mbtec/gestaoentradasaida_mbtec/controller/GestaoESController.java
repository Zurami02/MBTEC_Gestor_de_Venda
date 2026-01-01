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
import mbtec.gestaoentradasaida_mbtec.DAO.GestaoESDAO;
import mbtec.gestaoentradasaida_mbtec.domain.FluxodeCaixa;
import mbtec.gestaoentradasaida_mbtec.domain.GestaoES;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class GestaoESController implements Initializable {
    @FXML
    private TableColumn<GestaoES, Integer> colunaCodigo;

    @FXML
    private TableColumn<GestaoES, String> colunaData;

    @FXML
    private TableColumn<GestaoES, String> colunadescricao;

    @FXML
    private TableColumn<GestaoES, String> colunaplanoconta;

    @FXML
    private TableColumn<GestaoES, Integer> colunaquantidade;

    @FXML
    private TableColumn<GestaoES, Double> colunavalor;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TableView<GestaoES> tableviewGestaoes;

    @FXML
    private TextField txtdescricao;

    @FXML
    private TextField txtpesquisa;

    @FXML
    private TextField txtplanoconta;

    @FXML
    private ComboBox<String> comboboxTipoGestao;

    @FXML
    private TextField txtquantidade;

    @FXML
    private TextField txtvalor;

    @FXML
    void btnPesquisa(ActionEvent event) {
        String pesquisa = txtpesquisa.getText();
        List<GestaoES> resultadoEncontrado = gestaoESDAO.buscarPorNome(pesquisa);
        tableviewGestaoes.getItems().clear();
        limparCampos();

        if (!resultadoEncontrado.isEmpty()) {
            tableviewGestaoes.getItems().addAll(resultadoEncontrado);
        } else if (resultadoEncontrado.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("GestaoES não encontradas");
            alert.setHeaderText(null);
            alert.setContentText("Nenhuma atividade encontrada com esse nome.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );
            alert.showAndWait();
        }else{
            AlertaUtil.mostrarErro("Erro de pesquisa", "Campo de pesquisa em branco!");
        }

    }

    @FXML
    void btndeletar(ActionEvent event) {
        gestaoES = tableviewGestaoes.getSelectionModel().getSelectedItem();
        if (gestaoES != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir a GestaoES");
            alert.setContentText("Tem certeza que deseja excluir " + gestaoES.getPlanoconta() + "?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                gestaoESDAO.remover(gestaoES);
                carregarTableViewGestaoES();
                limparCampos();
            }
        } else {
            AlertaUtil.mostrarErro("Erro de exclusao", "Selecione a E/S na Tabela!");
        }
    }

    @FXML
    void btneditar(ActionEvent event) {
        gestaoES = tableviewGestaoes.getSelectionModel().getSelectedItem();
        if (gestaoES != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de atualizacao");
            alert.setHeaderText("Voce esta prestes a atualizar a GestaoES");
            alert.setContentText("Tem certeza que deseja atualizar " + gestaoES.getPlanoconta() + "?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // Verifica se houve alguma alteração antes de editar
                if (!gestaoES.getPlanoconta().equals(comboboxTipoGestao.getValue()) ||
                        !gestaoES.getDescricao().equals(txtdescricao.getText()) ||
                        gestaoES.getQuantidade() != Integer.parseInt(txtquantidade.getText()) ||
                        gestaoES.getValor() != Double.parseDouble(txtvalor.getText())) {

                    gestaoES.setData(String.valueOf(dataPicker.getValue()));
                    gestaoES.setPlanoconta(comboboxTipoGestao.getValue());
                    gestaoES.setDescricao(txtdescricao.getText());
                    gestaoES.setQuantidade(Integer.parseInt(txtquantidade.getText()));

                    double valorDigitado = Double.parseDouble(txtvalor.getText());
                    //AQUI DEFINIMOS O SINAL
                    if ("Saida".equalsIgnoreCase(comboboxTipoGestao.getValue())) {
                        gestaoES.setValor(-Math.abs(valorDigitado));//negativo
                    } else {
                        gestaoES.setValor(Math.abs(valorDigitado));//positivo
                    }
                    gestaoESDAO.editar(gestaoES);
                    carregarTableViewGestaoES();
                } else {
                    // Se não houve mudanças, você pode mostrar uma mensagem ou simplesmente retornar
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Nenhuma Alteração");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Nenhuma alteração foi feita na G.Entrada/Saida.");
                    stage = (Stage) infoAlert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(
                            new Image(Objects.requireNonNull(AlertaUtil.class.
                                    getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
                    );
                    infoAlert.show();
                }
            }
        } else {
            AlertaUtil.mostrarErro("Erro na edicao", "Selecione a E/S na Tabela!");
        }
    }

    @FXML
    void btnsalvar(ActionEvent event) {
        if (validarEntradadedados()) {

            gestaoES = new GestaoES();
            gestaoES.setData(String.valueOf(dataPicker.getValue()));
            gestaoES.setPlanoconta(comboboxTipoGestao.getValue());
            gestaoES.setDescricao(txtdescricao.getText());
            gestaoES.setQuantidade(Integer.parseInt(txtquantidade.getText()));
            double valorDigitado = Double.parseDouble(txtvalor.getText());

            //AQUI DEFINIMOS O SINAL
            if ("Saida".equalsIgnoreCase(comboboxTipoGestao.getValue())) {
                gestaoES.setValor(-Math.abs(valorDigitado));//negativo
            } else {
                gestaoES.setValor(Math.abs(valorDigitado));//positivo
            }
            gestaoESDAO.inserir(gestaoES);
            limparCampos();
            carregarTableViewGestaoES();
        }

    }

    private final GestaoESDAO gestaoESDAO = new GestaoESDAO();
    private GestaoES gestaoES;
    private List<GestaoES> gestaoESList;
    private ObservableList<GestaoES> observableGestaoesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableViewGestaoES();
        tableviewGestaoes.getSelectionModel().selectedItemProperty().addListener((
                obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String dataString = newSelection.getData(); // aqui a data é uma String
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // ajuste o padrão conforme necessário
                LocalDate data = LocalDate.parse(dataString, formatter); // converte a String para LocalDate
                dataPicker.setValue(data); // define o valor no DatePicker
                comboboxTipoGestao.setValue(newSelection.getPlanoconta());
                txtdescricao.setText(String.valueOf(newSelection.getDescricao()));
                txtquantidade.setText(String.valueOf(newSelection.getQuantidade()));
                txtvalor.setText(String.valueOf(newSelection.getValor()));
            }
        });
        carregarComboBoxGestaoTipo();
    }

    public void carregarComboBoxGestaoTipo(){
        comboboxTipoGestao.getItems().addAll("Entrada", "Saida");
    }

    /**
     * Validar entrada de Dados no Cadastro
     */
    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (dataPicker.getValue() == null || dataPicker.getValue() == null) {
            erroMessage += "Data invalida!\n";
        }
        if (comboboxTipoGestao.getValue() == null || comboboxTipoGestao.getValue().isEmpty()) {
            erroMessage += "Plano de conta invalido!\n";
        }
        if (txtdescricao.getText() == null || txtdescricao.getText().isEmpty()) {
            erroMessage += "Descricao invalida!\n";
        }
        if (txtquantidade.getText() == null || txtquantidade.getText().isEmpty()) {
            erroMessage += "A quantidade invalida!\n";
        }
        if (txtvalor.getText() == null || txtvalor.getText().isEmpty()) {
            erroMessage += "O valor invalido!\n";
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

    private void carregarTableViewGestaoES() {
        colunaCodigo.setCellValueFactory(new PropertyValueFactory<>("idgestao"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));

        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaData.setCellFactory(column ->new TableCell<GestaoES, String>(){
            @Override
            protected void updateItem(String data, boolean empty) {
                super.updateItem(data, empty);

                if (empty || data == null) {
                    setText(null);
                    return;
                }
                try {
                    LocalDate d = LocalDate.parse(data, dataEntrada);
                    setText(datasaida.format(d));
                }catch (Exception e){
                    setText(data);
                }
            }
        });
        colunaplanoconta.setCellValueFactory(new PropertyValueFactory<>("planoconta"));
        colunadescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaquantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunavalor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        //Listener para txtProcuraNome
        txtpesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewGestaoES();
            }
        });

        gestaoESList = gestaoESDAO.listar();

        observableGestaoesList = FXCollections.observableArrayList(gestaoESList);
        tableviewGestaoes.setItems(observableGestaoesList);
    }

    private void limparCampos() {
        txtdescricao.clear();
        comboboxTipoGestao.setValue("");
        txtquantidade.clear();
        txtvalor.clear();
    }
}
