package federico.amura.cp_grua.UI.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import federico.amura.cp_grua.DAO.Preferences.PrefLogin;
import federico.amura.cp_grua.Model.Grua;
import federico.amura.cp_grua.Model.Usuario;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Utils.UtilesUbicacion;

public class ActivityLogin extends AppCompatActivity {

    public static final int REQUEST_CODE_PERMISO_UBICACION = 99;

    private View mContenedor_Login;
    private View mContenedor_Permiso;
    private View mContenedor_Intro;
    private View mContenedor_BuscandoUbicacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (App.getInstance().isInit()) {
            this.finish();
            return;
        }
        initIntro();
        initPermiso();
        initBuscandoUbicacion();
        initLogin();

        mostrar(null);
        mostrar(mContenedor_Intro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!tienePermisoGPS()) {
                    mostrar(mContenedor_Permiso);
                } else {
                    pedirUbicacion();
                }
            }
        }, 1000);
    }

    private void initIntro() {
        mContenedor_Intro = findViewById(R.id.contenedor_Intro);
        mContenedor_Intro.setSoundEffectsEnabled(false);
        mContenedor_Intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initPermiso() {
        mContenedor_Permiso = findViewById(R.id.contenedor_PermisoUbicacion);
        mContenedor_Permiso.setSoundEffectsEnabled(false);
        mContenedor_Permiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_Permiso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirPermisoGPS();
            }
        });
    }

    private void initBuscandoUbicacion() {
        mContenedor_BuscandoUbicacion = findViewById(R.id.contenedor_BuscandoUbicacion);
        mContenedor_BuscandoUbicacion.setSoundEffectsEnabled(false);
        mContenedor_BuscandoUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    LatLng posicionActual;

    private void initLogin() {
        mContenedor_Login = findViewById(R.id.contenedor_Login);
        mContenedor_Login.setSoundEffectsEnabled(false);
        mContenedor_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final View mContenedor_Botones = mContenedor_Login.findViewById(R.id.contenedor_Botones);
        final View mContenedor_Usuario = mContenedor_Login.findViewById(R.id.contenedor_Usuario);
        final View mContenedor_Grua = mContenedor_Login.findViewById(R.id.contenedor_Grua);

        //Usuario
        mContenedor_Botones.findViewById(R.id.btn_Usuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContenedor_Botones.setVisibility(View.GONE);
                mContenedor_Usuario.setVisibility(View.VISIBLE);
            }
        });

        mContenedor_Usuario.findViewById(R.id.btn_VolverUsuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContenedor_Botones.setVisibility(View.VISIBLE);
                mContenedor_Usuario.setVisibility(View.GONE);
            }
        });

        mContenedor_Usuario.findViewById(R.id.btn_AccederUsuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mEditText = mContenedor_Usuario.findViewById(R.id.input_Email);
                final String email = mEditText.getText().toString().trim().toLowerCase();
                if (email.equals("")) {
                    Toast.makeText(App.getInstance(), "Inserte el email", Toast.LENGTH_SHORT).show();
                    mEditText.requestFocus();
                    return;
                }


                new MaterialDialog.Builder(ActivityLogin.this)
                        .progress(true, 0)
                        .content("Iniciando sesión")
                        .cancelable(false)
                        .showListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(final DialogInterface dialog) {
                                FirebaseFirestore.getInstance().collection("usuario").whereEqualTo("email", email).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            return;
                                        }

                                        List<Usuario> docs = queryDocumentSnapshots.toObjects(Usuario.class);

                                        //Ya existe
                                        if (docs.size() > 0) {
                                            PrefLogin.getInstance().setLoginCliente(App.getInstance(), docs.get(0).getId());
                                            App.getInstance().setUsuario(docs.get(0));
                                            App.getInstance().setPosicionActual(posicionActual);
                                            ActivityLogin.this.finish();
                                            dialog.dismiss();
                                            return;
                                        }

                                        //No existe

                                        String id = FirebaseFirestore.getInstance().collection("usuario").document().getId();

                                        final Usuario usuario = new Usuario();
                                        usuario.setEmail(email);
                                        usuario.setId(id);

                                        FirebaseFirestore.getInstance().collection("usuario")
                                                .document(id)
                                                .set(usuario)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        PrefLogin.getInstance().setLoginCliente(App.getInstance(), usuario.getId());
                                                        App.getInstance().setUsuario(usuario);
                                                        App.getInstance().setPosicionActual(posicionActual);
                                                        ActivityLogin.this.finish();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .show();
            }
        });

        //Grua
        mContenedor_Botones.findViewById(R.id.btn_Grua).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContenedor_Botones.setVisibility(View.GONE);
                mContenedor_Grua.setVisibility(View.VISIBLE);
            }
        });

        mContenedor_Grua.findViewById(R.id.btn_VolverGrua).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContenedor_Botones.setVisibility(View.VISIBLE);
                mContenedor_Grua.setVisibility(View.GONE);
            }
        });


        mContenedor_Grua.findViewById(R.id.btn_AccederGrua).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mEditText = mContenedor_Grua.findViewById(R.id.input_Patente);
                final String patente = mEditText.getText().toString().trim().toLowerCase();
                if (patente.equals("")) {
                    mEditText.requestFocus();
                    Toast.makeText(App.getInstance(), "Ingrese la patenten", Toast.LENGTH_LONG).show();
                    return;
                }

                new MaterialDialog.Builder(ActivityLogin.this)
                        .progress(true, 0)
                        .content("Iniciando sesión")
                        .cancelable(false)
                        .showListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(final DialogInterface dialog) {
                                FirebaseFirestore.getInstance().collection("grua").whereEqualTo("patente", patente).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            return;
                                        }

                                        List<Grua> docs = queryDocumentSnapshots.toObjects(Grua.class);

                                        //Ya existe
                                        if (docs.size() > 0) {
                                            PrefLogin.getInstance().setLoginGrua(App.getInstance(), docs.get(0).getId());
                                            App.getInstance().setGrua(docs.get(0));
                                            App.getInstance().setPosicionActual(posicionActual);
                                            ActivityLogin.this.finish();
                                            dialog.dismiss();
                                            return;
                                        }

                                        //No existe
                                        String id = FirebaseFirestore.getInstance().collection("grua").document().getId();
                                        final Grua grua = new Grua();
                                        grua.setId(id);
                                        grua.setPatente(patente);
                                        grua.setLatitud(posicionActual.latitude + "");
                                        grua.setLatitud(posicionActual.longitude + "");
                                        grua.setLibre(true);

                                        FirebaseFirestore.getInstance().collection("grua")
                                                .document(id)
                                                .set(grua)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        PrefLogin.getInstance().setLoginGrua(App.getInstance(), grua.getId());
                                                        App.getInstance().setGrua(grua);
                                                        App.getInstance().setPosicionActual(posicionActual);
                                                        ActivityLogin.this.finish();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(App.getInstance(), "Error procesando la solicitud", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .show();

            }
        });
    }

    public boolean tienePermisoGPS() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void pedirPermisoGPS() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISO_UBICACION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISO_UBICACION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        pedirUbicacion();
                    } else {
                        informarErrorGPS();
                    }
                } else {
                    informarErrorGPS();
                }
            }

        }
    }

    private void pedirUbicacion() {
        mostrar(mContenedor_BuscandoUbicacion);
        UtilesUbicacion.getInstance().pedirUbicacionInicial(this, new UtilesUbicacion.OnReady() {
            @Override
            public void onLocation(final LatLng latLng) {
                posicionActual = latLng;
                validarLogin();
            }

            @Override
            public void onError() {
                informarErrorGPS();
            }
        });
    }

    private void validarLogin() {
        if (PrefLogin.getInstance().getId(this) == null) {
            mostrar(mContenedor_Login);
            return;
        }


        mostrar(null);
        //Busco el usuario
        if (PrefLogin.getInstance().getEsCliente(App.getInstance())) {
            FirebaseFirestore.getInstance().collection("usuario")
                    .document(PrefLogin.getInstance().getId(App.getInstance()))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()) {
                                mostrar(mContenedor_Login);
                                return;
                            }

                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                            if (usuario == null) {
                                mostrar(mContenedor_Login);
                                return;
                            }

                            //Guardo y cierro
                            App.getInstance().setPosicionActual(posicionActual);
                            App.getInstance().setUsuario(usuario);
                            ActivityLogin.this.finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mostrar(mContenedor_Login);
                        }
                    });
        }
        //Busco la grua
        else {
            FirebaseFirestore.getInstance().collection("grua")
                    .document(PrefLogin.getInstance().getId(App.getInstance()))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()) {
                                mostrar(mContenedor_Login);
                                return;
                            }

                            Grua grua = documentSnapshot.toObject(Grua.class);
                            if (grua == null) {
                                mostrar(mContenedor_Login);
                                return;
                            }

                            //Guardo y cierro
                            App.getInstance().setPosicionActual(posicionActual);
                            App.getInstance().setGrua(grua);
                            ActivityLogin.this.finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mostrar(mContenedor_Login);
                        }
                    });
        }
    }

    private void informarErrorGPS() {
        new AlertDialog.Builder(this)
                .setTitle("Error obteniendo su ubicación")
                .setMessage("La aplicación no pudo encontrar su ubicacion actual. Por lo que no puede funcionar.")
                .setCancelable(false)
                .setPositiveButton("Cerrar aplicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityLogin.this.finish();
                    }
                })
                .create()
                .show();
    }

    private void mostrar(View view) {
        mContenedor_BuscandoUbicacion.setVisibility(View.GONE);
        mContenedor_Login.setVisibility(View.GONE);
        mContenedor_Intro.setVisibility(View.GONE);
        mContenedor_Permiso.setVisibility(View.GONE);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
