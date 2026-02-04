package mbtec.gestaoentradasaida_mbtec.domain;

public class Produtos {
    private int idproduto;
    private String descricao_produto;
    private int quantidade_produto;
    private double preco;
    private Categoria categoria;

    public Produtos() {}

    public Produtos(int idproduto, String descricao_produto, int quantidade_produto, double preco, Categoria categoria) {
        this.idproduto = idproduto;
        this.descricao_produto = descricao_produto;
        this.quantidade_produto = quantidade_produto;
        this.preco = preco;
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return descricao_produto;
    }
}
