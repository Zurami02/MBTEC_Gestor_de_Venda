package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Orcamento {
    private int idorcamento;
    private String numero_orcamento;
    private Cliente cliente;
    private String nuit;
    private String cliente_nome;
    private LocalDateTime data;
    private BigDecimal total;
    private BigDecimal taxaIVA;
    private int idusuario;
    List<ItemOrcamento> itens = new ArrayList<>();

    public Orcamento() {
        this.data = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public Orcamento(String numero_orcamento, Cliente cliente, String nuit,
                     String cliente_nome) {
        this.numero_orcamento = numero_orcamento;
        this.cliente = cliente;
        this.cliente_nome = cliente_nome;
        this.data = LocalDateTime.now();
    }

    public List<ItemOrcamento> getItens() {
        return itens;
    }

    public void adicionarItem(ItemOrcamento item) {
        itens.add(item);
    }

    public int getIdorcamento() {
        return idorcamento;
    }

    public BigDecimal getTaxaIVA() {
        return taxaIVA != null ?taxaIVA : BigDecimal.ZERO;
    }

    public void setTaxaIVA(BigDecimal taxaIVA) {
        this.taxaIVA = taxaIVA;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getNumero_orcamento() {
        return numero_orcamento;
    }

    public void setNumero_orcamento(String numero_orcamento) {
        this.numero_orcamento = numero_orcamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNuit() {
        return nuit;
    }

    public void setNuit(String nuit) {
        this.nuit = nuit;
    }

    public String getCliente_nome() {
        return cliente_nome;
    }

    public void setCliente_nome(String cliente_nome) {
        this.cliente_nome = cliente_nome;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public BigDecimal getTotal() {
        return total != null ? total : BigDecimal.ZERO;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void calculartotal(List<ItemOrcamento> itens) {

        BigDecimal soma = BigDecimal.ZERO;

        for (ItemOrcamento item : itens) {
            BigDecimal sub = item.getSubtotal() != null
                    ? item.getSubtotal()
                    : BigDecimal.ZERO;

            soma = soma.add(sub);
        }

        this.total = soma.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorIva() {
        BigDecimal subtotal = getSubtotal();
        return taxaIVA != null
                ? subtotal.multiply(taxaIVA).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public BigDecimal getTotalComIVA() {
        BigDecimal subtotal = getSubtotal();
        return subtotal.add(getValorIva()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubtotal() {
        return itens.stream()
                .map(item -> item.getSubtotal() != null
                        ? item.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Orcamento{" +
                "numero_orcamento='" + numero_orcamento + '\'' +
                ", cliente=" + cliente.getNome() +
                ", cliente_nome='" + cliente_nome + '\'' +
                ", data=" + data +
                ", total=" + total +
                '}';
    }
}
