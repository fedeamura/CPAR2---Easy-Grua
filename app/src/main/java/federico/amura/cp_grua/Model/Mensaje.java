package federico.amura.cp_grua.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class Mensaje {
    private String id;
    private String contenido;
    private boolean cliente;
    private long fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isCliente() {
        return cliente;
    }

    public void setCliente(boolean cliente) {
        this.cliente = cliente;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
