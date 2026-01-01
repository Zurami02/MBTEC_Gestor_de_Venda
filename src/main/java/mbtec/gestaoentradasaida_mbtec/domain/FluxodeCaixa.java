package mbtec.gestaoentradasaida_mbtec.domain;

public class FluxodeCaixa {
    private int idfluxocaixa;
    private String descricao_produto;
    private int quantidade;
    private double valor;
    private String data;
    private double desconto;

    private int idproduto;//comunicacao a banco de dados.
    private Produtos produto;//somente para exibicao na tela.

    private Categoria categoria;

    public FluxodeCaixa() {
    }

    public FluxodeCaixa(int idfluxocaixa, String descricao_produto, int quantidade,
                        double valor, String data, double desconto, int idproduto, Produtos produto) {
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

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
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

    public double getPreco() {
        return produto != null ? produto.getPreco() : 0.0;
    }

    public int getIdcategoria(){
        return categoria != null ? categoria.getIdcategoria() : 0;
    }

    public String getDescricao_categoria(){
        return categoria != null? categoria.getDescricao_categoria() : null;
    }

    public String getDescricao_produto(){
        return produto != null ? produto.getDescricao_produto() : descricao_produto;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
    }
}
