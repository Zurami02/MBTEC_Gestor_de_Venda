package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;

public class Fornecedores {
    private int idfornecedor;
    private String descricaoProduto;
    private int quantidade;
    private String fornecedor;
    private BigDecimal preco;

    public Fornecedores() {
    }

    public Fornecedores(int idfornecedor, String descricaoProduto, int quantidade, String fornecedor, BigDecimal preco) {
        this.idfornecedor = idfornecedor;
        this.descricaoProduto = descricaoProduto;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getIdfornecedor() {
        return idfornecedor;
    }

    public void setIdfornecedor(int idfornecedor) {
        this.idfornecedor = idfornecedor;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public BigDecimal getPreco() {
        return preco != null ? preco : BigDecimal.ZERO;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
}
