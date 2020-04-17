package menuFragment.Notifications;

import android.app.Activity;
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
import java.util.List;


import constants.constants;

public class soloNotification extends AsyncTask<String,Void,String> {
    final static private String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static String serverKey = "";
    private static String notificationType;
    //roadComments variables
    private static String myDocRef;
    private static boolean Video;
    private static String myIncident;
    private static String myInfo;
    private static String File;
    //profile variables
    public static String profileImage;
    public static String profileName;
    public static String profileRace;
    public static String profileGender;
    private static String profileHeight;
    public static String profileBuilt;
    public static String profileLastSeenDate;
    public static String profileLastSeenPlace;
    public static String profileAdditional;
    private static String profileDocRef;

    public static String getNotificationType() {
        return notificationType;
    }

    public static void setNotificationType(String notificationType) {
        soloNotification.notificationType = notificationType;
    }

    public static String getMyDocRef() {
        return myDocRef;
    }

    //roadComments variables
    public static void setMyDocRef(String myDocRef) {
        soloNotification.myDocRef = myDocRef;
    }

    public static boolean isVideo() {
        return Video;
    }

    public static void setVideo(boolean video) {
        Video = video;
    }

    public static String getMyIncident() {
        return myIncident;
    }

    public static void setMyIncident(String myIncident) {
        soloNotification.myIncident = myIncident;
    }

    public static String getMyInfo() {
        return myInfo;
    }

    public static void setMyInfo(String myInfo) {
        soloNotification.myInfo = myInfo;
    }

    public static String getFile() {
        return File;
    }

    public static void setFile(String file) {
        File = file;
    }

    //profile variables
    public static String getProfileImage() {
        return profileImage;
    }

    public static void setProfileImage(String profileImage) {
        soloNotification.profileImage = profileImage;
    }

    public static String getProfileName() {
        return profileName;
    }

    public static void setProfileName(String profileName) {
        soloNotification.profileName = profileName;
    }

    public static String getProfileRace() {
        return profileRace;
    }

    public static void setProfileRace(String profileRace) {
        soloNotification.profileRace = profileRace;
    }

    public static String getProfileGender() {
        return profileGender;
    }

    public static void setProfileGender(String profileGender) {
        soloNotification.profileGender = profileGender;
    }

    public static String getProfileHeight() {
        return profileHeight;
    }

    public static void setProfileHeight(String profileHeight) {
        soloNotification.profileHeight = profileHeight;
    }

    public static String getProfileBuilt() {
        return profileBuilt;
    }

    public static void setProfileBuilt(String profileBuilt) {
        soloNotification.profileBuilt = profileBuilt;
    }

    public static String getProfileLastSeenDate() {
        return profileLastSeenDate;
    }

    public static void setProfileLastSeenDate(String profileLastSeenDate) {
        soloNotification.profileLastSeenDate = profileLastSeenDate;
    }

    public static String getProfileLastSeenPlace() {
        return profileLastSeenPlace;
    }

    public static void setProfileLastSeenPlace(String profileLastSeenPlace) {
        soloNotification.profileLastSeenPlace = profileLastSeenPlace;
    }

    public static String getProfileAdditional() {
        return profileAdditional;
    }

    public static void setProfileAdditional(String profileAdditional) {
        soloNotification.profileAdditional = profileAdditional;
    }

    public static String getProfileDocRef() {
        return profileDocRef;
    }

    public static void setProfileDocRef(String profileDocRef) {
        soloNotification.profileDocRef = profileDocRef;
    }

    /**
     * Method to send push notification to Android FireBased Cloud messaging
     * Server.
     *
     * @param tokenId    Generated and provided from Android Client Developer
     * @param server_key Key which is Generated in FCM Server
     * @param message    which contains actual information.
     */

    public static void send_FCM_Notification(String tokenId, String server_key, String message, String title) {


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
            conn.setRequestProperty("Authorization", "key=" + server_key);
//Specify Message Format
            conn.setRequestProperty("Content-Type", "application/json");
//Create JSON Object & pass value
            JSONObject infoJson = new JSONObject();

            infoJson.put("title", title);
            infoJson.put("body", message);

            JSONObject json = new JSONObject();
            //json.put("to", tokenId.trim()); modified to the one below
            json.put("to", tokenId.trim());
            json.put("notification", infoJson);

            System.out.println("json :" + json.toString());
            System.out.println("infoJson :" + infoJson.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
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
                    System.out.println("Notification Response : TokenId : " + tokenId + " Error occurred :");
                } else if (status == 501) {
//server side error
                    System.out.println("Notification Response : [ errorCode=ServerError ] TokenId : " + tokenId);
                } else if (status == 503) {
//server side error
                    System.out.println("Notification Response : FCM Service is Unavailable TokenId : " + tokenId);
                }
            }
        } catch (MalformedURLException mlfexception) {
// Prototcal Error
            System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (Exception mlfexception) {
//URL problem
            System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }

    }

    public static void send_FCM_NotificationMulti(List<String> putIds2, String server_key, String message, String title) {
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
            conn.setRequestProperty("Authorization", "key=" + server_key);
            //Specify Message Format
            conn.setRequestProperty("Content-Type", "application/json");
            //Create JSON Object & pass value

            JSONArray regId = null;
            JSONObject objData = null;
            JSONObject data = null;
            JSONObject notif = null;

            regId = new JSONArray();
            for (int i = 0; i < putIds2.size(); i++) {
                regId.put(putIds2.get(i));
            }
            data = new JSONObject();
            data.put("message", message);
            notif = new JSONObject();
            notif.put("title", title);
            notif.put("text", message);

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


    }

    @Override
    protected String doInBackground(String... strings) {
        try {
// Create URL instance.
            URL url = new URL(FCM_URL);
// create connection.
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
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
            JSONObject infoJson = new JSONObject();

            infoJson.put("title", strings[0]);
            infoJson.put("body", strings[1]);

            //set extra data to notification
            JSONObject extraData = new JSONObject();
            extraData.put("isNotification", true);
            extraData.put("title", strings[0]);
            extraData.put("body", strings[1]);
            if(getNotificationType().equalsIgnoreCase("movement")){
                extraData.put("NotificationType", "movement");
            }else if(getNotificationType().equalsIgnoreCase("locator")){
                extraData.put("NotificationType", "locator");
                extraData.put("DocumentRef", getMyDocRef());
                extraData.put("isVideo", isVideo());
                extraData.put("Incident", getMyIncident());
                extraData.put("Info", getMyInfo());
                extraData.put("File", getFile());
            }else{
                extraData.put("NotificationType", "missing");
                extraData.put("Image", getProfileImage());
                extraData.put("Name", getProfileName());
                extraData.put("Race", getProfileRace());
                extraData.put("Gender", getProfileGender());
                extraData.put("Height", getProfileHeight());
                extraData.put("Built", getProfileBuilt());
                extraData.put("LastSeenDate", getProfileLastSeenDate());
                extraData.put("LastSeenPlace", getProfileLastSeenPlace());
                extraData.put("Info", getProfileAdditional());
                extraData.put("DocumentRef", getProfileDocRef());
            }
            JSONObject json = new JSONObject();
            //json.put("to", tokenId.trim()); modified to the one below
            json.put("to", strings[2].trim());
            json.put("notification", infoJson);
            json.put("data",extraData);
            json.put("priority","high");
            json.put("sound","enabled");
            json.put("volume" , "3.21.15");
            json.put("click_action","io.eyec.bombo.bothopele_TARGET_NOTIFICATION");
            System.out.println("json :" + json.toString());
            System.out.println("infoJson :" + infoJson.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
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
                    System.out.println("Notification Response : TokenId : " + strings[2] + " Error occurred :");
                } else if (status == 501) {
//server side error
                    System.out.println("Notification Response : [ errorCode=ServerError ] TokenId : " + strings[2]);
                } else if (status == 503) {
//server side error
                    System.out.println("Notification Response : FCM Service is Unavailable TokenId : " + strings[2]);
                }
            }
        } catch (MalformedURLException mlfexception) {
// Prototcal Error
            System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        } catch (Exception mlfexception) {
//URL problem
            System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }

        return null;
    }


}
