package mbtec.gestaoentradasaida_mbtec.service;

import java.math.BigDecimal;

public class OrcamentoService {
    public static BigDecimal parsePreco(String valor) {
        if (valor == null || valor.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(valor.replace(",", "."));
    }

}
