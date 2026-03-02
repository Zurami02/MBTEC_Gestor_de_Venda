package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Pagamento;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagamentoDAO {

    public void salvar(@NotNull Connection conn, @NotNull Pagamento p) {
        String sql = """
            INSERT INTO pagamento (idvenda, valorpagamento, formapagamento, datapagamento)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            ps.setInt(1, p.getVenda().getIdVenda());
            ps.setBigDecimal(2, p.getValorPagamento());
            ps.setString(3, p.getFormaPagamento());
            ps.setString(4, p.getDataPagamento().format(fmt));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Erro ao salvar pagamento", e);
        }
    }
}
