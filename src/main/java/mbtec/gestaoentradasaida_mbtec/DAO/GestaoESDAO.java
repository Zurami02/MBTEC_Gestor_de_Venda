package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.GestaoES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestaoESDAO {

    public boolean inserir(GestaoES gestaoES) {
        String sql = "INSERT INTO gestaoES (data, planoconta, descricao, quantidade, valor) VALUES (?,?,?,?,?)";
        try {
            Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, gestaoES.getData());
            stmt.setString(2, gestaoES.getPlanoconta());
            stmt.setString(3, gestaoES.getDescricao());
            stmt.setInt(4, gestaoES.getQuantidade());
            stmt.setDouble(5, gestaoES.getValor());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(GestaoESDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<GestaoES> listar() {
        String sql = "SELECT * FROM gestaoES";
        List<GestaoES> retorno = new ArrayList<>();
        try {
            Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                GestaoES gestaoES = new GestaoES();
                gestaoES.setIdgestao(resultado.getInt("idgestao"));
                gestaoES.setData(resultado.getString("data"));
                gestaoES.setPlanoconta(resultado.getString("planoconta"));
                gestaoES.setDescricao(resultado.getString("descricao"));
                gestaoES.setQuantidade(resultado.getInt("quantidade"));
                gestaoES.setValor(resultado.getDouble("valor"));
                retorno.add(gestaoES);
            }
        } catch (SQLException e) {
            Logger.getLogger(GestaoESDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public boolean editar(GestaoES gestaoES) {
        String sql = "UPDATE gestaoES SET data=?, planoconta=?, descricao=?, quantidade=?, valor=? WHERE idgestao=?";
        try {
            Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, gestaoES.getData());
            stmt.setString(2, gestaoES.getPlanoconta());
            stmt.setString(3, gestaoES.getDescricao());
            stmt.setInt(4, gestaoES.getQuantidade());
            stmt.setDouble(5, gestaoES.getValor());
            stmt.setInt(6, gestaoES.getIdgestao());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(GestaoESDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean remover(@org.jetbrains.annotations.NotNull GestaoES gestaoES) {
        String sql = "DELETE FROM gestaoES WHERE idgestao=?";
        try {
            Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, gestaoES.getIdgestao());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(GestaoESDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public GestaoES buscar(GestaoES gestaoES) {
        String sql = "SELECT * FROM gestaoES WHERE idgestao=?";
        GestaoES retorno = new GestaoES();
        try {
            Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, gestaoES.getIdgestao());
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                retorno.setIdgestao(resultado.getInt("idgestao"));
                retorno.setData(resultado.getString("data"));
                retorno.setPlanoconta(resultado.getString("planoconta"));
                retorno.setDescricao(resultado.getString("descricao"));
                retorno.setQuantidade(resultado.getInt("quantidade"));
                retorno.setValor(resultado.getDouble("valor"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestaoESDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public List<GestaoES> buscarPorNome(String nome) {
        String sql = "SELECT * FROM gestaoES WHERE planoconta LIKE ?";
        List<GestaoES> gestaoESList = new ArrayList<>();

        try {
            Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + nome + "%"); // Usando LIKE para permitir buscas parciais
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                GestaoES gestaoES = new GestaoES();
                gestaoES.setIdgestao(resultado.getInt("idgestao"));
                gestaoES.setData(resultado.getString("data"));
                gestaoES.setPlanoconta(resultado.getString("planoconta"));
                gestaoES.setDescricao(resultado.getString("descricao"));
                gestaoES.setQuantidade(resultado.getInt("quantidade"));
                gestaoES.setValor(resultado.getDouble("valor"));
                gestaoESList.add(gestaoES);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GestaoESDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return gestaoESList;
    }

    public List<GestaoES> listarGestaoPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<GestaoES> lista = new ArrayList<>();
        String sql = "SELECT g.idgestao, g.data, g.planoconta, g.descricao, g.quantidade, g.valor " +
                "FROM gestaoES g " +
                "WHERE g.data BETWEEN ? AND ?";

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, inicio.toString());
            stmt.setString(2, fim.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                GestaoES gestaoES = new GestaoES();
                gestaoES.setIdgestao(rs.getInt("idgestao"));
                gestaoES.setData(rs.getString("data"));
                gestaoES.setPlanoconta(rs.getString("planoconta"));
                gestaoES.setDescricao(rs.getString("descricao"));
                gestaoES.setQuantidade(rs.getInt("quantidade"));
                gestaoES.setValor(rs.getDouble("valor"));

                lista.add(gestaoES);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
