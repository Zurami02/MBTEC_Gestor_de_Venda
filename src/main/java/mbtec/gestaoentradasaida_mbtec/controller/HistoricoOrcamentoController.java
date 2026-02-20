package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mbtec.gestaoentradasaida_mbtec.DAO.ItemOrcamentoDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.OrcamentoDAO;
import mbtec.gestaoentradasaida_mbtec.domain.ItemOrcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Itemvenda;
import mbtec.gestaoentradasaida_mbtec.domain.Orcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Venda;
import mbtec.gestaoentradasaida_mbtec.service.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.service.OrcamentoService;
import mbtec.gestaoentradasaida_mbtec.service.TipoItem;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class HistoricoOrcamentoController implements Initializable {
    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private CheckBox checkBoxPesqNomeHistorico;


    @FXML
    private TableColumn<Orcamento, Integer> colunaCodigoOrcHistorico;

    @FXML
    private TableColumn<Orcamento, String> colunaDataOrcHistorico;

    @FXML
    private TableColumn<Orcamento, BigDecimal> colunaIVAOrcHistorico;

    @FXML
    private TableColumn<Orcamento, BigDecimal> colunaTotalOrcHistorico;

    @FXML
    private TableColumn<Orcamento, String> colunaclienteOrcHistorico;


    @FXML
    private TableColumn<ItemOrcamento, Integer> colunaCodigoItemOrc;

    @FXML
    private TableColumn<ItemOrcamento, BigDecimal> colunaPrecoItemOrc;

    @FXML
    private TableColumn<ItemOrcamento, String> colunaProdutoServicoItemOrc;

    @FXML
    private TableColumn<ItemOrcamento, Integer> colunaQTDItemOrc;

    @FXML
    private TableColumn<ItemOrcamento, BigDecimal> colunaSubtotalItemOrc;

    @FXML
    private TableColumn<ItemOrcamento, String> colunaTipoItemOrc;

    @FXML
    private DatePicker dataPickerFinalHistorico;

    @FXML
    private DatePicker dataPickerinicialHistorico;

    @FXML
    private Label lbNumeroOrc;

    @FXML
    private TableView<ItemOrcamento> tableViewItemOrc;

    @FXML
    private TableView<Orcamento> tableviewOrcHistorico;

    @FXML
    private TextField txtPesquisaNomeClienteHistorico;

    OrcamentoService service = new OrcamentoService();
    OrcamentoDAO orcDAO = new OrcamentoDAO();
    ItemOrcamentoDAO itemDAO = new ItemOrcamentoDAO();


    @FXML
    void btnImprimirOrcamento(ActionEvent event) {

    }

    @FXML
    void btnPesquisarOrcHistorico(ActionEvent event) {
        boolean porNome = checkBoxPesqNomeHistorico.isSelected();

        LocalDate dataInicial = porNome ? null : dataPickerinicialHistorico.getValue();
        LocalDate dataFinal = porNome ? null : dataPickerFinalHistorico.getValue();
        String textoPesquisa = txtPesquisaNomeClienteHistorico.getText().trim();

        if (checkBoxPesqNomeHistorico.isSelected()) {
            if (textoPesquisa.isBlank()) {
                AlertaUtil.piscarVermelho(txtPesquisaNomeClienteHistorico);
                return;
            }
        } else {
            if (dataInicial == null) {
                AlertaUtil.piscarVermelho(dataPickerinicialHistorico);
                return;
            }
        }

        if (!porNome) {
            if (dataInicial == null || dataFinal == null) {
                AlertaUtil.mostrarErro("Pesquisa", "Informe o período.");
                return;
            }

            if (dataFinal.isBefore(dataInicial)) {
                AlertaUtil.mostrarErro("Pesquisa", "Data final menor que a inicial.");
                return;
            }
        }

        List<Orcamento> orc = service.buscarOrcamento(dataInicial, dataFinal, textoPesquisa);

        tableviewOrcHistorico.setItems(FXCollections.observableArrayList(orc));
        tableViewItemOrc.getItems().clear();
    }

    @FXML
    void checkboxMarcado(ActionEvent event) {
        boolean pesquisarPorNome = checkBoxPesqNomeHistorico.isSelected();

        dataPickerinicialHistorico.setDisable(pesquisarPorNome);
        dataPickerFinalHistorico.setDisable(pesquisarPorNome);

        if (pesquisarPorNome) {
            txtPesquisaNomeClienteHistorico.setDisable(false);
            dataPickerinicialHistorico.setValue(null);
            dataPickerFinalHistorico.setValue(null);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPesquisaNomeClienteHistorico.setDisable(true);
        dataPickerinicialHistorico.focusedProperty().addListener(
                (obs, old, newV) ->
                {
                    if (newV != null) {
                        txtPesquisaNomeClienteHistorico.setDisable(true);
                        txtPesquisaNomeClienteHistorico.clear();
                    }

                }
        );
        carregarTableViewVendasHistorico();
        carregarTableViewItensOrcamento();
        carregarItensOrcSelecionadaListener();
    }

    private void carregarTableViewItensOrcamento() {
        colunaCodigoItemOrc.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        tableViewItemOrc.getItems().indexOf(cell.getValue()) + 1));
        colunaCodigoItemOrc.setSortable(false);

        colunaProdutoServicoItemOrc.setCellValueFactory(new PropertyValueFactory<>("descricaoitem")
        );

        colunaTipoItemOrc.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTipoitem() != null
                                ? data.getValue().getTipoitem().getDescricao()
                                : ""
                ));

        colunaQTDItemOrc.setCellValueFactory(new PropertyValueFactory<>("quantidade")
        );

        colunaPrecoItemOrc.setCellValueFactory(new PropertyValueFactory<>("precounitario"));

        colunaSubtotalItemOrc.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Apenas formatação visual
        colunaSubtotalItemOrc.setCellFactory(col -> new TableCell<ItemOrcamento, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("pt", "MZ"));
                    nf.setCurrency(Currency.getInstance("MZN"));
                    setText(nf.format(item));
                }
            }
        });
    }

    private void carregarTableViewVendasHistorico() {
        colunaCodigoOrcHistorico.setCellValueFactory(new PropertyValueFactory<>("idorcamento"));
        colunaclienteOrcHistorico.setCellValueFactory(cell -> {
            Orcamento o = cell.getValue();
            if (o.getCliente() != null) {
                return new SimpleStringProperty(o.getCliente().getNome());
            } else {
                return new SimpleStringProperty(o.getCliente_nome());
            }
        });

        colunaDataOrcHistorico.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getData().format(DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:HH"
                        ))
                )
        );

        colunaTotalOrcHistorico.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );

        colunaIVAOrcHistorico.setCellValueFactory(
                new PropertyValueFactory<>("valorIVA")
        );
    }

    private void carregarItensOrcSelecionadaListener() {
        tableviewOrcHistorico.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, orcSelecionado) -> {
                            if (orcSelecionado != null) {
                                carregarItensOrc(orcSelecionado.getIdorcamento());
                                lbNumeroOrc.setText(orcSelecionado.getNumero_orcamento());
                            } else {
                                tableViewItemOrc.getItems().clear();
                            }
                        }
                );
    }

    private void carregarItensOrc(int idorcamento) {

        List<ItemOrcamento> itens = service.buscarItensOrc(idorcamento);
        tableViewItemOrc.setItems(
                FXCollections.observableArrayList(itens)
        );
    }
}
