package federico.amura.cp_grua.UI.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import federico.amura.cp_grua.Model.Mensaje;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.Adapters.Adaptador_Chat;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Utils.UtilesMedidas;

public class ActivityChat extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Adaptador_Chat adaptador;
    private EditText mEditText;
    private View mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final String idGrua = getIntent().getStringExtra("idGrua");
        final String idUsuario = getIntent().getStringExtra("idUsuario");
        if (idGrua == null || idUsuario == null) {
            this.finish();
            return;
        }

        mRecyclerView = findViewById(R.id.listado);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mRecyclerView.setLayoutManager(lm);
        adaptador = new Adaptador_Chat();
        mRecyclerView.setAdapter(adaptador);

        mEditText = findViewById(R.id.input);

        mButton = findViewById(R.id.btn_EnviarMensaje);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contenido = mEditText.getText().toString().trim();
                if (contenido.equals("")) {
                    Toast.makeText(App.getInstance(), "Ingrese el contenido del mensaje", Toast.LENGTH_LONG).show();
                    mEditText.requestFocus();
                    return;
                }

                mEditText.setText("");
                mButton.setEnabled(false);
                String id = FirebaseFirestore.getInstance()
                        .collection("chat")
                        .document(idUsuario)
                        .collection(idGrua)
                        .document().getId();

                Mensaje mensaje = new Mensaje();
                mensaje.setId(id);
                mensaje.setContenido(contenido);
                mensaje.setCliente(App.getInstance().esUsuario());
                mensaje.setFecha(Calendar.getInstance().getTimeInMillis());

                FirebaseFirestore.getInstance()
                        .collection("chat")
                        .document(idUsuario)
                        .collection(idGrua)
                        .document(id).set(mensaje).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mButton.setEnabled(true);
                    }
                });

            }
        });

        FirebaseFirestore.getInstance()
                .collection("chat")
                .document(idUsuario)
                .collection(idGrua)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            adaptador.setItems(new ArrayList<Mensaje>());
                            return;
                        }

                        List<Mensaje> items = queryDocumentSnapshots.toObjects(Mensaje.class);
                        adaptador.setItems(items);
                    }
                });


        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                mRecyclerView.setPadding(
                        mRecyclerView.getPaddingLeft(),
                        mRecyclerView.getPaddingTop(),
                        mRecyclerView.getPaddingRight(),
                        (int) (mRecyclerView.getPaddingBottom() + UtilesMedidas.getInstance().convertDpToPixel(App.getInstance(), 72))
                );
                return false;
            }
        });

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityChat.this.finish();
            }
        });
    }


}
