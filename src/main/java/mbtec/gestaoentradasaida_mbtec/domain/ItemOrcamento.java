package mbtec.gestaoentradasaida_mbtec.domain;

import mbtec.gestaoentradasaida_mbtec.service.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemOrcamento {
    private int iditemorcamento;
    private Orcamento orcamento;
    private Produtos produto;
    private String descricaoitem;
    private TipoItem tipoitem;
    private int quantidade;
    private BigDecimal precounitario;
    private BigDecimal subtotal;

    public ItemOrcamento() {
    }

    public ItemOrcamento(Orcamento orcamento, Produtos produto, String descricaoitem,
                         TipoItem tipoitem, int quantidade, BigDecimal precounitario) {
        this.orcamento = orcamento;
        this.produto = produto;
        this.descricaoitem = descricaoitem;
        this.tipoitem = tipoitem;
        this.quantidade = quantidade;
        this.precounitario = precounitario != null ? precounitario : BigDecimal.ZERO;
        calcularSubtotal();
    }

    public int getIditemorcamento() {
        return iditemorcamento;
    }

    public void setIditemorcamento(int iditemorcamento) {
        this.iditemorcamento = iditemorcamento;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public Produtos getProduto() {
        return produto;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
    }

    public String getDescricaoitem() {
        return descricaoitem;
    }

    public void setDescricaoitem(String descricaoitem) {
        this.descricaoitem = descricaoitem;
    }

    public TipoItem getTipoitem() {
        return tipoitem;
    }

    public void setTipoitem(TipoItem tipoitem) {
        this.tipoitem = tipoitem;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecounitario() {
        return precounitario;
    }

    public void setPrecounitario(BigDecimal precounitario) {
        this.precounitario = precounitario != null ? precounitario : BigDecimal.ZERO;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        if (precounitario == null) {
            this.subtotal = BigDecimal.ZERO;
            return;
        }
        this.subtotal = precounitario
                .multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubtotal() {
        return subtotal != null ? subtotal : BigDecimal.ZERO;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "ItemOrcamento{" +
                "orcamento=" + orcamento.getIdorcamento() +
                ", produto=" + produto.getDescricao_produto() +
                ", descricaoitem='" + descricaoitem + '\'' +
                ", tipoitem=" + tipoitem.name() +
                ", quantidade=" + quantidade +
                ", precounitario=" + precounitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
