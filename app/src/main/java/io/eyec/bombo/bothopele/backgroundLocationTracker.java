package io.eyec.bombo.bothopele;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Constraints;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.constants;
import menuFragment.Notifications.soloNotification;
import methods.globalMethods;
import properties.accessKeys;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static properties.accessKeys.getDefaultUserId;

public class backgroundLocationTracker extends Service {
    private static Activity activity;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    //location variables
    public static boolean ontheMove;
    public static String onMoveLat;
    public static String onMoveLng;
    public static String initialLat;
    public static String initialLng;


    //location variables
    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        backgroundLocationTracker.activity = activity;
    }

    public static boolean isOntheMove() {
        return ontheMove;
    }

    public static void setOntheMove(boolean ontheMove) {
        backgroundLocationTracker.ontheMove = ontheMove;
    }

    public static String getOnMoveLat() {
        return onMoveLat;
    }

    public static void setOnMoveLat(String onMoveLat) {
        backgroundLocationTracker.onMoveLat = onMoveLat;
    }

    public static String getOnMoveLng() {
        return onMoveLng;
    }

    public static void setOnMoveLng(String onMoveLng) {
        backgroundLocationTracker.onMoveLng = onMoveLng;
    }

    public static String getInitialLat() {
        return initialLat;
    }

    public static void setInitialLat(String initialLat) {
        backgroundLocationTracker.initialLat = initialLat;
    }

    public static String getInitialLng() {
        return initialLng;
    }

    public static void setInitialLng( String initialLng) {
        backgroundLocationTracker.initialLng = initialLng;

    }


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            setInitialLat(String.valueOf(location.getLatitude()));
            setInitialLng(String.valueOf(location.getLongitude()));
            accessKeys.setLatitude((String.valueOf(location.getLatitude())));
            accessKeys.setLongitude(String.valueOf(location.getLongitude()));
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        MonitorLocation();
        roadIncidentAlerts();
        roadCommentsAlerts();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        handler.removeCallbacks(myRunnable);
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(myRunnable);
        handler.removeMessages(0);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public static final Handler handler = new Handler();
    final static int delay = 120000; //milliseconds Minutes
    public static Runnable myRunnable;
    static String targetDocument = "";

    public static void MonitorLocation(){
        handler.postDelayed(myRunnable = new Runnable(){
            public void run(){
                if(isOntheMove()){
                    String latitude = getInitialLat();
                    String longitute = getInitialLng();
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();//
                    db.collection(constants.locator).whereEqualTo("date",ToDate()).whereEqualTo("destination",getOnMoveLat()+","+getOnMoveLng()).whereEqualTo("live",true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean isFound = false;
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.get("location") != null && document.get("document ref") != null) {
                                        //existingUserDoc = document.get("document ref").toString();
                                        isFound = true;
                                        //count = Arrays.asList(document.get("location")).size();
                                        targetDocument = document.get("document ref").toString();
                                    }
                                }
                                //initialCommit(latitude,longitute);
                                if(isFound){
                                    updateLocation(latitude,longitute);
                                    //updateLatLng(latitude,longitute,count);
                                }else{
                                    initialCommit(latitude,longitute);
                                }
                            }
                        }
                    });
                }else {
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    handler.removeCallbacksAndMessages(null);
                    handler.removeCallbacksAndMessages(myRunnable);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private static void updateLocation(final String lat, final String lng){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //update user details
        DocumentReference ref = db.collection(constants.locator).document(targetDocument);
        //Map<String, Object> location = new HashMap<>();
        //location.put("location."+position, Arrays.asList(lat+","+lng));
        //location.put("location."+position, lat+","+lng);
        //location.put("location", Arrays.asList(lat+","+lng));
        //location.put("location", Arrays.asList(position, lat+","+lng));
        //ref.set(location, SetOptions.merge());
        ref.update("location", FieldValue.arrayUnion(accessKeys.getLatitude()+","+accessKeys.getLongitude()));

        /*CollectionReference collectionReference = db.collection(constants.locator);
        collectionReference.document(targetDocument).update(
                "location."+position, lat+","+lng
        )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //success
                            Loginfo("user Personal Details successfully added");
                            globalMethods.stopProgress = true;
                        }
                    }

                });*/
    }

    public static void updateLatLng(final String lat, final String lng, final int position){
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();


            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("location."+position, Arrays.asList(lat+","+lng));
            // Add a new document with a generated ID
            db.collection(constants.locator).document(targetDocument).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //set Realm user information;
                    Loginfo("user Personal Details successfully added");
                    globalMethods.stopProgress = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(Constraints.TAG, "Error writing document", e);
                    globalMethods.stopProgress = true;
                    Logerror("unable to add selected user!, " + e.getMessage());
                }
            });
        }catch (Exception exception){
            Log.w(Constraints.TAG, "Error writing document", exception);
            globalMethods.stopProgress = true;
            exception.getMessage();
            exception.printStackTrace();
        }
    }

    public static void initialCommit(final String lat, final String lng){
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("location", Arrays.asList(accessKeys.getLatitude()+","+accessKeys.getLongitude()));
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("destination", lat+","+lng);
            user.put("userid", getDefaultUserId());
            user.put("live", true);
            user.put("document ref", "n/a");

            // Add a new document with a generated ID
            db.collection(constants.locator)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.locator);
                            collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //set Realm user information;
                                        Loginfo("user Personal Details successfully added");
                                        globalMethods.stopProgress = true;
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(Constraints.TAG, "Error writing document", e);
                            globalMethods.stopProgress = true;
                            Logerror("unable to add selected user!, " + e.getMessage());
                        }
                    });
        }catch (Exception exception){
            Log.w(Constraints.TAG, "Error writing document", exception);
            globalMethods.stopProgress = true;
            exception.getMessage();
            exception.printStackTrace();
        }
    }
    static double maxDistance = 50.00;
    static int docCount = 0;
    static ArrayList <String> roadDocuments = new ArrayList<>();
    public static void roadIncidentAlerts(){
        docCount = 0;
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.road)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(ContentValues.TAG,e.toString());
                            return;
                        }
                        if (roadDocuments.size() == 0) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot :queryDocumentSnapshots) {
                                roadDocuments.add(queryDocumentSnapshot.getId());
                            }
                        }
                        for (QueryDocumentSnapshot queryDocumentSnapshot :queryDocumentSnapshots) {
                            if(queryDocumentSnapshot.exists()) {
                                boolean found = false;
                                for (int i = 0; i < roadDocuments.size(); i++) {
                                    if(queryDocumentSnapshot.getId().equals(roadDocuments.get(i))){
                                        found= true;
                                    }
                                }
                                if(queryDocumentSnapshot.get("imageFile")!=null && queryDocumentSnapshot.get("incidentCoordinates")!=null && accessKeys.getLatitude()!=null && accessKeys.getLongitude() != null && !found ){
                                    roadDocuments.add(queryDocumentSnapshot.getId());
                                    //db.document(constants.road+"/"+queryDocumentSnapshot.getId()).delete();
                                    String []Coords = queryDocumentSnapshot.get("incidentCoordinates").toString().split(",");
                                    String Incident = queryDocumentSnapshot.get("incidentType").toString();
                                    String IncidentPlace = queryDocumentSnapshot.get("incidentLastSeenPlace").toString();
                                    double Lat = Double.parseDouble(Coords[0]);
                                    double Lng = Double.parseDouble(Coords[1]);
                                    String distanceBetween = globalMethods.getDistanceBetween(Lat,Lng,Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()));
                                    if(maxDistance>=Double.parseDouble(distanceBetween.replace(",","."))){
                                        messagingHelper.setIncludeLargeIcon(true);
                                        messagingHelper.setLargeIcon(queryDocumentSnapshot.get("imageFile").toString());
                                        //set location variable to notification
                                        soloNotification.setNotificationType("locator");
                                        soloNotification.setMyDocRef(queryDocumentSnapshot.get("document ref").toString());
                                        soloNotification.setVideo(Boolean.parseBoolean(queryDocumentSnapshot.get("isVideo").toString()));
                                        soloNotification.setMyIncident(queryDocumentSnapshot.get("incidentType").toString());
                                        soloNotification.setMyInfo(queryDocumentSnapshot.get("moreInfo").toString());
                                        if(Boolean.parseBoolean(queryDocumentSnapshot.get("isVideo").toString())){
                                            soloNotification.setFile(queryDocumentSnapshot.get("videoFile").toString());
                                        }else{
                                            soloNotification.setFile(queryDocumentSnapshot.get("imageFile").toString());
                                        }
                                        //build notification
                                        String token = accessKeys.getMessagingToken();
                                        soloNotification.serverKey = activity.getResources().getString(R.string.messaging_api_key);
                                        new soloNotification().execute(Incident +" incident within your range!","A road-side "+ Incident +" incident along the "+IncidentPlace+" in your vacinity!",token);
                                    }
                                }
                            }

                        }
                }
        });
    }
    static ArrayList <String> commentsDocuments = new ArrayList<>();
    public static void roadCommentsAlerts(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.roadComments)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(ContentValues.TAG,e.toString());
                            return;
                        }
                        if (commentsDocuments.size() == 0) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot :queryDocumentSnapshots) {
                                commentsDocuments.add(queryDocumentSnapshot.getId());
                            }
                        }
                        for (QueryDocumentSnapshot queryDocumentSnapshot :queryDocumentSnapshots) {
                            if (queryDocumentSnapshot.exists()) {
                                boolean found = false;
                                for (int i = 0; i < commentsDocuments.size(); i++) {
                                    if(queryDocumentSnapshot.getId().equals(commentsDocuments.get(i))){
                                        found= true;
                                    }
                                }
                                if (queryDocumentSnapshot.get("incidentCoordinates") != null && queryDocumentSnapshot.get("locator") != null && accessKeys.getLatitude()!=null && accessKeys.getLongitude() != null && !found) {
                                    commentsDocuments.add(queryDocumentSnapshot.getId());
                                    //db.document(constants.roadComments+"/"+queryDocumentSnapshot.getId()).delete();
                                    boolean locator = Boolean.parseBoolean(queryDocumentSnapshot.get("locator").toString());
                                    if (locator) {
                                        String[] Coords = queryDocumentSnapshot.get("incidentCoordinates").toString().split(",");
                                        String IncidentPlace = queryDocumentSnapshot.get("incidentLastSeenPlace").toString();
                                        String IncidentTime = queryDocumentSnapshot.get("incidentTime").toString();
                                        String parentDocument = queryDocumentSnapshot.get("parentDocument").toString();
                                        double Lat = Double.parseDouble(Coords[0]);
                                        double Lng = Double.parseDouble(Coords[1]);
                                        String distanceBetween = globalMethods.getDistanceBetween(Lat, Lng, Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
                                        if (maxDistance >= Double.parseDouble(distanceBetween.replace(",", "."))) {
                                            db.collection(constants.road).whereEqualTo("document ref", parentDocument).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            //if(document.get("videoFile") != null && document.get("imageFile") != null){
                                                            //    commentsDocuments.remove(queryDocumentSnapshot.getId());
                                                            //}else {
                                                            messagingHelper.setIncludeLargeIcon(false);
                                                            messagingHelper.setLargeIcon(null);
                                                                //set location variable to notification
                                                            soloNotification.setNotificationType("locator");
                                                            soloNotification.setMyDocRef(document.get("document ref").toString());
                                                            soloNotification.setVideo(Boolean.parseBoolean(document.get("isVideo").toString()));
                                                            soloNotification.setMyIncident(document.get("incidentType").toString());
                                                            soloNotification.setMyInfo(document.get("moreInfo").toString());
                                                            if (Boolean.parseBoolean(document.get("isVideo").toString())) {
                                                                soloNotification.setFile(document.get("videoFile").toString());
                                                            } else {
                                                                soloNotification.setFile(document.get("imageFile").toString());
                                                            }
                                                            //build notification
                                                            String Incident = document.get("incidentType").toString();
                                                            String User = queryDocumentSnapshot.get("name").toString();
                                                            String comment = "";
                                                            if(queryDocumentSnapshot.get("comment").toString().equalsIgnoreCase("none")){
                                                                comment = "co-ordinates supplied for incident ("+Incident+")";
                                                            }else{
                                                                comment = queryDocumentSnapshot.get("comment").toString();
                                                            }
                                                            String token = accessKeys.getMessagingToken();
                                                            soloNotification.serverKey = activity.getResources().getString(R.string.messaging_api_key);
                                                            new soloNotification().execute(User +" left a comment!",comment, token);
                                                            //new soloNotification().execute(Incident + " incident related activity seen " + IncidentTime + "!", "A road-side " + Incident + " incident activity spotted along the " + IncidentPlace + " in your vacinity!", token);
                                                            //}
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                    }
                });

    }
}
