package mbtec.gestaoentradasaida_mbtec.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mbtec.gestaoentradasaida_mbtec.DAO.ConfiguracaoDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.ProdutosDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Cliente;
import mbtec.gestaoentradasaida_mbtec.domain.Itemvenda;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import mbtec.gestaoentradasaida_mbtec.domain.Venda;
import mbtec.gestaoentradasaida_mbtec.service.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.service.VendaCompletaService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
/**
public class VendasController2 implements Initializable {

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
    private TableColumn<Produtos, BigDecimal> colunaPrecoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, String> colunaProdutoProdutoDoSistema;

    @FXML
    private TableView<Itemvenda> tableViewCarrinho;

    @FXML
    private TableColumn<Itemvenda, BigDecimal> colunaDescontoCarrinho;

    @FXML
    private TableColumn<Itemvenda, BigDecimal> colunaPrecoUnitarioCarrinho;

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

    private Venda venda;
    private Itemvenda itemvenda;
    private BigDecimal iva;

    private List<Produtos> produtosList;
    private ObservableList<Produtos> produtosObservableList;
    private FilteredList<Produtos> produtosFilteredList;
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
            AlertaUtil.mostrarErro("Stock insuficiente", "Quantidade disponível: " +
                    produto.getQuantidade_produto());
            return;
        }
        BigDecimal produtoBig = new BigDecimal( produto.getPreco());
        itemvenda = new Itemvenda(produto, qtd,produtoBig, desconto, venda);
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
                produtosFilteredList.setPredicate(p -> true);
                tableviewProdutoDoSistema.getSelectionModel().clearSelection();
            }
            return;
        }
        if (event.getCode() != KeyCode.ENTER) return;

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
        lbDataHora.setText(dataAtual.format(dataFormatada));
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
        colunaProdutoCarrinho.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduto().getDescricao_produto()));
        colunaQTDCarrinho.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaPrecoUnitarioCarrinho.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colunaDescontoCarrinho.setCellValueFactory(new PropertyValueFactory<>("desconto"));
        colunaTotalCarrinho.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTotalComDesconto()));
    }

    private void limparCamposItem() {
        txtQuantidade.clear();
        txtDesconto.clear();
        tableviewProdutoDoSistema.getSelectionModel().clearSelection();
    }

    private void listenerIVA() {
        iva = Objects.requireNonNull(ConfiguracaoDAO.buscarPorChave("IVA")).getValor();
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
                txtTroco.setText(pago.subtract(total).setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                txtTroco.clear();
            }
        } catch (NumberFormatException e) {
            txtTroco.clear();
        }
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
            txtTroco.setText(pago.subtract(total).setScale(2, RoundingMode.HALF_UP).toString());
        } catch (NumberFormatException e) {
            txtTroco.clear();
            AlertaUtil.mostrarErro("Valor inválido", "Digite apenas números");
        }
    }

    private boolean validarCliente() {
        if (venda.getCliente() != null) {
            venda.setNomeCliente(null);
            venda.setNuitCliente(null);
            return true;
        }
        String nome = txtCliente.getText().trim();
        String nuit = txtNuit.getText().trim();
        if (nome.isEmpty()) {
            AlertaUtil.piscarVermelho(comboBoxClientenoSistema);
            AlertaUtil.piscarVermelho(txtCliente);
            return false;
        }
        if (nuit.isEmpty()) {
            nuit = "6660002-Indefinido";
        }
        venda.setNomeCliente(nome);
        venda.setNuitCliente(nuit);
        venda.setCliente(null);
        return true;
    }
}**/
