package mbtec.gestaoentradasaida_mbtec.util;

import mbtec.gestaoentradasaida_mbtec.domain.FluxodeCaixa;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;


import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioUtil {

    public void gerarRelatorioFluxoCaixaPorPeriodo(List<FluxodeCaixa> fluxodeCaixas, LocalDate inicio, LocalDate fim) {
        try {
            String caminho = "src/relatorios/relatorio_produtos_vendidos.jasper";
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fluxodeCaixas);

            Map<String, Object> datas = new HashMap<>();
            datas.put("dataInicio", inicio.toString());
            datas.put("dataFim", fim.toString());
            datas.put("titulo", "Relatório de Produtos Vendidos");

            JasperPrint print = JasperFillManager.fillReport(caminho, datas, dataSource);
            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void gerarRelatorio(
            String caminhoRelatorio,
            Map<String, Object> parametros,
            List<T> dados) {
        try {
            // Caminho compilado (.jasper)
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            JasperPrint print = JasperFillManager.fillReport(caminhoRelatorio, parametros, dataSource);

            // Exibir em tela
            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void gerarRelatorioStream(Collection<T> dados, String caminhoJasper, Map<String, Object> parametros) {
        try {
            // Localiza o arquivo .jasper nos resources
            InputStream arquivo = RelatorioUtil.class.getResourceAsStream(caminhoJasper);
            if (arquivo == null) {
                throw new RuntimeException("Arquivo Jasper não encontrado: " + caminhoJasper);
            }

            // Cria data source a partir da lista de beans
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

            // Se parâmetros forem nulos, inicializa vazio
            if (parametros == null) {
                parametros = new HashMap<>();
            }

            // Adiciona a referência da classe para localizar a imagem
            parametros.put("REPORT_CLASS", RelatorioUtil.class);

            // Preenche relatório
            JasperPrint print = JasperFillManager.fillReport(arquivo, parametros, dataSource);

            // Exibe relatório
            JasperViewer.viewReport(print, false);

        } catch (JRException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    public static void gerarPdfEAbrir(List<?> dados, String caminhoJasper,
                                      Map<String, Object> parametros, String caminhoPDF) {
        try {
            System.setProperty("java.awt.headless", "false");

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    RelatorioUtil.class.getResourceAsStream(caminhoJasper),
                    parametros,
                    new JRBeanCollectionDataSource(dados)
            );

            // Exporta PDF
            System.out.println("Salvando PDF em: " + caminhoPDF);
            JasperExportManager.exportReportToPdfFile(jasperPrint, caminhoPDF);
            System.out.println("PDF salvo com sucesso!");

            // Abre no visualizador padrão do Windows
            Desktop.getDesktop().open(new File(caminhoPDF));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }


}
