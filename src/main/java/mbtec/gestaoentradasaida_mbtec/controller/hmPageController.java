package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mbtec.gestaoentradasaida_mbtec.DAO.UsuarioDAO;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.EstadoApp;
import mbtec.gestaoentradasaida_mbtec.util.PularPaginasUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * A classe responsavel para monitorar Login ou registo de novo usuario
 * @versao 1.0
 */
public class hmPageController implements Initializable {

    @FXML
    private ImageView entrar;

    @FXML
    private Pane pane;

    @FXML
    private ImageView sair;

    @FXML
    private ImageView imageDBConnection;

    /**
     * o metodo nao foi usado na versao 1.0
     * @param event
     * @throws IOException
     */
    @FXML
    void entrar(MouseEvent event) throws IOException {

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (usuarioDAO.countUsuarios() == 0) {
            AlertaUtil.mostrarInfo("Primeiro cadastro", "Lembra 'Perfil' deve ser autorizado a cadastro de funcionarios");
            // Nenhum usuário no banco → primeira execução → cadastro de admin
            PularPaginasUtil.skipeToPaginas(event, "/mbtec/gestaoentradasaida_mbtec/cadastroUsuario.fxml");

        } else {
            // Já tem usuários → login
            PularPaginasUtil.skipeToPaginas(event, "/mbtec/gestaoentradasaida_mbtec/loginUsuario.fxml");
        }
    }

    @FXML
    void sair(MouseEvent event) {

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText("Tem certeza que deseja fechar o sistema?");
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
        );

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnSim) {
            Platform.exit();
            //stage.close();
        }
    }

    void verificarConexao(){
        if (EstadoApp.isDbConectado()) {
            imageDBConnection.setImage(new Image("/mbtec/gestaoentradasaida_mbtec/icones/db_connected.png"));
            imageDBConnection.setVisible(true);
        } else {
            imageDBConnection.setImage(new Image("/mbtec/gestaoentradasaida_mbtec/icones/db_Desconnected.png"));
            imageDBConnection.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        verificarConexao();
    }
}


