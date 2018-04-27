package federico.amura.cp_grua.UI.Utils;

import android.content.Context;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import federico.amura.cp_grua.Model.Grua;
import federico.amura.cp_grua.Model.Usuario;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.App;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class UtilesGoogleMaps {
    private static UtilesGoogleMaps instance;

    public static UtilesGoogleMaps getInstance() {
        if (instance == null) {
            instance = new UtilesGoogleMaps();
        }
        return instance;
    }

    public double getDistancia(LatLng latLng1, LatLng latLng2) {
        float pk = (float) (180.f / Math.PI);

        float a1 = (float) (latLng1.latitude / pk);
        float a2 = (float) (latLng1.longitude / pk);
        float b1 = (float) (latLng2.latitude / pk);
        float b2 = (float) (latLng2.longitude / pk);

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    private List<Grua> gruasPorProcesar;
    private List<Grua> gruasProcesadas;

    public void procesarGruas(Context context, List<Grua> gruas, OnGruasProcesadas listener) {
        gruasPorProcesar = new ArrayList<>();
        gruasPorProcesar.addAll(gruas);

        gruasProcesadas = new ArrayList<>();
        procesarDatosGrua(context, listener);
    }


    public void procesarGrua(Context context, Grua grua, final OnGruaProcesada listener) {
        gruasPorProcesar = new ArrayList<>();
        gruasPorProcesar.add(grua);
        gruasProcesadas = new ArrayList<>();


        procesarDatosGrua(context, new OnGruasProcesadas() {
            @Override
            public void onReady(List<Grua> gruas) {
                listener.onReady(gruas.get(0));
            }

            @Override
            public void onError() {
                listener.onError();
            }
        });
    }


    private void procesarDatosGrua(final Context context, final OnGruasProcesadas listener) {
        if (gruasPorProcesar.size() == 0) {
            listener.onReady(gruasProcesadas);
            return;
        }

        final Grua grua = gruasPorProcesar.get(0);

        GoogleDirection.withServerKey(context.getString(R.string.navigation_api_key))
                .from(grua.getLatLng())
                .to(App.getInstance().getPosicionActual())
                .departureTime("now")
                .language("es")
                .unit("metric")
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {

                        if (!direction.isOK()) {
                            listener.onError();
                            return;
                        }

                        try {

                            grua.setDistancia(direction.getRouteList().get(0).getLegList().get(0).getDistance().getText());
                            grua.setDistanciaVal(Double.parseDouble(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()));
                            grua.setDuracion(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());
                            grua.setDuracionVal(Double.parseDouble(direction.getRouteList().get(0).getLegList().get(0).getDuration().getValue()));
                            grua.setPuntos(direction.getRouteList().get(0).getOverviewPolyline().getPointList());
                            gruasProcesadas.add(grua);

                            //Quito la que ya procese
                            gruasPorProcesar.remove(0);

                            //Sigo procesando
                            procesarDatosGrua(context, listener);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            listener.onError();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        listener.onError();
                    }
                });
    }

    public interface OnGruasProcesadas {
        void onReady(List<Grua> gruas);

        void onError();
    }

    public interface OnGruaProcesada {
        void onReady(Grua grua);

        void onError();
    }


    public void procesarUsuario(final Context context, final Usuario usuario, final OnUsuarioProcesado listener) {
        try {
            GoogleDirection.withServerKey(context.getString(R.string.navigation_api_key))
                    .from(App.getInstance().getPosicionActual())
                    .to(new LatLng(Double.parseDouble(usuario.getLatitud()), Double.parseDouble(usuario.getLongitud())))
                    .departureTime("now")
                    .language("es")
                    .unit("metric")
                    .transportMode(TransportMode.DRIVING)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {

                            if (!direction.isOK()) {
                                listener.onError();
                                return;
                            }

                            try {

                                usuario.setDistancia(direction.getRouteList().get(0).getLegList().get(0).getDistance().getText());
                                usuario.setDistanciaVal(Double.parseDouble(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()));
                                usuario.setDuracion(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());
                                usuario.setDuracionVal(Double.parseDouble(direction.getRouteList().get(0).getLegList().get(0).getDuration().getValue()));
                                usuario.setPuntos(direction.getRouteList().get(0).getOverviewPolyline().getPointList());
                                listener.onReady(usuario);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                listener.onError();
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            listener.onError();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnUsuarioProcesado {
        void onReady(Usuario usuario);

        void onError();
    }


}
