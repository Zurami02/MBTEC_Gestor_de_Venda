package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mbtec.gestaoentradasaida_mbtec.DAO.UsuarioDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.PularPaginasUtil;
import mbtec.gestaoentradasaida_mbtec.util.UsuarioNoSistema;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * a classe responsavel pela entrada no sistema
 * @version 1.0
 * @author Mbtec :Tecnico Zulo mitumba
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtMostrarPassword;

    @FXML
    private Label labelAviso;

    @FXML
    private AnchorPane telaLogin;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private CheckBox checBoxMostrarPassword;

    @FXML
    private TextField txtUsuarioEmail;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    void checBoxMostrarPassword(ActionEvent event) {
        if (checBoxMostrarPassword.isSelected()) {
            //mostrar a senha
            txtMostrarPassword.setVisible(true);
            txtMostrarPassword.setManaged(true);
            txtSenha.setVisible(false);
            txtSenha.setManaged(false);
        } else {
            //ocultar a senha
            txtMostrarPassword.setVisible(false);
            txtMostrarPassword.setManaged(false);
            txtSenha.setVisible(true);
            txtSenha.setManaged(true);
        }
    }

    @FXML
    void cancelarLogin(ActionEvent event) throws IOException {
        PularPaginasUtil.skipeToPaginas(event, "/mbtec/gestaoentradasaida_mbtec/hmPage.fxml");
    }

    @FXML
    void esquecerSenha(ActionEvent event) throws IOException {
        PularPaginasUtil.skipeToPaginas(event, "/mbtec/gestaoentradasaida_mbtec/esqueceuSenha.fxml");
    }

    @FXML
    void loginEntrar(ActionEvent event) throws IOException {
        labelAviso.setVisible(true);
        String usuarioOuEmail = txtUsuarioEmail.getText().trim();
        String senha = txtSenha.getText();

        if (usuarioOuEmail.isEmpty() || senha.isEmpty()) {
            labelAviso.setText("Por favor, preencha todos os campos.");
            return;
        }

        Usuario usuarioEncontrado = usuarioDAO.buscarPorUsuarioOuEmail(usuarioOuEmail);

        if (usuarioEncontrado == null) {
            labelAviso.setText("Usuário ou e-mail não encontrado.");
            return;
        }

        if (!BCrypt.checkpw(senha, usuarioEncontrado.getSenha())) {
            labelAviso.setText("Senha incorreta.");
            return;
        }
        UsuarioNoSistema.getInstance().setUsuarioLogado(usuarioEncontrado);

        usuarioDAO.autenticar(usuarioOuEmail, senha);
        goToPaginas((Node) event.getSource(), "/mbtec/gestaoentradasaida_mbtec/telaprincipal.fxml");


    }

    /**
     * metodo responsavel pela entrada com tecla Enter(Listener no Initilizer)
     * @throws IOException
     */
    private void executarLogin() throws IOException {
        labelAviso.setVisible(true);
        String usuarioOuEmail = txtUsuarioEmail.getText().trim();
        String senha = txtSenha.getText();

        if (usuarioOuEmail.isEmpty() || senha.isEmpty()) {
            labelAviso.setText("Por favor, preencha todos os campos.");
            return;
        }

        Usuario usuarioEncontrado = usuarioDAO.buscarPorUsuarioOuEmail(usuarioOuEmail);

        if (usuarioEncontrado == null) {
            labelAviso.setText("Usuário ou e-mail não encontrado.");
            return;
        }

        if (!BCrypt.checkpw(senha, usuarioEncontrado.getSenha())) {
            labelAviso.setText("Senha incorreta.");
            return;
        }
        // Aqui guarda o usuario no sistema → guarda na sessão
        UsuarioNoSistema.getInstance().setUsuarioLogado(usuarioEncontrado);

        usuarioDAO.autenticar(usuarioOuEmail, senha);
        goToPaginas(txtSenha, "/mbtec/gestaoentradasaida_mbtec/telaprincipal.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Inicialmente, mostrar somente passwordfield
        txtMostrarPassword.setVisible(false);
        txtMostrarPassword.setManaged(false);
        //Sincronizar entre os campos de mostrarPassWord e passwordField
        txtSenha.textProperty().bindBidirectional(txtMostrarPassword.textProperty());

        //Sincroniza a tela de Login com tecla ENTER quando pressionada
        EventHandler<KeyEvent> enterHandle = e -> {
            if (e.getCode() == KeyCode.ENTER) {
                try {
                    executarLogin();
                } catch (IOException ex) {
                    AlertaUtil.mostrarErro("Erro na sincronizacao", ex.getMessage());
                }
            }
        };
        txtUsuarioEmail.setOnKeyPressed(enterHandle);
        txtSenha.setOnKeyPressed(enterHandle);
    }

    public void goToPaginas1(Node origem, String fxml) throws IOException {
        Stage stageAtual = (Stage) telaLogin.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) origem.getScene().getWindow();

        TelaprincipalController controller = loader.getController();
        controller.registrarControladorTela(scene);

        stage.setScene(scene);
        stage.show();
        stageAtual.close();
    }

    public void goToPaginas(Node origem, String fxml) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource(fxml));

        Parent root = loader.load();

        Stage novo = new Stage();
        novo.setScene(new Scene(root));
        novo.setTitle("MBTEC Gestor de Venda");
        novo.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                        "/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
        );
        novo.setResizable(false);
        novo.setOnCloseRequest(event -> {

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
                try {
                    Parent root2 = FXMLLoader.load(
                            Objects.requireNonNull(getClass().getResource(
                                    "/mbtec/gestaoentradasaida_mbtec/hmPage.fxml"))
                    );

                    Stage novoStage = new Stage();
                    novoStage.setScene(new Scene(root2));
                    novoStage.getIcons().add(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                                    "/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
                    );
                    novoStage.initStyle(StageStyle.UNDECORATED);
                    Stage stageAtual = (Stage) event.getSource();
                    novoStage.show();
                    stageAtual.close();

                } catch (IOException e) {
                    AlertaUtil.mostrarErro("Erro", "Erro ao carregar HomePage");
                }

            }
        });
        novo.show();

        Stage atual =
                (Stage) origem.getScene().getWindow();
        atual.close();
    }

}
