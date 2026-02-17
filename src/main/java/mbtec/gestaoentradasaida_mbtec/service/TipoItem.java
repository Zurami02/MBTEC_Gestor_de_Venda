package mbtec.gestaoentradasaida_mbtec.service;

public enum TipoItem {
    PRODUTO("Produto"),
    SERVICO("Serviço");

    private String descricao;

    TipoItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
