package mbtec.gestaoentradasaida_mbtec;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.EstadoApp;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class MainMBTEC extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            ConexaoSQLite.getConnection();
            EstadoApp.setDbConectado(true);
        } catch (Exception e) {
            EstadoApp.setDbConectado(false);
            AlertaUtil.mostrarErro(
                    "Erro crítico",
                    "Banco de dados não disponível."
            );
            Platform.exit();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/hmPage.fxml"));
        Scene scene = new Scene(loader.load());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("MBTEC Gestor de Venda");
        stage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
        );
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {

            event.consume();
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmação");
            alerta.setHeaderText(null);
            alerta.setContentText("Tem certeza que deseja sair do sistema?");
            ButtonType btnSim = new ButtonType("Sim");
            ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
            alerta.getButtonTypes().setAll(btnSim, btnNao);
            Optional<ButtonType> resultado = alerta.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnSim) {
                stage.close();
            }
        });
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}