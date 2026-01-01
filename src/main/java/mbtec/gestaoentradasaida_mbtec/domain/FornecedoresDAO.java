package mbtec.gestaoentradasaida_mbtec.domain;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Metodo de comunicacao com DB tabela fornecedor
 * @author Zulo Mitumba MBTEC
 */
public class FornecedoresDAO {

    public boolean inserir(Fornecedores fornecedores) {
        String sql = "INSERT INTO fornecedor (descricao_produto, quantidade, fornecedor, preco) VALUES (?,?,?,?)";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fornecedores.getDescricaoProduto());
            stmt.setInt(2, fornecedores.getQuantidade());
            stmt.setString(3, fornecedores.getFornecedor());
            stmt.setDouble(4, fornecedores.getPreco());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(FornecedoresDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean existeFornecedor(String fornecedor, String descricao_produto) {
        String sql = "SELECT COUNT(*) FROM fornecedor WHERE fornecedor = ? AND descricao_produto = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fornecedor);
            stmt.setString(2, descricao_produto);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;//Retorna True se houver pelo menos um registo.
            }
        } catch (SQLException e) {
            Logger.getLogger(FornecedoresDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public List<Fornecedores> listar() {
        String sql = "SELECT * FROM fornecedor";
        List<Fornecedores> retorno = new ArrayList<>();
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            while (resultado.next()) {
                Fornecedores fornecedores = new Fornecedores();
                fornecedores.setIdfornecedor(resultado.getInt("idfornecedor"));
                fornecedores.setDescricaoProduto(resultado.getString("descricao_produto"));
                fornecedores.setQuantidade(resultado.getInt("quantidade"));
                fornecedores.setFornecedor(resultado.getString("fornecedor"));
                fornecedores.setPreco(resultado.getDouble("preco"));
                retorno.add(fornecedores);
            }
        } catch (SQLException e) {
            Logger.getLogger(FornecedoresDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public boolean editar(Fornecedores fornecedores) {
        String sql = "UPDATE fornecedor SET descricao_produto=?, quantidade=?, fornecedor=?,  preco=? WHERE idfornecedor=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fornecedores.getDescricaoProduto());
            stmt.setInt(2, fornecedores.getQuantidade());
            stmt.setString(3, fornecedores.getFornecedor());
            stmt.setDouble(4, fornecedores.getPreco());
            stmt.setInt(5, fornecedores.getIdfornecedor());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(FornecedoresDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean remover(Fornecedores fornecedores) {
        String sql = "DELETE FROM fornecedor WHERE idfornecedor=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fornecedores.getIdfornecedor());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(FornecedoresDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<Fornecedores> buscarPorNome(String nome) {
        String sql = "SELECT * FROM fornecedor WHERE descricao_produto LIKE ?";
        List<Fornecedores> fornecedoresList = new ArrayList<>();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%"); // Usando LIKE para permitir buscas parciais
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                Fornecedores fornecedores = new Fornecedores();
                fornecedores.setIdfornecedor(resultado.getInt("idfornecedor"));
                fornecedores.setDescricaoProduto(resultado.getString("descricao_produto"));
                fornecedores.setQuantidade(resultado.getInt("quantidade"));
                fornecedores.setFornecedor(resultado.getString("fornecedor"));
                fornecedores.setPreco(resultado.getDouble("preco"));
                fornecedoresList.add(fornecedores);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FornecedoresDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fornecedoresList;
    }

    /**
     * Usado para edicao so
     * @param fornecedor
     * @param descricaoProduto
     * @param idAtual
     * @return
     */
    public boolean existeOutroCombinacao(String fornecedor, String descricaoProduto, int idAtual) {
        String sql = "SELECT COUNT(*) FROM fornecedor WHERE fornecedor = ? AND descricao_produto = ? AND idfornecedor != ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fornecedor);
            stmt.setString(2, descricaoProduto);
            stmt.setInt(3, idAtual);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
