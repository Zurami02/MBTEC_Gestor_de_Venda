package mbtec.gestaoentradasaida_mbtec.domain;

public class Usuario {
    private int idusuario;
    private String nome_usuario;
    private String sexo;
    private String data_nascimento;
    private String bilhete;
    private String email;
    private String telefone;
    private String cargo;
    private double salario;
    private String data_admissao;
    private int status;
    private String perfil;
    private String usuario;
    private String senha;
    private String ultimo_acesso;

    public Usuario() {
    }

    public Usuario(int idusuario, String nome_usuario, String sexo, String data_nascimento,
                   String bilhete, String email, String telefone, String cargo, double salario, String data_admissao,
                   int status, String perfil, String usuario, String senha, String ultimo_acesso) {
        this.idusuario = idusuario;
        this.nome_usuario = nome_usuario;
        this.sexo = sexo;
        this.data_nascimento = data_nascimento;
        this.bilhete = bilhete;
        this.email = email;
        this.telefone = telefone;
        this.cargo = cargo;
        this.salario = salario;
        this.data_admissao = data_admissao;
        this.status = status;
        this.perfil = perfil;
        this.usuario = usuario;
        this.senha = senha;
        this.ultimo_acesso = ultimo_acesso;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getNome_usuario() {
        return nome_usuario;
    }

    public void setNome_usuario(String nome_usuario) {
        this.nome_usuario = nome_usuario;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getData_nascimento() {
        return data_nascimento;
    }

    public void setData_nascimento(String data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getBilhete() {
        return bilhete;
    }

    public void setBilhete(String bilhete) {
        this.bilhete = bilhete;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getData_admissao() {
        return data_admissao;
    }

    public void setData_admissao(String dat_admissao) {
        this.data_admissao = dat_admissao;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUltimo_acesso() {
        return ultimo_acesso;
    }

    public void setUltimo_acesso(String ultimo_acesso) {
        this.ultimo_acesso = ultimo_acesso;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idusuario=" + idusuario +
                ", nome_usuario='" + nome_usuario + '\'' +
                ", sexo='" + sexo + '\'' +
                ", data_nascimento='" + data_nascimento + '\'' +
                ", bilhete='" + bilhete + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salario=" + salario +
                ", dat_admissao='" + data_admissao + '\'' +
                ", status=" + status +
                ", perfil='" + perfil + '\'' +
                ", usuario='" + usuario + '\'' +
                ", senha='" + senha + '\'' +
                ", ultimo_acesso='" + ultimo_acesso + '\'' +
                '}';
    }
}
