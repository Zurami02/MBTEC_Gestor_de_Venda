package mbtec.gestaoentradasaida_mbtec.DAO;

import mbtec.gestaoentradasaida_mbtec.domain.Orcamento;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class OrcamentoDAO {

    public int salvarOrcamento(Connection conn, Orcamento orc) {
        String sql = """
                INSERT INTO orcamento 
                (idusuario, numero_orcamento, idcliente, cliente_nome, nuit, data, total) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ps.setString(1, orc.getData().format(fmt));
            ps.setString(2, orc.getNumero_orcamento());
            if (orc.getCliente() != null) {
                ps.setInt(3, orc.getCliente().getIdcliente());
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setNull(2, Types.INTEGER);
                ps.setString(4, orc.getCliente_nome());
                ps.setString(5, orc.getNuit());
            }

            ps.setBigDecimal(6, orc.getTotalComIVA());
            ps.setInt(7, orc.getIdusuario());

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

    public int buscarProximoNumeroOrcamento(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(idorcamento), 0) + 1 FROM orcamento";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        }
    }
}
