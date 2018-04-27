package federico.amura.cp_grua.UI.Utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.constants.ProviderType;


import federico.amura.cp_grua.UI.App;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class UtilesUbicacion {
    private static UtilesUbicacion instance;

    public static UtilesUbicacion getInstance() {
        if (instance == null) {
            instance = new UtilesUbicacion();
        }
        return instance;
    }

    public interface OnReady {
        void onLocation(LatLng latLng);

        void onError();
    }

    public LocationListener pedirUbicacionInicial(Context context, final OnReady listener) {
        return pedirUbicacion(context, listener, true);
    }

    public LocationListener escucharUbicacion(Context context, final OnReady listener) {
        return pedirUbicacion(context, listener, false);
    }

    private LocationListener pedirUbicacion(Context context, final OnReady listener, boolean unaVez) {
        LocationRequest request;
        if (unaVez) {
            request = LocationRequest.create() //standard GMS LocationRequest
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setNumUpdates(1)
                    .setInterval(100);
        } else {
            request = LocationRequest.create() //standard GMS LocationRequest
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(100);
        }

        final LocationConfiguration awesomeConfiguration = new LocationConfiguration.Builder()
                .keepTracking(!unaVez)
                .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder()
                        .locationRequest(request)
                        .fallbackToDefault(true)
                        .askForGooglePlayServices(false)
                        .askForSettingsApi(true)
                        .failOnConnectionSuspended(true)
                        .failOnSettingsApiSuspended(false)
                        .ignoreLastKnowLocation(false)
                        .setWaitPeriod(20 * 1000)
                        .build())
                .useDefaultProviders(new DefaultProviderConfiguration.Builder()
                        .requiredTimeInterval(5 * 60 * 1000)
                        .requiredDistanceInterval(0)
                        .acceptableAccuracy(5.0f)
                        .acceptableTimePeriod(5 * 60 * 1000)
                        .gpsMessage("Turn on GPS?")
                        .setWaitPeriod(ProviderType.GPS, 20 * 1000)
                        .setWaitPeriod(ProviderType.NETWORK, 20 * 1000)
                        .build())
                .build();


        LocationManager awesomeLocationManager =  new LocationManager.Builder(App.getInstance().getApplicationContext())
                .configuration(awesomeConfiguration)
                .notify(new LocationBaseActivity() {
                    @Override
                    public LocationConfiguration getLocationConfiguration() {
                        return awesomeConfiguration;
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        listener.onLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }

                    @Override
                    public void onLocationFailed(int type) {
                        listener.onError();
                    }
                })
                .build();

        awesomeLocationManager.get();

//
//        @SuppressLint("MissingPermission")
//        Disposable a = locationProvider.getUpdatedLocation(request)
//                .subscribe(new Consumer<Location>() {
//                    @Override
//                    public void accept(Location location) throws Exception {
//                        listener.onLocation(new LatLng(location.getLatitude(), location.getLongitude()));
//                    }
//                });

//        if (unaVez) {
//            SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
//                @Override
//                public void onLocationUpdated(Location location) {
//                    listener.onLocation(new LatLng(location.getLatitude(), location.getLongitude()));
//                }
//            });
//        } else {
//            SmartLocation.with(context).location().start(new OnLocationUpdatedListener() {
//                @Override
//                public void onLocationUpdated(Location location) {
//                    listener.onLocation(new LatLng(location.getLatitude(), location.getLongitude()));
//                }
//            });
//        }
//
        return null;
//        LocationListener locationListener = new LocationListener() {
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.d("Ubicacion", "Status change " + provider + " " + status + " " + extras.toString());
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.d("Ubicacion", "Provider encabled " + provider);
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.d("Ubicacion", "disabled");
//            }
//
//            @Override
//            public void onLocationChanged(Location location) {
//                Log.d("Ubicacion", "Changed");
//                //Guardo la posicion actual
//                LatLng posicionActual = new LatLng(location.getLatitude(), location.getLongitude());
//                listener.onLocation(posicionActual);
//            }
//        };
//
//        long minTime = 10 * 60 * 1000;
//        long minDistance = 0;
//
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager == null) {
//            listener.onError();
//            return null;
//        }
//
//        //Creo solicitud
//        Criteria crit = new Criteria();
//        crit.setAccuracy(Criteria.ACCURACY_FINE);
//        crit.setPowerRequirement(Criteria.POWER_HIGH);
//        String provider = locationManager.getBestProvider(crit, true);
//        if (provider == null) {
//            listener.onError();
//            return null;
//        }
//
//        //Valido permiso
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            listener.onError();
//            return null;
//        }
//
//        //Mando
//        if (unaVez) {
//            locationManager.requestSingleUpdate(provider, locationListener, null);
//        } else {
//            locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener);
//        }
//
//        return locationListener;
    }

    public void dejarDePedirUbicacion(Context context, LocationListener listener) {
        try {
//            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            if (locationManager == null) return;
//            if (listener == null) return;
//            locationManager.removeUpdates(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
