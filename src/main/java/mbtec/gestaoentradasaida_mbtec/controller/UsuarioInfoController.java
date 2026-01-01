package mbtec.gestaoentradasaida_mbtec.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import mbtec.gestaoentradasaida_mbtec.DAO.UsuarioDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.ControladorSenha;
import mbtec.gestaoentradasaida_mbtec.util.CriptografarUtil;
import mbtec.gestaoentradasaida_mbtec.util.UsuarioNoSistema;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UsuarioInfoController implements Initializable {


    private enum EstadoSenha {
        INICIAL,
        DIGITANDO_ATUAL,
        DIGITANDO_NOVA;
    }

    private EstadoSenha estadoSenha = EstadoSenha.INICIAL;

    @FXML
    private JFXButton btnConfirmarSenhaUsuarioInfo;

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private TextField txtEmailUsuarioInfo;

    @FXML
    Label lbAvisoErroUsuarioInfo;

    @FXML
    private TextField txtSenhaAtual;
    @FXML
    private TextField txtNovaSenha;
    @FXML
    private TextField txtConfirmarSenha;

    @FXML
    private TextField txtNomeUsuarioInfo;

    @FXML
    private TextField txtPerfilUsuarioInfo;

    @FXML
    private TextField txtTelefoneUsuarioInfo;

    @FXML
    private TextField txtUsuarioUsuarioInfo;

    @FXML
    private TextField txtdataNascUsuarioInfo;

    @FXML
    private TextField txtnumeroBIUsuarioInfo;

    @FXML
    private Button btnMudarSenhaUsuarioInfo;

    @FXML
    void btnConfirmarSenhaUsuarioInfo(ActionEvent event) {
        salvarNovaSenha();
    }

    @FXML
    void btnMudarSenhaUsuarioInfo(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText("Tem certeza que deseja mudar a senha?");

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isEmpty() || resultado.get() != btnSim) {
            return;
        }

        iniciarDigitacaoSenhaAtual();

    }

    private void iniciarDigitacaoSenhaAtual() {
        lbAvisoErroUsuarioInfo.setText("Digita Senha atual");
        lbAvisoErroUsuarioInfo.setTextFill(Color.BLACK);
        txtSenhaAtual.clear();
        txtSenhaAtual.setVisible(true);
        txtSenhaAtual.requestFocus();
        btnMudarSenhaUsuarioInfo.setDisable(true);

        estadoSenha = EstadoSenha.DIGITANDO_ATUAL;
    }

    private void validarSenhaAtual() {

        String senhaDigitada = txtSenhaAtual.getText();
        if (senhaDigitada.isBlank()) {
            mostrarAviso("Digite senha atual");
            return;
        }
        mostrarAviso("");
        Usuario usuario = UsuarioNoSistema.getInstance().getUsuarioLogado();

        boolean senhaOk = CriptografarUtil.verificarSenha(
                senhaDigitada,
                usuario.getSenha()
        );

        if (!senhaOk) {
            mostrarAviso("A senha atual incorreta");
            return;
        }

        mostrarAviso("");
        liberarCamposNovaSenha();

    }

    public void mostrarAviso(String message){
        lbAvisoErroUsuarioInfo.setText(message);
        lbAvisoErroUsuarioInfo.setTextFill(Color.RED);
    }

    private void salvarNovaSenha() {

        String nova = txtNovaSenha.getText().trim();
        String confirmar = txtConfirmarSenha.getText().trim();
        if (!ControladorSenha.senhaValida(nova)) {
            AlertaUtil.mostrarErro(
                    "Senha fraca",
                    ControladorSenha.mensagemErro(nova)
            );
            return;
        }
        if (nova.isBlank() || confirmar.isBlank()) {
            mostrarAviso("Preencha todos os campos");
            return;
        }

        if (!nova.equals(confirmar)) {
            mostrarAviso("As senhas não coincidem");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText("Tem certeza que deseja mudar a senha?");

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isEmpty() || resultado.get() != btnSim) {
            txtNovaSenha.clear();
            txtConfirmarSenha.clear();
            txtSenhaAtual.clear();

            txtNovaSenha.setVisible(false);
            txtConfirmarSenha.setVisible(false);
            txtSenhaAtual.setVisible(false);

            lbAvisoErroUsuarioInfo.setVisible(false);
            btnConfirmarSenhaUsuarioInfo.setDisable(true);
            btnMudarSenhaUsuarioInfo.setDisable(false);
            return;
        }

        String hash = CriptografarUtil.gerarHash(nova);

        Usuario usuario = UsuarioNoSistema.getInstance().getUsuarioLogado();
        new UsuarioDAO().atualizarSenhaPorId(usuario.getIdusuario(), hash);

        lbAvisoErroUsuarioInfo.setText("Successfully");
        lbAvisoErroUsuarioInfo.setTextFill(Color.GREEN);

        resetarEstadoSenha();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetarTela();
        mostrarInfoUsuario();
        listenerVerificarSenhaAtual();
    }

    private void listenerVerificarSenhaAtual() {
        txtSenhaAtual.focusedProperty().addListener((obs, antigo, novo) -> {
            if (!novo) { // perdeu foco
                if (estadoSenha == EstadoSenha.DIGITANDO_ATUAL) {
                    validarSenhaAtual();
                }
            }
        });

        txtSenhaAtual.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (estadoSenha == EstadoSenha.DIGITANDO_ATUAL) {
                    validarSenhaAtual();
                }
            }
        });

        btnConfirmarSenhaUsuarioInfo.setDisable(true);
        txtNovaSenha.setVisible(false);
        txtConfirmarSenha.setVisible(false);
    }

    private void liberarCamposNovaSenha() {

        txtNovaSenha.clear();
        txtConfirmarSenha.clear();

        txtNovaSenha.setVisible(true);
        txtConfirmarSenha.setVisible(true);

        btnConfirmarSenhaUsuarioInfo.setDisable(false);

        estadoSenha = EstadoSenha.DIGITANDO_NOVA;
    }

    public void mostrarInfoUsuario() {
        Usuario usuario = UsuarioNoSistema.getInstance().getUsuarioLogado();
        if (usuario != null) {
            txtNomeUsuarioInfo.setText(usuario.getNome_usuario());
            txtEmailUsuarioInfo.setText(usuario.getEmail());
            txtdataNascUsuarioInfo.setText(usuario.getData_nascimento());
            txtnumeroBIUsuarioInfo.setText(usuario.getBilhete());
            txtTelefoneUsuarioInfo.setText(usuario.getTelefone());
            txtUsuarioUsuarioInfo.setText(usuario.getUsuario());
            txtPerfilUsuarioInfo.setText(usuario.getPerfil());
        }
    }

    private void resetarEstadoSenha() {

        txtSenhaAtual.setVisible(false);
        txtNovaSenha.setVisible(false);
        txtConfirmarSenha.setVisible(false);
        btnConfirmarSenhaUsuarioInfo.setDisable(true);
        btnMudarSenhaUsuarioInfo.setDisable(false);

        estadoSenha = EstadoSenha.INICIAL;
    }

    private void resetarTela() {
        estadoSenha = EstadoSenha.INICIAL;

        txtSenhaAtual.setVisible(false);
        txtNovaSenha.setVisible(false);
        txtConfirmarSenha.setVisible(false);

        btnConfirmarSenhaUsuarioInfo.setDisable(true);
    }
}
