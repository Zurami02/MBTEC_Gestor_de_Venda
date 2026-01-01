package mbtec.gestaoentradasaida_mbtec.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import mbtec.gestaoentradasaida_mbtec.DAO.ProdutosDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.UsuarioDAO;
import mbtec.gestaoentradasaida_mbtec.domain.FluxodeCaixa;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.util.AlertaUtil;
import mbtec.gestaoentradasaida_mbtec.util.ControladorSenha;
import mbtec.gestaoentradasaida_mbtec.util.RelatorioUtil;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UsuarioController implements Initializable {

    @FXML
    private Button btnAtualizarUsuario;

    @FXML
    private Button btnRegistarUsuario;

    @FXML
    private Button btnRelatorioUsuario;

    @FXML
    private Button btnRemoverUsuario;

    @FXML
    private TableColumn<Usuario, Integer> colunaCodigoUsuario;

    @FXML
    private TableColumn<Usuario, String> colunaNomeUsuario;

    @FXML
    private TableColumn<Usuario, String> colunaPerfi;

    @FXML
    private TableColumn<Usuario, Double> colunaSalarioUsuario;

    @FXML
    private TableColumn<Usuario, String> colunaSexoUsuario;

    @FXML
    private TableColumn<Usuario, String> colunaTelefoneUsuario;

    @FXML
    private DatePicker datapickerDataAdmissao;

    @FXML
    private DatePicker datapickerDataNascimento;

    @FXML
    private TableView<Usuario> tableviewUsuario;

    @FXML
    private TextField txtBI;

    @FXML
    private TextField txtCargo;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtPerfil;

    @FXML
    private TextField txtPesquisa;

    @FXML
    private TextField txtSalario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtSenhaConfirmar;

    @FXML
    private TextField txtSexo;

    @FXML
    private TextField txtTelefone;

    @FXML
    private TextField txtUsuario;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuario;
    private List<Usuario> usuarioList;
    private ObservableList<Usuario> observableUsuarioList;

    @FXML
    void btnAtualizarUsuario(ActionEvent event) {
        usuario = tableviewUsuario.getSelectionModel().getSelectedItem();

        if (usuario == null) {
            AlertaUtil.mostrarErro("Seleção necessária", "Por favor, selecione um usuário na tabela.");
            return;
        }

        Optional<ButtonType> resultado = AlertaUtil.mostrarConfirmacao(
                "Confirmação de atualização",
                "Tem certeza que deseja atualizar " + usuario.getNome_usuario() + "?");

        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }

        boolean houveAlteracao =
                !usuario.getNome_usuario().equals(txtNome.getText()) ||
                        !usuario.getSexo().equals(txtSexo.getText()) ||
                        !LocalDate.parse(usuario.getData_nascimento()).equals(datapickerDataNascimento.getValue()) ||
                        !usuario.getBilhete().equals(txtBI.getText()) ||
                        !usuario.getEmail().equals(txtEmail.getText()) ||
                        !usuario.getTelefone().equals(txtTelefone.getText()) ||
                        !usuario.getCargo().equals(txtCargo.getText()) ||
                        Math.abs(usuario.getSalario() - Double.parseDouble(txtSalario.getText())) > 0.0001 ||
                        !LocalDate.parse(usuario.getData_admissao()).equals(datapickerDataAdmissao.getValue()) ||
                        usuario.getStatus() != Integer.parseInt(txtEstado.getText()) ||
                        !usuario.getPerfil().equals(txtPerfil.getText());

        if (!houveAlteracao) {
            AlertaUtil.mostrarInfo("Nenhuma alteração", "Nenhuma alteração foi feita.");
            return;
        }

        usuario.setNome_usuario(txtNome.getText());
        usuario.setSexo(txtSexo.getText());
        usuario.setData_nascimento(datapickerDataNascimento.getValue().toString());
        usuario.setBilhete(txtBI.getText());
        usuario.setEmail(txtEmail.getText());
        usuario.setTelefone(txtTelefone.getText());
        usuario.setCargo(txtCargo.getText());
        usuario.setSalario(Double.parseDouble(txtSalario.getText()));
        usuario.setData_admissao(datapickerDataAdmissao.getValue().toString());
        usuario.setStatus(Integer.parseInt(txtEstado.getText()));
        usuario.setPerfil(txtPerfil.getText());

        usuarioDAO.atualizar(usuario);
        carregarTableviewUsuario();
        AlertaUtil.mostrarInfo("Sucesso", "Usuário atualizado com sucesso.");
    }

    @FXML
    void primeiraVezLogin(MouseEvent event) {

    }
    @FXML
    void btnRegistarUsuario(ActionEvent event) {
        if (validarEntradadedados()) {
            String senha = txtSenha.getText().trim();
            if (!ControladorSenha.senhaValida(senha)) {
                AlertaUtil.mostrarErro(
                        "Senha fraca",
                        ControladorSenha.mensagemErro(senha)
                );
                return;
            }
            if (!senha.equals(txtSenhaConfirmar.getText())) {
                AlertaUtil.mostrarErro("Erro", "As senhas não coincidem.");
                return;
            }

            String nomeUsuario = txtUsuario.getText();
            if (usuarioDAO.existeUsuario(nomeUsuario)) {
                nomeUsuario = gerarNomeAlternativo(nomeUsuario);

                boolean nomeAceito = mostrarConfirmacao(nomeUsuario);
                if (!nomeAceito) {
                    return;
                }

                // Atualiza o campo com o nome aceito
                txtUsuario.setText(nomeUsuario);
            }

            // Cria um novo objeto Usuario
            usuario = new Usuario();
            usuario.setNome_usuario(txtNome.getText());
            usuario.setSexo(txtSexo.getText());
            usuario.setData_nascimento(String.valueOf(datapickerDataNascimento.getValue()));
            usuario.setBilhete(txtBI.getText());
            usuario.setEmail(txtEmail.getText());
            usuario.setTelefone(txtTelefone.getText());
            usuario.setCargo(txtCargo.getText());
            usuario.setSalario(Double.parseDouble(txtSalario.getText()));
            usuario.setStatus(Integer.parseInt(txtEstado.getText()));
            usuario.setPerfil(txtPerfil.getText());
            usuario.setUsuario(nomeUsuario);  // Utiliza o nome de usuário validado
            usuario.setSenha(senha);  // A senha será criptografada no DAO
            usuario.setData_admissao(String.valueOf(datapickerDataAdmissao.getValue()));

            // Tenta inserir o usuário no banco de dados
            if (usuarioDAO.inserir(usuario)) {
                AlertaUtil.mostrarInfo("Sucesso", "Usuário cadastrado com sucesso!");
                limparCampos();  // Limpa os campos de entrada
                carregarTableviewUsuario();  // Atualiza a tabela de usuários
            } else {
                AlertaUtil.mostrarErro("Erro", "Erro ao cadastrar usuário.");
            }
        }
    }

    @FXML
    void btnRemoverUsuario(ActionEvent event) {
        usuario = tableviewUsuario.getSelectionModel().getSelectedItem();
        if (usuario != null) {
            Optional<ButtonType> resultado =  AlertaUtil.mostrarConfirmacao("Confirmacao de exclusao","Tem certeza que deseja excluir "
                    + usuario.getNome_usuario() + "?");
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                usuarioDAO.remover(usuario);
                carregarTableviewUsuario();
                limparCampos();
            }
        }else {
            AlertaUtil.mostrarAviso("Erro na Exclusao do Usuario", "Selecione usuario na Tabela");
        }
    }

    @FXML
    void pesquisarFuncionario(ActionEvent event){
        String pesquisa = txtPesquisa.getText();
        List<Usuario> resultadoEncontrado = usuarioDAO.buscarPornome(pesquisa);
        tableviewUsuario.getItems().clear();
        limparCampos();

        if (!resultadoEncontrado.isEmpty()) {
            tableviewUsuario.getItems().addAll(resultadoEncontrado);
        } else {
            AlertaUtil.mostrarAviso("Usuario nao encontrado",
                    "Nenhum usuario encontrado com esse nome");
        }
    }

    @FXML
    void pesquisarFuncionario(MouseEvent event) {
        String pesquisa = txtPesquisa.getText();
        List<Usuario> resultadoEncontrado = usuarioDAO.buscarPornome(pesquisa);
        tableviewUsuario.getItems().clear();
        limparCampos();

        if (!resultadoEncontrado.isEmpty()) {
            tableviewUsuario.getItems().addAll(resultadoEncontrado);
        } else {
            AlertaUtil.mostrarAviso("Usuario nao encontrado",
                    "Nenhum usuario encontrado com esse nome");
        }
    }

    @FXML
    void btnRelatorioUsuario(ActionEvent event) {
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/relatoriosjasper/Relatorio_de_funcionario.jasper");
            UsuarioDAO dao = new UsuarioDAO();
            List<Usuario> dados = dao.listar();
            for (Usuario u : dados) {
                String dataOriginal = u.getData_nascimento();
                try {
                    LocalDate dataFormatada =
                            LocalDate.parse(dataOriginal);
                    u.setData_nascimento(
                            dataFormatada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );
                } catch (Exception e) {
                    System.out.println("Data Invalida no registro: " + dataOriginal);
                }

            }
            if (dados.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Nenhum dado encontrado para o período selecionado.").show();
                return;
            }
            // Converter a lista para DataSource do Jasper
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // Sem parâmetros -> apenas mandar null
            Map<String, Object> parametros = new HashMap<>();

            // Adiciona a referência da classe para localizar a imagem
            parametros.put("REPORT_CLASS", RelatorioUtil.class);

            // Preencher relatório
            JasperPrint jp = JasperFillManager.fillReport(is, parametros, dataSource);

            // Mostrar o relatório na tela
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Lista de Funcionarios");
            viewer.setVisible(true);

        } catch (
                Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao gerar relatório: " + e.getMessage()).show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableviewUsuario();
        listenerTableviewUsuario();
    }

    private void limparCampos() {
        txtNome.clear();
        txtSexo.clear();
        txtBI.clear();
        txtEmail.clear();
        txtTelefone.clear();
        txtCargo.clear();
        txtSalario.clear();
        txtEstado.clear();
        txtPerfil.clear();
        txtUsuario.clear();
        txtSenha.clear();
        txtSenhaConfirmar.clear();
        datapickerDataNascimento.setValue(null);
        datapickerDataAdmissao.setValue(null);
    }

    public void carregarTableviewUsuario() {
        colunaCodigoUsuario.setCellValueFactory(new PropertyValueFactory<>("idusuario"));
        colunaNomeUsuario.setCellValueFactory(new PropertyValueFactory<>("nome_usuario"));
        colunaSexoUsuario.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colunaPerfi.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        colunaSalarioUsuario.setCellValueFactory(new PropertyValueFactory<>("salario"));
        colunaTelefoneUsuario.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        //Listener para txtProcuraNome
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableviewUsuario();
            }
        });
        System.out.println(usuarioDAO.buscarPorId(1));
        usuarioList = usuarioDAO.listar();

        observableUsuarioList = FXCollections.observableArrayList(usuarioList);
        tableviewUsuario.setItems(observableUsuarioList);
    }

    private boolean validarEntradadedados() {
        String erroMessage = "";

        if (txtNome.getText() == null || txtNome.getText().isEmpty()) {
            erroMessage += "Nome obrigatório!\n";
        }
        if (txtSexo.getText() == null || txtSexo.getText().isEmpty()) {
            erroMessage += "Sexo obrigatório!\n";
        }
        if (datapickerDataNascimento.getValue() == null) {
            erroMessage += "Data de nascimento obrigatória!\n";
        }
        if (txtBI.getText() == null || txtBI.getText().isEmpty()) {
            erroMessage += "BI obrigatório!\n";
        }
        if (txtEmail.getText() == null || txtEmail.getText().isEmpty()) {
            erroMessage += "Email obrigatório!\n";
        }
        if (txtTelefone.getText() == null || txtTelefone.getText().isEmpty()) {
            erroMessage += "Telefone obrigatório!\n";
        }
        if (txtCargo.getText() == null || txtCargo.getText().isEmpty()) {
            erroMessage += "Cargo obrigatório!\n";
        }
        if (txtSalario.getText() == null || txtSalario.getText().isEmpty()) {
            erroMessage += "Salário obrigatório!\n";
        }
        if (txtEstado.getText() == null || txtEstado.getText().isEmpty()) {
            erroMessage += "Estado obrigatório!\n";
        }
        if (txtPerfil.getText() == null || txtPerfil.getText().isEmpty()) {
            erroMessage += "Perfil obrigatório!\n";
        }
        if (txtUsuario.getText() == null || txtUsuario.getText().isEmpty()) {
            erroMessage += "Usuário obrigatório!\n";
        }
        if (txtSenha.getText() == null || txtSenha.getText().isEmpty()) {
            erroMessage += "Senha obrigatória!\n";
        }
        if (txtSenhaConfirmar.getText() == null || txtSenhaConfirmar.getText().isEmpty()) {
            erroMessage += "Confirmação de senha obrigatória!\n";
        }
        if (txtSenha.getText() != null && !txtSenha.getText().equals(txtSenhaConfirmar.getText())) {
            erroMessage += "As senhas não coincidem!\n";
        }
        if (datapickerDataAdmissao.getValue() == null) {
            erroMessage += "Data de admissão obrigatória!\n";
        }

        if (erroMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no Cadastro");
            alert.setHeaderText("Campos inválidos, por favor, corrija...");
            alert.setContentText(erroMessage);
            alert.show();
            return false;
        }
    }

    private String gerarNomeAlternativo(String nomeUsuario) {
        int contador = 1;
        String novoNomeUsuario = nomeUsuario + contador;
        while (usuarioDAO.existeUsuario(novoNomeUsuario)) {
            contador++;
            novoNomeUsuario = nomeUsuario + contador;
        }
        return novoNomeUsuario;
    }

    private boolean mostrarConfirmacao(String nomeUsuarioSugerido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nome de Usuário Sugerido");
        alert.setHeaderText("O nome de usuário \"" + nomeUsuarioSugerido + "\" já está disponível.");
        alert.setContentText("Você gostaria de usar esse nome de usuário?");

        ButtonType buttonYes = new ButtonType("Sim");
        ButtonType buttonNo = new ButtonType("Não");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonYes;
    }

    private void listenerTableviewUsuario() {
        tableviewUsuario.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                if (newSelection.getData_nascimento() != null && !newSelection.getData_nascimento().isEmpty()) {
                    datapickerDataNascimento.setValue(LocalDate.parse(newSelection.getData_nascimento(), formatter));
                }

                if (newSelection.getData_admissao() != null && !newSelection.getData_admissao().isEmpty()) {
                    datapickerDataAdmissao.setValue(LocalDate.parse(newSelection.getData_admissao(), formatter));
                }

                txtNome.setText(newSelection.getNome_usuario());
                txtSexo.setText(newSelection.getSexo());
                txtBI.setText(newSelection.getBilhete());
                txtEmail.setText(newSelection.getEmail());
                txtTelefone.setText(newSelection.getTelefone());
                txtCargo.setText(newSelection.getCargo());
                txtSalario.setText(String.valueOf(newSelection.getSalario()));
                txtEstado.setText(String.valueOf(newSelection.getStatus()));
                txtPerfil.setText(newSelection.getPerfil());
                txtUsuario.setText(newSelection.getUsuario());

                txtSenha.setText("");
                txtSenhaConfirmar.setText("");

                // Ajustar botões (exemplo: btnAtualizar e btnRegistrar)
                btnAtualizarUsuario.setDisable(false);
                btnRegistarUsuario.setDisable(true);

            } else {
                // Caso não tenha seleção, limpa campos e reseta botões
                limparCampos();
                btnAtualizarUsuario.setDisable(true);
                btnRegistarUsuario.setDisable(false);
            }
        });
    }


}
