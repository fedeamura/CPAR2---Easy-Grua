package federico.amura.cp_grua.UI.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import federico.amura.cp_grua.DAO.Preferences.PrefAjustes;
import federico.amura.cp_grua.DAO.Preferences.PrefLogin;
import federico.amura.cp_grua.Model.Grua;
import federico.amura.cp_grua.Model.Usuario;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.Activities.ActivityChat;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Utils.UtilesGoogleMaps;
import federico.amura.cp_grua.UI.Utils.UtilesMedidas;
import federico.amura.cp_grua.UI.Utils.UtilesResources;
import federico.amura.cp_grua.UI.Utils.UtilesUbicacion;

/**
 * Creado por federicoamura el 26/4/18.
 */

public class Fragment_Grua extends Fragment {

    public static Fragment_Grua newInstance() {
        Fragment_Grua frg = new Fragment_Grua();
        return frg;
    }

    private static final String TAG = "FRAGMENT_GRUA";
    private static final int MARKER_W = 56;

    private View mContenedor_Contenido;
    private View mContenedor_Asistencia;

    //Contenedor
    private GoogleMap map;
    private LocationListener locationListener;

    //Asistencia
    private View mButton_Mensajes;
    private View mContenedor_AsistenciaCargando;
    private View mButton_CompletarAsistencia;

    private Polyline mapa_Poligono;
    private Marker mapa_MarkerPosicion;
    private Marker mapa_MarkerUsuario;

//    private MapRipple mapRipple;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grua, container, false);
        init();
        return view;
    }


    private void init() {
        initContenedorContenido();
        initContenedorAsistencia();
        initBotones();
    }

    private void initContenedorContenido() {
        mContenedor_Contenido = view.findViewById(R.id.contenedor_Contenido);
        initMap();
    }

    private void initContenedorAsistencia() {
        mContenedor_Asistencia = view.findViewById(R.id.contenedor_Asistencia);
        mContenedor_Asistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mContenedor_Asistencia.setSoundEffectsEnabled(false);

        //Mensaje
        mButton_Mensajes = mContenedor_Asistencia.findViewById(R.id.btn_EnviarMensaje);
        mButton_Mensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.abrirChat(App.getInstance().getGrua().getIdUsuario(), PrefLogin.getInstance().getId(getActivity()));
            }
        });

        //Completar
        mButton_CompletarAsistencia = mContenedor_Asistencia.findViewById(R.id.btn_MarcarCompleto);
        mButton_CompletarAsistencia.setVisibility(View.GONE);
        mButton_CompletarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> update = new HashMap<>();
                update.put("libre", true);
                update.put("idUsuario", null);
                update.put("latitudDestino", null);
                update.put("longitudDestino", null);

                mContenedor_AsistenciaCargando.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection("grua").document(App.getInstance().getGrua().getId()).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mContenedor_AsistenciaCargando.setVisibility(View.GONE);
                    }
                });
            }
        });


        //Inicialmente lo pongo oculto
        mContenedor_Asistencia.setVisibility(View.GONE);
        mContenedor_Asistencia.setTranslationY(-UtilesMedidas.getInstance().getNavigationBarHeight(getActivity()));

        //Cargando
        mContenedor_AsistenciaCargando = mContenedor_Asistencia.findViewById(R.id.contenedor_Cargando);
        mContenedor_AsistenciaCargando.setSoundEffectsEnabled(false);
        mContenedor_AsistenciaCargando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initBotones() {
        //Ajustes
        mContenedor_Contenido.findViewById(R.id.imagen_BtnAjustes).setTranslationY(UtilesMedidas.getInstance().getStatusBarHeight(getActivity()));
        mContenedor_Contenido.findViewById(R.id.btn_Ajustes).setTranslationY(UtilesMedidas.getInstance().getStatusBarHeight(getActivity()));
        mContenedor_Contenido.findViewById(R.id.btn_Ajustes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("Opciones")
                        .items("Cerrar sesion")
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    case 0: {
                                        listener.cerrarSesion();
                                    }
                                    break;
                                }
                                return false;
                            }
                        })
                        .show();
            }
        });

        //Centrar
        mContenedor_Contenido.findViewById(R.id.imagen_BtnCerrar).setTranslationY(UtilesMedidas.getInstance().getStatusBarHeight(getActivity()));
        mContenedor_Contenido.findViewById(R.id.btn_Centrar).setTranslationY(UtilesMedidas.getInstance().getStatusBarHeight(getActivity()));
        mContenedor_Contenido.findViewById(R.id.btn_Centrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.newLatLng(App.getInstance().getPosicionActual()));
            }
        });
    }

    private void initMap() {
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (getActivity() == null) return;

                onMap(googleMap);
            }
        });
    }

    ListenerRegistration listenerGrua;

    @SuppressLint("MissingPermission")
    private void onMap(final GoogleMap map) {
        this.map = map;
        map.getUiSettings().setZoomControlsEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(App.getInstance().getPosicionActual(), 15));
        marcarUbicacionActual();


        listenerGrua = FirebaseFirestore.getInstance()
                .collection("grua")
                .document(PrefLogin.getInstance().getId(getActivity()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        final Grua grua = documentSnapshot.toObject(Grua.class);
                        if (grua == null) {
                            listener.cerrarSesion();
                            return;
                        }

                        App.getInstance().setGrua(grua);
                        App.getInstance().setPosicionActual(new LatLng(Double.parseDouble(grua.getLatitud()), Double.parseDouble(grua.getLongitud())));

                        //Asistencia
                        if (!grua.isLibre()) {
                            mContenedor_Asistencia.setVisibility(View.VISIBLE);

                            if (grua.getIdUsuario() == null || grua.getLatitudDestino() == null || grua.getLongitudDestino() == null) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                                //Cancelar
                                return;
                            }

                            final LatLng posDestino = new LatLng(Double.parseDouble(grua.getLatitudDestino()), Double.parseDouble(grua.getLongitudDestino()));

                            Usuario usuario = new Usuario();
                            usuario.setId(grua.getIdUsuario());
                            usuario.setLatitud(grua.getLatitudDestino());
                            usuario.setLongitud(grua.getLongitudDestino());


                            //Si la distancia es menor a 50 metros muestro el cancelar
                            double d = UtilesGoogleMaps.getInstance().getDistancia(App.getInstance().getPosicionActual(), posDestino);
                            if (d < 50) {
                                mButton_CompletarAsistencia.setVisibility(View.VISIBLE);
                            } else {
                                mButton_CompletarAsistencia.setVisibility(View.GONE);
                            }

                            if (d > 50) {

                                UtilesGoogleMaps.getInstance().procesarUsuario(getActivity(), usuario, new UtilesGoogleMaps.OnUsuarioProcesado() {
                                    @Override
                                    public void onReady(Usuario usuario) {
                                        ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Distancia)).setText(usuario.getDistancia());
                                        ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Tiempo)).setText(usuario.getDuracion());

                                        //Poligono
                                        if (mapa_Poligono != null) {
                                            mapa_Poligono.remove();
                                            mapa_Poligono = null;
                                        }

                                        if (usuario.getPuntos() != null) {
                                            //Creo el bounds para mostrar toda la ruta
                                            LatLngBounds.Builder bounds = LatLngBounds.builder();
                                            bounds.include(App.getInstance().getPosicionActual());
                                            bounds.include(posDestino);

                                            for (LatLng p : usuario.getPuntos()) {
                                                bounds.include(p);
                                            }

                                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));

                                            mapa_Poligono = map.addPolyline(
                                                    new PolylineOptions()
                                                            .addAll(usuario.getPuntos())
                                                            .width(UtilesMedidas.getInstance().convertDpToPixel(App.getInstance(), 8))
                                                            .color(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                                            );

                                        }

                                    }

                                    @Override
                                    public void onError() {
                                        ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Distancia)).setText("Sin datos");
                                        ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Tiempo)).setText("Sin datos");
                                    }
                                });
                            } else {
                                //Poligono
                                if (mapa_Poligono != null) {
                                    mapa_Poligono.remove();
                                    mapa_Poligono = null;
                                }

                                ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Distancia)).setText("A menos de 50m");
                                ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Tiempo)).setText("");
                            }


                            //Marcador usuario
                            if (mapa_MarkerUsuario != null) {
                                mapa_MarkerUsuario.setPosition(posDestino);
                            } else {
                                mapa_MarkerUsuario = map.addMarker(
                                        new MarkerOptions()
                                                .position(posDestino)
                                                .icon(BitmapDescriptorFactory.fromBitmap(UtilesResources.getInstance().getIconoMarcador(getActivity())))
                                                .title("Ubicación del usuario")
                                );
                            }


                            //Marcador mio
                            if (mapa_MarkerPosicion == null) {
                                mapa_MarkerPosicion = map.addMarker(new MarkerOptions()
                                        .position(App.getInstance().getPosicionActual())
                                        .icon(BitmapDescriptorFactory.fromBitmap(UtilesResources.getInstance().getIconoMarcadorUbicacion(getActivity())))
                                        .title("Ubicacion actual")
                                );
                            } else {
                                mapa_MarkerPosicion.setPosition(App.getInstance().getPosicionActual());
                            }

                            //Padding
                            int statusBar = UtilesMedidas.getInstance().getStatusBarHeight(getActivity());
                            int navigation = UtilesMedidas.getInstance().getNavigationBarHeight(getActivity());

                            int padding = 8;
                            map.setPadding(
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + MARKER_W) + statusBar),
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + 256) + navigation)
                            );

                        } else {
                            if (mapa_MarkerUsuario != null) {
                                mapa_MarkerUsuario.remove();
                                mapa_MarkerUsuario = null;
                            }

                            mContenedor_Asistencia.setVisibility(View.GONE);

                            //Poligono
                            if (mapa_Poligono != null) {
                                mapa_Poligono.remove();
                                mapa_Poligono = null;
                            }

                            int statusBar = UtilesMedidas.getInstance().getStatusBarHeight(getActivity());
                            int navigation = UtilesMedidas.getInstance().getNavigationBarHeight(getActivity());

                            //Padding mapa
                            int padding = 8;
                            map.setPadding(
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + MARKER_W) + statusBar),
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                                    (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding) + navigation)
                            );


                            //Centro
                            map.animateCamera(CameraUpdateFactory.newLatLng(App.getInstance().getPosicionActual()));
                        }


                    }
                });

        escucharUbicacionActual();
    }


    //Ubicacion

    private void escucharUbicacionActual() {
        if (locationListener == null) {
            UtilesUbicacion.getInstance().dejarDePedirUbicacion(getActivity(), locationListener);
        }

        locationListener = UtilesUbicacion.getInstance().escucharUbicacion(getActivity(), new UtilesUbicacion.OnReady() {
            @Override
            public void onLocation(LatLng latLng) {
                if (getActivity() == null) return;

                App.getInstance().setPosicionActual(latLng);

                //Marco la posicion
                marcarUbicacionActual();
            }

            @Override
            public void onError() {
                Toast.makeText(getActivity(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                listener.cerrarSesion();
            }
        });
    }

    private void marcarUbicacionActual() {
        if (getActivity() == null) return;

        if (mapa_MarkerPosicion == null) {
            mapa_MarkerPosicion = map.addMarker(new MarkerOptions()
                    .position(App.getInstance().getPosicionActual())
                    .icon(BitmapDescriptorFactory.fromBitmap(UtilesResources.getInstance().getIconoMarcadorUbicacion(getActivity())))
                    .title("Ubicacion actual")
            );
        } else {
            mapa_MarkerPosicion.setPosition(App.getInstance().getPosicionActual());
        }

        //Actualizo
        Map<String, Object> updates = new HashMap<>();
        updates.put("latitud", App.getInstance().getPosicionActual().latitude + "");
        updates.put("longitud", App.getInstance().getPosicionActual().longitude + "");
        FirebaseFirestore.getInstance().collection("grua").document(PrefLogin.getInstance().getId(getActivity())).update(updates);
    }


    private OnFragmentGrua listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnFragmentGrua) activity;
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    public interface OnFragmentGrua {
        void cerrarSesion();

        void abrirChat(String idUsuario, String idGrua);
    }

}
