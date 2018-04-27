package federico.amura.cp_grua.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import federico.amura.cp_grua.DAO.Preferences.PrefLogin;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Fragments.Fragment_Grua;
import federico.amura.cp_grua.UI.Fragments.Fragment_Usuario;
import federico.amura.cp_grua.UI.Utils.UtilesMedidas;

public class ActivityPrincipal extends AppCompatActivity implements Fragment_Usuario.OnFragmentUsuario, Fragment_Grua.OnFragmentGrua {

    public static final int REQUEST_CODE_LOGIN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilesMedidas.getInstance().convertirTransparente(this);
        setContentView(R.layout.activity_principal);
        FirebaseFirestore.getInstance().setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());
        if (!App.getInstance().isInit()) {
            pedirLogin();
            return;
        }
        init();
    }


    private void pedirLogin() {
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN) {
            if (!App.getInstance().isInit()) {
                this.finish();
                return;
            }

            init();
        }
    }


    private void init() {
        Fragment frg = getSupportFragmentManager().findFragmentByTag("frg");
        if (frg != null) {
            getSupportFragmentManager().beginTransaction().remove(frg).commit();
        }
        if (App.getInstance().esUsuario()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, Fragment_Usuario.newInstance(), "frg").commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, Fragment_Grua.newInstance(), "frg").commit();
        }
    }

    @Override
    public void onBackPressed() {
        App.getInstance().clear();
        super.onBackPressed();
    }

    @Override
    public void cerrarSesion() {
        Fragment frg = getSupportFragmentManager().findFragmentByTag("frg");
        getSupportFragmentManager().beginTransaction().remove(frg).commit();

        App.getInstance().setUsuario(null);
        App.getInstance().setGrua(null);
        App.getInstance().setPosicionActual(null);
        PrefLogin.getInstance().clear(this);

        recreate();
    }

    @Override
    public void abrirChat(String idUsuario, String idGrua) {
        Intent intent = new Intent(this, ActivityChat.class);
        intent.putExtra("idUsuario", idUsuario);
        intent.putExtra("idGrua", idGrua);
        startActivity(intent);
    }
}
