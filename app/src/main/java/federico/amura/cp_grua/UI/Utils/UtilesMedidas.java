package federico.amura.cp_grua.UI.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Creado por Federico Amura 27/4/17.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class UtilesMedidas {

    private static UtilesMedidas instance;

    public static UtilesMedidas getInstance() {
        if (instance == null) {
            instance = new UtilesMedidas();
        }
        return instance;
    }

    public int getStatusBarHeight(@NonNull Activity activity) {
        if (!isStatusBarTransparente(activity)) return 0;
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public float convertDpToPixel(@NonNull Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public float convertPixelsToDp(@NonNull Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public boolean hasNavBar(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (context.isInMultiWindowMode()) {
                Log.d("Medidas", "no hay navbar transparente");
                return false;
            }
        }

        return hasNavigationBar(context);

//        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            Log.d("Medidas", "hay navbar transparente");
//        } else {
//            Log.d("Medidas", "no hay navbar transparente");
//        }
//
//        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
//        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
//
//        return resourceId > 0 && !hasBackKey && !hasHomeKey;
    }

    public static boolean hasNavigationBar(@NonNull Activity activity) {
        Point realSize = new Point();
        Point screenSize = new Point();
        boolean hasNavBar = false;
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        }
        realSize.x = metrics.widthPixels;
        realSize.y = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        if (realSize.y != screenSize.y) {
            int difference = realSize.y - screenSize.y;
            int navBarHeight = 0;
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId);
            }
            if (navBarHeight != 0) {
                if (difference == navBarHeight) {
                    hasNavBar = true;
                }
            }

        }
        return hasNavBar;
    }

    public int getNavigationBarHeight(Activity context) {
        if (context == null) return 0;
        if (!hasNavBar(context)) return 0;

        Resources resources = context.getResources();
        int orientation = resources.getConfiguration().orientation;

        //Only phone between 0-599 has navigationbar can move
        boolean isSmartphone = resources.getConfiguration().smallestScreenWidthDp < 600;
        if (isSmartphone && Configuration.ORIENTATION_LANDSCAPE == orientation)
            return 0;

        int id = resources
                .getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
        if (id > 0)
            return resources.getDimensionPixelSize(id);

        return 0;
    }

    public int getNavigationBarWidth(Activity context) {
        if (context == null) return 0;
        if (!hasNavBar(context)) return 0;

        int orientation = context.getResources().getConfiguration().orientation;

        //Only phone between 0-599 has navigationbar can move
        boolean isSmartphone = context.getResources().getConfiguration().smallestScreenWidthDp < 600;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE && isSmartphone) {
            int id = context.getResources().getIdentifier("navigation_bar_width", "dimen", "android");
            if (id > 0)
                return context.getResources().getDimensionPixelSize(id);
        }

        return 0;
    }

    public void convertirTransparente(@NonNull Activity activity) {
        if (isStatusBarTransparente(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }

        if (isNavigationBarTransparente(activity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setNavigationBarColor(ResourcesCompat.getColor(activity.getResources(), android.R.color.transparent, null));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setNavigationBarColor(ResourcesCompat.getColor(activity.getResources(), android.R.color.black, null));
            }
        }
    }

    public boolean isStatusBarTransparenteService() {
        //Navigationbar transparente
        boolean resultado = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (resultado) {
            Log.d("Medidas", "no hay statusbar transparente");
        } else {
            Log.d("Medidas", "Hay statusbar transparente");
        }
        return resultado;
    }

    public boolean isStatusBarTransparente(Activity context) {
//        return  false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (context.isInMultiWindowMode()) {
                Log.d("Medidas", "no hay statusbar transparente");
                return false;
            }
        }

        //Navigationbar transparente
        boolean resultado = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (resultado) {
            Log.d("Medidas", "no hay statusbar transparente");
        } else {
            Log.d("Medidas", "Hay statusbar transparente");
        }
        return resultado;
    }

    public boolean isNavigationBarTransparente(@NonNull Activity activity) {
        //Navigationbar transparente
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return false;

        int navbar = UtilesMedidas.getInstance().hasNavBar(activity) ? UtilesMedidas.getInstance().getNavigationBarHeight(activity) : 0;
        return navbar != 0;
    }

    public void statusBarPadding(Activity activity, View view) {
        if (view == null) return;
        if (view.getContext() == null) return;
        if (!isStatusBarTransparente(activity)) return;
        view.setPadding(
                view.getPaddingLeft(),
                view.getPaddingTop() + UtilesMedidas.getInstance().getStatusBarHeight(activity),
                view.getPaddingRight(),
                view.getPaddingBottom());
    }

    public void statusBarY(Activity activity, View view) {
        if (view == null) return;
        if (view.getContext() == null) return;
        if (!isStatusBarTransparente(activity)) return;
        view.setTranslationY(getStatusBarHeight(activity));
    }

    public void navigationBarPadding(Activity activity, View view) {
        if (view == null) return;
        if (view.getContext() == null) return;
        if (!isNavigationBarTransparente(activity)) return;
        view.setPadding(
                view.getPaddingLeft(),
                view.getPaddingTop(),
                view.getPaddingRight(),
                view.getPaddingBottom() + getNavigationBarHeight(activity)
        );
    }

}
