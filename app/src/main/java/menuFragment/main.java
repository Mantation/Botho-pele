package menuFragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import adapters.MissingSlinder;
import connectionHandler.external.missingSlider_;
import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.backgroundLocationTracker;
import io.eyec.bombo.bothopele.R;
import io.eyec.bombo.bothopele.messagingHelper;
import menuFragment.Anonymous.mainAnon;
import menuFragment.Departments.Departments;
import menuFragment.Missing.mainMissing;
import menuFragment.Movement.mainMovement;
import menuFragment.Notifications.soloNotification;
import menuFragment.Road.mainRoad;
import menuFragment.Stations.policeStations;
import menuFragment.maps.MapView;
import methods.globalMethods;
import properties.accessKeys;

import static android.content.ContentValues.TAG;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static properties.accessKeys.getDefaultDocument;
import static properties.accessKeys.getDefaultUserId;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class main extends android.app.Fragment implements View.OnClickListener{
    View myview;
    TextView Auth;
    CardView roadSide;
    CardView missingPeople;
    CardView crime;
    CardView anonReport;
    CardView depressionAnxiety;
    CardView policeStation;
    FloatingActionButton addNew;
    static ProgressBar progressBar;
    private ViewPager viewPager;
    private static LinearLayout DotLayout;
    static ConstraintLayout mainLayer;
    private MissingSlinder slider;
    private static TextView [] mDots;
    private static TextView [] mDocuments;
    private static ImageView[] mImageView;
    private static int value;
    private static boolean locationFound;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    public static LocationManager runnablelocationManager;
    public static LocationListener runnablelocationListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference friendsRef;
    private DocumentReference approvalRef;
    public static boolean onMain;

    public static int getValue() {
        return value;
    }

    public static void setValue(int value) {
        main.value = value;
    }

    public main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_main, container, false);
        Auth = myview.findViewById(R.id.auth);
        roadSide = myview.findViewById(R.id.MyRoadSide);
        missingPeople = myview.findViewById(R.id.MyMissing);
        crime = myview.findViewById(R.id.MyCrime);
        anonReport = myview.findViewById(R.id.MyAnonimity);
        depressionAnxiety = myview.findViewById(R.id.MyDepression);
        policeStation = myview.findViewById(R.id.MyStations);
        addNew = myview.findViewById(R.id.send);
        viewPager = myview.findViewById(R.id.pager);
        DotLayout = myview.findViewById(R.id.linearLayout);
        progressBar = myview.findViewById(R.id.progress);
        mainLayer = myview.findViewById(R.id.main);
        roadSide.setOnClickListener(this);
        missingPeople.setOnClickListener(this);
        anonReport.setOnClickListener(this);
        depressionAnxiety.setOnClickListener(this);
        policeStation.setOnClickListener(this);
        crime.setOnClickListener(this);
        //if(accessKeys.isApproval())
        Auth.setVisibility(View.GONE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener  = new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                //if(!isLocationGrabbed) {
                boolean LocationEmpty = false;
                if(accessKeys.getLongitude()== null && accessKeys.getLatitude()==null) {
                    LocationEmpty = true;
                }
                System.out.println(location.getLatitude()+ ": "+location.getLongitude());
                accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                if (LocationEmpty){
                    accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                    accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                }
                for (int i = 0; i < mainLayer.getChildCount(); i++) {
                    View child = mainLayer.getChildAt(i);
                    child.setEnabled(true);
                    child.setClickable(true);
                }
                progressBar.setVisibility(View.GONE);

                //connectionHandler.external.spas_.getAllDocuments(getActivity(), recyclerView, progressBar,imageView);
                //    isLocationGrabbed = true;
                //}
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        globalMethods.geRoadLocationPermission(getActivity(), locationManager, locationListener);
        connectionHandler.external.missingSlider_.getDBDetails(getActivity(),viewPager);
        if(accessKeys.getLatitude() == null && accessKeys.getLongitude() == null){
            for (int i = 0; i < mainLayer.getChildCount(); i++) {
                View child = mainLayer.getChildAt(i);
                child.setEnabled(false);
                child.setClickable(false);
            }
            checkLocation();
        }else{
            progressBar.setVisibility(View.GONE);
        }
        if(missingSlider_.getTotal() > 0) {
            addDots(getActivity(), 0);
            resetCounter = 0;
            isCounted = false;
            selected = 0;
        }
        slideshow(viewPager);
        RejectedImage(getActivity());
        AlignDots(getActivity());
        setExitApplication(true);
        MapView.initiateMapContent();
        return myview;
    }

    public void checkLocation(){
        try{
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                new AlertDialog.Builder(getActivity())
                        .setMessage("Kindly enable your location")
                        .setTitle("Location")
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                locationFound = false;
                                MonitorLocation(getActivity());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                System.exit(0);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }catch (Exception e){
            Log.d(TAG, "errror is ",e);
            System.out.println(e);
        }

    }

    static int count;
    static boolean isCounted;
    static int totalCounts;
    static int resetCounter;
    static final int totalCountDown = 6;
    public static void addDots(final Activity activity, final int position){
        if(activity!=null) {
            /*final List Name = new ArrayList<String>();
            final List Images = new ArrayList<String>();
            final List Document = new ArrayList<String>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(constants.missing).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(totalCounts != task.getResult().size()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String Doc = String.valueOf(document.get("document ref"));
                                String img = String.valueOf(document.get("missinImage"));
                                String name = String.valueOf(document.get("victimName"));
                                Name.add(name);
                                Images.add(img);
                                Document.add(Doc);
                            }
                        }
                    }*/
                        int x = 0;
                        count = missingSlider_.getTotal();
                        mDots = new TextView[count];
                        DotLayout.removeAllViews();
                        while (count > 0) {
                            //Add Dots
                            if (activity != null) {
                                mDots[x] = new TextView(activity);
                                mDots[x].setText(Html.fromHtml("&#8226"));
                                mDots[x].setTextSize(50);
                                mDots[x].setTextColor(activity.getResources().getColor(R.color.colorTransparent));
                                DotLayout.addView(mDots[x]);
                                x++;
                                count--;
                            }
                        }


                    if (mDots.length > 0) {
                        if(position == 0){
                            resetCounter = 0;
                            isCounted = true;
                            totalCounts = missingSlider_.getTotal();
                            for (int i = 0; i < mDots.length; i++) {
                                mDots[i].setTextColor(activity.getResources().getColor(R.color.colorTransparent));
                            }
                            mDots[position].setTextColor(activity.getResources().getColor(R.color.colorAccent));
                        }else{
                            mDots[position-1].setTextColor(activity.getResources().getColor(R.color.colorTransparent));
                            if(position%6==0){
                                for (int i = 0; i < mDots.length; i++) {
                                    mDots[i].setTextColor(activity.getResources().getColor(R.color.colorTransparent));
                                }
                                mDots[0].setTextColor(activity.getResources().getColor(R.color.colorAccent));
                                resetCounter = 6;
                            }else{
                                resetCounter--;
                                for (int i = 0; i < mDots.length; i++) {
                                    mDots[i].setTextColor(activity.getResources().getColor(R.color.colorTransparent));
                                }
                                if(resetCounter > 0){
                                    mDots[totalCountDown - resetCounter].setTextColor(activity.getResources().getColor(R.color.colorAccent));
                                }else{
                                    mDots[position].setTextColor(activity.getResources().getColor(R.color.colorAccent));
                                }

                            }
                        }
                    }
                }
            //});
        //}
    }


    public void AlignDots(final Activity activity){

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selected = position;
                addDots(activity,position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private static int selected;
    public static int period = 5000;
    public static void slideshow(final ViewPager mPager){
        // Auto start of viewpager

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            int newcount = getValue();
            public void run() {
                period = 5000;
                if (getValue() < missingSlider_.getTotal()-2) {//missingSlider_.getTotal()
                    if (getValue()> selected){
                        newcount =selected+1;
                        setValue(newcount);
                        period+=5000;
                    }else if (getValue()< selected){
                        newcount =selected+1;
                        setValue(newcount);
                        period+=5000;
                    }else{
                        setValue(newcount);
                    }
                } else {
                    newcount = 0;
                    setValue(newcount);
                }
                if (newcount < missingSlider_.getTotal()-2) {
                    mPager.setCurrentItem(getValue(), true);
                    newcount++;
                }
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);

            }
        }, period, period);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.MyRoadSide) {
            methods.globalMethods.loadFragmentWithTag(R.id.main, new mainRoad(), getActivity(),"road");
        }else if(id==R.id.MyMissing) {
            methods.globalMethods.loadFragmentWithTag(R.id.main, new mainMissing(), getActivity(),"missing");
        }else if(id==R.id.MyAnonimity){
        methods.globalMethods.loadFragmentWithTag(R.id.main, new mainAnon(), getActivity(),"anonymous");
        }else if(id==R.id.MyDepression){
        methods.globalMethods.loadFragmentWithTag(R.id.main, new Departments(), getActivity(),"anonymous");
        }else if(id==R.id.MyStations){
            methods.globalMethods.loadFragmentWithTag(R.id.main, new policeStations(), getActivity(),"anonymous");
        }else if(id==R.id.MyCrime){
            methods.globalMethods.loadFragmentWithTag(R.id.main, new mainMovement(), getActivity(),"anonymous");
        }

    }

    boolean isApproved;
    @Override
    public void onStart() {
        friendsRef = db.document(constants.friends+"/"+accessKeys.getDefaultUserId());
        friendsRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG,e.toString());
                    return;
                }
                if(documentSnapshot.exists()) {
                    List<String> users = Arrays.asList(documentSnapshot.get("user").toString());
                    String []allValues = users.get(0).split(",");
                    for (int i = 0; i < allValues.length; i++) {
                        String []Info = allValues[i].split("~");
                        String Status = Info[1].replace("]","").trim();
                        String User = Info[0].replace("[","").trim();
                        if (Status.equalsIgnoreCase("pending") && documentSnapshot.getId().equalsIgnoreCase(getDefaultUserId())) {
                            db.collection(constants.users).whereEqualTo("userid", User).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    boolean isFound = false;
                                    String Name = "";
                                    String Surname = "";
                                    String UserId = "";
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            if (document.get("userid") != null) {
                                                String gender = "";
                                                if (document.get("gender").toString().equalsIgnoreCase("male")) {
                                                    Name = "Mr, " + document.get("name").toString();
                                                } else {
                                                    Name = "Ms, " + document.get("name").toString();
                                                }
                                                Surname = document.get("surname").toString();
                                                UserId = document.get("userid").toString();
                                                isFound = true;

                                            }
                                        }
                                        if (isFound) {
                                            acceptInvite(getActivity(), Name, Surname, UserId, friendsRef);
                                        }
                                    }
                                }
                            });

                        }
                    }

                }
            }
        });
        approvalRef = db.document(constants.users+"/"+getDefaultDocument());
        approvalRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG,e.toString());
                    return;
                }
                if(documentSnapshot.exists()) {
                    Boolean Approved = Boolean.parseBoolean(documentSnapshot.get("approved").toString());
                    if(isApproved && Approved){
                        InitiateApprovalDialog(getActivity());
                        isApproved = false;
                    }
                    if(!Approved){
                        isApproved = true;
                        Auth.setVisibility(View.VISIBLE);
                    }else{
                        Auth.setVisibility(View.GONE);
                        Random random = new Random();
                        int num = random.nextInt(30);
                        if((num+1) % 27 == 0){
                            RateApp(getActivity());
                        }
                    }
                }
            }
        });
        db.collection(constants.info)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(ContentValues.TAG,e.toString());
                            return;
                        }
                        for (QueryDocumentSnapshot queryDocumentSnapshot :queryDocumentSnapshots) {
                            if(queryDocumentSnapshot.get("active")!=null){
                                boolean Active = Boolean.parseBoolean(queryDocumentSnapshot.get("active").toString());
                                if (Active & !MainActivity.infoDisplayed){
                                    MainActivity.infoDisplayed = true;
                                    String Image = queryDocumentSnapshot.get("image").toString();
                                    String Info = queryDocumentSnapshot.get("info").toString();
                                    InformationDialog(getActivity(),Image,Info);
                                }
                            }
                        }
                    }
                });

        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        onMain = false;
    }
    @Override
    public void onDestroy() {
        handler.removeCallbacks(myRunnable);
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(myRunnable);
        handler.removeMessages(0);
        onMain = false;
        super.onDestroy();
    }
    //update PicturePath
    public static void readFriendship(final Activity activity, final String userid){
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> user = new HashMap<>();
            user.put("user", Arrays.asList(accessKeys.getDefaultUserId()+"~accepted"));
            user.put("live", false);

            // Add a new document with a generated ID
            db.collection(constants.friends).document(userid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //set Realm user information;
                    Loginfo("user Personal Details successfully added");
                    globalMethods.stopProgress = true;
                    Toast.makeText(activity,"request accepted",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                    globalMethods.stopProgress = true;
                    Logerror("unable to add selected user!, " + e.getMessage());
                }
            });

        }catch (Exception exception){
            Log.w(TAG, "Error writing document", exception);
            globalMethods.stopProgress = true;
            exception.getMessage();
            exception.printStackTrace();
        }
    }
    //accept friend invite
    static Dialog myDialog;
    public static void acceptInvite(final Activity activity, final String Name, final String Surname, final String userid ,final DocumentReference docRef) {
        //initiate
        if (myDialog == null) {
            myDialog = new Dialog(activity);
            myDialog.setContentView(R.layout.friendconfirmlayout);
            myDialog.setCancelable(true);
            final TextView name = (TextView) myDialog.findViewById(R.id.name);
            final TextView surname = (TextView) myDialog.findViewById(R.id.surname);
            final CardView confirm = (CardView) myDialog.findViewById(R.id.MyConfirm);
            final CardView reject = (CardView) myDialog.findViewById(R.id.MyReject);
            name.setText(Name);
            surname.setText(Surname);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                    docRef.update("user", FieldValue.arrayUnion(userid+"~accepted"));
                    docRef.update("user", FieldValue.arrayRemove(userid+"~pending"));
                    Loginfo("user Personal Details successfully added");
                    globalMethods.stopProgress = true;
                    readFriendship(activity, userid);
                    myDialog = null;
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                    docRef.update("user", FieldValue.arrayUnion(userid+"~rejected"));
                    docRef.update("user", FieldValue.arrayRemove(userid+"~pending"));
                    Loginfo("user Personal Details successfully added");
                    globalMethods.stopProgress = true;
                    Toast.makeText(activity, "request rejected", Toast.LENGTH_LONG).show();
                    myDialog = null;
                }
            });
            myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                    myDialog = null;
                }
            });

            myDialog.show();
        }
    }
    // reject ID/Profile picture document
    public static void RejectedImage(final Activity activity) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users).whereEqualTo("userid", accessKeys.getDefaultUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String documentRef = document.get("document ref").toString();
                        Boolean approved = Boolean.parseBoolean(document.get("approved").toString());
                        if(!approved){
                            if(document.get("IDRejectionReason")!=null){
                                if (!document.get("IDRejectionReason").toString().equalsIgnoreCase("awaiting authorization")) {
                                    rejectedDialog(activity, accessKeys.getIdImage(), document.get("IDRejectionReason").toString(), documentRef, true);
                                }else{
                                    if(document.get("profileImageReason")!=null){
                                        if (!document.get("profileImageReason").toString().equalsIgnoreCase("awaiting authorization"))
                                            rejectedDialog(activity,accessKeys.getUserImage(),document.get("profileImageReason").toString(),documentRef, false);
                                    }
                                }
                            }else if(document.get("profileImageReason")!=null){
                                if (!document.get("profileImageReason").toString().equalsIgnoreCase("awaiting authorization"))
                                    rejectedDialog(activity,accessKeys.getUserImage(),document.get("profileImageReason").toString(),documentRef, false);
                            }

                        }
                    }
                }
            }
        });
    }
    //set selected image on imageview
    public static void setProfileImage(Activity activity, String myImage) {
        Glide.with(activity).load(myImage).into(newImage);
        mySelectedImage = myImage;
    }
    static ImageView newImage;
    static String mySelectedImage;
    //display rejectedDialog
    public static void rejectedDialog(final Activity activity,final String Image, final String Reason, final String documentRef, final boolean isID) {
        mySelectedImage = "";
        //initiate
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.rejectionlayout);
            dialog.setCancelable(false);
            ImageView rejectedImage = (ImageView) dialog.findViewById(R.id.rejectedImage);
            final TextView reason = (TextView) dialog.findViewById(R.id.reason);
            newImage = (ImageView) dialog.findViewById(R.id.selectedImage);
            final CardView Confirm = (CardView) dialog.findViewById(R.id.MyConfirm);
            reason.setText(Reason);
            Glide.with(activity).load(Image).into(rejectedImage);
            newImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMain = true;
                    Loginfo("user Personal Details successfully added");
                    globalMethods.stopProgress = true;
                    InitiateCameraForProfilePic(activity);
                }
            });
            Confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mySelectedImage != null && !mySelectedImage.equalsIgnoreCase("")){
                        onMain = false;
                        dialog.dismiss();
                        Loginfo("user Personal Details successfully added");
                        globalMethods.stopProgress = true;
                        saveIDDocument(activity,mySelectedImage,documentRef,isID);
                    }else{
                        Toast.makeText(activity, "Please select a picture", Toast.LENGTH_LONG).show();
                    }
                }
            });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onMain = false;
                }
            });

        Glide.with(activity).load(Image).into(rejectedImage);
        dialog.show();
        Glide.with(activity).load(Image).into(rejectedImage);
    }
    //set id picture to storage
    public static String saveIDDocument(final Activity activity, final String Image, final String Document, final boolean isID){
        String targetString = "";
        if(isID){
            targetString = "idImage";
        }else{
            targetString = "ProfileImage";
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.users)
                .child(targetString).child(Document);
        UploadTask uploadTask = ref.putFile(Uri.parse(Image));
        final String url = String.valueOf(ref.getDownloadUrl());

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    updateIDDocument(activity,downloadUri.toString(),Document,isID);
                } else {
                    // Handle failures
                    // ...
                    globalMethods.stopProgress = true;
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return url;
    }
    //update IDPath
    public static void updateIDDocument(final Activity activity, final String picture, final String DocumentRef, final boolean isID){
        try {
            Map<String, Object> user = new HashMap<>();
            if(isID){
                user.put("ID", picture);
                user.put("IDRejectionReason", "awaiting authorization");
            }else{
                user.put("profileImage", picture);
                user.put("profileImageReason", "awaiting authorization");
            }

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.users );
            collectionReference.document(DocumentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + DocumentRef);
                        globalMethods.stopProgress = true;
                        mySelectedImage = null;
                        Toast.makeText(activity, "Picture Image successfully sent", Toast.LENGTH_SHORT).show();
                        if(isID){
                            accessKeys.setIdImage(picture);
                        }else{
                            accessKeys.setUserImage(picture);
                        }
                    }else {
                        // Handle failures
                        // ...
                        globalMethods.stopProgress = true;
                        Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(Constraints.TAG, "Error adding document", e);
                    globalMethods.stopProgress = true;
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
            globalMethods.stopProgress = true;
            Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
        }

    }

    public static void InitiateCameraForProfilePic(final Activity Myactivity){
        final Dialog dialog = new Dialog(Myactivity);
        dialog.setContentView(R.layout.camera_custom_layout);
        dialog.setCancelable(true);
        TextView dialogCamera = (TextView) dialog.findViewById(R.id.camera);
        TextView dialogfromPhone = (TextView) dialog.findViewById(R.id.fromphone);
        TextView dialogCancel = (TextView) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.capturePic = true;
                MainActivity.permissionfor = constants.camera;
                globalMethods.getCameraPermissions(Myactivity);
            }
        });
        dialogfromPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.selectPic = true;
                MainActivity.permissionfor = constants.choosefile;
                globalMethods.getReadWritePermissions(Myactivity);
                //getActivity().startActivityForResult(getFileChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);

            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void InitiateApprovalDialog(final Activity Myactivity){
        final Dialog dialog = new Dialog(Myactivity);
        dialog.setContentView(R.layout.approvallayout);
        dialog.setCancelable(true);
        CardView OK = (CardView) dialog.findViewById(R.id.MyConfirm);
        // if button is clicked, close the custom dialog
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    static int Rrated;
    public static void RateApp(final Activity activity) {
        final Dialog rateDialog = new Dialog(activity);
        rateDialog.setContentView(R.layout.custom_ratings);
        rateDialog.setCancelable(false);
        final TextView Rate = (TextView) rateDialog.findViewById(R.id.rate);
        final ImageView star1 = (ImageView) rateDialog.findViewById(R.id.rate1);
        final ImageView star2 = (ImageView) rateDialog.findViewById(R.id.rate2);
        final ImageView star3 = (ImageView) rateDialog.findViewById(R.id.rate3);
        final ImageView star4 = (ImageView) rateDialog.findViewById(R.id.rate4);
        final ImageView star5 = (ImageView) rateDialog.findViewById(R.id.rate5);

        star1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_unrated);
                star3.setImageResource(R.drawable.star_unrated);
                star4.setImageResource(R.drawable.star_unrated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 20;
                return false;
            }
        });
        star2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_unrated);
                star4.setImageResource(R.drawable.star_unrated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 40;
                return false;
            }
        });
        star3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_rated);
                star4.setImageResource(R.drawable.star_unrated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 60;
                return false;
            }
        });
        star4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_rated);
                star4.setImageResource(R.drawable.star_rated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 80;
                return false;
            }
        });
        star5.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_rated);
                star4.setImageResource(R.drawable.star_rated);
                star5.setImageResource(R.drawable.star_rated);
                Rrated = 100;
                return false;
            }
        });

        Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Rrated > 0) {
                    SubmitRatings(activity, Rrated, accessKeys.getDefaultUserId());
                    Toast.makeText(activity, "Thank you kindly!", Toast.LENGTH_SHORT).show();
                    Rrated = 0;
                    rateDialog.dismiss();
                } else {
                    Toast.makeText(activity, "Kindly rate this App", Toast.LENGTH_SHORT).show();
                }

            }
        });

        rateDialog.show();
    }
    public static void SubmitRatings(final Activity activity,final int ratings, final String userId){
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("rating", ratings);
            user.put("userid", userId);
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("name", accessKeys.getName() +" "+ accessKeys.getSurname());

            // Add a new document with a generated ID
            db.collection(constants.RatingsResults)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.RatingsResults);
                            collectionReference.document(documentReference.getId()).update("document ref", document)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i(Constraints.TAG, "Successfully rated the App");
                                            }
                                        }
                                    });
                            //Client_id(activity,name,surname,email, documentReference.getId(),sex);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(Constraints.TAG, "Error adding document", e);
                            Toast.makeText(activity, "Error rating this app", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
        }

    }
    //Dialog for information
    public static void InformationDialog(final Activity Myactivity, final String image, final String info){
        final Dialog dialog = new Dialog(Myactivity);
        dialog.setContentView(R.layout.infolayout);
        dialog.setCancelable(true);
        ImageView Image = (ImageView) dialog.findViewById(R.id.rejectedImage);
        TextView Info = (TextView) dialog.findViewById(R.id.selection);
        CardView OK = (CardView) dialog.findViewById(R.id.MyConfirm);
        Info.setText(info);
        Glide.with(Myactivity).load(image).into(Image);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static final Handler handler = new Handler();
    final static int delay = 1000; //milliseconds
    public static Runnable myRunnable;
    public static void MonitorLocation(final Activity activity){
        handler.postDelayed(myRunnable = new Runnable(){
            public void run(){
                if(!locationFound) {
                    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationListener  = new LocationListener(){

                            @Override
                            public void onLocationChanged(Location location) {
                                //if(!isLocationGrabbed) {
                                boolean LocationEmpty = false;
                                if(accessKeys.getLongitude()== null && accessKeys.getLatitude()==null) {
                                    LocationEmpty = true;
                                }
                                System.out.println(location.getLatitude()+ ": "+location.getLongitude());
                                accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                                accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                                if (LocationEmpty){
                                    for (int i = 0; i < mainLayer.getChildCount(); i++) {
                                        View child = mainLayer.getChildAt(i);
                                        child.setEnabled(false);
                                        child.setClickable(false);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                                //    isLocationGrabbed = true;
                                //}
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        };
                        locationFound = true;
                    }
                }else{
                    handler.removeMessages(0);
                    handler.removeCallbacks(myRunnable);
                    handler.removeCallbacksAndMessages(null);
                    handler.removeCallbacksAndMessages(myRunnable);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

   }
