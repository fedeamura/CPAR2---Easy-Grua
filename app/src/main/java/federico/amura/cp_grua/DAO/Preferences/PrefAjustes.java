package federico.amura.cp_grua.DAO.Preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Creado por federicoamura el 26/4/18.
 */

public class PrefAjustes {

    private static final String KEY_MAX_DISTANCIA = "max_distancia";
    private static final String PREFS = "prefDistancia";

    private static PrefAjustes instance;

    public static PrefAjustes getInstance() {
        if (instance == null) {
            instance = new PrefAjustes();
        }
        return instance;
    }

    @SuppressLint("ApplySharedPref")
    public void setMaxDistancia(Context context, int max) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_MAX_DISTANCIA, max).commit();
    }

    public int getMaxDistancia(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MAX_DISTANCIA, 20);
    }

}
