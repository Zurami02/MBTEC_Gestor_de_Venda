package mbtec.gestaoentradasaida_mbtec.service;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Venda;

import java.sql.Connection;

public class VendaCompletaService {

    private final VendaService vendaService = new VendaService();
    private final PagamentoService pagamentoService = new PagamentoService();

    public void finalizarVendaComPagamento(Venda venda, double valor, String forma) throws Exception {
        Connection conn = null;
        try {
            conn = ConexaoSQLite.getConnection();
            conn.setAutoCommit(false);

            vendaService.finalizarVenda(conn, venda);

            pagamentoService.registrarPagamento(conn, venda, valor, forma);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
}
