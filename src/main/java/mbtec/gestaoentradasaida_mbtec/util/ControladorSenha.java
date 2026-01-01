package mbtec.gestaoentradasaida_mbtec.util;

import java.util.regex.Pattern;

/**
 * Responsavel para controlar o cadastro de senha
 * garantindo a seguranca
 */
public class ControladorSenha {
    private ControladorSenha() {
    }

    // Mínimo recomendável
    private static final int MIN_TAMANHO = 6;

    private static final Pattern TEM_LETRA =
            Pattern.compile(".*[a-zA-Z].*");

    private static final Pattern TEM_NUMERO =
            Pattern.compile(".*\\d.*");

    private static final Pattern TEM_ESPECIAL =
            Pattern.compile(".*[@#$%!&*()].*");

    /**
     * Valida se a senha é aceitável para cadastro
     */
    public static boolean senhaValida(String senha) {

        if (senha == null)
            return false;

        if (senha.length() < MIN_TAMANHO || !TEM_LETRA.matcher(senha).matches() ||
                !TEM_NUMERO.matcher(senha).matches())
            return false;
        return true;
    }

    /**
     * Retorna mensagem amigável para Usuario
     */
    public static String mensagemErro(String senha) {

        if (senha == null || senha.isBlank())
            return "Senha não pode estar vazia";

        return "Senha deve ter no mínimo " + MIN_TAMANHO +
                " caracteres e conter pelo menos uma letra e um número";
    }
}
