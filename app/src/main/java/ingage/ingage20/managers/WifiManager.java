package ingage.ingage20.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import ingage.ingage20.BuildConfig;

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

        //check if running on emulator and using debug build
        if(isEmulator() && BuildConfig.BUILD_TYPE.equals("debug")){
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++) {
                        Log.i("STATE", "get emulator state: " + info[i].getState());
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
            }
            return false;
        }

        //running on device
        else {
            if (mWifi.isConnected()) {
                return true;
            } else {
                return false;
            }
        }

    }


    public boolean isEmulator(){

        boolean result= Build.FINGERPRINT.startsWith("generic")
                        ||Build.FINGERPRINT.startsWith("unknown")
                        ||Build.MODEL.contains("google_sdk")
                        ||Build.MODEL.contains("Emulator")
                        ||Build.MODEL.contains("Android SDK built for x86");
        if(result) {
            Log.i("STATE", "app is running on emulator");
            return true;
        }

        result|=Build.BRAND.startsWith("generic")&&Build.DEVICE.startsWith("generic");
        if(result) {
            Log.i("STATE", "app is running on emulator");
            return true;
        }
        //result|="google_sdk".equals(Build.PRODUCT);
        return result;
    }

}
