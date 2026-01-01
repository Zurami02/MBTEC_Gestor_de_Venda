package mbtec.gestaoentradasaida_mbtec.util;

import org.mindrot.jbcrypt.BCrypt;

public class CriptografarUtil {
public static String gerarHash(String senha){
    //Gerar o Hash da senha
    return BCrypt.hashpw(senha, BCrypt.gensalt(12));
}
    //Verificar se a senha digitada bate com o Hash da senha salvo
    public static boolean verificarSenha(String senhaDigitada, String hashSalvo){
    return BCrypt.checkpw(senhaDigitada, hashSalvo);
    }

}
