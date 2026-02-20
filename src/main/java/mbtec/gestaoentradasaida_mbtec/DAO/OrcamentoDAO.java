package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.DB.ConexaoSQLite;
import mbtec.gestaoentradasaida_mbtec.domain.Cliente;
import mbtec.gestaoentradasaida_mbtec.domain.Orcamento;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoDAO {

    public int salvarOrcamento(Connection conn, @NotNull Orcamento orc) {
        String sql = """
                INSERT INTO orcamento 
                (idusuario, numero_orcamento, idcliente, cliente_nome, nuit, data, total_sem_iva, 
                taxaiva, total, valor_iva)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ps.setInt(1, orc.getIdusuario());
            ps.setString(2, orc.getNumero_orcamento());
            if (orc.getCliente() != null) {
                ps.setInt(3, orc.getCliente().getIdcliente());
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setNull(3, Types.INTEGER);
                ps.setString(4, orc.getCliente_nome());
                ps.setString(5, orc.getNuit());
            }
            ps.setString(6, orc.getData().format(fmt));

            ps.setBigDecimal(7, orc.getTotal());
            ps.setBigDecimal(8, orc.getTaxaIVA());
            ps.setBigDecimal(9, orc.getTotalComIVA());
            ps.setBigDecimal(10, orc.getValorIva());


            ps.executeUpdate();

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("Erro ao gerar ID da orcamento.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Orcamento> historicoOrcamento(LocalDate dataInicial, LocalDate dataFinal, String textoCliente) {
        List<Orcamento> lista = new ArrayList<>();

        String sql = """
        SELECT
            o.idorcamento,
            o.numero_orcamento,
            o.data,
            o.total,
            o.taxaiva,
            o.idcliente,
            o.cliente_nome,
            o.valor_iva,
            o.nuit,
            c.nome AS nome_registado
        FROM orcamento o
        LEFT JOIN cliente c ON c.idcliente = o.idcliente
        WHERE
            ( ? IS NULL OR DATE(o.data) >= ? )
        AND ( ? IS NULL OR DATE(o.data) <= ? )
        AND (
               ? IS NULL
            OR c.nome LIKE ?
            OR o.cliente_nome LIKE ?
        )
        ORDER BY o.data DESC
    """;

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            //data inicial
            if (dataInicial == null) {
                ps.setNull(1, Types.DATE);
                ps.setNull(2, Types.DATE);
            } else {
                ps.setString(1, dataInicial.toString());
                ps.setString(2, dataInicial.toString());
            }

            //data final
            if (dataFinal == null) {
                ps.setNull(3, Types.DATE);
                ps.setNull(4, Types.DATE);
            } else {
                ps.setString(3, dataFinal.toString());
                ps.setString(4, dataFinal.toString());
            }

            //cliente
            if (textoCliente == null || textoCliente.isBlank()) {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            } else {
                String like = "%" + textoCliente + "%";
                ps.setString(5, textoCliente);
                ps.setString(6, like);
                ps.setString(7, like);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Orcamento orc = new Orcamento();
                orc.setIdorcamento(rs.getInt("idorcamento"));
                orc.setNumero_orcamento(rs.getString("numero_orcamento"));

                LocalDateTime dataPura =
                        LocalDateTime.parse(rs.getString("data").replace(" ", "T"));
                orc.setData(dataPura.withNano(0));

                orc.setTotal(rs.getBigDecimal("total"));
                orc.setTaxaIVA(rs.getBigDecimal("taxaiva"));
                orc.setValorIVA(rs.getBigDecimal("valor_iva"));

                int idCliente = rs.getInt("idcliente");
                if (!rs.wasNull()) {
                    Cliente c = new Cliente();
                    c.setIdcliente(idCliente);
                    c.setNome(rs.getString("nome_registado"));
                    orc.setCliente(c);
                } else {
                    orc.setCliente_nome(rs.getString("cliente_nome"));
                    orc.setNuit(rs.getString("nuit"));
                }

                lista.add(orc);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar histórico de orçamentos", e);
        }
        return lista;
    }

    public int buscarProximoNumeroOrcamento(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(idorcamento), 0) + 1 FROM orcamento";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }
}
