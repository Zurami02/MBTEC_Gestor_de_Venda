package mbtec.gestaoentradasaida_mbtec.service;


import mbtec.gestaoentradasaida_mbtec.DAO.ItemvendaDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.ProdutosDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.VendaDAO;
import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Itemvenda;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;
import mbtec.gestaoentradasaida_mbtec.domain.Venda;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VendaService {

    private VendaDAO vendaDAO = new VendaDAO();
    private ItemvendaDAO itemVendaDAO = new ItemvendaDAO();
    private ProdutosDAO produtoDAO = new ProdutosDAO();
    private boolean anuladaVenda;

    public boolean isAnuladaVenda() {
        return anuladaVenda;
    }

    public void setAnuladaVenda(boolean anuladaVenda) {
        this.anuladaVenda = anuladaVenda;
    }

    public void anularVenda(@NotNull Venda venda) {
        Connection conn = null;

        try {
            conn = ConexaoSQLite.getConnection();
            conn.setAutoCommit(false);

            //Buscar itens da venda
            List<Itemvenda> itens = itemVendaDAO.buscarPorVenda(venda.getIdVenda(), conn);

            //Devolver stock
            for (Itemvenda item : itens) {
                produtoDAO.adicionarStock(
                        item.getProduto().getIdproduto(),
                        item.getQuantidade(),
                        conn
                );
            }

            //Anular venda
            vendaDAO.anularVenda(venda.getIdVenda(), conn);

            //Commit
            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao anular venda", e);

        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        venda.setStatus("ANULADA");
    }

    public void finalizarVenda(Connection conn, @NotNull Venda venda) throws Exception {
        int proximoNumero = vendaDAO.buscarProximoNumeroVD(conn);
        String numeroVD = "VD-" + String.format("%06d", proximoNumero);
        venda.setNumerovd(numeroVD);

        Usuario usuario = UsuarioNoSistema.getInstance().getUsuarioLogado();
        venda.setIdUsuario(usuario.getIdusuario());

        int idVenda = vendaDAO.salvarVenda(conn, venda);
        venda.setIdVenda(idVenda);

        for (Itemvenda item : venda.getItens()) {
            itemVendaDAO.salvarItem(conn, idVenda, item);
            produtoDAO.baixarEstoqueControlador(conn, item.getProduto().getIdproduto(), item.getQuantidade());
            item.getProduto().setQuantidade_produto(item.getProduto().getQuantidade_produto() - item.getQuantidade());
        }
    }

}

