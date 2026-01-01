package mbtec.gestaoentradasaida_mbtec.util;

/**
 * Controla o Estado de Sistema para verificacao de banco de dados
 * e a criacao de pasta inicial para dB
 */
public final class EstadoApp {

        private static boolean dbConectado = false;

        private EstadoApp() {}

        public static boolean isDbConectado() {
            return dbConectado;
        }

        public static void setDbConectado(boolean status) {
            dbConectado = status;
        }
}
