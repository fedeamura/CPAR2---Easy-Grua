package federico.amura.cp_grua.UI.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import federico.amura.cp_grua.R;

/**
 * Creado por federicoamura el 25/4/18.
 */

public class UtilesResources {
    private static UtilesResources instance;

    public static UtilesResources getInstance() {
        if (instance == null) {
            instance = new UtilesResources();
        }
        return instance;
    }

    private Bitmap bitmapMarcadorGrua;

    public Bitmap getIconoMarcadorGrua(@NonNull Context context) {
        if (bitmapMarcadorGrua != null) return bitmapMarcadorGrua;

        int height = (int) context.getResources().getDimension(R.dimen.marcador_w);
        int width = (int) context.getResources().getDimension(R.dimen.marcador_w);
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.marker_grua);
        Bitmap b = bitmapdraw.getBitmap();
        bitmapMarcadorGrua = Bitmap.createScaledBitmap(b, width, height, false);
        return bitmapMarcadorGrua;
    }

    private Bitmap bitmapMarcador;

    public Bitmap getIconoMarcador(@NonNull Context context) {
        if (bitmapMarcador != null) return bitmapMarcador;

        int height = (int) context.getResources().getDimension(R.dimen.marcador_w);
        int width = (int) context.getResources().getDimension(R.dimen.marcador_w);
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.marker);
        Bitmap b = bitmapdraw.getBitmap();
        bitmapMarcador = Bitmap.createScaledBitmap(b, width, height, false);
        return bitmapMarcador;
    }

    private Bitmap bitmapMarcadorUbicacion;

    public Bitmap getIconoMarcadorUbicacion(@NonNull Context context) {
        if (bitmapMarcadorUbicacion != null) return bitmapMarcadorUbicacion;

        int height = (int) context.getResources().getDimension(R.dimen.marcador_ubicacion_w);
        int width = (int) context.getResources().getDimension(R.dimen.marcador_ubicacion_w);
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.circulo);
        bitmapdraw.setAlpha(100);
        Bitmap b = bitmapdraw.getBitmap();

        bitmapMarcadorUbicacion = Bitmap.createScaledBitmap(b, width, height, false);
        return bitmapMarcadorUbicacion;
    }


}
