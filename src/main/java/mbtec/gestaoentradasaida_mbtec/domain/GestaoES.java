package mbtec.gestaoentradasaida_mbtec.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class GestaoES {
    private int idgestao;
    private String data;
    private String planoconta;
    private String descricao;
    private int quantidade;
    private double valor;

    public GestaoES() {
    }

    public GestaoES(int idGestaoES, String data, String planoConta, String descricao, int quantidade, double valor) {
        this.idgestao = idGestaoES;
        this.data = data;
        this.planoconta = planoConta;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valor = valor;
    }

    public int getIdgestao() {
        return idgestao;
    }

    public void setIdgestao(int idgestao) {
        this.idgestao = idgestao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
            this.data = data;
    }

    private boolean isDataValida(String data) {
        try {
            LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String getPlanoconta() {
        return planoconta;
    }

    public void setPlanoconta(String planoconta) {
        this.planoconta = planoconta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
}
