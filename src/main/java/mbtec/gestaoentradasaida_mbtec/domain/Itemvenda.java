package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Metodo responsavel em instanciar objeto itens de venda
 * Contendo calculos de Subtotal e desconto
 * Versao de aprendizagem em Venda e Itens de Venda
 * @version 1.1
 * @author Mbtec subtecnico Zulo Rajabo mitumba
 */
public class Itemvenda {

    private int idItemVenda;
    private Produtos produto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal desconto;
    private Venda venda;

    public Itemvenda() {}

    public Itemvenda(Produtos produto, int quantidade, BigDecimal precoUnitario, BigDecimal desconto, Venda venda) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario != null ? precoUnitario : produto.getPreco();
        this.desconto = desconto != null ? desconto : BigDecimal.ZERO;
        this.venda = venda;
    }

    public int getIdItemVenda() {
        return idItemVenda;
    }

    public void setIdItemVenda(int idItemVenda) {
        this.idItemVenda = idItemVenda;
    }

    public Produtos getProduto() {
        return produto;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
        this.precoUnitario = produto.getPreco();
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario){
        if (precoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço unitário inválido");
        }
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        if (desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Desconto inválido");
        }
        this.desconto = desconto;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    // CÁLCULOS
    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalComDesconto() {
        BigDecimal total = getSubtotal().subtract(desconto);
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total;
    }

    @Override
    public String toString() {
        return produto.getDescricao_produto() +
                " | Qtd: " + quantidade +
                " | Unit: " + precoUnitario +
                " | Desc: " + desconto +
                " | Total: " + getTotalComDesconto();
    }
}