package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;

public class FluxodeCaixa {
    private int idfluxocaixa;
    private String descricao_produto;
    private int quantidade;
    private BigDecimal valor;
    private String data;
    private BigDecimal desconto;

    private int idproduto;//comunicacao a banco de dados.
    private Produtos produto;//somente para exibicao na tela.

    private Categoria categoria;

    public FluxodeCaixa() {
    }

    public FluxodeCaixa(int idfluxocaixa, String descricao_produto, int quantidade,
                        BigDecimal valor, String data, BigDecimal desconto, int idproduto, Produtos produto) {
        this.idfluxocaixa = idfluxocaixa;
        this.descricao_produto = descricao_produto;
        this.quantidade = quantidade;
        this.valor = valor;
        this.data = data;
        this.desconto = desconto;
        this.idproduto = idproduto;
        this.produto = produto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public int getIdfluxocaixa() {
        return idfluxocaixa;
    }

    public void setIdfluxocaixa(int idfluxocaixa) {
        this.idfluxocaixa = idfluxocaixa;
    }

    public void setDescricao_produto(String descricao_produto) {
        this.descricao_produto = descricao_produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(int idproduto) {
        this.idproduto = idproduto;
    }

    public Produtos getProduto() {
        return produto;
    }

    public int getQuantidade_produto() {
        return produto != null ? produto.getQuantidade_produto() : 0;
    }

    public BigDecimal getPreco() {
        return produto != null ? produto.getPreco() : BigDecimal.ZERO;
    }

    public int getIdcategoria() {
        return categoria != null ? categoria.getIdcategoria() : 0;
    }

    public String getDescricao_categoria() {
        return categoria != null ? categoria.getDescricao_categoria() : null;
    }

    public String getDescricao_produto() {
        return produto != null ? produto.getDescricao_produto() : descricao_produto;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
    }
}
