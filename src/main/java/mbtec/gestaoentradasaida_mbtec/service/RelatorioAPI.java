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
import java.util.Objects;

/**
 * Classe responsavel em gerar Venda a dinheiro (VD) e imprimir diretamente
 * a impressora configurada no sistema
 */
public class RelatorioAPI {

    public static JasperPrint gerarVD(Connection conn, Integer idvenda) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("idvenda", idvenda);
            String subreportDir = Objects.requireNonNull(
                    RelatorioUtil.class.getResource("/relatoriosjasper/")
            ).getPath();

            params.put("SUBREPORT_DIR", subreportDir);

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
}
