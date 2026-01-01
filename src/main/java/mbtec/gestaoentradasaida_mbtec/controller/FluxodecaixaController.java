package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.StringConverter;
import mbtec.gestaoentradasaida_mbtec.DAO.FluxodeCaixaDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.ProdutosDAO;
import mbtec.gestaoentradasaida_mbtec.domain.FluxodeCaixa;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FluxodecaixaController implements Initializable {
    @FXML
    private TableColumn<FluxodeCaixa, Integer> colunaCodigoFC;

    @FXML
    private TableColumn<FluxodeCaixa, String> colunaDataFC;

    @FXML
    private TableColumn<FluxodeCaixa, Double> colunaDesconto;

    @FXML
    private TableColumn<FluxodeCaixa, Double> colunaPrecoTotalFC;

    @FXML
    private TableColumn<FluxodeCaixa, String> colunaProdutoFC;

    @FXML
    private TableColumn<FluxodeCaixa, Integer> colunaQuantidadeFC;

    @FXML
    private TableView<FluxodeCaixa> tableviewFluxodeCaixa;

    @FXML
    private ComboBox<Produtos> txtcomboboxProdutoFC;

    @FXML
    private DatePicker txtdatapickerFC;

    @FXML
    private TextField txtprecoUnitarioFC;

    @FXML
    private TextField txtdesconto;

    @FXML
    private TextField txtprecototalFC;

    @FXML
    private TextField txtquantidadeFC;

    @FXML
    private TextField txtpesquisaFC;

    @FXML
    private Label precoTotalLabel;

    @FXML
    private Label descontoLabel;

    @FXML
    private Label estoqueLabel;

    @FXML
    void btnDeletar(ActionEvent event) {
        fluxodeCaixa = tableviewFluxodeCaixa.getSelectionModel().getSelectedItem();
        if (fluxodeCaixa != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir o fluxo de caixa");
            alert.setContentText("Tem certeza que deseja excluir " + fluxodeCaixa.getProduto().getDescricao_produto() + "?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // 1. Repor estoque do produto
                Produtos produto = fluxodeCaixa.getProduto();
                int quantidadeRemovida = fluxodeCaixa.getQuantidade();
                produto.setQuantidade_produto(produto.getQuantidade_produto() + quantidadeRemovida);

                // 2. Atualizar no banco
                produtosDAO.editar(produto);
                fluxodeCaixaDAO.deletar(fluxodeCaixa);
                carregarTableviewFluxodecaixa();
                limparCampos();
            }
        } else {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Erro na exclusao de dados!");
            infoAlert.setHeaderText("Nenhuma selecao de fluxo de caixa foi feita");
            infoAlert.setContentText("Selecione o Fluxo de Caixa na tabela.");
            Stage stage = (Stage) infoAlert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(
                    new Image(Objects.requireNonNull(AlertaUtil.class.
                            getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );
            infoAlert.show();
        }
    }

    @FXML
    void btnEditarFC(ActionEvent event) {
        if (!validarEntradadedados()) return;

        try {
            fluxodeCaixa = tableviewFluxodeCaixa.getSelectionModel().getSelectedItem();
            if (fluxodeCaixa == null) {
                AlertaUtil.mostrarInfo("Atualização", "Selecione o Fluxo de Caixa na tabela.");
                return;
            }

            Optional<ButtonType> resultado = AlertaUtil.mostrarConfirmacao(
                    "Confirmação de Atualização",
                    "Tem certeza que deseja atualizar " + fluxodeCaixa.getProduto().getDescricao_produto() + "?"
            );
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return; // cancelado
            }

            Produtos produtoSelecionado = txtcomboboxProdutoFC.getValue();
            int quantidade = Integer.parseInt(txtquantidadeFC.getText());
            LocalDate dataNova = txtdatapickerFC.getValue();
            double precoUnitario = produtoSelecionado.getPreco();

            // Desconto
            double desconto = 0.0;
            String texto = txtdesconto.getText();
            if (texto != null && !texto.trim().isEmpty()) {
                try {
                    texto = texto.replace(",", ".");
                    desconto = Double.parseDouble(texto);
                } catch (NumberFormatException e) {
                    desconto = 0.0;
                    System.out.println("Desconto inválido, usando 0.0");
                }
            }

            double precoTotal = precoUnitario * quantidade * (1 - (desconto / 100));

            // Comparações
            boolean alterado =
                    fluxodeCaixa.getProduto().getIdproduto() != produtoSelecionado.getIdproduto() ||
                            fluxodeCaixa.getQuantidade() != quantidade ||
                            !LocalDate.parse(fluxodeCaixa.getData()).equals(dataNova) ||
                            Math.abs(fluxodeCaixa.getValor() - precoTotal) > 0.0001 ||
                            Math.abs(fluxodeCaixa.getDesconto() - desconto) > 0.0001;

            if (alterado) {
                fluxodeCaixa.setProduto(produtoSelecionado);
                fluxodeCaixa.setQuantidade(quantidade);
                fluxodeCaixa.setValor(precoTotal);
                fluxodeCaixa.setData(dataNova.toString());
                fluxodeCaixa.setDesconto(desconto);

                fluxodeCaixaDAO.atualizar(fluxodeCaixa);
                carregarTableviewFluxodecaixa();
                limparCampos();
            } else {
                AlertaUtil.mostrarInfo("Atualização", "Nenhum dado foi modificado.");
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro", "Ocorreu um erro: " + e.getMessage());
        }
    }


    @FXML
    void btnPesquisaFC(ActionEvent event) {
        String pesquisa = txtpesquisaFC.getText();
        List<FluxodeCaixa> resultadoencontrado = fluxodeCaixaDAO.buscarPorNome(pesquisa);
        tableviewFluxodeCaixa.getItems().clear();
        limparCampos();
        if (!resultadoencontrado.isEmpty()) {
            tableviewFluxodeCaixa.getItems().addAll(resultadoencontrado);
        } else {
            AlertaUtil.mostrarAviso("Fluxo de caixa não encontrado",
                    "Nenhum fluxo encontrado com esse nome.");
            carregarTableviewFluxodecaixa();
        }
    }

    @FXML
    void btnSalvarFC(ActionEvent event) {

        if (validarEntradadedados()) {
            try {
                Produtos produtoSelecionado = txtcomboboxProdutoFC.getValue();
                int quantidade = Integer.parseInt(txtquantidadeFC.getText());
                double precoUnitario = produtoSelecionado.getPreco();
                double desconto = 0.0;
                String texto = txtdesconto.getText();
                if (texto != null && !texto.trim().isEmpty()) {
                    try {
                        texto = texto.replace(",", "."); // suporta vírgula como decimal
                        desconto = Double.parseDouble(texto);
                    } catch (NumberFormatException e) {
                        desconto = 0.0;
                        System.out.println("Desconto inválido, usando 0.0");
                    }
                }
                //total com desconto
                double precoTotal = precoUnitario * quantidade * (1 - (desconto / 100));
                double valorDesconto = precoUnitario * quantidade * (desconto / 100);
                String precoFormatado = String.format("%.2f", precoTotal);

                String data = String.valueOf(txtdatapickerFC.getValue());

                // Verifica se há estoque suficiente
                if (produtoSelecionado.getQuantidade_produto() < quantidade) {
                    AlertaUtil.mostrarAviso("Estoque insuficiente",
                            "A quantidade solicitada excede o estoque disponível.");
                    return;
                }

                FluxodeCaixa fluxo = new FluxodeCaixa();
                fluxo.setProduto(produtoSelecionado);
                fluxo.setQuantidade(quantidade);
                fluxo.setValor(precoTotal);
                fluxo.setData(data);
                fluxo.setDesconto(desconto);
                fluxodeCaixaDAO.inserir(fluxo);

                // Atualiza estoque do produto
                int novaQuantidade = produtoSelecionado.getQuantidade_produto() - quantidade;
                produtoSelecionado.setQuantidade_produto(novaQuantidade);
                produtosDAO.editar(produtoSelecionado);
                carregarTableviewFluxodecaixa();
                limparCampos();
                precoTotalLabel.setText(precoFormatado + "MZN");
                //descontoLabel.setText(desconto + "%");
                descontoLabel.setText(String.format(" %.2f MZN", valorDesconto));

            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter quantidade: " + e.getMessage());
            }
        }
    }

    private final FluxodeCaixaDAO fluxodeCaixaDAO = new FluxodeCaixaDAO();
    private final ProdutosDAO produtosDAO = new ProdutosDAO();
    private FluxodeCaixa fluxodeCaixa = new FluxodeCaixa();
    private List<FluxodeCaixa> fluxodeCaixaList = new ArrayList<>();
    private ObservableList<FluxodeCaixa> fluxodeCaixaObservableList;
    private List<Produtos> listProdutos;
    private ObservableList<Produtos> produtosObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarCombboxProdutosAutoCompletado();
        carregarTableviewFluxodecaixa();
        emtemporealDesconto();
        tableviewFluxodeCaixaListener();

        txtprecoUnitarioFC.setEditable(false);
        txtprecototalFC.setEditable(false);
        tableviewFluxodeCaixa.setEditable(true);

    }

    private void tableviewFluxodeCaixaListener() {
        tableviewFluxodeCaixa.getSelectionModel().selectedItemProperty().addListener((
                obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 1. Converter a data string para LocalDate
                String dataString = newSelection.getData();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate data = LocalDate.parse(dataString, formatter);
                txtdatapickerFC.setValue(data);

                // 2. Preencher os campos com dados do objeto selecionado
                txtcomboboxProdutoFC.setValue(newSelection.getProduto());
                txtquantidadeFC.setText(String.valueOf(newSelection.getQuantidade()));
                txtprecoUnitarioFC.setText(String.format("%.2f", newSelection.getProduto().getPreco()));
                txtdesconto.setText(String.valueOf(newSelection.getDesconto()));

                // 3. Calcular valores para exibição
                double precoUnitario = newSelection.getProduto().getPreco();
                int quantidade = newSelection.getQuantidade();
                double descontoPercentual = newSelection.getDesconto();

                double valorBruto = precoUnitario * quantidade;
                double valorDesconto = valorBruto * (descontoPercentual / 100);
                double precoTotal = valorBruto - valorDesconto;

                // 4. Atualiza os labels
                precoTotalLabel.setText(String.format("%.2f MZN", precoTotal));
                descontoLabel.setText(String.format(" %.2f MZN", valorDesconto));

                // 5. Estoque atual
                estoqueLabel.setText(newSelection.getProduto().getDescricao_produto() + ": " +
                        newSelection.getProduto().getQuantidade_produto());
            }
        });


        txtcomboboxProdutoFC.setOnAction(e -> {
            Produtos produtoSelecionado = txtcomboboxProdutoFC.getValue();
            if (produtoSelecionado != null) {
                txtprecoUnitarioFC.setText(String.valueOf(produtoSelecionado.getPreco()));
                atualizarPrecoTotal(); // atualizar o total se já tiver quantidade
                estoqueLabel.setText(String.valueOf(produtoSelecionado.getDescricao_produto() + ": " +
                        produtoSelecionado.getQuantidade_produto()));
            }
        });

        txtquantidadeFC.setTextFormatter(new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d*")) { // apenas dígitos
                return change;
            }
            return null; // rejeita a entrada
        }));

        txtquantidadeFC.textProperty().addListener((obs,
                                                    oldVal, newVal) -> {
            atualizarPrecoTotal();
        });
    }

    public void carregarComboBoxProdutos() {

        listProdutos = produtosDAO.listar(); // ou outro método que lista produtos
        produtosObservableList = FXCollections.observableArrayList(listProdutos);
        // Cria uma lista filtrável
        FilteredList<Produtos> produtosFiltrados = new FilteredList<>(produtosObservableList, p -> true);
        txtcomboboxProdutoFC.setItems(produtosFiltrados);

        // Define os itens no ComboBox
        txtcomboboxProdutoFC.setItems(produtosFiltrados);

        // Permite edição
        txtcomboboxProdutoFC.setEditable(true);

        // Listener para texto digitado
        txtcomboboxProdutoFC.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = txtcomboboxProdutoFC.getEditor();
            final Produtos selected = txtcomboboxProdutoFC.getSelectionModel().getSelectedItem();

            // Atualiza a filtragem
            produtosFiltrados.setPredicate(produto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lower = newValue.toLowerCase();
                return produto.getDescricao_produto().toLowerCase().contains(lower);
            });

            // Se o item selecionado não for o mesmo que o texto digitado, limpa seleção
            if (selected == null || !selected.getDescricao_produto().equals(editor.getText())) {
                txtcomboboxProdutoFC.getSelectionModel().clearSelection();
            }
        });

        txtcomboboxProdutoFC.setConverter(new StringConverter<Produtos>() {
            @Override
            public String toString(Produtos produto) {
                return produto != null ? produto.toString() : "";
            }

            @Override
            public Produtos fromString(String string) {
                return txtcomboboxProdutoFC.getItems().stream()
                        .filter(p -> p.toString().equals(string))
                        .findFirst().orElse(null);
            }
        });

    }

    private void limparCampos() {
        txtcomboboxProdutoFC.setValue(null);
        txtquantidadeFC.clear();
        txtdatapickerFC.setValue(null);
        txtprecoUnitarioFC.clear();
        txtprecototalFC.clear();
        txtdesconto.clear();
        txtdesconto.clear();
        precoTotalLabel.setText("");
        estoqueLabel.setText("");
        descontoLabel.setText("");

    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.show();
    }

    public void carregarCombboxProdutosAutoCompletado() {
        // Converte a lista de produtos em uma lista observável
        listProdutos = new ProdutosDAO().listar(); // ou seu método
        produtosObservableList = FXCollections.observableArrayList(listProdutos);
        txtcomboboxProdutoFC.setItems(produtosObservableList);

        // Define um filtro dinâmico
        FilteredList<Produtos> produtosFiltrados = new FilteredList<>(produtosObservableList, p -> true);

        txtcomboboxProdutoFC.setItems(produtosFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        txtcomboboxProdutoFC.setEditable(true);
        txtcomboboxProdutoFC.getEditor().textProperty().addListener((obs,
                                                                     oldValue, newValue) -> {
            final String filtro = newValue.toLowerCase();

            // Aplica filtro
            produtosFiltrados.setPredicate(produto -> {
                if (filtro == null || filtro.isEmpty()) {
                    return true;
                }
                return produto.getDescricao_produto().toLowerCase().contains(filtro);
            });

            // Mostra o menu dropdown automaticamente
            if (!txtcomboboxProdutoFC.isShowing()) {
                txtcomboboxProdutoFC.show();
            }
        });

        // Corrige o comportamento de seleção para manter o objeto Produtos real
        txtcomboboxProdutoFC.setConverter(new StringConverter<Produtos>() {
            @Override
            public String toString(Produtos produto) {
                return produto != null ? produto.getDescricao_produto() : "";
            }

            @Override
            public Produtos fromString(String string) {
                return produtosObservableList.stream()
                        .filter(p -> p.getDescricao_produto().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    public void carregarTableviewFluxodecaixa() {
        colunaCodigoFC.setCellValueFactory(new PropertyValueFactory<>("idfluxocaixa"));
        colunaProdutoFC.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduto().getDescricao_produto()));
        colunaQuantidadeFC.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunaPrecoTotalFC.setCellFactory(tc -> new TableCell<FluxodeCaixa, Double>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    setText(String.format("MZN %.2f", valor));
                }
            }
        });
        colunaPrecoTotalFC.setCellValueFactory(new PropertyValueFactory<>("valor"));

        colunaDataFC.setCellValueFactory(new PropertyValueFactory<>("data"));
        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colunaDataFC.setCellFactory(column ->new TableCell<FluxodeCaixa, String>(){
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
        colunaDesconto.setCellValueFactory(new PropertyValueFactory<>("desconto"));

        //Listener para txtProcuraNome
        txtpesquisaFC.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableviewFluxodecaixa();
            }
        });

        fluxodeCaixaList = fluxodeCaixaDAO.listar();

        fluxodeCaixaObservableList = FXCollections.observableArrayList(fluxodeCaixaList);
        tableviewFluxodeCaixa.setItems(fluxodeCaixaObservableList);
    }

    //Validar entrada de Dados no Cadastro
    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (txtcomboboxProdutoFC.getValue() == null || txtcomboboxProdutoFC.getValue() == null) {
            erroMessage += "produto invalido!\n";
        }
        if (txtquantidadeFC.getText() == null || txtquantidadeFC.getText().isEmpty()) {
            erroMessage += "Quantidade invalida!\n";
        }
        if (txtdatapickerFC.getValue() == null || txtdatapickerFC.getValue() == null) {
            erroMessage += "A data invalida!\n";
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

    private void atualizarPrecoTotal() {
        Produtos produtoSelecionado = txtcomboboxProdutoFC.getValue();
        String quantidadeTexto = txtquantidadeFC.getText();

        if (produtoSelecionado != null && quantidadeTexto.matches("\\d+")) {
            int quantidade = Integer.parseInt(quantidadeTexto);
            double preco = produtoSelecionado.getPreco();
            double total = preco * quantidade;
            txtprecototalFC.setText(String.format("%.2f", total));
        } else {
            txtprecototalFC.clear();
        }
    }

    //Para mostrar assim que o usuario digita desconto antes de executar venda
    private void atualizarValoresCalculados() {
        Produtos produto = txtcomboboxProdutoFC.getValue();
        if (produto == null) return;

        double precoUnitario = produto.getPreco();

        int quantidade = 0;
        try {
            quantidade = Integer.parseInt(txtquantidadeFC.getText());
        } catch (NumberFormatException e) {
            // quantidade inválida, considerar 0
        }

        double descontoPercentual = 0.0;
        String texto = txtdesconto.getText();
        if (texto != null && !texto.trim().isEmpty()) {
            try {
                texto = texto.replace(",", ".");
                descontoPercentual = Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                descontoPercentual = 0.0;
            }
        }

        double valorBruto = precoUnitario * quantidade;
        double valorDesconto = valorBruto * (descontoPercentual / 100);
        double valorFinal = valorBruto - valorDesconto;

        descontoLabel.setText(String.format(" %.2f MZN", valorDesconto));
        precoTotalLabel.setText(String.format(" %.2f MZN", valorFinal));
    }

    private void emtemporealDesconto(){
            txtdesconto.textProperty().addListener((observable, oldValue, newValue) -> {
                atualizarValoresCalculados();
            });

            txtquantidadeFC.textProperty().addListener((obs, oldVal, newVal) -> {
                atualizarValoresCalculados();
            });

            txtcomboboxProdutoFC.valueProperty().addListener((obs, oldVal, newVal) -> {
                atualizarValoresCalculados();
            });

    }
}
