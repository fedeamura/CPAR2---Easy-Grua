package federico.amura.cp_grua.DAO.Preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Creado por federicoamura el 26/4/18.
 */

public class PrefLogin {

    private static final String KEY_ID = "id";
    private static final String KEY_CLIENTE = "esCliente";

    private static final String PREFS = "prefLogin";

    private static PrefLogin instance;

    public static PrefLogin getInstance() {
        if (instance == null) {
            instance = new PrefLogin();
        }
        return instance;
    }

    @SuppressLint("ApplySharedPref")
    public void setLoginCliente(Context context, String idCliente) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_CLIENTE, true)
                .putString(KEY_ID, idCliente)
                .commit();
    }

    @SuppressLint("ApplySharedPref")
    public void setLoginGrua(Context context, String idGrua) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_CLIENTE, false)
                .putString(KEY_ID, idGrua)
                .commit();
    }

    public String getId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        if (!prefs.contains(KEY_CLIENTE)) return null;

        return prefs.getString(KEY_ID, null);
    }

    public boolean getEsCliente(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_CLIENTE, true);
    }

    @SuppressLint("ApplySharedPref")
    public void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

}
