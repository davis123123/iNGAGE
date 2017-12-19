package ingage.ingage20.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by wuv66 on 12/18/2017.
 */

public class WifiManager {

    private Context context;

    public WifiManager(Context context){
        this.context = context;
    }

    public boolean checkInternet() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }

        else{
            return false;
        }
    }



}
