package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;

public class Configuracao {
    private final String chave;
    private BigDecimal valor;

    public Configuracao(String chave, BigDecimal valor) {
        this.chave = chave;
        this.valor = valor != null ? valor : BigDecimal.ZERO;
    }

    public String getChave() {
        return chave;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor != null ? valor : BigDecimal.ZERO;
    }
}
