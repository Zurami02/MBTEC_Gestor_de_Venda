package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mbtec.gestaoentradasaida_mbtec.DAO.ItemvendaDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.VendaDAO;
import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Itemvenda;
import mbtec.gestaoentradasaida_mbtec.domain.Venda;
import mbtec.gestaoentradasaida_mbtec.service.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @version 1.1
 * class controller de Historico venda responsavel apenas para exibicao de vendas feitas
 */
public class HistoricoVendasController implements Initializable {
    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaCodigoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaQTDDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaPrecoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, BigDecimal> colunaDescontoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaProdutoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaSubtotalDetalheVenda;

    @FXML
    private TableView<Itemvenda> tableViewDetalheVenda;

    @FXML
    private TableColumn<Venda, String> colunaDataVendaHistorico;

    @FXML
    private TableColumn<Venda, Integer> colunaCodigoVendaHistorico;

    @FXML
    private TableColumn<Venda, Double> colunaTotalVendaHistorico;

    @FXML
    private TableColumn<Venda, BigDecimal> colunaIVAVendaHistorico;

    @FXML
    private TableColumn<Venda, String> colunaclienteVendaHistorico;

    @FXML
    private TableView<Venda> tableviewVendaHistorico;

    @FXML
    private DatePicker datPickerFinalHistorico;

    @FXML
    private DatePicker datPickerinicialHistorico;

    @FXML
    private Label lbFeedBack;

    @FXML
    private Label lbTAXAIVAVendas;

    @FXML
    private TextField txtCodigoVendaHistorico;

    @FXML
    private TextField txtDataVendaHistorico;

    @FXML
    private TextField txtNomeClienteHistorico;

    @FXML
    private TextField txtTotalVendaHistorico;

    @FXML
    private TextField txtPesquisaNomeClienteHistorico;

    @FXML
    private CheckBox checkBoxPesqNomeHistorico;

    private VendaService vendaService = new VendaService();

    private VendaDAO vendaDAO = new VendaDAO();
    private ItemvendaDAO itemvendaDAO = new ItemvendaDAO();

    @FXML
    void btnImprimirRecibo(ActionEvent event) {
        Venda venda1 = tableviewVendaHistorico.getSelectionModel().getSelectedItem();
        imprimirVD(venda1);
    }

    @FXML
    private void btnAnularVenda() {
        Venda venda = tableviewVendaHistorico.getSelectionModel().getSelectedItem();

        if (venda == null) {
            AlertaUtil.mostrarErro("Erro", "Selecione uma venda.");
            return;
        }

        if ("ANULADA".equals(venda.getStatus())) {
            AlertaUtil.mostrarInfo("Importante", "Esta venda já está anulada.");
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja anular a venda?");
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.//linha 146
                        getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
        );
        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnSim) {
            vendaService.anularVenda(venda);
        }
        carregarTableViewVendasHistorico();
        pintarAnulada();
    }

    @FXML
    void checkboxMarcado(ActionEvent event) {
        boolean pesquisarPorNome = checkBoxPesqNomeHistorico.isSelected();

        datPickerinicialHistorico.setDisable(pesquisarPorNome);
        datPickerFinalHistorico.setDisable(pesquisarPorNome);

        if (pesquisarPorNome) {
            txtPesquisaNomeClienteHistorico.setDisable(false);
            datPickerinicialHistorico.setValue(null);
            datPickerFinalHistorico.setValue(null);
        }
    }

    @FXML
    void btnPesquisarVendaHistorico(ActionEvent event) {
        boolean porNome = checkBoxPesqNomeHistorico.isSelected();

        LocalDate dataInicial = porNome ? null : datPickerinicialHistorico.getValue();
        LocalDate dataFinal = porNome ? null : datPickerFinalHistorico.getValue();
        String textoPesquisa = txtPesquisaNomeClienteHistorico.getText().trim();

        if (checkBoxPesqNomeHistorico.isSelected()) {
            if (textoPesquisa.isBlank()) {
                AlertaUtil.piscarVermelho(txtPesquisaNomeClienteHistorico);
                return;
            }
        } else {
            if (dataInicial == null) {
                AlertaUtil.piscarVermelho(datPickerinicialHistorico);
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

        List<Venda> vendas = vendaDAO.historicoVendas(
                dataInicial,
                dataFinal,
                textoPesquisa
        );

        tableviewVendaHistorico.setItems(FXCollections.observableArrayList(vendas));
        tableViewDetalheVenda.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPesquisaNomeClienteHistorico.setDisable(true);
        carregarTableViewVendasHistorico();
        carregarTableViewItensvendas();
        datPickerinicialHistorico.focusedProperty().addListener(
                (obs, old, newV) ->
                {
                    if (newV != null) {
                        txtPesquisaNomeClienteHistorico.setDisable(true);
                        txtPesquisaNomeClienteHistorico.clear();
                    }

                }
        );
        pintarAnulada();
        carregarItensVendaSelecionadaListener();

        tableviewVendaHistorico.getSelectionModel().selectedItemProperty().addListener((obs, old, vendaselecionada) ->
        {
            if (vendaselecionada != null) {
                System.out.println(vendaselecionada.getTaxaIva());
                System.out.println("IVA db:" + vendaselecionada.getTaxaIvaDB().multiply(new BigDecimal("100")) + "%");
                System.out.println(vendaselecionada.getSubtotal());
            }
        });
    }

    private void pintarAnulada() {
        tableviewVendaHistorico.setRowFactory(tabela ->
                new TableRow<>() {
                    @Override
                    protected void updateItem(Venda venda, boolean empty) {
                        super.updateItem(venda, empty);
                        if (venda == null || empty) {
                            setStyle("");
                        } else if (venda.isAnulada()) {
                            setStyle("""
                                    -fx-background-color: #F08080;
                                    -fx-text-fill: #ebebeb;
                                    -fx-font-style: italic;
                                    """);
                        } else {
                            setStyle("");
                        }
                    }
                });
    }

    private void imprimirVD(@NotNull Venda venda) {

        String impressora = ConfigUtil1.get("printer.default");

        if (impressora == null || impressora.isBlank()) {
            AlertaUtil.mostrarErro("", "Nenhuma impressora configurada!");
            return;
        }

        try (Connection conn = ConexaoSQLite.getConnection()) {

            JasperPrint print = RelatorioAPI.gerarVD(conn, venda.getIdVenda());

            RelatorioAPI.imprimir(print, impressora);

            AlertaUtil.mostrarInfo("", "VD enviada para impressão!");

        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro", "Falha ao imprimir VD:" + e.getMessage());
        }
    }

    private void carregarItensVendaSelecionadaListener() {
        tableviewVendaHistorico.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, vendaselecionada) -> {
                            if (vendaselecionada != null) {
                                carregarItensDaVenda(vendaselecionada.getIdVenda());
                            } else {
                                tableViewDetalheVenda.getItems().clear();
                            }
                        }
                );
    }

    private void carregarItensDaVenda(int idVenda) {

        List<Itemvenda> itens =
                itemvendaDAO.listarPorVenda(idVenda);
        tableViewDetalheVenda.setItems(
                FXCollections.observableArrayList(itens)
        );
    }

    private void carregarTableViewVendasHistorico() {
        colunaCodigoVendaHistorico.setCellValueFactory(new PropertyValueFactory<>("idVenda"));
        colunaclienteVendaHistorico.setCellValueFactory(cell -> {
            Venda v = cell.getValue();
            if (v.getCliente() != null) {
                return new SimpleStringProperty(v.getCliente().getNome());
            } else {
                return new SimpleStringProperty(v.getNomeCliente());
            }
        });

        colunaDataVendaHistorico.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getDataVenda().format(DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm"
                        ))
                )
        );

        colunaTotalVendaHistorico.setCellValueFactory(
                new PropertyValueFactory<>("totalDb")
        );

        colunaIVAVendaHistorico.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        cell.getValue().getValorIVA()
                )
        );
    }

    private void carregarTableViewItensvendas() {
        colunaCodigoDetalheVenda.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        tableViewDetalheVenda.getItems().indexOf(cell.getValue()) + 1));
        colunaCodigoDetalheVenda.setSortable(false);

        colunaProdutoDetalheVenda.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getProduto().getDescricao_produto()
                )
        );

        colunaQTDDetalheVenda.setCellValueFactory(new PropertyValueFactory<>("quantidade")
        );

        colunaPrecoDetalheVenda.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format("%.2f", cell.getValue().getPrecoUnitario())
                )
        );

        colunaSubtotalDetalheVenda.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format("%.2f", cell.getValue().getTotalComDesconto())
                )
        );

        colunaDescontoDetalheVenda.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        cell.getValue().getDesconto()
                )
        );
    }
}
