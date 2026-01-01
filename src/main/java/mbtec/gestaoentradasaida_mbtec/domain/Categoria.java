package mbtec.gestaoentradasaida_mbtec.domain;

public class Categoria {
    private int idcategoria;
    private String descricao_categoria;

    public Categoria() {
    }

    public Categoria(int idcategoria, String descricao_categoria) {
        this.idcategoria = idcategoria;
        this.descricao_categoria = descricao_categoria;
    }

    public int getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getDescricao_categoria() {
        return descricao_categoria;
    }

    public void setDescricao_categoria(String descricao_categoria) {
        this.descricao_categoria = descricao_categoria;
    }
}
