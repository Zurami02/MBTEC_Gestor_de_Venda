package mbtec.gestaoentradasaida_mbtec.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import mbtec.gestaoentradasaida_mbtec.DAO.*;
import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.*;
import mbtec.gestaoentradasaida_mbtec.service.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @version 1.1.1
 * Metodo responsavel para cadastrar a venda
 */

public class VendasController implements Initializable {

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private JFXCheckBox checkBoxIVA;

    @FXML
    private JFXCheckBox checkBoxVD;

    @FXML
    private TableView<Produtos> tableviewProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Integer> colunaCodigoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Integer> colunaEstoqueProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Double> colunaPrecoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, String> colunaProdutoProdutoDoSistema;

    @FXML
    private TableView<Itemvenda> tableViewCarrinho;

    @FXML
    private TableColumn<Itemvenda, BigDecimal> colunaDescontoCarrinho;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaPrecoUnitarioCarrinho;

    @FXML
    private TableColumn<Itemvenda, String> colunaProdutoCarrinho;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaQTDCarrinho;

    @FXML
    private TableColumn<Itemvenda, BigDecimal> colunaTotalCarrinho;

    @FXML
    private JFXCheckBox checkBoxClienteNaoRegistado;

    @FXML
    private JFXComboBox<Cliente> comboBoxClientenoSistema;

    @FXML
    private JFXComboBox<String> comboboxFormapagamento;

    @FXML
    private Label lbDataHora;

    @FXML
    private Label lbTAXAIVAVendas;

    @FXML
    private TextField txtCliente;

    @FXML
    private TextField txtCodigoProdutoPesquisa;

    @FXML
    private TextField txtDesconto;

    @FXML
    private TextField txtDinheiroPago;

    @FXML
    private TextField txtIVA;

    @FXML
    private TextField txtNomeProdutoPesquisa;

    @FXML
    private TextField txtNuit;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private TextField txtSubtotal;

    @FXML
    private TextField txtTotal;

    @FXML
    private TextField txtTroco;

    @FXML
    private Label lbFeedBack;

    private final ProdutosDAO produtosDAO = new ProdutosDAO();

    private Venda venda = new Venda();
    private Itemvenda itemvenda;
    private BigDecimal iva;

    private List<Produtos> produtosList;                // lista vinda do DAO
    private ObservableList<Produtos> produtosObservableList; // lista base da TableView
    private FilteredList<Produtos> produtosFilteredList;     // filtro da TableView
    private final ObservableList<Itemvenda> itemvendaObservableList = FXCollections.observableArrayList();
    private ObservableList<Cliente> clienteObservableList;

    @FXML
    void historico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/historicovendas.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Historico de Venda");
            stage.centerOnScreen();
            stage.getIcons().add(new Image(Objects.requireNonNull(AlertaUtil.class.getResourceAsStream(
                    "mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png"))));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnAdicionarCarrinho(ActionEvent event) {

        Produtos produto = tableviewProdutoDoSistema.getSelectionModel().getSelectedItem();

        if (produto == null) {
            AlertaUtil.piscarVermelho(tableviewProdutoDoSistema);
            AlertaUtil.mostrarErro("Produto", "Selecione um produto");
            return;
        }

        int qtd;
        BigDecimal desconto = BigDecimal.ZERO;

        try {
            qtd = Integer.parseInt(txtQuantidade.getText());
            if (!txtDesconto.getText().isBlank()) {
                desconto = new BigDecimal(txtDesconto.getText());
            }
        } catch (NumberFormatException e) {
            AlertaUtil.piscarVermelho(txtQuantidade);
            AlertaUtil.piscarVermelho(txtDesconto);
            return;
        }

        if (!produtosDAO.temEstoqueSuficiente(produto.getIdproduto(), qtd)) {
            AlertaUtil.piscarVermelho(txtQuantidade);
            AlertaUtil.mostrarErro
                    ("Stock insuficiente", "Quantidade disponível: " +
                            produto.getQuantidade_produto());
            return;
        }
        itemvenda = new Itemvenda(produto, qtd, produto.getPreco(), desconto, venda);

        venda.adicionarItem(itemvenda);
        itemvendaObservableList.add(itemvenda);

        atualizarValoresVenda();
        limparCamposItem();
    }

    @FXML
    void btnAdicionarIVA(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/iva.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Cadastro de IVA");
            stage.centerOnScreen();
            stage.getIcons().add(new Image
                    (Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png"))));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void pesquisarProdutoPorCodigo(KeyEvent event) {

        String texto = txtCodigoProdutoPesquisa.getText();
        if (texto.isEmpty()) {
            if (produtosFilteredList != null) {
                produtosFilteredList.setPredicate(p -> true); // mostra todos
                tableviewProdutoDoSistema.getSelectionModel().clearSelection();
            }
            return;
        }
        if (event.getCode() != KeyCode.ENTER) return;

        if (texto.isBlank()) return;

        int codigo;
        try {
            codigo = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            AlertaUtil.piscarVermelho(txtCodigoProdutoPesquisa);
            AlertaUtil.mostrarErro("Código inválido", "Digite apenas números");
            return;
        }

        boolean encontrado = false;

        for (Produtos p : produtosList) {
            if (p.getIdproduto() == codigo) {
                produtosFilteredList.setPredicate(prod -> prod.getIdproduto() == codigo);

                tableviewProdutoDoSistema.getSelectionModel().select(p);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            AlertaUtil.piscarVermelho(txtCodigoProdutoPesquisa);
            AlertaUtil.mostrarErro("Produto não encontrado", "Nenhum produto com esse código");
        }
    }

    @FXML
    void btnFinalizar(ActionEvent event) {

        String valorPago = txtDinheiroPago.getText().trim();
        String pagamento = comboboxFormapagamento.getValue();

        if (!validarCliente()) return;

        if (valorPago.isBlank() || pagamento == null || pagamento.isBlank()) {
            AlertaUtil.piscarVermelho(txtDinheiroPago);
            AlertaUtil.piscarVermelho(comboboxFormapagamento);
            return;
        }

        BigDecimal valorPagamento;
        try {
            valorPagamento = new BigDecimal(valorPago);
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Erro", "Valor pago inválido");
            return;
        }


        try {
            VendaCompletaService vendacompleta = new VendaCompletaService();
            vendacompleta.finalizarVendaComPagamento(venda, valorPagamento, pagamento);

            if (venda.isVd()) {
                imprimirVD(venda);
            }

            mostrarFeedback();
            carregarProdutosNoSistema();
            limparFormulario();

        } catch (Exception e) {
            AlertaUtil.mostrarErro("Erro", e.getMessage());
        }
    }

    @FXML
    void checkBoxClienteNaoRegistado(ActionEvent event) {
        controloClienteNaoRegistado();
    }

    @FXML
    void btnRemoverItem(ActionEvent event) {
        Itemvenda itemSelecionado = tableViewCarrinho.getSelectionModel().getSelectedItem();

        if (itemSelecionado == null) {
            AlertaUtil.piscarVermelho(tableViewCarrinho);
            AlertaUtil.mostrarAviso("Remover item", "Selecione um item do carrinho");
            return;
        }

        venda.getItens().remove(itemSelecionado);
        itemvendaObservableList.remove(itemSelecionado);
        atualizarValoresVenda();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        venda = new Venda();
        LocalDate dataAtual = LocalDate.now();
        DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datareal = dataAtual.format(dataFormatada);
        lbDataHora.setText(datareal);
        carregarTableViewProdutosNoSistema();
        carregarProdutosNoSistema();
        pesquisarProdutoPorNome();
        carregarCombboxClienteNoSistema();
        carregarCombboxFormaPagamento();
        inicializarListeners();
        tableViewCarrinho.setItems(itemvendaObservableList);
        carregarTableViewCarrinho();
        listenerAtualizarTabelaProdutosNoSistema();
    }

    private void controloClienteNaoRegistado() {
        boolean marcado = checkBoxClienteNaoRegistado.isSelected();

        if (marcado) {
            comboBoxClientenoSistema.hide();
            comboBoxClientenoSistema.setDisable(true);
            comboBoxClientenoSistema.getSelectionModel().clearSelection();

            txtCliente.clear();
            txtNuit.clear();

            txtCliente.setDisable(false);
            txtNuit.setDisable(false);

            venda.setCliente(null);

        } else {
            comboBoxClientenoSistema.setDisable(false);

            txtCliente.clear();
            txtNuit.clear();

            txtCliente.setDisable(true);
            txtNuit.setDisable(true);
        }
    }

    private void carregarTableViewProdutosNoSistema() {
        colunaCodigoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("idproduto"));
        colunaProdutoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("descricao_produto"));
        colunaPrecoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colunaEstoqueProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("quantidade_produto"));
    }

    private void carregarTableViewCarrinho() {
        colunaProdutoCarrinho.setCellValueFactory
                (data -> new SimpleStringProperty(data.getValue().getProduto().getDescricao_produto()));
        colunaQTDCarrinho.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaPrecoUnitarioCarrinho.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colunaDescontoCarrinho.setCellValueFactory(new PropertyValueFactory<>("desconto"));
        colunaTotalCarrinho.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getTotalComDesconto())
        );

        colunaTotalCarrinho.setCellFactory(col -> new TableCell<Itemvenda, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(NumberFormat.getCurrencyInstance(Locale.of("pt", "MZ")).format(item));
                }
            }
        });
    }

    private void limparCamposItem() {
        txtQuantidade.clear();
        txtDesconto.clear();
        tableviewProdutoDoSistema.getSelectionModel().clearSelection();
    }

    public void carregarCombboxClienteNoSistema() {
        List<Cliente> clienteList = new ClienteDAO().listar();
        clienteObservableList = FXCollections.observableArrayList(clienteList);

        // Define um filtro dinâmico
        FilteredList<Cliente> clienteFiltrados = new FilteredList<>(clienteObservableList, c -> true);

        comboBoxClientenoSistema.setItems(clienteFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        comboBoxClientenoSistema.setEditable(true);
        //Flag
        final boolean[] itemSelecionado = {false};

        comboBoxClientenoSistema.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newVal)->{
            if (newVal != null){
                itemSelecionado[0] = true; //marca que foi uma selecao real
            }
        });

        comboBoxClientenoSistema.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            //Se foi selecao real, ignore e reseta a fla
            if (itemSelecionado[0]){
                itemSelecionado[0] = false;
                return;
            }

            final String filtro = newValue == null ? "" : newValue.toLowerCase();

            //Quando vazio, atualiza a lista
            if (filtro.isEmpty()){
                clienteFiltrados.setPredicate(c -> true);
            }else {
                clienteFiltrados.setPredicate(c ->
                        c.getNome().toLowerCase().contains(filtro));
            }

            if (!comboBoxClientenoSistema.isShowing()) {
                comboBoxClientenoSistema.show();
            }
        });

        // Corrige o comportamento de seleção para manter o objeto Cliente real
        comboBoxClientenoSistema.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente != null ? cliente.getNome() : "";
            }

            @Override
            public Cliente fromString(String string) {
                return clienteObservableList.stream().filter(c -> c.getNome().equals(string)).findFirst().orElse(null);
            }
        });
    }

    public void carregarCombboxFormaPagamento() {
        List<String> formasPag = Arrays.asList("Dinheiro", "E-mola", "M-Pesa", "Transferencia Bancaria");
        ObservableList<String> pagamentoList = FXCollections.observableArrayList(formasPag);
        // Define um filtro dinâmico
        FilteredList<String> pagamentoFiltrados = new FilteredList<>(pagamentoList, p -> true);

        comboboxFormapagamento.setItems(pagamentoFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        comboboxFormapagamento.setEditable(true);

        comboboxFormapagamento.getEditor().textProperty().addListener(
                (obs, oldValue, newValue) -> {
                    final String filtro = newValue.toLowerCase().trim();

                    pagamentoFiltrados.setPredicate(pagamento -> {
                        if (filtro.isEmpty()) {
                            return true;
                        }
                        return pagamento.toLowerCase().contains(filtro);
                    });

                    if (!pagamentoFiltrados.isEmpty()) {
                        comboboxFormapagamento.show();
                    } else {
                        comboboxFormapagamento.hide();
                    }
                });

        comboboxFormapagamento.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: Black;"); // all items white
                }
            }
        });

        // Ensure the selected item also shows in white
        comboboxFormapagamento.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: White;"); // selected item white
                }
            }
        });

    }

    private void pesquisarProdutoPorNome() {

        txtNomeProdutoPesquisa.textProperty().addListener((obs, oldValue, newValue) -> {

            produtosFilteredList.setPredicate(produto -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                return produto.getDescricao_produto().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void inicializarListeners() {
        VendaService vs = new VendaService();
        anchorPaneMain.setStyle("-fx-border-color: red;");
        if (vs.isAnuladaVenda()) {
            carregarProdutosNoSistema();
            tableviewProdutoDoSistema.refresh();
        }
        listenerPesquisaProduto();
        listenerCliente();
        listenerIVA();
        listenerVD();
        listenerPagamento();
        txtCliente.setDisable(true);
        txtNuit.setDisable(true);
    }

    private void listenerVD() {
        checkBoxVD.selectedProperty().addListener((obs, oldValue,
                                                   marcado) -> {
            venda.setVd(marcado);
        });
    }

    private void listenerPesquisaProduto() {
        txtNomeProdutoPesquisa.focusedProperty().addListener((obs, old, focou) -> {
            if (focou) {
                txtCodigoProdutoPesquisa.clear();
            }
        });

        txtCodigoProdutoPesquisa.focusedProperty().addListener((obs, old, focou) -> {
            if (focou) {
                txtNomeProdutoPesquisa.clear();
            }
        });
    }

    private void listenerPagamento() {
        txtDinheiroPago.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) calcularTroco();
        });

        txtDinheiroPago.focusedProperty().addListener((obs, o, focou) -> {
            if (!focou) calcularTroco();
        });
    }

    private void listenerIVA() {
        BigDecimal iva;
        Configuracao config = ConfiguracaoDAO.buscarPorChave("IVA");
        if (config != null) {
            iva = config.getValor();
        }else {
            iva = new BigDecimal("17");
        }
        lbTAXAIVAVendas.setText("IVA (" + iva + "%)");
        checkBoxIVA.selectedProperty().addListener((obs, oldValue, marcado) -> {
            if (marcado) {
                venda.setTaxaIva(iva);
                lbTAXAIVAVendas.setText("IVA (" + iva + "%)");
            } else {
                venda.setTaxaIva(BigDecimal.ZERO);
            }

            atualizarValoresVenda();
        });

    }

    private void listenerCliente() {

        comboBoxClientenoSistema.valueProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                txtCliente.setText(novo.getNome());
                txtNuit.setText(novo.getNuit());
                txtCliente.setDisable(true);
                txtNuit.setDisable(true);
                venda.setCliente(novo);
            }
        });
    }

    private void listenerAtualizarTabelaProdutosNoSistema() {
        Stage stage = new Stage();
        stage.focusedProperty().addListener((obs, old, atualizado) ->
                {
                    if (atualizado) {
                        carregarProdutosNoSistema();
                        tableviewProdutoDoSistema.refresh();
                    }
                }
        );
    }

    private void calcularTroco() {
        String texto = txtDinheiroPago.getText();

        if (texto == null || texto.isBlank()) {
            txtTroco.clear();
            return;
        }

        try {
            BigDecimal pago = new BigDecimal(texto);
            BigDecimal total = venda.getTotalFinal();

            if (pago.compareTo(total) < 0) {
                AlertaUtil.piscarVermelho(txtDinheiroPago);
                txtTroco.clear();
                return;
            }

            BigDecimal troco = pago.subtract(total).setScale(2, RoundingMode.HALF_UP);
            txtTroco.setText(troco.toString());

        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Valor inválido", "Digite apenas números");
            txtTroco.clear();
        }
    }

    private void atualizarValoresVenda() {
        txtSubtotal.setText(venda.getSubtotal().setScale(2, RoundingMode.HALF_UP).toString());
        txtIVA.setText(venda.getValorIva().setScale(2, RoundingMode.HALF_UP).toString());
        txtTotal.setText(venda.getTotalFinal().setScale(2, RoundingMode.HALF_UP).toString());

        recalcularTrocoSePossivel();
    }

    private void recalcularTrocoSePossivel() {
        String texto = txtDinheiroPago.getText();

        if (texto == null || texto.isBlank()) {
            txtTroco.clear();
            return;
        }

        try {
            BigDecimal pago = new BigDecimal(texto);
            BigDecimal total = venda.getTotalFinal();

            if (pago.compareTo(total) >= 0) {
                BigDecimal troco = pago.subtract(total).setScale(2, RoundingMode.HALF_UP);
                txtTroco.setText(troco.toString());
            } else {
                txtTroco.clear();
            }

        } catch (NumberFormatException e) {
            txtTroco.clear();
            AlertaUtil.mostrarErro("Valor inválido", "Digite apenas números");
        }
    }

    private void limparFiltro() {
        produtosFilteredList.setPredicate(p -> true);
    }

    private void carregarProdutosNoSistema() {

        produtosList = produtosDAO.listar();

        produtosObservableList = FXCollections.observableArrayList(produtosList);

        produtosFilteredList = new FilteredList<>(produtosObservableList, p -> true);

        tableviewProdutoDoSistema.setItems(produtosFilteredList);
    }

    private boolean validarCliente() {

        // Cliente registado
        if (venda.getCliente() != null) {
            venda.setNomeCliente(null);
            venda.setNuitCliente(null);
            return true;
        }

        // Cliente não registado
        String nome = txtCliente.getText().trim();
        String nuit = txtNuit.getText().trim();

        if (nome.isEmpty()) {
            AlertaUtil.piscarVermelho(comboBoxClientenoSistema);
            AlertaUtil.piscarVermelho(txtCliente);
            return false;
        }

        if (nuit.isEmpty()) {
            //consumidor final
            nuit = "6660002-Indefinido";
        }

        venda.setNomeCliente(nome);
        venda.setNuitCliente(nuit);
        venda.setCliente(null);

        return true;
    }

    private void limparFormulario() {
        venda = new Venda();
        tableViewCarrinho.getItems().clear();
        itemvendaObservableList.clear();
        txtTotal.setText("0.00");
        txtSubtotal.clear();
        txtCliente.clear();
        txtNuit.clear();
        txtDinheiroPago.clear();
        txtIVA.clear();
        txtTroco.clear();
        checkBoxVD.setSelected(false);
        checkBoxIVA.setSelected(false);
        checkBoxClienteNaoRegistado.setSelected(false);
        comboBoxClientenoSistema.getSelectionModel().clearSelection();
        comboboxFormapagamento.getSelectionModel().clearSelection();
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
            AlertaUtil.mostrarErro("Erro", "Falha ao imprimir VD: " + e.getMessage());
        }
    }

    private void mostrarFeedback()  {

        lbFeedBack.setText("Venda finalizada com sucesso");
        lbFeedBack.setStyle(
                "-fx-background-color:" + "#2ecc71" + ";" +
                        "-fx-padding:12 30;" +
                        "-fx-background-radius:8;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;"
        );

        lbFeedBack.setOpacity(1);
        lbFeedBack.setVisible(true);

        // Piscar 2 vezes
        FadeTransition fade = new FadeTransition(Duration.seconds(3), lbFeedBack);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setCycleCount(3); // 2 piscas
        fade.setAutoReverse(true);

        fade.setOnFinished(e -> lbFeedBack.setVisible(false));

        fade.play();
    }

}
