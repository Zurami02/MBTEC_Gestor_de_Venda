package mbtec.gestaoentradasaida_mbtec.util;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PularPaginasUtil {
    /**
     * metodo skipeToPaginas funcionara tanto para ActionEvent quanto MouseEvent, para tal
     * o metodo tem dois parametros Event event e String paginas k recebera o ficheiro FXML
     */
    public static void skipeToPaginas(Event event, String pagina) throws IOException {

        FXMLLoader loader = new FXMLLoader(PularPaginasUtil.class.getResource(pagina));
        Scene novaScene = new Scene(loader.load());
        Stage appStage = (Stage) ((Node)  event.getSource()).getScene().getWindow();
//        novaPagina.setOnMousePressed(event2 -> {
//            double x = event2.getSceneX();
//            double y = event2.getSceneY();
//        });
//        novaPagina.setOnMouseDragged(event1 -> {
//            appStage.setX(event1.getSceneX());
//            appStage.setY(event1.getSceneY());
//        });
        appStage.setScene(novaScene);

        appStage.show();

    }

    public static void skipeToPaginas(Node node, String fxml) throws IOException {

        Stage stage = (Stage) node.getScene().getWindow();

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(PularPaginasUtil.class.getResource(fxml))
        );
        Scene newScane = new Scene(root);
        stage.setScene(newScane);
        stage.show();
    }
}
