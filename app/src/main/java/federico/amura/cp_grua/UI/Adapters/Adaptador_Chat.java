package federico.amura.cp_grua.UI.Adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import federico.amura.cp_grua.Model.Mensaje;
import federico.amura.cp_grua.R;
import federico.amura.cp_grua.UI.App;
import federico.amura.cp_grua.UI.Utils.UtilesMedidas;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class Adaptador_Chat extends RecyclerView.Adapter<Adaptador_Chat.VH_Item> {

    private List<Mensaje> items;

    public Adaptador_Chat() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<Mensaje> items) {
        Collections.sort(items, new Comparator<Mensaje>() {
            @Override
            public int compare(Mensaje o1, Mensaje o2) {
                return Double.compare(o2.getFecha(), o1.getFecha());
            }
        });
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId().hashCode();
    }

    @NonNull
    @Override
    public VH_Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new VH_Item(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Item holder, int position) {
        Mensaje mensaje = this.items.get(position);

        //Mensaje
        holder.mTextView_Mensaje.setText(mensaje.getContenido());

        //Acomodo

        if (App.getInstance().esUsuario()) {
            if (!mensaje.isCliente()) {
                holder.mCardView.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.burbuja_otro, null));
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).leftMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 0);
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).rightMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 72);
            } else {
                holder.mCardView.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.burbuja_mia, null));
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).leftMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 72);
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).rightMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 0);
            }
        } else {
            if (mensaje.isCliente()) {
                holder.mCardView.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.burbuja_otro, null));
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).leftMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 0);
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).rightMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 72);
            } else {
                holder.mCardView.setCardBackgroundColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.burbuja_mia, null));
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).leftMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 72);
                ((ViewGroup.MarginLayoutParams) holder.mCardView.getLayoutParams()).rightMargin = (int) UtilesMedidas.getInstance().convertDpToPixel(holder.mCardView.getContext(), 0);
            }

        }
        holder.mCardView.requestLayout();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VH_Item extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView mTextView_Mensaje;

        public VH_Item(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card);
            mTextView_Mensaje = itemView.findViewById(R.id.textView_Mensaje);
        }
    }

}
