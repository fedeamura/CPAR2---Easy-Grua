package federico.amura.cp_grua.UI;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import federico.amura.cp_grua.DAO.Preferences.PrefLogin;
import federico.amura.cp_grua.Model.Grua;
import federico.amura.cp_grua.Model.Usuario;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class App extends Application {

    private static App instance;

    private LatLng posicionActual;
    private Usuario usuario;
    private Grua grua;

    public static App getInstance() {
        return instance;
    }

    public App() {
        super();
        instance = this;
        posicionActual = null;
        usuario = null;
        grua = null;
    }

    public void clear() {
        this.posicionActual = null;
    }

    public boolean isInit() {
        if (posicionActual == null) {
            Log.d("App", "EsInit: Sin posicion actual");
            return false;
        }
        if (PrefLogin.getInstance().getEsCliente(this)) {
            boolean conUsuario = getUsuario() != null;
            Log.d("App", "EsInit: Con usuario " + conUsuario);
            return conUsuario;
        } else {
            boolean conGrupo = getGrua() != null;
            Log.d("App", "EsInit: Con grua " + conGrupo);
            return conGrupo;
        }
    }

    public void setPosicionActual(LatLng latLng) {
        this.posicionActual = latLng;
    }

    public LatLng getPosicionActual() {
        return posicionActual;
    }

    public boolean esUsuario() {
        return usuario != null;
    }

    public Usuario getUsuario() {
        if (!PrefLogin.getInstance().getEsCliente(this)) return null;
        return usuario;
    }

    public Grua getGrua() {
        if (PrefLogin.getInstance().getEsCliente(this)) return null;
        return grua;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setGrua(Grua grua) {
        this.grua = grua;
    }
}
