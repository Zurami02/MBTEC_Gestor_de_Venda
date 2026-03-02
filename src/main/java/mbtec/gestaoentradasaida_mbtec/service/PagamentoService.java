package mbtec.gestaoentradasaida_mbtec.service;

import mbtec.gestaoentradasaida_mbtec.DAO.PagamentoDAO;
import mbtec.gestaoentradasaida_mbtec.domain.Pagamento;
import mbtec.gestaoentradasaida_mbtec.domain.Venda;

import java.math.BigDecimal;
import java.sql.Connection;

public class PagamentoService {

    private final PagamentoDAO dao = new PagamentoDAO();

    public void registrarPagamento(Connection conn, Venda v, BigDecimal valor, String forma) {
        Pagamento p = new Pagamento();
        p.setVenda(v);
        p.setValorPagamento(valor);
        p.setFormaPagamento(forma);

        dao.salvar(conn,p);
    }
}
