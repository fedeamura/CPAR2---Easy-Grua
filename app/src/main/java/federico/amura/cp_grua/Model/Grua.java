package federico.amura.cp_grua.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Creado por federicoamura el 25/4/18.
 */

@SuppressWarnings("unused")
public class Grua {
    private String id;
    private String patente;
    private boolean libre;
    private String latitud;
    private String longitud;
    private String latitudDestino;
    private String longitudDestino;
    private String idUsuario;

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

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public boolean isLibre() {
        return libre;
    }

    public void setLibre(boolean libre) {
        this.libre = libre;
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

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getLatitudDestino() {
        return latitudDestino;
    }

    public void setLatitudDestino(String latitudDestino) {
        this.latitudDestino = latitudDestino;
    }

    public String getLongitudDestino() {
        return longitudDestino;
    }

    public void setLongitudDestino(String longitudDestino) {
        this.longitudDestino = longitudDestino;
    }

    @Exclude
    public LatLng getLatLng() {
        if (latitud == null || latitud.equals("")) return null;
        if (longitud == null || longitud.equals("")) return null;

        try {
            double lat = Double.parseDouble(latitud);
            double lng = Double.parseDouble(longitud);
            return new LatLng(lat, lng);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
