package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.util.CriptografarUtil;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAO {
    public boolean inserir(@NotNull Usuario usuario) {
        String sql = "INSERT INTO usuario (nome_usuario, sexo, data_nascimento, bilhete, email, telefone, cargo," +
                " salario, data_admissao, status, perfil, usuario, senha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome_usuario());
            stmt.setString(2, usuario.getSexo());
            stmt.setString(3, usuario.getData_nascimento());
            stmt.setString(4, usuario.getBilhete());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getTelefone());
            stmt.setString(7, usuario.getCargo());
            stmt.setDouble(8, usuario.getSalario());
            stmt.setString(9, usuario.getData_admissao());
            stmt.setInt(10, usuario.getStatus());
            stmt.setString(11, usuario.getPerfil());
            stmt.setString(12, usuario.getUsuario());

            //Criptografar senha
            String senhaCriptografada = CriptografarUtil.gerarHash(usuario.getSenha());
            stmt.setString(13, senhaCriptografada);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<Usuario> listar(){
        String sql = "SELECT * FROM usuario";
        List<Usuario> retorno = new ArrayList<>();
        try(Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                retorno.add(criarUsuarioAPartirDoResultSet(rs));
            }

        } catch (SQLException e) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public void remover(@NotNull Usuario usuario){
        String sql = "DELETE FROM usuario WHERE idusuario =?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1,usuario.getIdusuario());
            stmt.execute();
        } catch (SQLException e) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean atualizar(@NotNull Usuario usuario){
        String sql = "UPDATE usuario SET nome_usuario=?, sexo=?, data_nascimento=?, bilhete=?, email=?, telefone=?, cargo=?, " +
                "salario=?, data_admissao=?, status=?, perfil=? WHERE idusuario=?";

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Preencher os parâmetros do PreparedStatement
            stmt.setString(1, usuario.getNome_usuario());
            stmt.setString(2, usuario.getSexo());
            stmt.setString(3, usuario.getData_nascimento());
            stmt.setString(4, usuario.getBilhete());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getTelefone());
            stmt.setString(7, usuario.getCargo());
            stmt.setDouble(8, usuario.getSalario());
            stmt.setString(9, usuario.getData_admissao());
            stmt.setInt(10, usuario.getStatus());
            stmt.setString(11, usuario.getPerfil());
            stmt.setInt(12, usuario.getIdusuario());
            stmt.execute();
            return true;

        } catch (SQLException e) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean existeUsuario(String nomeUsuario) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE usuario = ? COLLATE NOCASE";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomeUsuario);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE idusuario = ?";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return criarUsuarioAPartirDoResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> buscarPornome(String nome) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE nome_usuario LIKE ? COLLATE NOCASE";

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add( criarUsuarioAPartirDoResultSet(rs));
            }
        } catch (SQLException e) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        return usuarios;
    }

    public int countUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * a  Buscar usuário por nome de usuário OU email (sem validar senha)
     */

    public Usuario buscarPorUsuarioOuEmail(String usuarioOuEmail) {
        String sql = "SELECT * FROM usuario WHERE usuario = ? OR email = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuarioOuEmail);
            stmt.setString(2, usuarioOuEmail);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return criarUsuarioAPartirDoResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Autenticar verificando também a senha
    public Usuario autenticar(String usuarioOuEmail, String senha) {
        Usuario usuario = buscarPorUsuarioOuEmail(usuarioOuEmail);
        if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
            atualizarUltimoAcesso(usuario.getIdusuario());
            return usuario;
        }
        return null;
    }

    private void atualizarUltimoAcesso(int idUsuario) {
        String sql = "UPDATE usuario SET ultimo_acesso = ? WHERE idusuario = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, LocalDateTime.now().toString()); // ou formatado como quiser
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Usuario criarUsuarioAPartirDoResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdusuario(rs.getInt("idusuario"));
        usuario.setNome_usuario(rs.getString("nome_usuario"));
        usuario.setSexo(rs.getString("sexo"));
        usuario.setData_nascimento(rs.getString("data_nascimento"));
        usuario.setBilhete(rs.getString("bilhete"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefone(rs.getString("telefone"));
        usuario.setCargo(rs.getString("cargo"));
        usuario.setSalario(rs.getDouble("salario"));
        usuario.setStatus(rs.getInt("status"));
        usuario.setPerfil(rs.getString("perfil"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setData_admissao(rs.getString("data_admissao"));
        usuario.setUltimo_acesso(rs.getString("ultimo_acesso"));

        return usuario;
    }

    public Usuario buscarPorEmailEData(String email, String dataNascimento) {

        String sql = "SELECT * FROM usuario WHERE email = ? AND data_nascimento = ?";

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, dataNascimento);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdusuario(rs.getInt("idusuario"));
                u.setEmail(rs.getString("email"));
                u.setBilhete(rs.getString("bilhete"));
                u.setData_nascimento(rs.getString("data_nascimento"));
                u.setNome_usuario(rs.getString("nome_usuario"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizarSenhaPorId(int idUsuario, String senhaCriptografada){
        String sql = """
                UPDATE usuario SET senha = ?
                WHERE idusuario =?
                """;
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, senhaCriptografada);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
