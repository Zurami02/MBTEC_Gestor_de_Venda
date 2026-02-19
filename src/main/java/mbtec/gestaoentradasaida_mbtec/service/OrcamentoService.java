package mbtec.gestaoentradasaida_mbtec.service;

import mbtec.gestaoentradasaida_mbtec.DAO.ItemOrcamentoDAO;
import mbtec.gestaoentradasaida_mbtec.DAO.OrcamentoDAO;
import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.ItemOrcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Orcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Usuario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class OrcamentoService {

    private OrcamentoDAO orcamentoDAO = new OrcamentoDAO();
    private ItemOrcamentoDAO itemOrcamentoDAO = new ItemOrcamentoDAO();

    public static BigDecimal parsePreco(String valor) {
        if (valor == null || valor.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(valor.replace(",", "."));
    }

    public void finalizarOrcamento(Orcamento orcamento) throws Exception{
        Connection conn = null;
        try {
            conn = ConexaoSQLite.getConnection();
            conn.setAutoCommit(false);

            int proximoNumero = orcamentoDAO.buscarProximoNumeroOrcamento(conn);
            String numeroOrcamento = "orc-" + String.format("%06d", proximoNumero);
            orcamento.setNumero_orcamento(numeroOrcamento);
            Usuario u = UsuarioNoSistema.getInstance().getUsuarioLogado();
            orcamento.setIdusuario(u.getIdusuario());

            int idorcamento = orcamentoDAO.salvarOrcamento(conn, orcamento);
            orcamento.setIdorcamento(idorcamento);

            for (ItemOrcamento item : orcamento.getItens()) {
                itemOrcamentoDAO.salvarItemOrcamento(conn, idorcamento, item);
            }
            conn.commit();
        }catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }


}
