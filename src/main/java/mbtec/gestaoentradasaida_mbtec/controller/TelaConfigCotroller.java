package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import mbtec.gestaoentradasaida_mbtec.util.TemporizadorConfig;

import java.net.URL;
import java.util.ResourceBundle;

public class TelaConfigCotroller implements Initializable {

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private Spinner<Integer> spinnerTemporizador;

    @FXML
    private ImageView temporizadorOK;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spinnerListener();
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
}
