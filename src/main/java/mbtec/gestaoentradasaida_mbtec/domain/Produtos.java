package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;

public class Produtos {
    private int idproduto;
    private String descricao_produto;
    private int quantidade_produto;
    private BigDecimal preco;
    private Categoria categoria;

    public Produtos() {
        this.preco = BigDecimal.ZERO;
    }

    public Produtos(int idproduto, String descricao_produto, int quantidade_produto, BigDecimal preco, Categoria categoria) {
        this.idproduto = idproduto;
        this.descricao_produto = descricao_produto;
        this.quantidade_produto = quantidade_produto;
        this.preco = preco != null ? preco : BigDecimal.ZERO;
        this.categoria = categoria;
    }

    public int getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(int idproduto) {
        this.idproduto = idproduto;
    }

    public String getDescricao_produto() {
        return descricao_produto;
    }

    public void setDescricao_produto(String descricao_produto) {
        this.descricao_produto = descricao_produto;
    }

    public int getQuantidade_produto() {
        return quantidade_produto;
    }

    public void setQuantidade_produto(int quantidade_produto) {
        this.quantidade_produto = quantidade_produto;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
       this.preco = preco != null ? preco : BigDecimal.ZERO;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDescricao_categoria() {
        return categoria != null ? categoria.getDescricao_categoria() : "";
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return descricao_produto;
    }
}
