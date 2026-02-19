package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.ItemOrcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;
import mbtec.gestaoentradasaida_mbtec.service.TipoItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemOrcamentoDAO {
    public void salvarItemOrcamento(Connection conn, int idorcamento, ItemOrcamento item) {
        String sql = """
                INSERT INTO item_orcamento
                (idorcamento, idproduto, descricaoitem, tipoitem, quantidade, precounitario, subtotal)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idorcamento);
            if (item.getProduto() != null) {
                ps.setInt(2, item.getProduto().getIdproduto());
            }else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, item.getDescricaoitem());
            ps.setString(4, item.getTipoitem().getDescricao());
            ps.setInt(5, item.getQuantidade());
            ps.setBigDecimal(6, item.getPrecounitario());
            ps.setBigDecimal(7, item.getSubtotal());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ItemOrcamento> listarPorOrcamento(int idOrcamento){
        List<ItemOrcamento> lista = new ArrayList<>();
        String sql = """
                SELECT
                i.descricaoitem,
                i.tipoitem,
                i.quantidade,
                i.precounitario,
                i.subtotal,
                p.idproduto,
                p.preco,
                p.descricao
                FROM item_orcamento i
                LEFT JOIN produtos p ON p.idproduto = i.idproduto
                WHERE i.idorcamento = ?
                """;
        try (Connection conn = ConexaoSQLite.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, idOrcamento);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                ItemOrcamento item = new ItemOrcamento();
                item.setDescricaoitem(rs.getString("descricaoitem"));

                String tipoStr = rs.getString("tipoitem");
                if (tipoStr != null) {
                    item.setTipoitem(TipoItem.fromDescricao(tipoStr));
                }

                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecounitario(rs.getBigDecimal("precounitario"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));

                int idProduto = rs.getInt("idproduto");

                if (!rs.wasNull()) {
                    Produtos p = new Produtos();
                    p.setIdproduto(idProduto);
                    p.setDescricao_produto(rs.getString("descricao"));
                    p.setPreco(rs.getBigDecimal("preco"));
                    item.setProduto(p);
                }

                lista.add(item);
            }

        }catch (SQLException e){
            throw new RuntimeException("Erro ao buscar itens do orcamento", e);
        }
        return lista;
    }
}
