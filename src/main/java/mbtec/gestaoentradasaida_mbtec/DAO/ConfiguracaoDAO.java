package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Configuracao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracaoDAO {

    public static @Nullable Configuracao buscarPorChave(String chave) {
        String sql = "SELECT chave, valor FROM configuracoes WHERE chave = ?";

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, chave);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String valorStr = rs.getString("valor");
                BigDecimal valor = valorStr != null ? new BigDecimal(valorStr) : BigDecimal.ZERO;

                return new Configuracao(rs.getString("chave"), valor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void salvar(@NotNull Configuracao config) {
        String sql = """
        INSERT INTO configuracoes (chave, valor)
        VALUES (?, ?)
        ON CONFLICT(chave)
        DO UPDATE SET valor = excluded.valor
    """;

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, config.getChave());
            ps.setString(2, config.getValor().toPlainString()); // salva como texto
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
