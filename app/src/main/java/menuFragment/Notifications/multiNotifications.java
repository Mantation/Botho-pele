package menuFragment.Notifications;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import constants.constants;

public class multiNotifications extends AsyncTask <String,Void,String>{

    public List<String> Tokens = new ArrayList<>();

    final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";//"https://fcm.googleapis.com/fcm/send";
    public static String serverKey = "";

    @Override
    protected String doInBackground(String... strings) {
        try {
            // Create URL instance.
            URL url = new URL(FCM_URL);
            // create connection.
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //set method as POST or GET
            conn.setRequestMethod("POST");
            //pass FCM server key
            conn.setRequestProperty("Authorization", "key=" + serverKey);
            //Specify Message Format
            conn.setRequestProperty("Content-Type", "application/json");
            //Create JSON Object & pass value

            JSONArray regId = null;
            JSONObject objData = null;
            JSONObject data = null;
            JSONObject notif = null;

            regId = new JSONArray();
            for (int i = 0; i < Tokens.size(); i++) {
                regId.put(Tokens.get(i));
            }
            data = new JSONObject();
            data.put("message", strings[1]);
            notif = new JSONObject();
            notif.put("title", strings[0]);
            notif.put("text", strings[1]);

            objData = new JSONObject();
            objData.put("registration_ids", regId);
            objData.put("data", data);
            objData.put("notification", notif);
            System.out.println("!_@rj@_group_PASS:>" + objData.toString());


            System.out.println("json :" + objData.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(objData.toString());
            wr.flush();
            int status = 0;
            if (null != conn) {
                status = conn.getResponseCode();
            }
            if (status != 0) {

                if (status == 200) {
                    //SUCCESS message
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    System.out.println("Android Notification Response : " + reader.readLine());
                } else if (status == 401) {
                    //client side error
                    System.out.println("Notification Response : TokenId : " + regId.toString() + " Error occurred :");
                } else if (status == 501) {
                    //server side error
                    System.out.println("Notification Response : [ errorCode=ServerError ] TokenId : " + regId.toString());
                } else if (status == 503) {
                    //server side error
                    System.out.println("Notification Response : FCM Service is Unavailable TokenId : " + regId.toString());
                }
            }
        } catch (MalformedURLException mlfexception) {
            // Prototcal Error
            System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (IOException mlfexception) {
            //URL problem
            System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (Exception exception) {
            //General Error or exception.
            System.out.println("Error occurred while sending push Notification!.." + exception.getMessage());
        }
        return null;
    }
}
