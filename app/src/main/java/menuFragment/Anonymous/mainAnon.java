package menuFragment.Anonymous;


import android.app.Activity;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.DialogFragments.incidentList;
import menuFragment.maps.MapView;
import methods.globalMethods;
import properties.accessKeys;

import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.InitializeFirstLetter;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.getDistanceBetween;
import static methods.globalMethods.getStringAddress;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class mainAnon extends android.app.Fragment implements View.OnTouchListener, View.OnClickListener{
    View myview;
    EditText placeofLastSeen;
    EditText Additional;
    CardView Submit;
    ConstraintLayout mainLayer;
    static ProgressBar progressBar;
    public static incidentList incident;
    public static String incidentType;
    static TextView type;
    CardView Type;

    public static void setIncidentInfo(final Activity activity, final String text) {
        type.setText(text);
        type.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        incidentType = text;
    }


    public mainAnon() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_main_anon, container, false);
        Additional = myview.findViewById(R.id.body);
        placeofLastSeen = myview.findViewById(R.id.placeofLastSeen);
        Submit = myview.findViewById(R.id.MySubmit);
        mainLayer = myview.findViewById(R.id.mainLayer);
        progressBar = myview.findViewById(R.id.progress);
        Type = myview.findViewById(R.id.type);
        type = myview.findViewById(R.id.incidentview);
        progressBar.setVisibility(View.GONE);
        placeofLastSeen.setText("");
        placeofLastSeen.setOnTouchListener(this);
        Type.setOnTouchListener(this);
        Submit.setOnClickListener(this);
        setExitApplication(false);
        return  myview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(accessKeys.getTargetLat()!=null && accessKeys.getTargetLong() !=null && MapView.isOnAnon()){
            if (incidentType != null) {
                type.setText(incidentType);
                type.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }
            placeofLastSeen.setText(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong()))+" km");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(accessKeys.getTargetLat()!=null && accessKeys.getTargetLong() !=null && MapView.isOnAnon()){
            if (incidentType != null) {
                type.setText(incidentType);
                type.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }
            placeofLastSeen.setText(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong()))+" km");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(accessKeys.getTargetLat()!=null && accessKeys.getTargetLong() !=null && MapView.isOnAnon()){
            if (incidentType != null) {
                type.setText(incidentType);
                type.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }
            placeofLastSeen.setText(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong()))+" km");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        FragmentManager fm = ((MainActivity) getActivity()).getSupportFragmentManager();
        Fragment oldFragment = fm.findFragmentById(R.id.map);
        if (oldFragment != null) {
            //getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
            ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
        String myPlace = placeofLastSeen.getText().toString();
        String myInfo = Additional.getText().toString();
        String myType = type.getText().toString();
        String Reporter = "";
        if (myInfo.isEmpty() || myInfo.length() < 20) {
            Toast.makeText(getActivity(), "Please be more descriptive", Toast.LENGTH_LONG).show();
            Additional.requestFocus();
        } else if (myType.isEmpty()) {
            Toast.makeText(getActivity(), "Please select an incident type", Toast.LENGTH_LONG).show();
            type.requestFocus();
        } else if (myPlace.isEmpty()) {
            placeofLastSeen.requestFocus();
            Toast.makeText(getActivity(), "Please indicate where the incident happened", Toast.LENGTH_LONG).show();
        } else {
                if (globalMethods.isNetworkAvailable(getActivity())) {

                    if(accessKeys.getGender().equalsIgnoreCase("male")){
                        Reporter = "Mr " + InitializeFirstLetter(accessKeys.getName()) + " " + InitializeFirstLetter(accessKeys.getSurname());
                    }else{
                        Reporter = "Ms " + InitializeFirstLetter(accessKeys.getName()) + " " + InitializeFirstLetter(accessKeys.getSurname());
                    }
                    //Disable all views
                    for (int i = 0; i < mainLayer.getChildCount(); i++) {
                        View child = mainLayer.getChildAt(i);
                        child.setEnabled(false);
                    }
                    submitAnonymousReport(v,Reporter,myType,myPlace,myInfo,placeofLastSeen,type,Additional,mainLayer);
                }else{
                    globalMethods.networkerror(getActivity());
                }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if(id == R.id.placeofLastSeen){
            MapView.setOnAnon(true);
            methods.globalMethods.loadFragments(R.id.main, new MapView(), getActivity());
        }else{
            final android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
            incident = new incidentList();
            incident.show(fragmentManager, "so");
        }

        return false;
    }

    //sending user Message
    private static void submitAnonymousReport(final View view, final String Reporter,final String type, final String LastSeenPlace, final String Additional, final TextView Place,final TextView Location, final TextView Report, final ConstraintLayout Layout){
        progressBar.setVisibility(View.VISIBLE);
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("userid", accessKeys.getDefaultUserId());
            user.put("incidentType", type);
            user.put("reporter", Reporter);
            user.put("report", Additional);
            user.put("reportLocation", LastSeenPlace);
            user.put("submitterCoordinates",accessKeys.getLatitude() +","+accessKeys.getLongitude());
            user.put("reportCoordinates",accessKeys.getTargetLat() +","+accessKeys.getTargetLong());
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("distanceDifference",getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            // Add a new document with a generated ID
            db.collection(constants.anon)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.anon);
                            collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //set Realm user information;
                                        progressBar.setVisibility(View.GONE);
                                        Loginfo("missing profile Details successfully added");
                                        Place.setText("");
                                        Report.setText("");
                                        Location.setText("");
                                        accessKeys.setTargetLat(null);
                                        accessKeys.setTargetLong(null);
                                        //Enable all views
                                        for (int i = 0; i < Layout.getChildCount(); i++) {
                                            View child = Layout.getChildAt(i);
                                            child.setEnabled(true);
                                        }
                                        globalMethods.ConfirmResolution(view,"Information successfully submitted!");
                                    }else{
                                        progressBar.setVisibility(View.GONE);
                                        globalMethods.stopProgress = true;
                                        Logerror("unable to add missing profile Details, ");
                                    }
                                }

                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.w(Constraints.TAG, "Error adding document", e);
                            globalMethods.stopProgress = true;
                            Logerror("unable to add missing profile Details, " + e.getMessage());
                        }
                    });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
        }
    }

}
