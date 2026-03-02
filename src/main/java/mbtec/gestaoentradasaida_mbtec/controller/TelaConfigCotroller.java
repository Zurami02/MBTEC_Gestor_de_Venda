package mbtec.gestaoentradasaida_mbtec.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import mbtec.gestaoentradasaida_mbtec.service.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.service.ConfigUtil;
import mbtec.gestaoentradasaida_mbtec.service.ConfigUtil1;
import mbtec.gestaoentradasaida_mbtec.service.TemporizadorConfig;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.net.URL;
import java.util.ResourceBundle;

public class TelaConfigCotroller implements Initializable {

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private Label lblImpressora;

    @FXML
    private Label lblImpressoraOrcamento;

    @FXML
    private Spinner<Integer> spinnerTemporizador;

    @FXML
    private ImageView temporizadorOK;

    @FXML
    private JFXComboBox<PrintService> comboboxImpressora;

    @FXML
    private JFXComboBox<PrintService> comboboxImpressoraOrcamento;

    @FXML
    void btnSalvarTemporizador(ActionEvent event) {

        int novoValor = spinnerTemporizador.getValue();
        TemporizadorConfig.getInstance().setMinutoTemporizador(novoValor);
        new Alert(Alert.AlertType.INFORMATION, " Temporizador :" + novoValor + " Minutos");

        temporizadorOK.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e->
                temporizadorOK.setVisible(false));
        pause.play();
    }

    @FXML
    void btnSalvarImpressora(ActionEvent event){
        PrintService selecionada = comboboxImpressora.getValue();
        if (selecionada != null) {
            ConfigUtil1.set("printer.default", selecionada.getName());
            lblImpressora.setText(selecionada.getName());
            AlertaUtil.mostrarInfo("","Impressora salva com sucesso!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spinnerListener();
        pegarImpressora();
        inicializarImpressoraOrcamento();
        atualizarLabelImpressora();
        String impressoraSalva = ConfigUtil1.get("printer.default");
        if (impressoraSalva != null && !impressoraSalva.isBlank()){

            lblImpressora.setText(impressoraSalva);
        }else {
            lblImpressora.setText("Nenhuma impressora foi configurada");
        }
    }

    private void spinnerListener() {
        TemporizadorConfig.getInstance();
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120,
                        TemporizadorConfig.getInstance().getMinutoTemporizador());
        spinnerTemporizador.setValueFactory(valueFactory);
        spinnerTemporizador.valueProperty().addListener((obs, oldValue, newValue) -> {
            TemporizadorConfig.getInstance().setMinutoTemporizador(newValue);
        });
    }

    private void pegarImpressora() {
        PrintService[] services =
                PrintServiceLookup.lookupPrintServices(null, null);

        comboboxImpressora.getItems().addAll(services);

        comboboxImpressora.setConverter(new StringConverter<>() {
            @Override
            public String toString(PrintService ps) {
                return ps == null ? "" : ps.getName();
            }

            @Override
            public PrintService fromString(String s) {
                return null;
            }
        });

        String impressoraSalva = ConfigUtil1.get("printer.default");
        if (impressoraSalva != null) {
            for (PrintService ps : services) {
                if (ps.getName().equalsIgnoreCase(impressoraSalva)) {
                    comboboxImpressora.getSelectionModel().select(ps);
                    break;
                }
            }
        }
    }

    private void inicializarImpressoraOrcamento() {
        // Buscar todas as impressoras
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        comboboxImpressoraOrcamento.getItems().addAll(services);

        // Converter para mostrar apenas o nome
        comboboxImpressoraOrcamento.setConverter(new StringConverter<>() {
            @Override
            public String toString(PrintService ps) {
                return ps == null ? "" : ps.getName();
            }

            @Override
            public PrintService fromString(String s) {
                return null;
            }
        });

        // Carregar impressora salva
        String impressoraSalva = ConfigUtil.get(
                ConfigUtil.TipoConfig.IMPRESSORA_ORCAMENTO,
                "impressora"
        );

        if (impressoraSalva != null && !impressoraSalva.isBlank()) {
            lblImpressoraOrcamento.setText(impressoraSalva);

            // Selecionar no ComboBox
            for (PrintService ps : services) {
                if (ps.getName().equalsIgnoreCase(impressoraSalva)) {
                    comboboxImpressoraOrcamento.getSelectionModel().select(ps);
                    break;
                }
            }
        } else {
            lblImpressoraOrcamento.setText("Nenhuma impressora configurada");
        }

        // Listener para atualizar label quando selecionar
        comboboxImpressoraOrcamento.valueProperty().addListener((obs, old, selecionado) -> {
            if (selecionado != null) {
                lblImpressoraOrcamento.setText(selecionado.getName());
            }
        });
    }

    private void atualizarLabelImpressora() {
        String impressoraSalva = ConfigUtil1.get("printer.default");

        if (impressoraSalva != null && !impressoraSalva.isBlank()) {
            lblImpressora.setText(impressoraSalva);
        } else {
            lblImpressora.setText("Nenhuma impressora foi configurada");
        }

        comboboxImpressora.valueProperty().addListener((obs, old, selecionado)->
        {
            if (selecionado != null){
                lblImpressora.setText(impressoraSalva);
            }
        });
    }

    @FXML
    public void btnSalvarImpressoraOrcamento(ActionEvent event) {
        PrintService selecionada = comboboxImpressoraOrcamento.getValue();

        if (selecionada != null) {
            // Salvar configuração
            ConfigUtil.set(ConfigUtil.TipoConfig.IMPRESSORA_ORCAMENTO,
                    "impressora",
                    selecionada.getName());

            lblImpressoraOrcamento.setText(selecionada.getName());

            AlertaUtil.mostrarInfo("Sucesso",
                    "");
        } else {
            AlertaUtil.mostrarAviso("Atenção",
                    "Selecione uma impressora para Orçamentos");
        }
    }

    @FXML
    void btnAtualizarImpressoras(ActionEvent event) {
        comboboxImpressoraOrcamento.getItems().clear();
        inicializarImpressoraOrcamento();

        AlertaUtil.mostrarInfo("Sucesso",
                "Lista de impressoras atualizada!");
    }

    @FXML
    public void btnTestarImpressoraOrcamento(ActionEvent event) {

    }
}
