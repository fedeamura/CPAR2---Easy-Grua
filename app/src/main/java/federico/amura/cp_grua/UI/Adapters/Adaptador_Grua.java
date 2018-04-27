package federico.amura.cp_grua.UI.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import federico.amura.cp_grua.Model.Grua;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Utils.UtilesGoogleMaps;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class Adaptador_Grua extends RecyclerView.Adapter<Adaptador_Grua.VH_Item> {

    private List<Grua> gruas;

    public Adaptador_Grua(List<Grua> gruas) {
        this.gruas = gruas;
    }

    @NonNull
    @Override
    public VH_Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grua, parent, false);
        return new VH_Item(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Item holder, int position) {
        LatLng latLng = gruas.get(position).getLatLng();
        if (latLng == null || App.getInstance().getPosicionActual() == null) {
            holder.mTextView_Distancia.setText("");
        } else {
            double distancia = UtilesGoogleMaps.getInstance().getDistancia(App.getInstance().getPosicionActual(), latLng);
            holder.mTextView_Distancia.setText("" + distancia + "m");
        }

    }

    @Override
    public int getItemCount() {
        return gruas.size();
    }

    public class VH_Item extends RecyclerView.ViewHolder {

        TextView mTextView_Distancia;

        public VH_Item(View itemView) {
            super(itemView);
            mTextView_Distancia = itemView.findViewById(R.id.textView_Tiempo);
        }
    }

}
