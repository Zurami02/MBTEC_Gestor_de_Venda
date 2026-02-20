package mbtec.gestaoentradasaida_mbtec.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import mbtec.gestaoentradasaida_mbtec.DAO.ClienteDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.ConfiguracaoDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.ProdutosDAO;
import mbtec.gestaoentradasaida_mbtec.domain.*;
import mbtec.gestaoentradasaida_mbtec.service.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.service.OrcamentoService;
import mbtec.gestaoentradasaida_mbtec.service.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static mbtec.gestaoentradasaida_mbtec.service.OrcamentoService.parsePreco;

public class OrcamentoController implements Initializable {

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private JFXCheckBox checkBoxClienteNaoRegistado;

    @FXML
    private JFXCheckBox checkBoxIVA;

    @FXML
    private JFXButton btnOrcar;

    @FXML
    private TableColumn<Produtos, Integer> colunaCodigoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Integer> colunaEstoqueProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Double> colunaPrecoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, String> colunaProdutoProdutoDoSistema;

    @FXML
    private TableView<Produtos> tableviewProdutoDoSistema;

    @FXML
    private TableView<ItemOrcamento> tableViewCarrinho;

    @FXML
    private TableColumn<ItemOrcamento, String> colunaDescricaoItens;


    @FXML
    private TableColumn<ItemOrcamento, BigDecimal> colunaPrecoItens;

    @FXML
    private TableColumn<ItemOrcamento, Integer> colunaQTDItens;

    @FXML
    private TableColumn<ItemOrcamento, BigDecimal> colunaSubtotalItens;

    @FXML
    private TableColumn<ItemOrcamento, String> colunaTipoItens;

    @FXML
    private JFXComboBox<Cliente> comboBoxClientenoSistema;

    @FXML
    private JFXComboBox<TipoItem> comboBoxTipoDescricao;

    @FXML
    private Label lbDataHora;

    @FXML
    private Label lbFeedBack;

    @FXML
    private Label lbTAXAIVAVendas;

    @FXML
    private Label lbTotalComIVA;

    @FXML
    private TextField txtCliente;

    @FXML
    private TextField txtPrecoServico;

    @FXML
    private TextField txtCodigoProdutoPesquisa;

    @FXML
    private TextField txtIVA;

    @FXML
    private TextField txtNomeProdutoPesquisa;

    @FXML
    private TextField txtNuit;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private TextField txtServico;

    @FXML
    private TextField txtSubtotal;

    @FXML
    private TextField txtTotal;

    private final ProdutosDAO produtosDAO = new ProdutosDAO();

    private Orcamento orcamento = new Orcamento();
    private ItemOrcamento itens = new ItemOrcamento();
    private List<Produtos> produtosList;
    private ObservableList<Produtos> produtosObservableList;
    private FilteredList<Produtos> produtosFilteredList;
    private final ObservableList<ItemOrcamento> itemOrcamentoObservableList = FXCollections.observableArrayList();
    private ObservableList<Cliente> clienteObservableList;

    @FXML
    void btnAdicionarCarrinho(ActionEvent event) {
        Produtos produto = tableviewProdutoDoSistema.getSelectionModel().getSelectedItem();
        String servico = txtServico.getText().trim();

        if (produto == null && servico.isBlank()) {
            AlertaUtil.piscarVermelho(tableviewProdutoDoSistema);
            AlertaUtil.mostrarErro("Produto ou Serviço", "Selecione um produto ou informe um serviço");
            return;
        }

        // Se produto foi selecionado, usar preço dele
        if (produto != null) {
            txtPrecoServico.setText(String.valueOf(produto.getPreco()));
        }

        BigDecimal precoServico = parsePreco(txtPrecoServico.getText());

        int qtd;
        try {
            qtd = Integer.parseInt(txtQuantidade.getText());
        } catch (NumberFormatException e) {
            AlertaUtil.piscarVermelho(txtQuantidade);
            return;
        }

        // Valida estoque apenas se houver produto
        if (produto != null && !produtosDAO.temEstoqueSuficiente(produto.getIdproduto(), qtd)) {
            AlertaUtil.piscarVermelho(txtQuantidade);
            AlertaUtil.mostrarErro("Stock insuficiente",
                    "Quantidade disponível: " + produto.getQuantidade_produto());
            return;
        }

        TipoItem tipo = comboBoxTipoDescricao.getValue();
        if (tipo == null) {
            AlertaUtil.piscarVermelho(comboBoxTipoDescricao);
            return;
        }

        itens = new ItemOrcamento(orcamento, produto, servico, tipo, qtd, precoServico);
        orcamento.adicionarItem(itens);
        itemOrcamentoObservableList.add(itens);
        orcamento.calculartotal(itemOrcamentoObservableList);
        atualizarValoresOrcamento();
        limparCamposItem();
    }

    @FXML
    void btnOrcar(ActionEvent event) {
        if (!validarCliente()) return;
        try {
            OrcamentoService orcamentoService = new OrcamentoService();
            orcamentoService.finalizarOrcamento(orcamento);
            imprimirOrcamento(orcamento);
            AlertaUtil.mostrarInfo("", "OK!");
            limparFormulario();
        } catch (Exception e) {
            AlertaUtil.mostrarErro("Erro", e.getMessage());
        }
    }

    @FXML
    void btnRemoverItem(ActionEvent event) {
        ItemOrcamento itemSelecionado = tableViewCarrinho.getSelectionModel().getSelectedItem();

        if (itemSelecionado == null) {
            AlertaUtil.piscarVermelho(tableViewCarrinho);
            AlertaUtil.mostrarAviso("Remover item", "Selecione um item do carrinho");
            return;
        }

        orcamento.getItens().remove(itemSelecionado);
        itemOrcamentoObservableList.remove(itemSelecionado);
        atualizarValoresOrcamento();
    }

    @FXML
    void checkBoxClienteNaoRegistado(ActionEvent event) {
        controloClienteNaoRegistado();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnOrcar.disableProperty().bind(
                Bindings.isEmpty(itemOrcamentoObservableList)
        );

        LocalDate dataAtual = LocalDate.now();
        DateTimeFormatter dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String datareal = dataAtual.format(dataFormatada);
        lbDataHora.setText(datareal);
        tableViewCarrinho.setItems(itemOrcamentoObservableList);
        incializarListener();
    }

    private void incializarListener() {
        carregarProdutosNoSistema();
        carregarTableViewProdutosNoSistema();
        carregarCombboxClienteNoSistema();
        carregarCombboxTipoItem();
        pesquisarProdutoPorNome();
        carregarTableViewCarrinho();
        listenerCliente();
        controlarProdutoServicoListener();
        deletarItensCarrinhoPorTeclaDelete();
        listenerIVA();
    }

    private void carregarProdutosNoSistema() {

        produtosList = produtosDAO.listar();

        produtosObservableList = FXCollections.observableArrayList(produtosList);

        produtosFilteredList = new FilteredList<>(produtosObservableList, p -> true);

        tableviewProdutoDoSistema.setItems(produtosFilteredList);
    }

    private void carregarTableViewProdutosNoSistema() {
        colunaCodigoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("idproduto"));
        colunaProdutoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("descricao_produto"));
        colunaPrecoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colunaEstoqueProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("quantidade_produto"));
    }

    public void carregarCombboxClienteNoSistema() {
        List<Cliente> clienteList = new ClienteDAO().listar();
        clienteObservableList = FXCollections.observableArrayList(clienteList);
        comboBoxClientenoSistema.setItems(clienteObservableList);
        // Define um filtro dinâmico
        FilteredList<Cliente> clienteFiltrados = new FilteredList<>(clienteObservableList, c -> true);

        comboBoxClientenoSistema.setItems(clienteFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        comboBoxClientenoSistema.setEditable(true);
        comboBoxClientenoSistema.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final String filtro = newValue.toLowerCase().trim();

            clienteFiltrados.setPredicate(cliente -> {
                if (filtro.isEmpty()) {
                    return true;
                }
                return cliente.getNome().toLowerCase().contains(filtro);
            });

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

    public void carregarCombboxTipoItem() {

        ObservableList<TipoItem> tipoItensList = FXCollections.observableArrayList(TipoItem.values());
        // Define um filtro dinâmico
        FilteredList<TipoItem> itensFiltrados = new FilteredList<>(tipoItensList, p -> true);

        comboBoxTipoDescricao.setItems(itensFiltrados);

    }

    private void carregarTableViewCarrinho() {
        colunaDescricaoItens.setCellValueFactory(new PropertyValueFactory<>("descricaoitem"));

        colunaTipoItens.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getTipoitem() != null
                                ? data.getValue().getTipoitem().getDescricao()
                                : ""
                ));

        colunaQTDItens.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        colunaPrecoItens.setCellValueFactory(new PropertyValueFactory<>("precounitario"));

        colunaSubtotalItens.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Apenas formatação visual
        colunaSubtotalItens.setCellFactory(col -> new TableCell<ItemOrcamento, BigDecimal>() {
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

    private boolean validarCliente() {

        // Cliente registado
        if (orcamento.getCliente() != null) {
            orcamento.setCliente_nome(null);
            orcamento.setNuit(null);
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

        orcamento.setCliente_nome(nome);
        orcamento.setNuit(nuit);
        orcamento.setCliente(null);

        return true;
    }

    private void atualizarValoresOrcamento() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("pt", "MZ"));
        nf.setCurrency(Currency.getInstance("MZN"));
        txtSubtotal.setText(orcamento.getSubtotal().toString());
        txtIVA.setText(orcamento.getValorIva().toString());
        txtTotal.setText(orcamento.getTotalComIVA().toString());


    }

    private void listenerCliente() {

        comboBoxClientenoSistema.valueProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                txtCliente.setText(novo.getNome());
                txtNuit.setText(novo.getNuit());
                txtCliente.setDisable(true);
                txtNuit.setDisable(true);
                orcamento.setCliente(novo);
            }
        });
    }

    private void controlarProdutoServicoListener() {

        tableviewProdutoDoSistema.getSelectionModel().selectedItemProperty().addListener((obs, oldProduto, novoProduto) -> {
            if (novoProduto != null) {
                txtServico.setText(novoProduto.getDescricao_produto());
                txtPrecoServico.setText(String.valueOf(novoProduto.getPreco()));
            }
        });


        // Listener para foco no campo de serviço
        txtServico.focusedProperty().addListener((obs, oldValue, ganhouFoco) -> {
            if (ganhouFoco) {
                tableviewProdutoDoSistema.getSelectionModel().clearSelection();
                txtPrecoServico.clear();
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

            orcamento.setCliente(null);

        } else {
            comboBoxClientenoSistema.setDisable(false);

            txtCliente.clear();
            txtNuit.clear();

            txtCliente.setDisable(true);
            txtNuit.setDisable(true);
        }
    }

    private void limparCamposItem() {
        txtQuantidade.clear();
        txtPrecoServico.clear();
        txtServico.clear();
        comboBoxTipoDescricao.setValue(null);
        tableviewProdutoDoSistema.getSelectionModel().clearSelection();
    }

    /**private void listenerIVA() {

     BigDecimal iva = ConfiguracaoDAO.buscarPorChave("IVA").getValor();
     lbTAXAIVAVendas.setText("IVA (" + iva + "%)");
     checkBoxIVA.selectedProperty().addListener((obs, oldValue, marcado) -> {
     if (marcado) {
     BigDecimal taxa = new BigDecimal(iva).divide(new BigDecimal("100"));
     orcamento.setTaxaIVA(taxa);
     lbTAXAIVAVendas.setText("IVA (" + iva + "%)");
     } else {
     orcamento.setTaxaIVA(new BigDecimal("0.0"));
     }

     atualizarValoresOrcamento();
     });

     }**/

    private void listenerIVA() {

        BigDecimal iva = Objects.requireNonNull(ConfiguracaoDAO.buscarPorChave("IVA")).getValor();

        lbTAXAIVAVendas.setText("IVA (" + iva + "%)");

        checkBoxIVA.selectedProperty().addListener((obs, oldValue, marcado) -> {
            if (marcado) {
                BigDecimal taxa = iva.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                orcamento.setTaxaIVA(taxa);
                lbTAXAIVAVendas.setText("IVA (" + iva + "%)");
            } else {
                orcamento.setTaxaIVA(BigDecimal.ZERO);
            }

            atualizarValoresOrcamento();
        });
    }


    //deve ser chamdo no Itilize porque eh 1Listener
    private void deletarItensCarrinhoPorTeclaDelete() {
        tableViewCarrinho.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {

                ItemOrcamento selecionado = tableViewCarrinho.getSelectionModel().getSelectedItem();

                if (selecionado != null) {

                    itemOrcamentoObservableList.remove(selecionado);

                    // recalcular total
                    orcamento.calculartotal(itemOrcamentoObservableList);

                    txtTotal.setText(NumberFormat.getCurrencyInstance().format(orcamento.getTotal()));
                }
            }
        });

    }

    private void limparFormulario() {
        orcamento = new Orcamento();
        tableViewCarrinho.getItems().clear();
        itemOrcamentoObservableList.clear();
        txtSubtotal.clear();
        txtCliente.clear();
        txtIVA.clear();
        txtNuit.clear();
        comboBoxTipoDescricao.getSelectionModel().clearSelection();
        comboBoxClientenoSistema.getSelectionModel().clearSelection();
        checkBoxIVA.setSelected(false);

    }

    private void imprimirOrcamento(Orcamento orcamento) {

    }
}
