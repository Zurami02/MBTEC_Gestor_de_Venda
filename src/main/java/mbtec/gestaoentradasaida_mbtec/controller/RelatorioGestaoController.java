package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mbtec.gestaoentradasaida_mbtec.DAO.GestaoESDAO;
import mbtec.gestaoentradasaida_mbtec.domain.GestaoES;
import mbtec.gestaoentradasaida_mbtec.util.RelatorioUtil;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RelatorioGestaoController implements Initializable {
    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private TableColumn<GestaoES, Integer> colunaQTDTableviewgestao;

    @FXML
    private TableColumn<GestaoES, Double> colunaValorTableviewgestao;

    @FXML
    private TableColumn<GestaoES, String> colunadataTableviewgestao;

    @FXML
    private TableColumn<GestaoES, String> colunadescricaoTableviewgestao;

    @FXML
    private TableColumn<GestaoES, String> colunatipoTableviewgestao;

    @FXML
    private TableView<GestaoES> tableviewRelatorioGestao;

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

        GestaoESDAO dao = new GestaoESDAO();
        List<GestaoES> lista = dao.listarGestaoPorPeriodo(inicio, fim);

        if (lista.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Nenhum resultado encontrado.").show();
        } else {
            tableviewRelatorioGestao.getItems().setAll(lista); // Preenche a tabela
        }
        carregarTableviewRelatorioGestaoES();

    }

    @FXML
    void onGerarRelatorioPorPeriodo(ActionEvent event) {
        InputStream is = getClass().getResourceAsStream("/relatoriosjasper/RelatorioGestaoES.jasper");

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
        GestaoESDAO dao = new GestaoESDAO();
        List<GestaoES> dados = dao.listarGestaoPorPeriodo(dataInicial, dataFinal);
        for (GestaoES g : dados) {
            String dataOriginal = g.getData();
            try {
                LocalDate dataFormatada =
                        LocalDate.parse(dataOriginal);
                g.setData(
                        dataFormatada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            } catch (Exception e) {
                System.out.println("Data Invalida no registro: " + dataOriginal);
            }

        }

        if (dados.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Nenhum dado encontrado para o período selecionado.").show();
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("inicio", dataInicial.toString());
        parametros.put("fim", dataFinal.toString());
        parametros.put("titulo", "Relatório de Gestao de Entrada e Saida");

                RelatorioUtil.gerarRelatorioStream(dados,
                        "/relatoriosjasper/RelatorioGestaoES.jasper", parametros);
    }

    private final GestaoESDAO gestaoESDAO = new GestaoESDAO();
    private GestaoES gestaoES = new GestaoES();
    private List<GestaoES> gestaoESList = new ArrayList<>();
    private ObservableList<GestaoES> gestaoESObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void carregarTableviewRelatorioGestaoES() {
        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunadataTableviewgestao.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunadataTableviewgestao.setCellFactory(column ->new TableCell<GestaoES, String>(){
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
        colunatipoTableviewgestao.setCellValueFactory(new PropertyValueFactory<>(
                "planoconta"));
        colunadescricaoTableviewgestao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaQTDTableviewgestao.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        colunaValorTableviewgestao.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaValorTableviewgestao.setCellFactory(column -> new TableCell<>() {
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

        LocalDate inicio = dataPickerInicial.getValue();
        LocalDate fim = dataPickerFinal.getValue();

        if (inicio != null && fim != null) {
            gestaoESList = gestaoESDAO.listarGestaoPorPeriodo(inicio, fim);
            gestaoESObservableList = FXCollections.observableArrayList(gestaoESList);
            tableviewRelatorioGestao.setItems(gestaoESObservableList);
        } else {
            System.out.println("Datas não selecionadas.");
        }
    }
}
