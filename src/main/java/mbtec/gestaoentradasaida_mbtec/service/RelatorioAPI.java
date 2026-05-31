package mbtec.gestaoentradasaida_mbtec.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsavel em gerar Venda a dinheiro (VD) e imprimir diretamente
 * a impressora configurada no sistema
 */
public class RelatorioAPI {

    /**
     * Impressao de Vd para venda
     *
     * @param conn
     * @param idvenda
     * @return
     */
    public static JasperPrint gerarVD(Connection conn, Integer idvenda) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("idvenda", idvenda);

            //a referência da classe para localizar a imagem
            params.put("REPORT_CLASS", RelatorioUtil.class);

            InputStream relatorio =
                    RelatorioAPI.class.getResourceAsStream(
                            "/relatoriosjasper/VD.jasper"
                    );

            if (relatorio == null) {
                throw new RuntimeException("VD.jasper não encontrado");
            }

            return JasperFillManager.fillReport(relatorio, params, conn);

        } catch (JRException e) {

            throw new RuntimeException("Falha ao gerar relatório VD", e);
        }
    }

    /**
     * Gera relatório de Orçamento
     */
    public static JasperPrint gerarOrcamento(Connection conn, Integer idOrcamento) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("idorcamento", idOrcamento);

            params.put("REPORT_CLASS", RelatorioAPI.class);

            InputStream relatorio = RelatorioAPI.class.getResourceAsStream(
                    "/relatoriosjasper/Orcamento.jasper"
            );

            if (relatorio == null) {
                throw new RuntimeException("Orcamento.jasper não encontrado");
            }

            return JasperFillManager.fillReport(relatorio, params, conn);

        } catch (JRException e) {
            throw new RuntimeException("Falha ao gerar relatório de Orçamento", e);
        }
    }

    public static void imprimir(JasperPrint print, String impressora)
            throws JRException {

        PrintService service = Arrays.stream(
                        PrintServiceLookup.lookupPrintServices(null, null)
                )
                .filter(p -> p.getName().equalsIgnoreCase(impressora))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Impressora não encontrada: " + impressora)
                );

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();

        // Entrada do relatório
        exporter.setExporterInput(new SimpleExporterInput(print));

        // Configuração da impressora
        SimplePrintServiceExporterConfiguration config =
                new SimplePrintServiceExporterConfiguration();

        config.setPrintService(service);
        config.setDisplayPrintDialog(false);
        config.setDisplayPageDialog(false);

        exporter.setConfiguration(config);

        exporter.exportReport();
    }

    /**
     * Imprime VD usando a impressora configurada (80mm)
     */
    public static void imprimirVD(JasperPrint print) throws JRException {
        String impressora = ConfigUtil.getImpressoraVenda();

        if (impressora == null || impressora.isEmpty()) {
            throw new RuntimeException(
                    "Impressora para vendas não configurada. " +
                            "Configure em: Configurações > Impressoras"
            );
        }

        imprimir(print, impressora);
    }

    /**
     * Imprime Orçamento usando a impressora configurada (A4)
     */
    public static void imprimirOrcamento(JasperPrint print) throws JRException {
        String impressora = ConfigUtil.getImpressoraOrcamento();

        if (impressora == null || impressora.isEmpty()) {
            throw new RuntimeException(
                    "Impressora para orçamentos não configurada. " +
                            "Configure em: Configurações > Impressoras"
            );
        }

        imprimir(print, impressora);
    }

    /**
     * Imprime Relatório usando a impressora configurada (A4)
     */
    public static void imprimirRelatorio(JasperPrint print) throws JRException {
        String impressora = ConfigUtil.getImpressoraRelatorio();

        if (impressora == null || impressora.isEmpty()) {
            throw new RuntimeException(
                    "Impressora para relatórios não configurada. " +
                            "Configure em: Configurações > Impressoras"
            );
        }

        imprimir(print, impressora);
    }

    /**
     * Gera e imprime VD em um único método
     */
    public static void gerarEImprimirVD(Connection conn, Integer idvenda) {
        try {
            JasperPrint print = gerarVD(conn, idvenda);
            imprimirVD(print);
        } catch (JRException e) {
            throw new RuntimeException("Erro ao imprimir venda", e);
        }
    }

    /**
     * Gera e imprime Orçamento em um único método
     */
    public static void gerarEImprimirOrcamento(Connection conn, Integer idOrcamento) {
        try {
            JasperPrint print = gerarOrcamento(conn, idOrcamento);
            imprimirOrcamento(print);
        } catch (JRException e) {
            throw new RuntimeException("Erro ao imprimir orçamento", e);
        }
    }
}
