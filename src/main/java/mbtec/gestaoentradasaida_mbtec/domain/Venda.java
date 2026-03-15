package mbtec.gestaoentradasaida_mbtec.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Responsavel em instanciar Venda calculando ou adicionando os
 * itens de venda, somar os itens, calcular total de venda
 * Versao de aprendizagem em Venda e Itens de Venda
 * @version 1.1
 * @author Mbtec subtecnico Zulo Rajabo mitumba
 */
public class Venda {

    private int idVenda;
    private LocalDateTime dataVenda;
    private Cliente cliente;
    private String nomeCliente;
    private String nuitCliente;
    private boolean pago;
    private BigDecimal taxaIva;
    private List<Itemvenda> itens = new ArrayList<>();
    private boolean vd;
    private String status;
    private String numerovd;
    private int idUsuario;

    private BigDecimal totalDb;
    private BigDecimal valorIVA;
    private BigDecimal taxaIvaDB;

    public Venda() {
        this.dataVenda = LocalDateTime.now();
    }

    public Venda(int idVenda, String nomeCliente, String nuitCliente,
                 boolean pago, BigDecimal taxaIva) {
        this.idVenda = idVenda;
        this.nomeCliente = nomeCliente;
        this.nuitCliente = nuitCliente;
        this.pago = pago;
        this.taxaIva = taxaIva;
    }

    public Venda(int idVenda, Cliente cliente, boolean pago, BigDecimal taxaIva) {
        this();
        this.idVenda = idVenda;
        this.cliente = cliente;
        this.pago = pago;
        this.taxaIva = taxaIva;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNumerovd() {
        return numerovd;
    }

    public void setNumerovd(String numerovd) {
        this.numerovd = numerovd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAnulada(){
        return "ANULADA".equalsIgnoreCase(status);
    }

    public BigDecimal getValorIVA() {
        return valorIVA != null  ? valorIVA : BigDecimal.ZERO;

    }

    public void setValorIVA(BigDecimal valorIVA) {
        this.valorIVA = valorIVA;
        System.out.println(valorIVA);
    }

    public BigDecimal getTotalDb() {
        return totalDb != null ? totalDb : BigDecimal.ZERO;
    }

    public void setTotalDb(BigDecimal totalDb) {
        this.totalDb = totalDb;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public boolean isVd() {
        return vd;
    }

    public void setVd(boolean vd) {
        this.vd = vd;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getNuitCliente() {
        return nuitCliente;
    }

    public void setNuitCliente(String nuitCliente) {
        this.nuitCliente = nuitCliente;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public BigDecimal getTaxaIvaDB() {
        return taxaIvaDB;
    }

    public void setTaxaIvaDB(BigDecimal taxaIvaDB) {
        this.taxaIvaDB = taxaIvaDB;
    }

    public BigDecimal getTaxaIva() {
        return taxaIva != null ? taxaIva : BigDecimal.ZERO;
    }

    public void setTaxaIva(BigDecimal taxaIva) {
        this.taxaIva = taxaIva.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
    }

    public List<Itemvenda> getItens() {
        return itens;
    }

    public void adicionarItem(Itemvenda item) {
        itens.add(item);
    }

    public BigDecimal getSubtotal() {
        return itens.stream()
                .map(Itemvenda::getTotalComDesconto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValorIva() {
        BigDecimal subtotal = getSubtotal();
        return taxaIva != null
                ? subtotal.multiply(taxaIva).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public BigDecimal getTotalFinal() {
        return getSubtotal().add(calcularValorIva()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValorIva() {
        BigDecimal subtotal = getSubtotal();
        return taxaIva != null
                ? subtotal.multiply(taxaIva).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Venda: " + idVenda +
                " | Data: " + dataVenda +
                " | Total: " + getTotalFinal();
    }
}

