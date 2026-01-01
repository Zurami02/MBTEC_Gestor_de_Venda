package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.IdleMonitor;
import mbtec.gestaoentradasaida_mbtec.util.TemporizadorConfig;
import mbtec.gestaoentradasaida_mbtec.util.UsuarioNoSistema;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
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
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/telaprincipalCopy.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Tela Padrao", e.getMessage());
        }
    }

    /*
    void menuitemClose(ActionEvent event) {
        try {
            Parent novaPagina = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/hmPage.fxml")));
            Scene novaScene = new Scene(novaPagina);


            Stage appStage = (Stage) ((Node) ((MenuItem) event.getSource()).getParentPopup().getOwnerNode()).getScene().getWindow();
            appStage.initStyle(StageStyle.UNDECORATED);

            novaPagina.setOnMousePressed(event2 -> {
                x = event2.getSceneX();
                y = event2.getSceneY();
            });
            novaPagina.setOnMouseDragged(event1 -> {
                appStage.setX(event1.getSceneX());
                appStage.setY(event1.getSceneY());
            });
            appStage.setScene(novaScene);
            appStage.show();
        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Botao Close", e.getMessage());
        }
    }*/
    @FXML
    void menuitemClose(ActionEvent event) {
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/config.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro!", "Falha ao acessar configuracoes");
        }
    }

    /**
     * Metodo nao usado na versao 1.0
     */
    @FXML
    void menuitemcadastroServico(ActionEvent event) {
        //try {
//            // Carregar o FXML
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/servico.fxml"));
//            Parent root = loader.load();
//
//            // Limpar o AnchorPane atual e adicionar o novo conteúdo
//            anchorPaneMain.getChildren().clear();
//            anchorPaneMain.getChildren().add(root);
//
//        } catch (IOException e) {
//            e.printStackTrace(); // Trate a exceção adequadamente
//        }
    }

    @FXML
    void menuitemcadastroFornecedor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/cadastroFornecedor.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Cadastro de Fornecedores", String.valueOf(e));
        }
    }

    @FXML
    void menuitemcadastroEntradaSaida(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/gestaoES.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Cadastro de Entradas e Saidas", e.getMessage());
        }
    }

    @FXML
    void menuitemcadastroProdutos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/cadastroProdutos.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Cadastro de Produtos", e.getMessage());
        }

    }

    @FXML
    void menuitemUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/usuarioInfo.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Informacao de Usuario", e.getMessage());
        }
    }

    @FXML
    void menuitemcadastroFluxodeCaixa(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/fluxodecaixa.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Cadastro de Fluxo de Caixa", e.getMessage());
        }

    }

    @FXML
    private Menu menuitemMBTEC;

    @FXML
    void menuitemMBTEC(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mbtec/gestaoentradasaida_mbtec/sobreMBTEC.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Sobre MBTEC", e.getMessage());
        }
    }

    @FXML
    void menuitemRelatorioFluxodeCaixa(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/relatorioPeriodo.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Relatorio Fluxo de Caixa", e.getMessage());
        }
    }

    @FXML
    void menuitemRelatorioGestaoES(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/relatorioGestaoES.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            AlertaUtil.mostrarErro("Erro ao carregar Relatorio GestaoES", e.getMessage());
        }
    }

    /**
     * Metodo usado na versao 1.0
     */
    @FXML
    void menuitemcadastrofuncionario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/mbtec/gestaoentradasaida_mbtec/cadastroUsuario.fxml"));
            Parent root = loader.load();

            anchorPaneMain.getChildren().clear();
            anchorPaneMain.getChildren().add(root);

        } catch (IOException e) {
            System.out.println(" TelaPrincipal " + e);
            AlertaUtil.mostrarErro("Erro ao carregar Cadastro de Usuario", e.getMessage());
        }
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

}
