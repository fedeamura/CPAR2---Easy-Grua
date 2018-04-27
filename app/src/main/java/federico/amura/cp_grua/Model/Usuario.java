package federico.amura.cp_grua.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Creado por federicoamura el 26/4/18.
 */

public class Usuario {
    private String id;
    private String email;

    @Exclude
    private String latitud;
    @Exclude
    private String longitud;
    @Exclude
    private String distancia;
    @Exclude
    private double distanciaVal;
    @Exclude
    private String duracion;
    @Exclude
    private double duracionVal;
    @Exclude
    private List<LatLng> puntos;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getDistancia() {
        return distancia;
    }

    @Exclude
    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    @Exclude
    public double getDistanciaVal() {
        return distanciaVal;
    }

    @Exclude
    public void setDistanciaVal(double distanciaVal) {
        this.distanciaVal = distanciaVal;
    }

    @Exclude
    public String getDuracion() {
        return duracion;
    }

    @Exclude
    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    @Exclude
    public double getDuracionVal() {
        return duracionVal;
    }

    @Exclude
    public void setDuracionVal(double duracionVal) {
        this.duracionVal = duracionVal;
    }

    @Exclude
    public List<LatLng> getPuntos() {
        return puntos;
    }

    @Exclude
    public void setPuntos(List<LatLng> puntos) {
        this.puntos = puntos;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
