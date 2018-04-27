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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Utils.UtilesGoogleMaps;
import federico.amura.cp_grua.UI.Utils.UtilesMedidas;
import federico.amura.cp_grua.UI.Utils.UtilesResources;
import federico.amura.cp_grua.UI.Utils.UtilesUbicacion;

/**
 * Creado por federicoamura el 26/4/18.
 */

public class Fragment_Usuario extends Fragment {

    public static Fragment_Usuario newInstance() {
        return new Fragment_Usuario();
    }


    private static final String TAG = "TAG";
    private static final int MARKER_W = 56;

    private View mContenedor;
    private View mContenedor_Contenido;
    private View mContenedor_Asistencia;

    //Contenedor
    private GoogleMap map;
    private LocationListener locationListener;
    private View mButton_SolicitarAsistencia;

    //Asistencia
    private View mButton_CancelarAsistencia;
    private View mButton_Mensajes;
    private View mContenedor_AsistenciaCargando;
    private ViewGroup mContenedor_MasOpciones;
    private ViewGroup mContenedor_Opciones;

    private List<Grua> gruas;
    private List<Grua> gruasElegibles;
    private Polyline mapa_Poligono;
    private Marker mapa_MarkerPosicion;
    private List<Marker> mapa_MarkersGruas;
    private Marker mapa_MarkerGruaAsistencia;

    private boolean modoEspera = true;
    private Grua gruaSiguiendo = null;
    private List<Grua> gruasOpciones = null;
    private boolean buscandoGruaCercana = false;

    private ListenerRegistration listenerBuscarGruas;
    private ListenerRegistration listenerBuscarGruaAsistencia;

//    MapRipple mapRipple;

    private boolean primerInicio = true;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usuario, container, false);
        init();
        return view;
    }


    private void init() {
        mContenedor = view.findViewById(R.id.contenedor);

        initContenedorContenido();

        initContenedorAsistencia();
        initBotones();
    }

    private void initContenedorContenido() {
        mContenedor_Contenido = view.findViewById(R.id.contenedor_Contenido);

        initMap();

        //Solicitar asistencia
        mButton_SolicitarAsistencia = view.findViewById(R.id.btn_SolicitarAsistencia);
        mButton_SolicitarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setModoAsistencia();
            }
        });

        if (UtilesMedidas.getInstance().isNavigationBarTransparente(getActivity())) {
            mButton_SolicitarAsistencia.setTranslationY(-UtilesMedidas.getInstance().getNavigationBarHeight(getActivity()));
        }
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

                listener.abrirChat(PrefLogin.getInstance().getId(App.getInstance()), gruaSiguiendo.getId());
            }
        });

        //Cancelar
        mButton_CancelarAsistencia = view.findViewById(R.id.btn_CancelarAsistencia);
        mButton_CancelarAsistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarAsistencia();
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

        mContenedor_MasOpciones = mContenedor_Asistencia.findViewById(R.id.contenedor_MasOpciones);
        mContenedor_Opciones = mContenedor_Asistencia.findViewById(R.id.contenedor_Opciones);
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

    @SuppressLint("MissingPermission")
    private void onMap(final GoogleMap map) {
        this.map = map;
        map.getUiSettings().setZoomControlsEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(App.getInstance().getPosicionActual(), 15));
        marcarUbicacionActual();

        escucharUbicacionActual();

        setModoEspera();
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

                //Filtrar
                filtrarGruas();

                //Dibujo las gruas
                dibujarGruas();
            }

            @Override
            public void onError() {

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
    }


    //Modo espera

    private boolean centrarPrimeraVezGruas = false;

    private void setModoEspera() {
        modoEspera = true;
        centrarPrimeraVezGruas = true;

        dejarDeBuscarGruas();
        dejarDeBuscarGruaAsistencia();

        //Quito poligono
        if (mapa_Poligono != null) {
            mapa_Poligono.remove();
            mapa_Poligono = null;
        }

        //Quito el marcador de asistencia
        if (mapa_MarkerGruaAsistencia != null) {
            mapa_MarkerGruaAsistencia.remove();
            mapa_MarkerGruaAsistencia = null;
        }

        //Oculto el panel de asistencia
        mContenedor_Asistencia.setVisibility(View.GONE);

        //Busco gruas
        buscarGruas();

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

    }

    private void buscarGruas() {
        dejarDeBuscarGruas();

        if (!modoEspera) return;

        //Busco en la db las gruas
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query docRef = db.collection("grua");
        listenerBuscarGruas = docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (!modoEspera) return;

                if (e != null) {
                    Log.w(TAG, "Error");
                    return;
                }

                if (queryDocumentSnapshots == null) {
                    Log.w(TAG, "Vacio");
                    return;
                }

                try {

                    //Obtengo los items y los filtro
                    List<Grua> items = queryDocumentSnapshots.toObjects(Grua.class);
                    List<Grua> itemsValidos = new ArrayList<>();
                    for (Grua item : items) {
                        LatLng latLng = item.getLatLng();
                        if (latLng != null) {
                            itemsValidos.add(item);
                        }
                    }

                    //Los agrego al listado
                    gruas = new ArrayList<>();
                    gruas.addAll(itemsValidos);
                    if (gruaSiguiendo != null) {
                        gruas.add(gruaSiguiendo);
                    }
                    filtrarGruas();
                    dibujarGruas();


                    if (primerInicio) {
                        primerInicio = false;
                        FirebaseFirestore.getInstance()
                                .collection("grua")
                                .whereEqualTo("libre", false)
                                .whereEqualTo("idUsuario", PrefLogin.getInstance().getId(getActivity()))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (getActivity() == null) return;

                                        if (queryDocumentSnapshots.isEmpty()) {
                                            return;
                                        }
                                        List<Grua> gruasDB = queryDocumentSnapshots.toObjects(Grua.class);

                                        gruaSiguiendo = gruasDB.get(0);
                                        modoEspera = false;
                                        mContenedor_Asistencia.setVisibility(View.VISIBLE);
                                        mContenedor_AsistenciaCargando.setVisibility(View.VISIBLE);

                                        //Padding mapa
                                        int statusBar = UtilesMedidas.getInstance().getStatusBarHeight(getActivity());
                                        int navigation = UtilesMedidas.getInstance().getNavigationBarHeight(getActivity());

                                        int padding = 8;
                                        map.setPadding(
                                                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                                                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + MARKER_W) + statusBar),
                                                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                                                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + 256) + navigation)
                                        );

                                        dejarDeBuscarGruaAsistencia();
                                        dejarDeBuscarGruas();
                                        onGruaSeleccionada();
                                    }
                                });
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void dejarDeBuscarGruas() {
        if (listenerBuscarGruas != null) {
            listenerBuscarGruas.remove();
            listenerBuscarGruas = null;
        }

        borrarGruas();
    }

    private void filtrarGruas() {

        if (gruas == null) return;
        if (map == null) return;

        gruasElegibles = new ArrayList<>();
        for (Grua grua : gruas) {
            if (grua.isLibre()) {
                double distanciaEnKM = (UtilesGoogleMaps.getInstance().getDistancia(App.getInstance().getPosicionActual(), grua.getLatLng()) / 1000);
                if (distanciaEnKM < PrefAjustes.getInstance().getMaxDistancia(getActivity())) {
                    gruasElegibles.add(grua);
                }
            }
        }
    }

    private void dibujarGruas() {
        if (map == null) return;
        if (!modoEspera) return;

        borrarGruas();

        if (gruasElegibles == null || gruasElegibles.size() == 0) {
            return;
        }


        LatLngBounds.Builder bounds = LatLngBounds.builder();

        //Agrego sus marcadores
        for (Grua item : gruasElegibles) {
            LatLng latLng = item.getLatLng();
            Marker marcador = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(UtilesResources.getInstance().getIconoMarcadorGrua(getActivity())))
                    .title(item.getPatente())
            );
            marcador.setTag(item);
            mapa_MarkersGruas.add(marcador);
            bounds.include(latLng);
        }

        //Me incluyo
        bounds.include(App.getInstance().getPosicionActual());

        //Animo
        if (centrarPrimeraVezGruas) {
            centrarPrimeraVezGruas = false;
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));
        }

        if (mapa_MarkerPosicion != null) {
            mapa_MarkerPosicion.setZIndex(10000);
        }
    }

    private void borrarGruas() {
        if (map == null) return;
        if (mapa_MarkersGruas != null && !mapa_MarkersGruas.isEmpty()) {
            for (Marker marcador : mapa_MarkersGruas) {
                marcador.remove();
            }
        }
        mapa_MarkersGruas = new ArrayList<>();
    }


    //Modo asistencia

    private void setModoAsistencia() {
        if (map == null) return;

        if (gruas.size() == 0) {
            Toast.makeText(getActivity(), "No hay gruas disponibles", Toast.LENGTH_LONG).show();
            return;
        }

        modoEspera = false;


        //Dejo de buscar gruas
        dejarDeBuscarGruas();

        //Borro el poligono
        if (mapa_Poligono != null) {
            mapa_Poligono.remove();
        }

        //Dejo de buscar grua de asistencia
        dejarDeBuscarGruaAsistencia();

        //Estado
        buscandoGruaCercana = true;
        gruaSiguiendo = null;
        gruasOpciones = null;

        //Muestro el panel de asistencia
        mContenedor_Asistencia.setVisibility(View.VISIBLE);
        mContenedor_AsistenciaCargando.setVisibility(View.VISIBLE);

        //Padding mapa
        int statusBar = UtilesMedidas.getInstance().getStatusBarHeight(getActivity());
        int navigation = UtilesMedidas.getInstance().getNavigationBarHeight(getActivity());

        int padding = 8;
        map.setPadding(
                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + MARKER_W) + statusBar),
                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding * 2) + MARKER_W / 2),
                (int) (UtilesMedidas.getInstance().convertDpToPixel(getActivity(), padding + 256) + navigation)
        );

        //centro en mi ubicacion
        map.animateCamera(CameraUpdateFactory.newLatLng(App.getInstance().getPosicionActual()));

        //Armo el listado de gruas que quiero procesar
        ArrayList<Grua> gruasParaAnalizar = new ArrayList<>();
        gruasParaAnalizar.addAll(gruas);
        UtilesGoogleMaps.getInstance().procesarGruas(getActivity(), gruasParaAnalizar, new UtilesGoogleMaps.OnGruasProcesadas() {
            @Override
            public void onReady(List<Grua> gruasProcesadas) {
                if (getActivity() == null) return;

                if (gruasProcesadas.size() == 0) {
                    Toast.makeText(getActivity(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                    cancelarAsistencia();
                    return;
                }

                buscandoGruaCercana = false;

                //Busco la grua
                Grua gruaCercanaGeograficamente = null;

                for (Grua grua : gruas) {
                    double d = UtilesGoogleMaps.getInstance().getDistancia(App.getInstance().getPosicionActual(), grua.getLatLng());
                    if (d < 50) {
                        gruaCercanaGeograficamente = grua;
                        break;
                    }
                }

                //Si no hay una geograficamente cerca, elijo la que este mas cerca segun google
                if (gruaCercanaGeograficamente == null) {
                    gruaCercanaGeograficamente = gruasProcesadas.get(0);
                    for (Grua grua : gruasProcesadas) {
                        if (grua.getDistanciaVal() < gruaCercanaGeograficamente.getDistanciaVal()) {
                            gruaCercanaGeograficamente = grua;
                        }
                    }
                }

                //Tengo la grua
                gruaSiguiendo = gruaCercanaGeograficamente;
                gruasOpciones = gruasProcesadas;

                //Evento
                onGruaSeleccionada();
            }

            @Override
            public void onError() {
                if (getActivity() == null) return;

                Toast.makeText(App.getInstance(), "Algo salió mal al buscar la grua", Toast.LENGTH_LONG).show();
                setModoEspera();
            }
        });
    }

    private void onGruaSeleccionada() {
        //Le cambio estado
        Map<String, Object> updates = new HashMap<>();
        updates.put("libre", false);
        updates.put("latitudDestino", App.getInstance().getPosicionActual().latitude + "");
        updates.put("longitudDestino", App.getInstance().getPosicionActual().longitude + "");
        updates.put("idUsuario", PrefLogin.getInstance().getId(getActivity()));
        gruaSiguiendo.setLibre(false);
        FirebaseFirestore.getInstance().collection("grua").document(gruaSiguiendo.getId()).update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (getActivity() == null) return;

                        buscarGruaAsistencia();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (getActivity() == null) return;

                        Toast.makeText(App.getInstance(), "Error comunicandose con la grua", Toast.LENGTH_LONG).show();
                        setModoEspera();
                    }
                });


        //Cargo las opciones
        cargarOpciones(gruasOpciones);
    }

    private void buscarGruaAsistencia() {
        if (modoEspera) return;
        if (map == null) return;

        dejarDeBuscarGruaAsistencia();

        if (listenerBuscarGruaAsistencia != null) {
            listenerBuscarGruaAsistencia.remove();
        }

        mContenedor_AsistenciaCargando.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("grua").document(gruaSiguiendo.getId());
        listenerBuscarGruaAsistencia = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (getActivity() == null) return;

                if (modoEspera) return;

                if (e != null) {
                    Toast.makeText(App.getInstance(), "Error buscando la grua", Toast.LENGTH_LONG).show();
                    setModoEspera();
                    return;
                }

                if (!documentSnapshot.exists()) {
                    Toast.makeText(App.getInstance(), "Error buscando la grua", Toast.LENGTH_LONG).show();
                    setModoEspera();
                    return;
                }

                gruaSiguiendo = documentSnapshot.toObject(Grua.class);
                if (gruaSiguiendo == null) {
                    Toast.makeText(App.getInstance(), "Error buscando la grua", Toast.LENGTH_LONG).show();
                    setModoEspera();
                    return;
                }

                if (gruaSiguiendo.isLibre()) {
                    Toast.makeText(getActivity(), "Atencion completada", Toast.LENGTH_LONG).show();
                    dejarDeBuscarGruaAsistencia();
                    dejarDeBuscarGruas();
                    setModoEspera();
                    return;
                }

                double d = UtilesGoogleMaps.getInstance().getDistancia(App.getInstance().getPosicionActual(), gruaSiguiendo.getLatLng());
                if (d < 50) {
                    mContenedor_AsistenciaCargando.setVisibility(View.GONE);
                    cargarGruaAsistencia(gruaSiguiendo, true);
                    return;
                }

                UtilesGoogleMaps.getInstance().procesarGrua(App.getInstance(), gruaSiguiendo, new UtilesGoogleMaps.OnGruaProcesada() {
                    @Override
                    public void onReady(Grua grua) {
                        if (getActivity() == null) return;

                        mContenedor_AsistenciaCargando.setVisibility(View.GONE);
                        cargarGruaAsistencia(grua, false);
                    }

                    @Override
                    public void onError() {
                        if (getActivity() == null) return;

                        mContenedor_AsistenciaCargando.setVisibility(View.GONE);
                        Toast.makeText(App.getInstance(), "Algo salió mal al obtener los datos de la grua", Toast.LENGTH_LONG).show();
                        setModoEspera();
                    }
                });
            }
        });
    }

    private void dejarDeBuscarGruaAsistencia() {
        if (listenerBuscarGruaAsistencia != null) {
            listenerBuscarGruaAsistencia.remove();
        }

        if (mapa_MarkerGruaAsistencia != null) {
            mapa_MarkerGruaAsistencia.remove();
        }
    }

    private void cargarGruaAsistencia(Grua grua, boolean muyCercano) {
        ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Patente)).setText(grua.getPatente());

        if (muyCercano) {
            ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Distancia)).setText("A menos de 50m");
            ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Tiempo)).setText("");
        } else {
            ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Distancia)).setText(grua.getDistancia());
            ((TextView) mContenedor_Asistencia.findViewById(R.id.texto_Tiempo)).setText(grua.getDuracion());
        }

        if (mapa_MarkerGruaAsistencia == null) {
            mapa_MarkerGruaAsistencia = map.addMarker(
                    new MarkerOptions()
                            .title(grua.getPatente())
                            .position(grua.getLatLng())
                            .icon(BitmapDescriptorFactory.fromBitmap(UtilesResources.getInstance().getIconoMarcadorGrua(App.getInstance())))
            );

        } else {
            mapa_MarkerGruaAsistencia.setPosition(gruaSiguiendo.getLatLng());
        }


        //Creo el bounds para mostrar toda la ruta
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        bounds.include(App.getInstance().getPosicionActual());
        bounds.include(grua.getLatLng());

        //Dibujo el poligono
        if (mapa_Poligono != null) {
            mapa_Poligono.remove();
            mapa_Poligono = null;
        }
        if (grua.getPuntos() != null) {
            mapa_Poligono = map.addPolyline(
                    new PolylineOptions()
                            .addAll(grua.getPuntos())
                            .width(UtilesMedidas.getInstance().convertDpToPixel(App.getInstance(), 8))
                            .color(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
            );

            for (LatLng p : grua.getPuntos()) {
                bounds.include(p);
            }
        }

        //Animo para que se vea la ruta
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 0));
    }

    private void cargarOpciones(final List<Grua> opciones) {
        if (opciones == null || opciones.size() == 0) {
            mContenedor_MasOpciones.setVisibility(View.GONE);
        } else {
            mContenedor_MasOpciones.setVisibility(View.VISIBLE);

            mContenedor_Opciones.removeAllViews();
            for (Grua g : opciones) {
                if (!g.getId().equals(gruaSiguiendo.getId())) {
                    ViewGroup vg = (ViewGroup) LayoutInflater.from(mContenedor_Opciones.getContext()).inflate(R.layout.item_grua_opcion, mContenedor_Opciones, false);
                    vg.setTag(g.getId());
                    mContenedor_Opciones.addView(vg);

                    //Cargo
                    double d = UtilesGoogleMaps.getInstance().getDistancia(App.getInstance().getPosicionActual(), g.getLatLng());
                    if (d < 50) {
                        ((TextView) vg.findViewById(R.id.texto_Tiempo)).setText("Menos de 50m");
                        vg.findViewById(R.id.texto_Distancia).setVisibility(View.GONE);
                    } else {
                        ((TextView) vg.findViewById(R.id.texto_Tiempo)).setText(g.getDuracion());
                        vg.findViewById(R.id.texto_Distancia).setVisibility(View.VISIBLE);
                        ((TextView) vg.findViewById(R.id.texto_Distancia)).setText(g.getDistancia());
                    }

                    vg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String idActual = (String) v.getTag();

                            Grua gruaActual = null;
                            for (Grua gClick : gruas) {
                                if (gClick.getId().equals(idActual)) {
                                    gruaActual = gClick;
                                }
                            }

                            if (gruaActual == null) {
                                Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_SHORT).show();
                                setModoEspera();
                                return;
                            }

                            dejarDeBuscarGruas();
                            dejarDeBuscarGruaAsistencia();

                            //Quito el poligono
                            if (mapa_Poligono != null) {
                                mapa_Poligono.remove();
                                mapa_Poligono = null;
                            }

                            //Quito el marcador
                            if (mapa_MarkerGruaAsistencia != null) {
                                mapa_MarkerGruaAsistencia.remove();
                                mapa_MarkerGruaAsistencia = null;
                            }

                            //Centro en la posicion actual
                            map.animateCamera(CameraUpdateFactory.newLatLng(App.getInstance().getPosicionActual()));

                            //Cargo mientras pongo libre la actual y paso a la nueva
                            mContenedor_AsistenciaCargando.setVisibility(View.VISIBLE);
                            Map<String, Object> update = new HashMap<>();
                            update.put("libre", true);
                            update.put("latitudDestino", null);
                            update.put("longitudDestino", null);
                            update.put("idUsuario", null);
                            gruaSiguiendo.setLibre(true);
                            final Grua finalGruaActual = gruaActual;
                            FirebaseFirestore.getInstance().collection("grua").document(gruaSiguiendo.getId()).update(update)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseFirestore.getInstance().collection("grua").document(finalGruaActual.getId()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if (getActivity() == null) return;

                                                            if (!documentSnapshot.exists()) {
                                                                Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                                                                cancelarAsistencia();
                                                                return;
                                                            }

                                                            gruaSiguiendo = documentSnapshot.toObject(Grua.class);
                                                            if (gruaSiguiendo == null) {
                                                                Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                                                                cancelarAsistencia();
                                                                return;
                                                            }

                                                            if (!gruaSiguiendo.isLibre()) {
                                                                gruaSiguiendo = null;
                                                                Toast.makeText(App.getInstance(), "La grua no se encuentra libre", Toast.LENGTH_LONG).show();
                                                                cancelarAsistencia();
                                                                return;
                                                            }

                                                            onGruaSeleccionada();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            if (getActivity() == null) return;

                                                            Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                                                            cancelarAsistencia();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (getActivity() == null) return;

                                            Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                                            cancelarAsistencia();
                                        }
                                    });
                        }
                    });
                }
            }
            mContenedor_Opciones.requestLayout();
        }
    }

    private void cancelarAsistencia() {
        if (modoEspera) return;
        if (buscandoGruaCercana) return;

        dejarDeBuscarGruaAsistencia();
        dejarDeBuscarGruas();

        //Cambio el estado de la grua
        if (gruaSiguiendo == null) {
            setModoEspera();
            return;
        }


        Map<String, Object> updates = new HashMap<>();
        updates.put("libre", true);
        updates.put("latitudDestino", null);
        updates.put("longitudDestino", null);
        updates.put("idUsuario", null);
        gruaSiguiendo.setLibre(true);

        //Marco como libre
        mContenedor_AsistenciaCargando.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("grua").document(gruaSiguiendo.getId()).update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (getActivity() == null) return;

                        setModoEspera();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (getActivity() == null) return;

                        mContenedor_AsistenciaCargando.setVisibility(View.GONE);
                        Toast.makeText(App.getInstance(), "Algo salio mal al realizar la operación", Toast.LENGTH_LONG).show();
                    }
                });

    }


    private OnFragmentUsuario listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnFragmentUsuario) activity;
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    public interface OnFragmentUsuario {
        void cerrarSesion();

        void abrirChat(String idUsuario, String idGrua);
    }

}
