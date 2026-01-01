package mbtec.gestaoentradasaida_mbtec.util;

import mbtec.gestaoentradasaida_mbtec.domain.Usuario;

/**
 * O metodo responsavel pela observacao e salvacao do usuario no sistema
 * Campos como MenuUsuario que mostra nome completo do usuario
 * MenuItemUsuario que mostra as info. do usuario do Banco de dados.
 */
public class UsuarioNoSistema {

    private static UsuarioNoSistema instancia;
    private Usuario usuarioLogado;

    private UsuarioNoSistema() { }

    public static UsuarioNoSistema getInstance() {
        if (instancia == null) {
            instancia = new UsuarioNoSistema();
        }
        return instancia;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void limparSession() {
        usuarioLogado = null;
    }
}
