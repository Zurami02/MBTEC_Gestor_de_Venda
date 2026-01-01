package mbtec.gestaoentradasaida_mbtec.util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.prefs.Preferences;

/**
 * @author Mbtec subTecnico Zulo Mitumba
 * @email Zuraminakwatcha02@gmail.com
 * A classe define comportamento de um temporizador configuravel
 */
public class TemporizadorConfig {

    private static TemporizadorConfig instance;
    private final Preferences pref;
    private final SimpleIntegerProperty minutoProperty;

    private TemporizadorConfig() {
        pref = Preferences.userNodeForPackage(TemporizadorConfig.class);
        int minutoSalvo = pref.getInt("minutoTemporizador", 5);
        minutoProperty = new SimpleIntegerProperty(minutoSalvo);
    }

    public static TemporizadorConfig getInstance() {
        if (instance == null) {
            instance = new TemporizadorConfig();
        }
        return instance;
    }

    public int getMinutoTemporizador() {
        return minutoProperty.get();
    }

    public void setMinutoTemporizador(int minutos) {
        minutoProperty.set(minutos);
        pref.putInt("minutoTemporizador", minutos);
    }

    public IntegerProperty minutoProperty() {
        return minutoProperty;
    }
}

