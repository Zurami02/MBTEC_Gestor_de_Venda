package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.service.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.service.IdleMonitor;
import mbtec.gestaoentradasaida_mbtec.service.TemporizadorConfig;
import mbtec.gestaoentradasaida_mbtec.service.UsuarioNoSistema;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller principal para gerenciar todas as telas do sistema
 * @author Mbtec : Tecnico Zulo Mitumba
 * @version 1.0
 * metodos contidos na classe chamam menuItens na tela principal
 */
public class TelaprincipalController implements Initializable {

    double x, y = 0;

    @FXML
    private MenuItem menuitemUsuario;

    @FXML
    private MenuItem menuitemcadastrofuncionario;

    private IdleMonitor controladorTela;

    @FXML
    private Menu menuUsuario;

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private Label txtLabel;

    @FXML
    void menuitemHome(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/telaprincipalCopy.fxml");
    }

    @FXML
    void menuitemClose(@NotNull ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(
                            "/mbtec/gestaoentradasaida_mbtec/hmPage.fxml")));

            Scene scene = new Scene(root);

            // Fecha o stage atual
            Stage stageAtual = (Stage) ((MenuItem) event.getSource())
                    .getParentPopup()
                    .getOwnerWindow();
            stageAtual.close();

            // Cria um NOVO stage
            Stage novoStage = new Stage();
            novoStage.getIcons().add(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                            "/mbtec/gestaoentradasaida_mbtec/icones/mbtecShort.png")))
            );
            novoStage.initStyle(StageStyle.UNDECORATED);
            novoStage.setScene(scene);

            // Permite arrastar a janela
            final double[] offsetX = new double[1];
            final double[] offsetY = new double[1];

            root.setOnMousePressed(e -> {
                offsetX[0] = e.getSceneX();
                offsetY[0] = e.getSceneY();
            });

            root.setOnMouseDragged(e -> {
                novoStage.setX(e.getScreenX() - offsetX[0]);
                novoStage.setY(e.getScreenY() - offsetY[0]);
            });

            novoStage.show();

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar tela", e.getMessage());
        }
    }


    @FXML
    void menuitemConfiguracoes(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/config.fxml");
    }

    @FXML
    void menuItemOrcamento(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/orcamento.fxml");
    }

    /**
     * Metodo nao usado na versao 1.0
     */
    @FXML
    void menuitemcadastroServico(ActionEvent event) {
//        carregarTela("/mbtec/gestaoentradasaida_mbtec/servico.fxml");
    }


    @FXML
    void menuitemcadastroFornecedor(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/cadastroFornecedor.fxml");
    }

    @FXML
    void menuitemcadastroEntradaSaida(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/gestaoES.fxml");
    }

    @FXML
    void menuitemcadastroProdutos(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/cadastroProdutos.fxml");
    }

    @FXML
    void menuitemUsuario(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/usuarioInfo.fxml");
    }

    @FXML
    void menuitemcadastroFluxodeCaixa(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/fluxodecaixa.fxml");
    }

    @FXML
    void btnMenuItemVendas(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/vendas.fxml");
    }

    @FXML
    void menuitemRelatorioHistoricoVendas(ActionEvent event){
        carregarTela("/mbtec/gestaoentradasaida_mbtec/historicovendas.fxml");
    }

    @FXML
    void menuitemRelatorioHistoricoOrcamento(ActionEvent event){
        carregarTela("/mbtec/gestaoentradasaida_mbtec/historicoorcamento.fxml");
    }

    @FXML
    private Menu menuitemMBTEC;

    @FXML
    void menuitemMBTEC(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/sobreMBTEC.fxml");
    }

    @FXML
    void menuitemRelatorioFluxodeCaixa(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/relatorioPeriodo.fxml");
    }

    @FXML
    void menuitemRelatorioGestaoES(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/relatorioGestaoES.fxml");
    }

    /**
     * Metodo usado na versao 1.0
     */
    @FXML
    void menuitemcadastrofuncionario(ActionEvent event) {
        carregarTela("/mbtec/gestaoentradasaida_mbtec/cadastroUsuario.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iniciarControladorTela();

        // Observa mudanças no temporizador e reinicia monitor
        TemporizadorConfig.getInstance().minutoProperty().addListener(
                (obs, oldVal, newVal) ->
                {
                    Platform.runLater(() -> reiniciarControladorComNovoValor(newVal.intValue()));
                });

        Scene scene = anchorPaneMain.getScene();
        Platform.runLater(() -> {
            reiciarTemporizador(anchorPaneMain.getScene());
            mostrarUsuario();
        });
        controlarAcessibilidadeNosistema();

    }

    /**
     * responsavel pela recolocacao de minutos configurado pelo usuario nas configuracoes
     * @param minutos
     */
    private void reiniciarControladorComNovoValor(int minutos) {
        if (controladorTela != null) {
            controladorTela.stop();
        }
        controladorTela = new IdleMonitor(Duration.minutes(minutos));
        controladorTela.setOnIdle(() -> Platform.runLater(this::carregarTelaPrincipal));
        registrarControladorTela(anchorPaneMain.getScene());
    }

    /**
     * metodo responsavel a chamar telaPrincipalCopy
     * que eh homPage dos sistema.
     */
    private void iniciarControladorTela() {
        controladorTela = new IdleMonitor(Duration.minutes(TemporizadorConfig.getInstance().getMinutoTemporizador()));
        controladorTela.setOnIdle(() ->
                Platform.runLater(this::carregarTelaPrincipal));
    }

    /**
     * Responsavel pela reinicializacao de contador caso
     * troca ou fecho de sistema
     */
    public void reiciarTemporizador(Scene scene) {
        if (controladorTela != null) {
            controladorTela.reset();
        }
        iniciarControladorTela();
        registrarControladorTela(scene);
    }

    /**
     * Responsavel a colocar tela em modo de controlo ( a ser vigiada pela
     * inatividade no sistema)
     */
    public void registrarControladorTela(Scene scene) {
        controladorTela.register(scene);
    }

    private void carregarTelaPrincipal() {
        try {
            Parent tela = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/telaprincipalCopy.fxml")));
            anchorPaneMain.getChildren().setAll(tela);
        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Tela padrao", e.getMessage());
        }
    }

    /**
     * O metodo eh responsvel para mostrar ou guardar o usuario
     * no sistema, dando suporte de usuario ter acesso aos seus dados pessoais
     */
    private void mostrarUsuario() {

        if (menuitemUsuario == null) {
            System.err.println("menuitemUsuario é null telaPrincipalController");
            return;
        }
        Usuario usuario = UsuarioNoSistema.getInstance().getUsuarioLogado();
        if (usuario != null) {
            menuitemUsuario.setText(usuario.getUsuario());
            menuUsuario.setText(usuario.getNome_usuario());
        } else {
            System.err.println("menuitemUsuario não foi injetado!");
        }
    }

    /**
     * Controla as funcionalidades quanto a perfil dos usuarios no sistema
     */
    public void controlarAcessibilidadeNosistema() {
        Usuario usuario = UsuarioNoSistema.getInstance().getUsuarioLogado();

        boolean admin = usuario.getPerfil().equalsIgnoreCase("Diretor") ||
                usuario.getPerfil().equalsIgnoreCase("Administrador") ||
                usuario.getPerfil().equalsIgnoreCase("Director") ||
                usuario.getPerfil().equalsIgnoreCase("Gestor");
        if (!admin) {
            try {
                //menuitemcadastrofuncionario.setDisable(true);
                menuitemcadastrofuncionario.setVisible(false);
            } catch (Exception e) {
                AlertaUtil.mostrarErro("Erro ao definir acessibilidade no sistema", e.getMessage());
            }

        }
    }

    private void carregarTela(String caminho){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Falha ao carregar tela", e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
