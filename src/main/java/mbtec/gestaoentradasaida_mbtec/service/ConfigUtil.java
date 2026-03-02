package mbtec.gestaoentradasaida_mbtec.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil {

    private static final String CONFIG_DIR = ".mbtec";
    private static final Map<String, Properties> configuracoes = new HashMap<>();
    
    // Tipos de configuração (cada um terá seu próprio arquivo)
    public enum TipoConfig {
        IMPRESSORA_VENDA("impressora_venda.properties"),      // 80mm
        IMPRESSORA_ORCAMENTO("impressora_orcamento.properties"), // A4
        IMPRESSORA_RELATORIO("impressora_relatorio.properties"), // A4
        GERAL("config.properties");                           // Configurações gerais

        private final String nomeArquivo;
        
        TipoConfig(String nomeArquivo) {
            this.nomeArquivo = nomeArquivo;
        }
        
        public String getNomeArquivo() {
            return nomeArquivo;
        }
    }

    static {
        // Carrega todas as configurações ao iniciar
        for (TipoConfig tipo : TipoConfig.values()) {
            carregarConfig(tipo);
        }
    }

    private static void carregarConfig(TipoConfig tipo) {
        Path path = getPath(tipo);
        Properties props = new Properties();
        
        try {
            if (Files.exists(path)) {
                try (InputStream in = Files.newInputStream(path)) {
                    props.load(in);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar configuração " + tipo + ": " + e.getMessage());
        }
        
        configuracoes.put(tipo.name(), props);
    }

    private static Path getPath(TipoConfig tipo) {
        return Paths.get(
            System.getProperty("user.home"), 
            CONFIG_DIR, 
            tipo.getNomeArquivo()
        );
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Define uma configuração
     * @param tipo Tipo de configuração (qual impressora/config)
     * @param key Chave da propriedade
     * @param value Valor da propriedade
     */
    public static void set(TipoConfig tipo, String key, String value) {
        Properties props = configuracoes.get(tipo.name());
        if (props == null) {
            props = new Properties();
            configuracoes.put(tipo.name(), props);
        }
        
        props.setProperty(key, value);
        salvar(tipo, props);
    }

    /**
     * Obtém uma configuração
     * @param tipo Tipo de configuração
     * @param key Chave da propriedade
     * @return Valor ou null se não existir
     */
    public static String get(TipoConfig tipo, String key) {
        Properties props = configuracoes.get(tipo.name());
        return props != null ? props.getProperty(key) : null;
    }

    /**
     * Obtém uma configuração com valor padrão
     */
    public static String get(TipoConfig tipo, String key, String defaultValue) {
        String value = get(tipo, key);
        return value != null ? value : defaultValue;
    }

    /**
     * Remove uma configuração
     */
    public static void remove(TipoConfig tipo, String key) {
        Properties props = configuracoes.get(tipo.name());
        if (props != null) {
            props.remove(key);
            salvar(tipo, props);
        }
    }

    /**
     * Limpa todas as configurações de um tipo
     */
    public static void limpar(TipoConfig tipo) {
        Properties props = configuracoes.get(tipo.name());
        if (props != null) {
            props.clear();
            salvar(tipo, props);
        }
    }

    private static void salvar(TipoConfig tipo, Properties props) {
        Path path = getPath(tipo);
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Configurações Mbtec - " + tipo.name());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar configuração " + tipo + ": " + e.getMessage());
        }
    }

    /**
     * Define a impressora para vendas (80mm)
     */
    public static void setImpressoraVenda(String nomeImpressora) {
        set(TipoConfig.IMPRESSORA_VENDA, "impressora", nomeImpressora);
    }

    public static String getImpressoraVenda() {
        return get(TipoConfig.IMPRESSORA_VENDA, "impressora");
    }

    /**
     * Define a impressora para orçamentos (A4)
     */
    public static void setImpressoraOrcamento(String nomeImpressora) {
        set(TipoConfig.IMPRESSORA_ORCAMENTO, "impressora", nomeImpressora);
    }

    public static String getImpressoraOrcamento() {
        return get(TipoConfig.IMPRESSORA_ORCAMENTO, "impressora");
    }

    /**
     * Define a impressora para relatórios (A4)
     */
    public static void setImpressoraRelatorio(String nomeImpressora) {
        set(TipoConfig.IMPRESSORA_RELATORIO, "impressora", nomeImpressora);
    }

    public static String getImpressoraRelatorio() {
        return get(TipoConfig.IMPRESSORA_RELATORIO, "impressora");
    }
}