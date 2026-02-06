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
    private Spinner<Integer> spinnerTemporizador;

    @FXML
    private ImageView temporizadorOK;

    @FXML
    private JFXComboBox<PrintService> comboboxImpressora;

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
            ConfigUtil.set("printer.default", selecionada.getName());
            lblImpressora.setText(selecionada.getName());
            AlertaUtil.mostrarInfo("","Impressora salva com sucesso!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spinnerListener();
        pegarImpressora();
        atualizarLabelImpressora();
        String impressoraSalva = ConfigUtil.get("printer.default");
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

        String impressoraSalva = ConfigUtil.get("printer.default");
        if (impressoraSalva != null) {
            for (PrintService ps : services) {
                if (ps.getName().equalsIgnoreCase(impressoraSalva)) {
                    comboboxImpressora.getSelectionModel().select(ps);
                    break;
                }
            }
        }
    }

    private void atualizarLabelImpressora() {
        String impressoraSalva = ConfigUtil.get("printer.default");

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
}
