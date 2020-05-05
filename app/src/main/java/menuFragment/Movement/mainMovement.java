package menuFragment.Movement;


import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import io.eyec.bombo.bothopele.backgroundLocationTracker;
import io.eyec.bombo.bothopele.messagingHelper;
import menuFragment.Notifications.soloNotification;
import methods.globalMethods;
import properties.accessKeys;

import static android.content.ContentValues.TAG;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.clearList;
import static properties.accessKeys.getDefaultUserId;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class mainMovement extends android.app.Fragment implements OnMapReadyCallback,View.OnClickListener, FragmentManager.OnBackStackChangedListener, RoutingListener {
    View myview;
    ImageView defaultMarker;
    TextView Loc;
    FrameLayout frameLayout;
    GoogleMap myMap;
    CardView MySet;
    CardView MySetInner;
    TextView Estimate;
    ImageView Walker;
    FloatingActionButton Addnew;
    static FragmentManager fm;
    private List<Polyline> polylines;
    private RoutingListener routingListener;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorGreen, R.color.colorAccent, R.color.colorltBlue};

    public mainMovement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_main_movement, container, false);
        MySet = myview.findViewById(R.id.MySet);
        MySetInner = myview.findViewById(R.id.MySetInner);
        frameLayout = myview.findViewById(R.id.map);
        Loc = myview.findViewById(R.id.location);
        Estimate = myview.findViewById(R.id.estimate);
        Addnew = myview.findViewById(R.id.addNew);
        defaultMarker = myview.findViewById(R.id.confirm_address_map_custom_marker);
        Walker = myview.findViewById(R.id.walker);
        Walker.setVisibility(View.GONE);
        polylines = new ArrayList<>();
        MySet.setVisibility(View.GONE);
        fm = ((MainActivity) getActivity()).getSupportFragmentManager();/// getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        Addnew.setOnClickListener(this);
        MySet.setOnClickListener(this);
        supportMapFragment.getMapAsync(this);
        setExitApplication(false);
        return myview;
    }

    boolean locationLabel;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng Now = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
        googleMap.addMarker(new MarkerOptions().position(Now).title("Your Location")).showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Now, 11f));
        myMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double CameraLat = myMap.getCameraPosition().target.latitude;
                double CameraLong = myMap.getCameraPosition().target.longitude;
                if (locationLabel && !Loc.getText().toString().equalsIgnoreCase("Cancel Trip")) {
                    //Loc.setText(getStringAddress(getActivity(),CameraLat,CameraLong));
                    Loc.setText("Select destination");
                } else {
                    locationLabel = true;
                }

                MySet.setVisibility(View.VISIBLE);
            }
        });
        myMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                MySet.setVisibility(View.GONE);
            }
        });
        getFriendsMovement(getActivity(),myMap,Walker);
        MonitorFriendLocation(getActivity(),myMap,Walker);
        if (backgroundLocationTracker.isOntheMove()) {
            LatLng origin = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
            LatLng destination = new LatLng(Double.parseDouble(backgroundLocationTracker.getOnMoveLat()), Double.parseDouble(backgroundLocationTracker.getOnMoveLng()));
            //draw route
            Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.DRIVING).withListener(this).waypoints(origin, destination).key(getResources().getString(R.string.places_api_key)).build();
            routing.execute();
            //set marker
            myMap.addCircle(new CircleOptions().center(destination).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destination);
            markerOptions.title("Your destination");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            myMap.addMarker(markerOptions).showInfoWindow();
            MySetInner.setCardBackgroundColor(getActivity().getResources().getColor(R.color.Red));
            Loc.setText("Cancel Trip");
            defaultMarker.setVisibility(View.GONE);
            //count number of friends
            //get time distance estimates
            CalculateDistanceTime distance_task = new CalculateDistanceTime(getActivity());
            distance_task.getDirectionsUrl(origin, destination);
            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                @Override
                public void taskCompleted(String[] time_distance) {

                    friendsCounts(getActivity(),Estimate, time_distance[0], time_distance[1],false);
                    //Send my Location to server per 5minutes
                    //Go Live
                    backgroundLocationTracker.setOntheMove(true);
                    activateLive();
                }
            });
        }else{
            getSelfMove(getActivity(),this,myMap,MySetInner,Loc,defaultMarker,Estimate);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        routingListener = this;
        if (id == R.id.MySet) {
            if (accessKeys.isApproval()) {
                if (Loc.getText().toString().equalsIgnoreCase("Cancel Trip")) {
                    //Deactivate Live
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //update user details
                    CollectionReference collectionReference = db.collection(constants.friends);
                    collectionReference.document(accessKeys.getDefaultUserId()).update("live", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //success
                                Loginfo("user Personal Details successfully added");
                                globalMethods.stopProgress = true;
                                //reshape map
                                myMap.clear();
                                Loc.setText("Select destination");
                                MySetInner.setCardBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
                                defaultMarker.setVisibility(View.VISIBLE);
                                LatLng Now = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
                                myMap.addMarker(new MarkerOptions().position(Now).title("Your Location")).showInfoWindow();
                                //myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Now, 16f));
                                //deactivate movement monitor
                                backgroundLocationTracker.setOntheMove(false);
                                Estimate.setText("");
                                closeLiveLocation();
                            }
                        }

                    });
                } else {
                    //activate Live
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //update user details
                    CollectionReference collectionReference = db.collection(constants.friends);
                    collectionReference.document(accessKeys.getDefaultUserId()).update("live", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //success
                                Loginfo("user Personal Details successfully added");
                                globalMethods.stopProgress = true;
                                //reshape map
                                //close all live feeds
                                getFirebaseLocatorDocuments();
                                //set target & show estimates
                                LatLng center = myMap.getCameraPosition().target;
                                accessKeys.setTargetLat(String.valueOf(center.latitude));
                                accessKeys.setTargetLong(String.valueOf(center.longitude));
                                //set background current position
                                backgroundLocationTracker.setInitialLat(String.valueOf(center.latitude));
                                backgroundLocationTracker.setInitialLng(String.valueOf(center.longitude));
                                MySetInner.setCardBackgroundColor(getActivity().getResources().getColor(R.color.Red));
                                Loc.setText("Cancel Trip");
                                CalculateDistanceTime distance_task = new CalculateDistanceTime(getActivity());
                                LatLng origin = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
                                LatLng destination = new LatLng(Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong()));
                                //set movement destination to background
                                backgroundLocationTracker.setOnMoveLat(String.valueOf(center.latitude));
                                backgroundLocationTracker.setOnMoveLng(String.valueOf(center.longitude));
                                //get time distance estimates
                                distance_task.getDirectionsUrl(origin, destination);
                                distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                                    @Override
                                    public void taskCompleted(String[] time_distance) {

                                        friendsCounts(getActivity(), Estimate, time_distance[0], time_distance[1], true);
                                        //Send my Location to server per 5minutes
                                        //Go Live
                                        backgroundLocationTracker.setOntheMove(true);
                                    }
                                });
                                //draw route
                                Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.DRIVING).withListener(routingListener).waypoints(origin, destination).key(getResources().getString(R.string.places_api_key)).build();
                                routing.execute();
                            /*myMap.addPolyline(new PolylineOptions()
                            .add(origin)
                            .add(destination)
                            .width(8f)
                            .color(getActivity().getResources().getColor(R.color.colorPrimaryDark)));*/
                                myMap.addCircle(new CircleOptions().center(destination).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(destination);
                                markerOptions.title("Your destination");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                myMap.addMarker(markerOptions).showInfoWindow();
                                defaultMarker.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            }else{
                Toast.makeText(getActivity(), "Cannot perform transaction, your verification status is pending!", Toast.LENGTH_SHORT).show();
            }
        } else {
            methods.globalMethods.loadFragmentWithTag(R.id.main, new Friends(), getActivity(), "friends");
        }
    }

    static int friendsCount;

    //count active friends
    public static void friendsCounts(final Activity activity,final TextView Textview, final String Distance, final String Duration, final boolean notify) {
        friendsCount = 0;
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.friends).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.getId().equalsIgnoreCase(accessKeys.getDefaultUserId()) && document.get("user") != null) {
                            List<String> users = Arrays.asList(document.get("user").toString());
                            String []allValues = users.get(0).split(",");
                            for (int i = 0; i < allValues.length; i++) {
                                String []Info = allValues[i].split("~");
                                String Status = Info[1].replace("]","").trim();
                                String User = Info[0].replace("[","").trim();
                                if (Status.equalsIgnoreCase("accepted")) {
                                    friendsCount++;
                                    if(notify)
                                        initialCommit(activity,User,backgroundLocationTracker.getOnMoveLat(),backgroundLocationTracker.getOnMoveLng());
                                }
                            }
                        }
                    }
                }
                String text = "Distance : <font color=#cc0000>" + Distance + "</font>\nDuration : <font color=#cc0000>" + Duration + "</font><br>Friends monitoring movement (<font color=#008000>" + friendsCount + ")";
                Textview.setText(Html.fromHtml(text));
            }
        });
    }

    //set primary destination target
    public static void initialCommit(final Activity activity, final String User, final String lat, final String lng){
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
                                        BuildNotification(activity,User);
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

    //get tokens and build a notification
    public static void BuildNotification(final Activity activity, final String UserId) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users).whereEqualTo("userid", UserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //String token = "faODJuL9fRA:APA91bGVgtICrXq-WNmJCKr298eBv0bUQs4ZVJyvoLsPDjptI-gsPJlYMI3aKdRQzl1v_k2EKWHZvyxHlmJMnfawgzOCRbJCgxzqoarc9OhsdNPcNS6QNSYyPwceWHLWjxZlXxiPTu0c";
                        messagingHelper.setIncludeLargeIcon(false);
                        messagingHelper.setLargeIcon(null);
                        //set Notification type
                        soloNotification.setNotificationType("movement");
                        //build notification
                        String token = document.get("token").toString();
                        soloNotification.serverKey = activity.getResources().getString(R.string.messaging_api_key);
                        new soloNotification().execute("On the move!!!","Your contact "+globalMethods.InitializeFirstLetter(accessKeys.getName()) +" "+globalMethods.InitializeFirstLetter(accessKeys.getSurname()) +
                                " is on the move, kindly keep a watch, thanks.",token);
                        //update all live feed to off
                    }
                }
            }
        });
    }


    private String getURL(LatLng origin, LatLng destination, String directionMode) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googlePlaceUrl.append("origin=" + origin);
        googlePlaceUrl.append("&destination=" + destination);
        googlePlaceUrl.append("&mode=" + directionMode);
        googlePlaceUrl.append("&key=" + getResources().getString(R.string.places_api_key));
        return googlePlaceUrl.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        Fragment oldFragment = fm.findFragmentById(R.id.map);
        if (oldFragment != null) {
            //getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
            ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
        MainActivity.onMap = true;
    }

    @Override
    public void onRoutingFailure(RouteException e) {
// The Routing request failed
        if (e != null) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = myMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    private static void activateLive() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //update user details
        CollectionReference collectionReference = db.collection(constants.friends);
        collectionReference.document(accessKeys.getDefaultUserId()).update("live", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //success
                    Loginfo("user Personal Details successfully added");
                    globalMethods.stopProgress = true;
                }
            }

        });
    }

    //get all documents from firestore and turn off live feed
    public static void getFirebaseLocatorDocuments() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.locator).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("", "Error : " + e.getMessage());
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Log.d("Brand Name: ", doc.getDocument().getId());
                        doc.getDocument().getReference().collection(doc.getDocument().getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.d("", "Error : " + e.getMessage());
                                }

                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        Log.d("SubBrands Name: ", doc.getDocument().getId());
                                    }
                                }

                            }
                        });
                    }

                }
            }
        });
        final FirebaseFirestore dbs = FirebaseFirestore.getInstance();
        dbs.collection(constants.locator).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Documents.add(document.getId());
                    }
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        //Documents.add(document.getId());
                    }
                    List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                    //Documents.addAll(myListOfDocuments);
                    //getCompanyInformation(activity);
                    closeLiveLocation();
                    //getIssues(activity,context, view, recyclerView); //ammended
                }
            }
        });
    }

    private static void closeLiveLocation() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.locator).whereEqualTo("userid", getDefaultUserId()).whereEqualTo("date", ToDate()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isFound = false;
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String targetDocument = document.get("document ref").toString();
                        //update all live feed to off
                        CollectionReference collectionReference = db.collection(constants.locator);
                        collectionReference.document(targetDocument).update("live", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //success
                                    Loginfo("user Personal Details successfully added");
                                    globalMethods.stopProgress = true;
                                }
                            }

                        });
                    }
                }
            }
        });
    }

    public static final Handler handler = new Handler();
    final static int delay = 200000; //milliseconds Minutes(2)
    public static Runnable myRunnable;
    private static int Count;
    private static Circle circle;

    public static void MonitorFriendLocation(final Activity activity, final GoogleMap myMap, final ImageView imageView) {
        Count = 0;
        handler.postDelayed(myRunnable = new Runnable() {
            public void run() {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(constants.friends).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equalsIgnoreCase(accessKeys.getDefaultUserId()) && document.get("user") != null) {
                                    List<String> users = Arrays.asList(document.get("user").toString());
                                    for (int i = 0; i < users.size(); i++) {
                                        String []Info = users.get(i).split("~");
                                        String Status = Info[1].replace("]","");
                                        String currentUser = Info[0].replace("[","");
                                        if (Status.equalsIgnoreCase("accepted")) {
                                            Count++;
                                            db.collection(constants.locator).whereEqualTo("date", ToDate()).whereEqualTo("userid", currentUser).whereEqualTo("live", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            if (document.get("location") != null) {
                                                                List<String> location = Arrays.asList(document.get("location").toString());
                                                                int locationSize = Arrays.asList(document.get("location")).size();
                                                                String []latlng = location.get(0).split(",");
                                                                //String latlng[] = location.get(locationSize - 1).split(",");
                                                                LatLng currentPosition = new LatLng(Double.parseDouble(latlng[latlng.length-2].replace("[","").replace("]","")), Double.parseDouble(latlng[latlng.length-1].replace("[","").replace("]","")));
                                                                //MarkerOptions markerOptions = new MarkerOptions();
                                                                //markerOptions.position(currentPosition);
                                                                //setCurrentUser(currentUser,myMap, markerOptions);
                                                                setCurrentUser(activity,currentUser,myMap,currentPosition);
                                                                imageView.setVisibility(View.VISIBLE);
                                                                if(circle!=null){
                                                                    circle.remove();
                                                                }
                                                                switch (Count) {
                                                                    case 1:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(10, 200, 90, 50)));
                                                                        break;
                                                                    case 2:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.BLACK).fillColor(Color.argb(20, 190, 80, 50)));
                                                                        break;
                                                                    case 3:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.BLUE).fillColor(Color.argb(30, 180, 70, 50)));
                                                                        break;
                                                                    case 4:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.GREEN).fillColor(Color.argb(40, 170, 60, 50)));
                                                                        break;
                                                                    case 5:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.MAGENTA).fillColor(Color.argb(50, 160, 50, 50)));
                                                                        break;
                                                                    case 6:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.LTGRAY).fillColor(Color.argb(60, 150, 40, 50)));
                                                                        break;
                                                                    case 7:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.DKGRAY).fillColor(Color.argb(70, 140, 30, 50)));
                                                                        break;
                                                                    case 8:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.CYAN).fillColor(Color.argb(80, 130, 20, 50)));
                                                                        break;
                                                                    case 9:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.YELLOW).fillColor(Color.argb(90, 120, 10, 50)));
                                                                        break;
                                                                    case 10:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(R.color.colorPrimaryDark).fillColor(Color.argb(100, 110, 5, 50)));
                                                                        break;
                                                                    default:
                                                                        circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));
                                                                        Count = 0;
                                                                        break;
                                                                }

                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                            if (Count == 0) {
                                handler.removeMessages(0);
                                handler.removeCallbacks(myRunnable);
                                handler.removeCallbacksAndMessages(null);
                                handler.removeCallbacksAndMessages(myRunnable);
                            }
                        }
                    }
                });
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
    static ArrayList<Marker> markers = new ArrayList<>();
    static Marker marker;
    private static void setCurrentUser(final Activity activity, final String currentUser,final GoogleMap myMap, final LatLng position) {
        String result = "";
        //gets all documents from firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users).whereEqualTo("userid", currentUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.get("name") != null && document.get("surname") != null) {
                            for (int i = 0; i < markers.size(); i++) {
                                if(marker.equals(markers.get(i))){
                                    marker.remove();
                                    markers.remove(i);
                                }
                            }
                            String Name = document.get("name").toString();
                            String Surname = document.get("surname").toString();
                            String Image = document.get("profileImage").toString();
                            //markerOptions.title(Name + " " + Surname);
                            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            //Draw canvas to replace marker
                            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                            Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
                            Canvas canvas1 = new Canvas(bmp);

                            // paint defines the text color, stroke width and size
                            Paint color = new Paint();
                            color.setTextSize(35);
                            color.setColor(Color.BLACK);

                            // modify canvas
                            //canvas1.drawBitmap(BitmapFactory.decodeFile(
                            //        Image), 0,0, color);
                            //Bitmap picture = BitmapFactory.decodeFile(Image);
                            //Drawable drawable = new BitmapDrawable(picture);
                            //canvas1.drawBitmap(BitmapFactory.decodeResource(activity.getResources(),drawable),0,0,color);
                            //canvas1.drawBitmap(globalMethods.getBitmapFromURL(Image),0,0,color);
                            //Glide.with(context).load(Images[position]).into(canvas1);
                            //canvas1.drawText(Name +" "+Surname, 30, 40, color);
                            /*Object dataTransfer[] = new Object[7];
                            dataTransfer[0] = Image;
                            dataTransfer[1] = canvas1;
                            dataTransfer[2] = color;
                            dataTransfer[3] = Name+" "+Surname;
                            dataTransfer[4] = markerOptions;
                            dataTransfer[5] = marker;
                            dataTransfer[6] = myMap;
                            DownloadBitMapTask downloadBitmap = new DownloadBitMapTask();
                            downloadBitmap.execute(dataTransfer);*/
                            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                            createCustomMarker(activity,activity,Image,Name+" "+Surname,myMap,position);
                            //marker = myMap.addMarker(new MarkerOptions().position(position));
                            /*marker.setIcon((BitmapDescriptorFactory.fromBitmap(
                                            createCustomMarker(activity,Image,Name+" "+Surname))));
                            marker.setTitle(globalMethods.getStringAddress(activity,position.latitude,position.longitude));
                            marker.showInfoWindow();*/
                            //LatLngBound will cover all your marker on Google Maps
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(position); //Taking Point B (Second LatLng)
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                            //myMap.moveCamera(cu);
                            //myMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);


                        }
                    }
                }
            }
        });
    }

    public static void createCustomMarker(Context context,Activity activity, String ImageRes, String name,GoogleMap Map, LatLng position) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        ImageView markerImage = (ImageView) marker.findViewById(R.id.user_dp);
        Object dataTransfer[] = new Object[10];
        dataTransfer[0] = ImageRes;
        dataTransfer[1] = markerImage;
        dataTransfer[2] = context;
        dataTransfer[3] = activity;
        dataTransfer[4] = marker;
        dataTransfer[5] = name;
        dataTransfer[6] = Map;
        dataTransfer[7] = position;
        DownloadImageBitMapTask downloadImageBitMapTask = new DownloadImageBitMapTask();
        downloadImageBitMapTask.execute(dataTransfer);
        /*/markerImage.setImageResource(resource);
        //Glide.with(context).load(ImageRes).into(markerImage);
        //TextView txt_name = (TextView)marker.findViewById(R.id.name);
        //txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;*/
    }

    private static void getFriendsMovement(final Activity activity, final GoogleMap myMap, final ImageView imageView){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.friends).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.getId().equalsIgnoreCase(accessKeys.getDefaultUserId()) && document.get("user") != null) {
                            List<String> users = Arrays.asList(document.get("user").toString());
                            for (int i = 0; i < users.size(); i++) {
                                String []Info = users.get(i).split("~");
                                String Status = Info[1].replace("]","");
                                String currentUser = Info[0].replace("[","");
                                if (Status.equalsIgnoreCase("accepted")) {
                                    Count++;
                                    db.collection(constants.locator).whereEqualTo("date", ToDate()).whereEqualTo("userid", currentUser).whereEqualTo("live", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    if (document.get("location") != null) {
                                                        List<String> location = Arrays.asList(document.get("location").toString());
                                                        int locationSize = Arrays.asList(document.get("location")).size();
                                                        String []latlng = location.get(0).split(",");
                                                        //String latlng[] = location.get(locationSize - 1).split(",");
                                                        LatLng currentPosition = new LatLng(Double.parseDouble(latlng[latlng.length-2].replace("[","")), Double.parseDouble(latlng[latlng.length-1].replace("]","")));
                                                        //MarkerOptions markerOptions = new MarkerOptions();
                                                        //markerOptions.position(currentPosition);
                                                        //setCurrentUser(currentUser,myMap, markerOptions);
                                                        setCurrentUser(activity,currentUser,myMap,currentPosition);
                                                        imageView.setVisibility(View.VISIBLE);
                                                        if(circle!=null){
                                                            circle.remove();
                                                        }
                                                        switch (Count) {
                                                            case 1:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(10, 200, 90, 50)));
                                                                break;
                                                            case 2:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.BLACK).fillColor(Color.argb(20, 190, 80, 50)));
                                                                break;
                                                            case 3:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.BLUE).fillColor(Color.argb(30, 180, 70, 50)));
                                                                break;
                                                            case 4:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.GREEN).fillColor(Color.argb(40, 170, 60, 50)));
                                                                break;
                                                            case 5:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.MAGENTA).fillColor(Color.argb(50, 160, 50, 50)));
                                                                break;
                                                            case 6:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.LTGRAY).fillColor(Color.argb(60, 150, 40, 50)));
                                                                break;
                                                            case 7:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.DKGRAY).fillColor(Color.argb(70, 140, 30, 50)));
                                                                break;
                                                            case 8:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.CYAN).fillColor(Color.argb(80, 130, 20, 50)));
                                                                break;
                                                            case 9:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.YELLOW).fillColor(Color.argb(90, 120, 10, 50)));
                                                                break;
                                                            case 10:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(R.color.colorPrimaryDark).fillColor(Color.argb(100, 110, 5, 50)));
                                                                break;
                                                            default:
                                                                circle = myMap.addCircle(new CircleOptions().center(currentPosition).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));
                                                                Count = 0;
                                                                break;
                                                        }

                                                    }
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

    private static void getSelfMove(final Activity activity,final RoutingListener routingListener, final GoogleMap myMap, final CardView MySetInner, final TextView Loc, final ImageView defaultMarker, final TextView Estimate) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.friends)
                .document(accessKeys.getDefaultUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.get("live")!=null){
                            boolean Live = Boolean.parseBoolean(documentSnapshot.get("live").toString());
                            if(Live) {
                                db.collection(constants.locator).whereEqualTo("date", ToDate()).whereEqualTo("userid", accessKeys.getDefaultUserId()).whereEqualTo("live", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                if (document.get("location") != null) {
                                                    List<String> location = Arrays.asList(document.get("location").toString());
                                                    int locationSize = Arrays.asList(document.get("location")).size();
                                                    //String[] latlng = location.get(0).split(",");
                                                    //String latlng[] = location.get(locationSize - 1).split(",");
                                                    //LatLng currentPosition = new LatLng(Double.parseDouble(latlng[latlng.length - 2].replace("[", "")), Double.parseDouble(latlng[latlng.length - 1].replace("]", "")));
                                                    LatLng origin = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
                                                    String[] getDest = document.get("destination").toString().split(",");
                                                    LatLng destination = new LatLng(Double.parseDouble(getDest[0]), Double.parseDouble(getDest[1]));
                                                    //draw route
                                                    Routing routing = new Routing.Builder().travelMode(Routing.TravelMode.DRIVING).withListener(routingListener).waypoints(origin, destination).key(activity.getResources().getString(R.string.places_api_key)).build();
                                                    routing.execute();
                                                    //set marker
                                                    myMap.addCircle(new CircleOptions().center(destination).radius(5000.0).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));
                                                    MarkerOptions markerOptions = new MarkerOptions();
                                                    markerOptions.position(destination);
                                                    markerOptions.title("Your destination");
                                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                                    myMap.addMarker(markerOptions).showInfoWindow();
                                                    MySetInner.setCardBackgroundColor(activity.getResources().getColor(R.color.Red));
                                                    Loc.setText("Cancel Trip");
                                                    defaultMarker.setVisibility(View.GONE);
                                                    //count number of friends
                                                    //get time distance estimates
                                                    CalculateDistanceTime distance_task = new CalculateDistanceTime(activity);
                                                    distance_task.getDirectionsUrl(origin, destination);
                                                    distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                                                        @Override
                                                        public void taskCompleted(String[] time_distance) {

                                                            friendsCounts(activity, Estimate, time_distance[0], time_distance[1], false);
                                                            //Send my Location to server per 5minutes
                                                            //Go Live
                                                            backgroundLocationTracker.setOntheMove(true);
                                                            backgroundLocationTracker.setOnMoveLat(getDest[0]);
                                                            backgroundLocationTracker.setOnMoveLng(getDest[1]);
                                                            //activateLive();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }else{
                            backgroundLocationTracker.setOntheMove(false);
                        }
                    }
                });
    }



    static class DownloadBitMapTask extends AsyncTask<Object,String,Bitmap> {
    String url;
    Canvas canvas;
    Paint color;
    String Name;
    MarkerOptions markerOptions;
    Marker newMarker;
    GoogleMap Map;

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Bitmap doInBackground(Object... objects) {
            url = (String)objects[0];
            canvas = (Canvas) objects[1];
            color = (Paint) objects[2];
            Name = (String) objects[3];
            markerOptions = (MarkerOptions) objects[4];
            newMarker = (Marker) objects[5];
            Map = (GoogleMap) objects[6];

            return globalMethods.getBitmapFromURL(url);
        }


        protected void onPostExecute(Bitmap bitmap) {
            // dismiss progress dialog and update ui
            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

            /*Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_map_pin_filled_blue_48dp);
            background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
            vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
            Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            background.draw(canvas);
            vectorDrawable.draw(canvas);*/

            canvas.drawBitmap(bitmap,0,0,color);
            canvas.drawText(Name, 30, 40, color);

            /*markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            markerOptions.anchor(0.5f,1);
            marker = Map.addMarker(markerOptions);
            marker.showInfoWindow();
            markers.add(marker);*/

        }

        private static BitmapDescriptor bitmapDescriptor (Context context, int vectorResId){
            Drawable vecorDrawable = ContextCompat.getDrawable(context,vectorResId);
            vecorDrawable.setBounds(0,0,vecorDrawable.getIntrinsicWidth(),vecorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vecorDrawable.getIntrinsicWidth(),vecorDrawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vecorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }


    }

    static class DownloadImageBitMapTask extends AsyncTask<Object,Bitmap,Bitmap> {
        String url;
        ImageView imageView;
        Context context;
        Activity activity;
        View view;
        String Name;
        GoogleMap Map;
        LatLng Position;

        protected void onPreExecute() {
            //display progress dialog.

        }

        protected Bitmap doInBackground(Object... objects) {
            url = (String)objects[0];
            imageView = (ImageView) objects[1];
            context = (Context) objects[2];
            activity = (Activity) objects[3];
            view = (View) objects[4];
            Name = (String) objects[5];
            Map = (GoogleMap) objects[6];
            Position = (LatLng) objects[7];
            return globalMethods.getBitmapFromURL(url);
        }


        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            //Glide.with(context).load(globalMethods.BitMapToString(bitmap)).asBitmap().into(imageView);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            Bitmap bitMap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitMap);
            view.draw(canvas);
            marker = Map.addMarker(new MarkerOptions().position(Position));
            marker.setIcon((BitmapDescriptorFactory.fromBitmap(bitMap)));
            marker.setTitle(Name +" :- "+globalMethods.getStringAddress(activity,Position.latitude,Position.longitude));
            marker.showInfoWindow();
            markers.add(marker);
        }

    }
}
