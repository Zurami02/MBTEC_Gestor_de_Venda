package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Itemvenda;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemvendaDAO {
    public void salvarItem(@NotNull Connection conn, int idVenda, @NotNull Itemvenda item)
            throws SQLException {

        String sql = """
        INSERT INTO itemvenda
        (idvenda, idproduto, quantidade, precounitario, desconto)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenda);
            ps.setInt(2, item.getProduto().getIdproduto());
            ps.setInt(3, item.getQuantidade());
            ps.setDouble(4, item.getPrecoUnitario());
            ps.setDouble(5, item.getDesconto());
            ps.executeUpdate();
        }
    }

    public List<Itemvenda> listarPorVenda(int idVenda) {

        List<Itemvenda> lista = new ArrayList<>();

        String sql = """
        SELECT
            i.quantidade,
            i.precounitario,
            i.desconto,
            p.descricao,
            p.idproduto,
            p.preco
        FROM itemvenda i
        JOIN produtos p ON p.idproduto = i.idproduto
        WHERE i.idvenda = ?
    """;

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenda);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Itemvenda item = new Itemvenda();
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getDouble("precounitario"));
                item.setDesconto(rs.getDouble("desconto"));

                Produtos p = new Produtos();
                p.setIdproduto(rs.getInt("idproduto"));
                p.setDescricao_produto(rs.getString("descricao"));
                p.setPreco(rs.getDouble("preco"));
                item.setProduto(p);

                lista.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens da venda", e);
        }

        return lista;
    }

    public List<Itemvenda> buscarPorVenda(int idVenda, Connection conn) {
        List<Itemvenda> itens = new ArrayList<>();
        String sql = "SELECT * FROM itemvenda WHERE idvenda = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenda);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Itemvenda item = new Itemvenda();
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getDouble("precounitario"));

                Produtos p = new Produtos();
                p.setIdproduto(rs.getInt("idproduto"));
                item.setProduto(p);

                itens.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itens;
    }





}
