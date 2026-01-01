package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import mbtec.gestaoentradasaida_mbtec.DAO.UsuarioDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.ControladorSenha;
import mbtec.gestaoentradasaida_mbtec.util.CriptografarUtil;
import mbtec.gestaoentradasaida_mbtec.util.PularPaginasUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class EsqueceuSenhaController implements Initializable {

    @FXML
    private DatePicker dataPickerRecup;

    @FXML
    private Label lbSenha;

    @FXML
    private Label lbNomeUsuario;

    @FXML
    private Label lbSenhaConfirmar;

    @FXML
    private Label lbtituloBI;

    @FXML
    private Label lbtituloNovaSenha;

    @FXML
    private Button btnConfirmar;

    private int idUsuarioRecup;

    @FXML
    private Button btnCancelarrecup;

    @FXML
    private RadioButton rb1;

    @FXML
    private RadioButton rb2;

    @FXML
    private RadioButton rb3;

    @FXML
    private ToggleGroup togglerbBI;

    @FXML
    private AnchorPane telaLogin;

    private String bilheteCorreto;

    @FXML
    private PasswordField txtSenhaConfirmacaoRecup;

    @FXML
    private PasswordField txtSenhaRecup;

    @FXML
    private TextField txtUsuarioEmailRecup;

    private int tentativasBI = 0;

    private int MAX_TENTATIVAS = 2;

    @FXML
    void ConfirmarRecup(ActionEvent event) throws IOException {
        String senha = txtSenhaRecup.getText();
        String confirmar = txtSenhaConfirmacaoRecup.getText();
        if (!ControladorSenha.senhaValida(senha)) {
            AlertaUtil.mostrarErro(
                    "Senha fraca",
                    ControladorSenha.mensagemErro(senha)
            );
            return;
        }
        if (senha.isBlank() || confirmar.isBlank()) {
            AlertaUtil.mostrarErro("Erro!", "Preencha todos os campos.");
            return;
        }

        if (!senha.equals(confirmar)) {
            AlertaUtil.mostrarErro("Erro!", "As senhas não coincidem. Verifique e tente novamente");
            return;
        }

        String senhaCriptografada = CriptografarUtil.gerarHash(senha);

        UsuarioDAO dao = new UsuarioDAO();
        boolean sucesso = dao.atualizarSenhaPorId(idUsuarioRecup, senhaCriptografada);

        if (!sucesso) {
            AlertaUtil.mostrarErro("Erro!", "Erro ao atualizar senha.");
            return;
        }

        AlertaUtil.mostrarInfo("Sucesso", "Senha redefinida com sucesso!");

        PularPaginasUtil.skipeToPaginas(event,
                "/mbtec/gestaoentradasaida_mbtec/loginUsuario.fxml");
    }

    @FXML
    void btnCancelarrecup(ActionEvent event) throws IOException {
        PularPaginasUtil.skipeToPaginas(event, "/mbtec/gestaoentradasaida_mbtec/loginUsuario.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comportamentoInicial();
        configurarListenerBI();
        listenerTextfielDataPicker();
    }

    public void validarUsuario() throws SQLException {

        String email = txtUsuarioEmailRecup.getText().trim().toLowerCase();
        LocalDate data = dataPickerRecup.getValue();

        if (email.isBlank() || data == null) return;

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.buscarPorEmailEData(email, data.toString());

        if (usuario == null) {
            lbNomeUsuario.setText("NA");
            lbNomeUsuario.setTextFill(Color.RED);
            mostrarCamposBI(false);
            return;
        }

        idUsuarioRecup = usuario.getIdusuario();
        lbNomeUsuario.setText(usuario.getNome_usuario());
        lbNomeUsuario.setTextFill(Color.CADETBLUE);
        iniciarDesafioBI(usuario);
    }

    private void iniciarDesafioBI(Usuario usuario) {

        tentativasBI = 0;
        bilheteCorreto = usuario.getBilhete();

        gerarNovaRodadaBI();
        mostrarCamposBI(true);

        btnConfirmar.setDisable(true);
        mostrarCamposSenha(false);
    }

    private void configurarListenerBI() {

        togglerbBI.selectedToggleProperty().addListener((obs, oldT, newT) -> {

            if (newT == null) return;

            rb1.setDisable(true);
            rb2.setDisable(true);
            rb3.setDisable(true);

            RadioButton rbSelecionado = (RadioButton) newT;
            String biSelecionado = (String) rbSelecionado.getUserData();

            if (bilheteCorreto.equals(biSelecionado)) {
                liberarRedefinicaoSenha();
                return;
            }

            tentativasBI++;

            if (tentativasBI >= MAX_TENTATIVAS) {
                new Alert(Alert.AlertType.ERROR,
                        "Número máximo de tentativas atingido.\nProcesso encerrado.")
                        .showAndWait();
                btnConfirmar.setDisable(true);
                txtUsuarioEmailRecup.setDisable(true);
                dataPickerRecup.setDisable(true);
                return;
            }

            new Alert(Alert.AlertType.WARNING,
                    "BI incorreto. Tentativa " + tentativasBI + " de " + MAX_TENTATIVAS)
                    .show();

            gerarNovaRodadaBI();
        });
    }

    private void liberarRedefinicaoSenha() {
        mostrarCamposSenha(true);
        btnConfirmar.setDisable(false);
    }

    /**
     * O metodo reage assim que o usuario termina de digitar dados
     * e largar os campos email e data de nascimento fields
     */
    public void listenerTextfielDataPicker() {
        txtUsuarioEmailRecup.focusedProperty().addListener((obs, oldV, newV) ->
        {
            if (!newV) {
                try {
                    validarUsuario();
                } catch (SQLException e) {
                    AlertaUtil.mostrarErro("Erro na validacao Email ou usuario", e.getMessage());
                }
            }
        });

        dataPickerRecup.valueProperty().addListener((obs, oldV, newV) ->
        {
            if (newV != null) {
                try {
                    validarUsuario();
                } catch (SQLException e) {
                    AlertaUtil.mostrarErro("Erro na validacao de data", e.getMessage());
                }
            }
        });
    }

    /**
     * Desabilita os componentes deixando o necessario pois validacao de dados
     */
    public void comportamentoInicial() {

        mostrarCamposBI(false);
        mostrarCamposSenha(false);

        btnConfirmar.setDisable(true);

        togglerbBI = new ToggleGroup();
        rb1.setToggleGroup(togglerbBI);
        rb2.setToggleGroup(togglerbBI);
        rb3.setToggleGroup(togglerbBI);
    }

    private void mostrarCamposSenha(boolean mostrar) {
        lbtituloNovaSenha.setVisible(mostrar);
        lbSenha.setVisible(mostrar);
        lbSenhaConfirmar.setVisible(mostrar);
        txtSenhaRecup.setVisible(mostrar);
        txtSenhaConfirmacaoRecup.setVisible(mostrar);
    }

    private void mostrarCamposBI(boolean mostrar) {
        lbtituloBI.setVisible(mostrar);
        rb1.setVisible(mostrar);
        rb2.setVisible(mostrar);
        rb3.setVisible(mostrar);
    }

    private String gerarBIFalso() {
        Random random = new Random();
        String bi;

        do {
            StringBuilder sb = new StringBuilder();

            // 12 números
            for (int i = 0; i < 12; i++) {
                sb.append(random.nextInt(10));
            }

            // 1 letra final
            char letraFinal = (char) ('A' + random.nextInt(26));
            sb.append(letraFinal);

            bi = sb.toString();
        } while (bi.equals(bilheteCorreto)); // garante que nunca seja o real

        return bi;
    }

    private String mascararBI(String bi) {
        return "*".repeat(10) + bi.substring(10);
    }

    private void gerarNovaRodadaBI() {

        List<String> bis = new ArrayList<>(List.of(
                bilheteCorreto,
                gerarBIFalso(),
                gerarBIFalso()
        ));

        Collections.shuffle(bis);

        RadioButton[] botoes = {rb1, rb2, rb3};

        for (int i = 0; i < botoes.length; i++) {
            RadioButton rb = botoes[i];

            rb.setText(mascararBI(bis.get(i))); // o que o usuário vê
            rb.setUserData(bis.get(i));         // o valor REAL (lógica)
            rb.setDisable(false);
        }

        togglerbBI.selectToggle(null); // força nova escolha
    }

}
