package ingage.ingage20.handlers;

import android.content.Context;

/**
 * Created by Davis on 4/14/2017.
 */

public class MySQLDbHelper {

    public static String json_string = "a";
    Context mContext;


    public static String getJson_string(){
        return json_string;
    }

    public static void setJson_string(String mjson_string){
        json_string = mjson_string;
    }


}
