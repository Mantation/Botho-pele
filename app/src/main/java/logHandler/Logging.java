package logHandler;

import android.util.Log;

public class Logging {
    //Log info
    public static void Loginfo(String info){
        Log.i("INFO", info);
    }
    //Log warning
    public static void Logwarn(String warn){
        Log.w("WARN", warn);
    }
    //Log error
    public static void Logerror(String error){
        Log.e("ERROR", error);
    }
}
