package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoriaDAO {

    public boolean inserir(Categoria categoria) {
        String sql = "INSERT INTO categoria (descricao_categoria) VALUES (?)";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, categoria.getDescricao_categoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean existeDescricaoCategoria(String descricao_categoria) {
        String sql = "SELECT COUNT(*) FROM categoria WHERE descricao_categoria = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, descricao_categoria);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;//Retorna True se houver pelo menos um registo.
            }
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public List<Categoria> listar() {
        String sql = "SELECT * FROM categoria";
        List<Categoria> retorno = new ArrayList<>();
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()){
            while (resultado.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria"));
                categoria.setDescricao_categoria(resultado.getString("descricao_categoria"));
                retorno.add(categoria);
            }
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public boolean editar(Categoria categoria) {
        String sql = "UPDATE categoria SET descricao_categoria=? WHERE idcategoria=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, categoria.getDescricao_categoria());
            stmt.setInt(2, categoria.getIdcategoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean remover(Categoria categoria) {
        String sql = "DELETE FROM categoria WHERE idcategoria=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, categoria.getIdcategoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<Categoria> buscarPorNome(String nome) {
        String sql = "SELECT * FROM categoria WHERE descricao_categoria LIKE ?";
        List<Categoria> categoriasList = new ArrayList<>();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, "%" + nome + "%"); // Usando LIKE para permitir buscas parciais
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria"));
                categoria.setDescricao_categoria(resultado.getString("descricao_categoria"));
                categoriasList.add(categoria);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return categoriasList;
    }

    public Map<Integer, Categoria> mapearPorId() {
        String sql = "SELECT * FROM categoria";
        Map<Integer, Categoria> mapa = new HashMap<>();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(rs.getInt("idcategoria"));
                categoria.setDescricao_categoria(rs.getString("descricao_categoria"));
                mapa.put(categoria.getIdcategoria(), categoria);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mapa;
    }


}
