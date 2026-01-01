package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mbtec.gestaoentradasaida_mbtec.DAO.FluxodeCaixaDAO;
import mbtec.gestaoentradasaida_mbtec.domain.FluxodeCaixa;
import mbtec.gestaoentradasaida_mbtec.util.RelatorioUtil;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RelatorioFluxoController implements Initializable {
    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private TableColumn<FluxodeCaixa, String> colunaDatatableviewRelatorio;

    @FXML
    private TableColumn<FluxodeCaixa, Double> colunaDescontotableviewRelatorio;

    @FXML
    private TableColumn<FluxodeCaixa, String> colunaProdutotableviewRelatorio;

    @FXML
    private TableColumn<FluxodeCaixa, Integer> colunaQuantidadetableviewRelatorio;

    @FXML
    private TableColumn<FluxodeCaixa, Double> colunaValortableviewRelatorio;

    @FXML
    private TableView<FluxodeCaixa> tableviewRelatorio;

    @FXML
    private DatePicker dataPickerFinal;

    @FXML
    private DatePicker dataPickerInicial;

    @FXML
    void btnOKrelatorio(ActionEvent event) {
        LocalDate inicio = dataPickerInicial.getValue();
        LocalDate fim = dataPickerFinal.getValue();

        if (inicio == null || fim == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione as datas.").show();
            return;
        }

        FluxodeCaixaDAO dao = new FluxodeCaixaDAO();
        List<FluxodeCaixa> lista = dao.listarFluxoPorPeriodo(inicio, fim);

        if (lista.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Nenhum resultado encontrado.").show();
        } else {
            tableviewRelatorio.getItems().setAll(lista); // Preenche a tabela
        }
        carregarTableviewRelatorioFluxodecaixa();

    }

    @FXML
    void onGerarRelatorioPorPeriodo(ActionEvent event) {
        InputStream is = getClass().getResourceAsStream("/relatoriosjasper/RelatorioFluxo.jasper");

        LocalDate dataInicial = dataPickerInicial.getValue();
        LocalDate dataFinal = dataPickerFinal.getValue();

        if (dataInicial == null || dataFinal == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione as duas datas.").show();
            return;
        }

        if (dataInicial.isAfter(dataFinal)) {
            new Alert(Alert.AlertType.WARNING, "A data inicial não pode ser depois da final.").show();
            return;
        }

        // Consulta dados
        FluxodeCaixaDAO dao = new FluxodeCaixaDAO();
        List<FluxodeCaixa> dados = dao.listarFluxoPorPeriodo(dataInicial, dataFinal);
        for (FluxodeCaixa f : dados) {
            String dataOriginal = f.getData();
            try {
                LocalDate dataFormatada =
                        LocalDate.parse(dataOriginal);
                f.setData(
                        dataFormatada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            } catch (Exception e) {
                System.out.println("Data Invalida no registro: " + dataOriginal);
            }

        }

        if (dados.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Nenhum dado encontrado para o período selecionado.").show();
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("inicio", dataInicial.toString());
        parametros.put("fim", dataFinal.toString());
        parametros.put("titulo", "Relatório de Produtos Vendidos");

                RelatorioUtil.gerarRelatorioStream(dados,
                        "/relatoriosjasper/RelatorioFluxo.jasper", parametros);
    }

    private final FluxodeCaixaDAO fluxodeCaixaDAO = new FluxodeCaixaDAO();
    private List<FluxodeCaixa> fluxodeCaixaList = new ArrayList<>();
    private ObservableList<FluxodeCaixa> fluxodeCaixaObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void carregarTableviewRelatorioFluxodecaixa() {

        colunaProdutotableviewRelatorio.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduto().getDescricao_produto()));
        colunaQuantidadetableviewRelatorio.setCellValueFactory(new PropertyValueFactory<>(
                "quantidade"));
        colunaValortableviewRelatorio.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colunaValortableviewRelatorio.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", valor));
                }
            }
        });
        colunaDatatableviewRelatorio.setCellValueFactory(new PropertyValueFactory<>("data"));
        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colunaDatatableviewRelatorio.setCellFactory(column ->new TableCell<FluxodeCaixa, String>(){
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
        colunaDescontotableviewRelatorio.setCellValueFactory(new PropertyValueFactory<>("desconto"));

        LocalDate inicio = dataPickerInicial.getValue();
        LocalDate fim = dataPickerFinal.getValue();

        if (inicio != null && fim != null) {
            fluxodeCaixaList = fluxodeCaixaDAO.listarFluxoPorPeriodo(inicio, fim);
            fluxodeCaixaObservableList = FXCollections.observableArrayList(fluxodeCaixaList);
            tableviewRelatorio.setItems(fluxodeCaixaObservableList);
        } else {
            System.out.println("Datas não selecionadas.");
        }
    }
}
