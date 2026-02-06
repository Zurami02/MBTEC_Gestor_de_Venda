package mbtec.gestaoentradasaida_mbtec;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class ImpressoraAPI {
    public static void main(String[] args) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        System.out.println("Impressoras encontradas: ");
        for (PrintService ps: services){
            System.out.println(" - "+
                    ps.getName());
        }
    }
}
