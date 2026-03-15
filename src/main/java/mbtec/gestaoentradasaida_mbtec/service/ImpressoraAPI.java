package mbtec.gestaoentradasaida_mbtec.service;

import mbtec.gestaoentradasaida_mbtec.domain.Cliente;
import mbtec.gestaoentradasaida_mbtec.domain.ItemOrcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Orcamento;
import mbtec.gestaoentradasaida_mbtec.domain.Produtos;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ImpressoraAPI {
    public static void main(String[] args) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Impressoras encontradas: ");
        for (PrintService ps: services){
            System.out.println(" - "+
                    ps.getName());
        }
        Cliente c = new Cliente();
        Produtos p = new Produtos(1,"PC HP",3,new BigDecimal(12000),null);
        Produtos p1 = new Produtos(2,"Teclas HP",3,new BigDecimal(500),null);
        Produtos p2 = new Produtos(3,"Mouse HP",3,new BigDecimal(350),null);
        Produtos p3 = new Produtos();
        Orcamento orc = new Orcamento(
                "orc001",c,"88887222","Mitumba");

        ItemOrcamento itens = new ItemOrcamento();
        itens.setProduto(p);
        itens.setOrcamento(orc);
        itens.setQuantidade(2);
        itens.setPrecounitario(p.getPreco());
        itens.setTipoitem(TipoItem.PRODUTO);
        itens.calcularSubtotal();

        ItemOrcamento itens1 = new ItemOrcamento();
        itens1.setProduto(p1);
        itens1.setOrcamento(orc);
        itens1.setQuantidade(3);
        itens1.setPrecounitario(p1.getPreco());
        itens1.setTipoitem(TipoItem.PRODUTO);
        itens1.calcularSubtotal();

        ItemOrcamento itens2 = new ItemOrcamento();
        itens2.setProduto(p3);
        itens2.setDescricaoitem("Instalacao de Rede Local com 3 impressora");
        itens2.setOrcamento(orc);
        itens2.setQuantidade(1);
        itens2.setPrecounitario(new BigDecimal("2000"));
        itens2.setTipoitem(TipoItem.SERVICO);
        itens2.calcularSubtotal();

        List<ItemOrcamento> listaitens = new ArrayList<>();
        listaitens.add(itens);
        listaitens.add(itens1);
        listaitens.add(itens2);
        listaitens.add(itens);
        orc.calculartotal(listaitens);
        System.out.println(orc+"\n________________________________");

        for (ItemOrcamento lista : listaitens){
            System.out.println(lista);
        }
    }
}
