package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pagamento {
    private int idpagamento;
    private Venda venda;
    private BigDecimal valorPagamento;
    private String formaPagamento;
    private LocalDateTime dataPagamento;



    public Pagamento(){
        this.dataPagamento = LocalDateTime.now();
    }

    public Pagamento(int idpagamento, Venda venda, BigDecimal valorPagamento, String formaPagamento, LocalDateTime dataPagamento) {
        this.idpagamento = idpagamento;
        this.venda = venda;
        this.valorPagamento = valorPagamento;
        this.formaPagamento = formaPagamento;
        this.dataPagamento = dataPagamento;
    }

    public int getIdpagamento() {
        return idpagamento;
    }

    public void setIdpagamento(int idpagamento) {
        this.idpagamento = idpagamento;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento != null ? valorPagamento : BigDecimal.ZERO;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }
}
