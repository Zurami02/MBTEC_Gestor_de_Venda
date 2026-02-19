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

    public static TipoItem fromDescricao(String descricao) {
        for (TipoItem tipo : values()) {
            if (tipo.getDescricao().equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoItem inválido: " + descricao);
    }
}
