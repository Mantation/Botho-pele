package menuFragment.Road;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.RoadCommentsAdapter;
import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import io.eyec.bombo.bothopele.backgroundLocationTracker;
import menuFragment.Movement.CalculateDistanceTime;
import methods.globalMethods;
import properties.accessKeys;

import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.InitializeFirstLetter;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.clearList;
import static methods.globalMethods.getDistanceBetween;
import static methods.globalMethods.getStringAddress;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class Locator extends Fragment implements OnMapReadyCallback,View.OnClickListener,FragmentManager.OnBackStackChangedListener{
    View myview;
    TextView Loc;
    FrameLayout frameLayout;
    GoogleMap myMap;
    CardView MySet;
    static ProgressBar progressBar;
    static FragmentManager fm;
    public static RoadCommentsAdapter myAdapter;

    static String myDocRef;

    public static String getMyDocRef() {
        return myDocRef;
    }

    public static void setMyDocRef(String myDocRef) {
        Locator.myDocRef = myDocRef;
    }

    public Locator() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_locator, container, false);
        MySet = myview.findViewById(R.id.MySet);
        frameLayout = myview.findViewById(R.id.map);
        Loc = myview.findViewById(R.id.location);
        progressBar = myview.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        MySet.setVisibility(View.GONE);
        fm = ((MainActivity) getActivity()).getSupportFragmentManager();/// getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        MySet.setOnClickListener(this);
        ((MainActivity)getActivity()).getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                fm.popBackStack();
                fm.popBackStack();
            }
        });
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    boolean locationLabel;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng Now = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
        //googleMap.addMarker(new MarkerOptions().position(Now)
        //        .title("Your Location")).showInfoWindow();
        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        Now,11f));
        myMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double CameraLat = myMap.getCameraPosition().target.latitude;
                double CameraLong = myMap.getCameraPosition().target.longitude;
                if (locationLabel){
                    //Loc.setText(getStringAddress(getActivity(),CameraLat,CameraLong));
                    Loc.setText("I see it here!");
                    //Submit Info here
                }else{
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
        getFirestoreCoordinates(getMyDocRef(),myMap);
    }

    //Report incident Location Time
    public static void reportIncidentTimePlace(final String Reporter, final String IncidentTime, final String LastSeenPlace, final String TargetLat, final String TargetLong, final String Document, final TabLayout tbLayout, final View view, final CardView cardView) {
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("userid", accessKeys.getDefaultUserId());
            user.put("reporter", Reporter);
            user.put("incidentTime", IncidentTime);
            user.put("parentDocument", Document);
            user.put("incidentLastSeenPlace", LastSeenPlace);
            user.put("incidentCoordinates", TargetLat + "," + TargetLong);//accessKeys.getTargetLat() + "," + accessKeys.getTargetLong());
            user.put("submitterCoordinates", accessKeys.getLatitude() + "," + accessKeys.getLongitude());
            user.put("name", InitializeFirstLetter(accessKeys.getName())+" "+InitializeFirstLetter(accessKeys.getSurname()));
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("Image", accessKeys.getUserImage());
            user.put("comment", "none");
            user.put("commentImage", "none");
            user.put("locator", true);
            user.put("distanceDifference", getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()), Double.parseDouble(TargetLat), Double.parseDouble(TargetLong)));
            // Add a new document with a generated ID
            db.collection(constants.roadComments).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    final String document = documentReference.getId();
                    CollectionReference collectionReference = db.collection(constants.roadComments);
                    collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //set Realm user information;
                                Loginfo("missing profile Details successfully added");
                                cardView.setVisibility(View.VISIBLE);
                                tbLayout.getTabAt(1).select();
                                globalMethods.ConfirmResolution(view, "information updated!");
                                myAdapter.notifyDataSetChanged();
                            } else {
                                Logerror("unable to add missing profile Details, ");
                            }
                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(Constraints.TAG, "Error adding document", e);
                    Logerror("unable to add missing profile Details, " + e.getMessage());
                }
            });
        } catch (Exception exception) {
            exception.getMessage();
            exception.printStackTrace();
        }
    }

    // update recyclerview
    private static void updateRecycler(final Activity activity, final String Reporter, final String IncidentTime, final String LastSeenPlace, final String TargetLat, final String TargetLong, final String Document, final TabLayout tbLayout, final View view, final CardView cardView, final int count, final String messageContent,final String pictureContent){
        //get initial values
        String image_[] = new String[myAdapter.Dates.length];
        String name_[] = new String[myAdapter.Dates.length];
        String dates_[] = new String[myAdapter.Dates.length];
        String times_[] = new String[myAdapter.Dates.length];
        String messages_[] = new String[myAdapter.Dates.length];
        String commentImage_[] = new String[myAdapter.Dates.length];
        String IncidentTime_[] = new String[myAdapter.Dates.length];
        String IncidentPlace_[] = new String[myAdapter.Dates.length];
        boolean locator_[] = new boolean [myAdapter.Dates.length];

        for (int i = 0; i < myAdapter.Dates.length; i++) {
            dates_[i] = myAdapter.Dates[i];
            times_[i] = myAdapter.Times[i];
            messages_[i] = myAdapter.Comment[i];
            image_[i] = myAdapter.Image[i];
            name_[i] = myAdapter.Name[i];
            commentImage_[i] = myAdapter.CommentImage[i];
            IncidentTime_[i] = myAdapter.IncidentTime[i];
            IncidentPlace_[i] = myAdapter.IncidentPlace[i];
            locator_[i] = myAdapter.Locator[i];
        }
        //extend array
        myAdapter.Dates = new String[count + 1];
        myAdapter.Times = new String[count + 1];
        myAdapter.Comment = new String[count + 1];
        myAdapter.Image = new String[count + 1];
        myAdapter.Name = new String[count + 1];
        myAdapter.CommentImage = new String[count + 1];
        myAdapter.IncidentPlace = new String[count + 1];
        myAdapter.IncidentTime = new String[count + 1];
        myAdapter.Locator = new boolean[count + 1];

        //reallocate variables
        for (int x = 0; x < dates_.length; x++) {
            myAdapter.Dates[x] = dates_[x];
            myAdapter.Times[x] = times_[x];
            myAdapter.Comment[x] = messages_[x];
            myAdapter.Image[x] = image_[x];
            myAdapter.Name[x] = name_[x];
            myAdapter.CommentImage[x] = commentImage_[x];
            myAdapter.IncidentPlace[x] = IncidentPlace_[x];
            myAdapter.IncidentTime[x] = IncidentTime_[x];
            myAdapter.Locator[x] = locator_[x];
        }
        //allocate variables
        myAdapter.Dates[count] = ToDate();
        myAdapter.Times[count] = Time();
        myAdapter.Comment[count] = messageContent;
        myAdapter.Image[count] = accessKeys.getUserImage();
        myAdapter.Name[count] = InitializeFirstLetter(accessKeys.getName())+" "+InitializeFirstLetter(accessKeys.getSurname());
        myAdapter.CommentImage[count] = pictureContent;
        myAdapter.IncidentTime[count] = IncidentTime;
        myAdapter.IncidentPlace[count] = LastSeenPlace;
        myAdapter.Locator[count] = true;
        myAdapter.notifyDataSetChanged();
        reportIncidentTimePlace(Reporter,IncidentTime,LastSeenPlace,TargetLat,TargetLong,Document,tbLayout, view, cardView);
    }



    @Override
    public void onClick(View v) {
        if(accessKeys.isApproval()){
            MySet.setVisibility(View.GONE);
            TabLayout tbLayout=(TabLayout) getActivity().findViewById(R.id.tab);
            LatLng center = myMap.getCameraPosition().target;
            String Reporter= "";
            if (accessKeys.getGender().equalsIgnoreCase("male")) {
                Reporter = "Mr " + InitializeFirstLetter(accessKeys.getName()) + " " + InitializeFirstLetter(accessKeys.getSurname());
            } else {
                Reporter = "Ms " + InitializeFirstLetter(accessKeys.getName()) + " " + InitializeFirstLetter(accessKeys.getSurname());
            }
            int count = myAdapter.getItemCount();
            updateRecycler(getActivity(),Reporter,"now",getStringAddress(getActivity(), center.latitude,center.longitude),String.valueOf(center.latitude),String.valueOf(center.longitude),getMyDocRef(),tbLayout,
                    v,MySet,count,"none","none");
        }else{
            Toast.makeText(getActivity(), "Cannot perform transaction, your verification status is pending!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        Fragment oldFragment = fm.findFragmentById(R.id.map);
        if (oldFragment != null) {
            try {
                //getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
            }catch (Exception e){
                e.printStackTrace();
                FragmentManager fragmentManager = ((MainActivity) getActivity()).getSupportFragmentManager();
                final Fragment current = fragmentManager.findFragmentById(R.id.map);
                if(current != null){
                    ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(current).commitAllowingStateLoss();
                }
                MainActivity.returnToRoadHome = false;
            }finally {
                MainActivity.returnToRoadHome = false;
            }
        }
    }


    @Override
    public void onBackStackChanged() {
        ((MainActivity)getActivity()).getSupportFragmentManager().popBackStack();
        ((MainActivity)getActivity()).getSupportFragmentManager().popBackStack();
    }

    static ArrayList<Double> CoordsLat = new ArrayList<>();
    static ArrayList<Double> CoordsLng = new ArrayList<>();
    static ArrayList<String> Coords = new ArrayList<>();
    //get all documents from firestore
    public static  void getFirestoreCoordinates(final String DocumentRef, final GoogleMap Map){
        clearList(CoordsLat);
        clearList(CoordsLng);
        clearList(Coords);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.roadComments).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
            }});
        final FirebaseFirestore dbs = FirebaseFirestore.getInstance();
        dbs.collection(constants.roadComments)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            getCoordinates(DocumentRef,Map);
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }

    //get patients
    public static void getCoordinates(final String DocumentRef, final GoogleMap Map){
        clearList(CoordsLat);
        clearList(CoordsLng);
        clearList(Coords);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.roadComments)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("parentDocument")!=null  && document.get("date")!=null &&  document.get("time")!=null && document.get("comment")!=null && document.get("name")!=null && document.get("document ref")!=null &&
                                        document.get("Image")!=null  && document.get("commentImage")!=null && document.get("userid")!=null &&  document.get("incidentTime")!=null && document.get("incidentLastSeenPlace")!=null &&  document.get("locator")!=null) {
                                    if(DocumentRef.equals(document.get("parentDocument").toString())) {
                                        if(document.get("submitterCoordinates") != null){
                                            String []coordinates = document.get("submitterCoordinates").toString().split(",");
                                            Map.addCircle(new CircleOptions()
                                                    .center(new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])))
                                                    .radius(500)
                                                    .strokeColor(Color.RED)
                                                    .fillColor(Color.BLUE));
                                            CoordsLat.add(Double.parseDouble(coordinates[0]));
                                            CoordsLng.add(Double.parseDouble(coordinates[1]));
                                            Coords.add(Double.parseDouble(coordinates[0])+","+Double.parseDouble(coordinates[1]));
                                        }
                                    }
                                }
                            }
                            Collections.sort(CoordsLat);
                            Collections.sort(CoordsLng);
                            double highLat = CoordsLat.get(0);
                            double highLng = CoordsLng.get(0);
                            double LowLat = CoordsLat.get(CoordsLat.size()-1);
                            double LowLng = CoordsLng.get(CoordsLng.size()-1);
                            double AvgLat = (highLat + LowLat) / 2;
                            double AvgLng = (highLng + LowLng) / 2;
                            LatLng midPoint  = new LatLng(AvgLat,AvgLng);
                            //calculate the circumference
                            double circumference = (Double.parseDouble(globalMethods.getDistanceBetween(AvgLat,AvgLng,highLat,highLng).replace(",","."))*1000)+1000;
                            Map.addCircle(new CircleOptions().center(midPoint).radius(circumference).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));




                            /*double totalLat = 0;
                            double totalLng = 0;
                            for (int i = 0; i < CoordsLat.size(); i++) {
                                totalLat+=CoordsLat.get(i);
                                totalLng+=CoordsLng.get(i);
                            }
                            double midLat = totalLat/CoordsLat.size();
                            double midLng = totalLng/CoordsLng.size();
                            LatLng average = new LatLng(midLat,midLng);*/




                            /*Collections.sort(Coords);
                            String []High = Coords.get(0).split(",");
                            String []Low = Coords.get(Coords.size()-1).split(",");
                            double highLat = Double.parseDouble(High[0]);
                            double highLng = Double.parseDouble(High[1]);
                            double LowLat = Double.parseDouble(Low[0]);
                            double LowLng = Double.parseDouble(Low[1]);
                            double AvgLat = (highLat + LowLat) / 2;
                            double AvgLng = (highLng + LowLng) / 2;
                            LatLng midPoint  = new LatLng(AvgLat,AvgLng);
                            //calculate the circumference
                            double circumference = (Double.parseDouble(globalMethods.getDistanceBetween(AvgLat,AvgLng,highLat,highLng).replace(",","."))*1000)+1000;
                            Map.addCircle(new CircleOptions().center(midPoint).radius(circumference).strokeWidth(1f).strokeColor(Color.RED).fillColor(Color.argb(70, 150, 50, 50)));*/
                        }
                    }
                });
    }





}
