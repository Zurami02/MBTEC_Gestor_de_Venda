package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Categoria;
import mbtec.gestaoentradasaida_mbtec.domain.FluxodeCaixa;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FluxodeCaixaDAO {

    public boolean inserir(@NotNull FluxodeCaixa fluxodeCaixa) {
        String sql = "INSERT INTO fluxo_caixa (quantidade, valor, data, idproduto, desconto) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fluxodeCaixa.getQuantidade());
            stmt.setDouble(2, fluxodeCaixa.getValor());
            stmt.setString(3, fluxodeCaixa.getData());
            stmt.setInt(4, fluxodeCaixa.getProduto().getIdproduto());
            stmt.setDouble(5, fluxodeCaixa.getDesconto());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public List<FluxodeCaixa> listar() {
        List<FluxodeCaixa> lista = new ArrayList<>();
        String sql = "SELECT fc.idfluxocaixa, fc.quantidade, fc.valor, fc.data, fc.desconto, " +
                "p.idproduto, p.descricao AS descricao_produto, p.quantidade AS quantidade_produto, p.preco, " +
                "c.idcategoria, c.descricao_categoria " +
                "FROM fluxo_caixa fc " +
                "JOIN produtos p ON fc.idproduto = p.idproduto " +
                "JOIN categoria c ON p.idcategoria = c.idcategoria";

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Criar objeto Categoria
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(rs.getInt("idcategoria"));
                categoria.setDescricao_categoria(rs.getString("descricao_categoria"));

                // Criar objeto Produto
                Produtos produto = new Produtos();
                produto.setIdproduto(rs.getInt("idproduto"));
                produto.setDescricao_produto(rs.getString("descricao_produto"));
                produto.setQuantidade_produto(rs.getInt("quantidade_produto"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setCategoria(categoria);

                // Criar objeto Fluxo de Caixa
                FluxodeCaixa fluxo = new FluxodeCaixa();
                fluxo.setIdfluxocaixa(rs.getInt("idfluxocaixa"));
                fluxo.setQuantidade(rs.getInt("quantidade"));
                fluxo.setValor(rs.getDouble("valor"));
                fluxo.setData(rs.getString("data"));
                fluxo.setDesconto(rs.getDouble("desconto"));
                fluxo.setProduto(produto);

                lista.add(fluxo);
            }

        } catch (SQLException e) {
            Logger.getLogger(FluxodeCaixaDAO.class.getName()).log(Level.SEVERE, null, e);
        }

        return lista;
    }

    public void deletar(FluxodeCaixa fluxodeCaixa) {
        String sql = "DELETE FROM fluxo_caixa WHERE idfluxocaixa = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fluxodeCaixa.getIdfluxocaixa());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(@NotNull FluxodeCaixa fluxo) {
        String sql = "UPDATE fluxo_caixa SET quantidade=?, valor=?, data=?, idproduto=?, desconto=? WHERE idfluxocaixa=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fluxo.getQuantidade());
            stmt.setDouble(2, fluxo.getValor());
            stmt.setString(3, fluxo.getData());
            stmt.setInt(4, fluxo.getProduto().getIdproduto());
            stmt.setDouble(5, fluxo.getDesconto());
            stmt.setInt(6, fluxo.getIdfluxocaixa());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FluxodeCaixa> buscarPorNome(String nome) {
        String sql = "SELECT fc.idfluxocaixa, fc.quantidade AS quantidade_fc, fc.valor, fc.data, fc.desconto, " +
                "p.idproduto, p.descricao AS descricao_produto, " +
                "p.quantidade AS quantidade_produto, p.preco, c.idcategoria, c.descricao_categoria " +
                "FROM fluxo_caixa fc " +
                "JOIN produtos p ON fc.idproduto = p.idproduto " +
                "JOIN categoria c ON p.idcategoria = c.idcategoria " +
                "WHERE LOWER(p.descricao) LIKE LOWER(?)";

        List<FluxodeCaixa> fluxodeCaixas = new ArrayList<>();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome.toLowerCase() + "%");
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria"));
                categoria.setDescricao_categoria(resultado.getString("descricao_categoria"));

                Produtos produto = new Produtos();
                produto.setIdproduto(resultado.getInt("idproduto"));
                produto.setDescricao_produto(resultado.getString("descricao_produto"));
                produto.setQuantidade_produto(resultado.getInt("quantidade_produto"));
                produto.setPreco(resultado.getDouble("preco"));
                produto.setCategoria(categoria);

                FluxodeCaixa fluxodeCaixa = new FluxodeCaixa();
                fluxodeCaixa.setIdfluxocaixa(resultado.getInt("idfluxocaixa"));
                fluxodeCaixa.setQuantidade(resultado.getInt("quantidade_fc"));
                fluxodeCaixa.setValor(resultado.getDouble("valor"));
                fluxodeCaixa.setData(resultado.getString("data"));
                fluxodeCaixa.setDesconto(resultado.getDouble("desconto"));
                fluxodeCaixa.setProduto(produto);

                fluxodeCaixas.add(fluxodeCaixa);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FluxodeCaixaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fluxodeCaixas;
    }

    public List<FluxodeCaixa> listarFluxoPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<FluxodeCaixa> lista = new ArrayList<>();
        String sql = "SELECT fc.idfluxocaixa, fc.quantidade, fc.valor, fc.data, fc.desconto, " +
                "p.idproduto, p.descricao AS descricao_produto, p.quantidade AS quantidade_produto, p.preco, " +
                "c.idcategoria, c.descricao_categoria " +
                "FROM fluxo_caixa fc " +
                "JOIN produtos p ON fc.idproduto = p.idproduto " +
                "JOIN categoria c ON p.idcategoria = c.idcategoria " +
                "WHERE date(fc.data) BETWEEN ? AND ?";

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, inicio.toString());
            stmt.setString(2, fim.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("idcategoria"),
                        rs.getString("descricao_categoria")
                );

                Produtos produto = new Produtos(
                        rs.getInt("idproduto"),
                        rs.getString("descricao_produto"),
                        rs.getInt("quantidade_produto"),
                        rs.getDouble("preco"),
                        categoria
                );

                FluxodeCaixa fluxo = new FluxodeCaixa();
                fluxo.setIdfluxocaixa(rs.getInt("idfluxocaixa"));
                fluxo.setQuantidade(rs.getInt("quantidade"));
                fluxo.setValor(rs.getDouble("valor"));
                fluxo.setData(rs.getString("data"));
                fluxo.setDesconto(rs.getDouble("desconto"));
                fluxo.setProduto(produto);

                lista.add(fluxo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


}
