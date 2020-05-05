package menuFragment.Missing;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import constants.constants;
import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import io.eyec.bombo.bothopele.messagingHelper;
import menuFragment.Notifications.soloNotification;
import menuFragment.maps.MapView;
import methods.globalMethods;
import properties.accessKeys;

import static io.eyec.bombo.bothopele.MainActivity.TAG;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.InitializeFirstLetter;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.clearList;
import static methods.globalMethods.getCameraPermissions;
import static methods.globalMethods.getDistanceBetween;
import static methods.globalMethods.getReadWritePermissions;
import static methods.globalMethods.getStringAddress;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class initiate extends android.app.Fragment implements View.OnTouchListener, View.OnClickListener{
    View myview;
    static CircleImageView victimpic;
    EditText name;
    RadioButton Male;
    RadioButton Female;
    RadioButton Black;
    RadioButton White;
    RadioButton Asian;
    RadioButton Indian;
    EditText Height;
    RadioButton Slim;
    RadioButton Average;
    RadioButton Athletic;
    RadioButton Thick;
    EditText dateofLastSeen;
    EditText placeofLastSeen;
    EditText Additional;
    CardView Submit;
    ConstraintLayout mainLayer;
    static ProgressBar progressBar;
    private static boolean locationFound;
    public static String selectedImage;
    public static boolean onMissing;
    public static LocationManager locationManager;
    public static  LocationListener locationListener;


    //set selected image on imageview
    public static void setVictimImage(Activity activity, String myImage) {
        Glide.with(activity).load(myImage).into(victimpic);
        selectedImage = myImage;
    }

    public initiate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_initiate, container, false);
        //initialize location
        victimpic = myview.findViewById(R.id.victimpic);
        name = myview.findViewById(R.id.name);
        Male = myview.findViewById(R.id.male);
        Female = myview.findViewById(R.id.female);
        Black = myview.findViewById(R.id.black);
        White = myview.findViewById(R.id.white);
        Asian = myview.findViewById(R.id.asian);
        Indian = myview.findViewById(R.id.indian);
        Height = myview.findViewById(R.id.height);
        Slim = myview.findViewById(R.id.slim);
        Average = myview.findViewById(R.id.average);
        Athletic = myview.findViewById(R.id.athletic);
        Thick = myview.findViewById(R.id.thick);
        dateofLastSeen = myview.findViewById(R.id.dateofLastSeen);
        placeofLastSeen = myview.findViewById(R.id.placeofLastSeen);
        Additional = myview.findViewById(R.id.body);
        Submit = myview.findViewById(R.id.MySubmit);
        progressBar = myview.findViewById(R.id.progress);
        mainLayer = myview.findViewById(R.id.mainLayer);
        progressBar.setVisibility(View.GONE);
        Male.setChecked(true);
        Black.setChecked(true);
        Slim.setChecked(true);
        victimpic.setOnClickListener(this);
        dateofLastSeen.setOnTouchListener(this);
        placeofLastSeen.setOnTouchListener(this);
        Submit.setOnClickListener(this);
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
        globalMethods.getMissingLocationPermission(getActivity(),locationManager,locationListener);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(accessKeys.getTargetLat()!=null && accessKeys.getTargetLong() !=null && MapView.isOnMissing()){
            if(selectedImage!=null){
                Glide.with(getActivity()).load(selectedImage).into(victimpic);
            }
            placeofLastSeen.setText(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong()))+" km");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(accessKeys.getTargetLat()!=null && accessKeys.getTargetLong() !=null && MapView.isOnMissing()){
            if(selectedImage!=null){
                Glide.with(getActivity()).load(selectedImage).into(victimpic);
            }
            placeofLastSeen.setText(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong()))+" km");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(accessKeys.getTargetLat()!=null && accessKeys.getTargetLong() !=null && MapView.isOnMissing()){
            if(selectedImage!=null){
                Glide.with(getActivity()).load(selectedImage).into(victimpic);
            }
            placeofLastSeen.setText(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong()))+" km");
        }
    }

    @Override
    public void onDestroy() {
        onMissing = false;
        super.onDestroy();
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

    public static void InitiateCameraForMissingPic(final Activity Myactivity){
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
                getCameraPermissions(Myactivity);
            }
        });
        dialogfromPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.selectPic = true;
                MainActivity.permissionfor = constants.choosefile;
                getReadWritePermissions(Myactivity);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        onMissing = true;
        if(id == R.id.victimpic) {
            InitiateCameraForMissingPic(getActivity());
        }else {
            if (accessKeys.isApproval()) {
                onMissing = false;
                String Gender = "";
                String Built = "";
                String Race = "";
                String Reporter = "";
                String myName = name.getText().toString();
                String myHeight = Height.getText().toString();
                String myDate = dateofLastSeen.getText().toString();
                String myPlace = placeofLastSeen.getText().toString();
                String myInfo = Additional.getText().toString();

                if (selectedImage == null) {
                    Toast.makeText(getActivity(), "Please provide victim's picture", Toast.LENGTH_LONG).show();
                    victimpic.requestFocus();
                } else if (myName.isEmpty() || myName.length() < 3) {
                    Toast.makeText(getActivity(), "Please provide victim's full name and surname", Toast.LENGTH_LONG).show();
                    name.requestFocus();
                } else if (myHeight.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Height", Toast.LENGTH_LONG).show();
                    Height.requestFocus();
                } else if (myDate.isEmpty()) {
                    Toast.makeText(getActivity(), "Please indicate when last was the victim seen", Toast.LENGTH_LONG).show();
                    dateofLastSeen.requestFocus();
                } else if (myPlace.isEmpty()) {
                    placeofLastSeen.requestFocus();
                    Toast.makeText(getActivity(), "Please indicate where last was the victim seen", Toast.LENGTH_LONG).show();
                } else {
                    if (globalMethods.isNetworkAvailable(getActivity())) {
                        //gender
                        if (Male.isChecked()) {
                            Gender = "Male";
                        } else {
                            Gender = "Female";
                        }
                        //race
                        if (Black.isChecked()) {
                            Race = "Black";
                        } else if (White.isChecked()) {
                            Race = "White";
                        } else if (Asian.isChecked()) {
                            Race = "Asian";
                        } else {
                            Race = "Indian";
                        }
                        //built
                        if (Slim.isChecked()) {
                            Built = "Slim";
                        } else if (Average.isChecked()) {
                            Built = "Average";
                        } else if (Athletic.isChecked()) {
                            Built = "Athletic";
                        } else {
                            Built = "Thick";
                        }
                        if (accessKeys.getGender().equalsIgnoreCase("male")) {
                            Reporter = "Mr " + InitializeFirstLetter(accessKeys.getName()) + " " + InitializeFirstLetter(accessKeys.getSurname());
                        } else {
                            Reporter = "Ms " + InitializeFirstLetter(accessKeys.getName()) + " " + InitializeFirstLetter(accessKeys.getSurname());
                        }
                        //Disable all views
                        for (int i = 0; i < mainLayer.getChildCount(); i++) {
                            View child = mainLayer.getChildAt(i);
                            child.setEnabled(false);
                        }
                        submitMissingProfile(getActivity(), Reporter, myName, Gender, Race, myHeight, Built, myDate, myPlace, myInfo);
                    } else {
                        globalMethods.networkerror(getActivity());
                    }
                }
            }else{
                Toast.makeText(getActivity(), "Cannot perform transaction, your verification status is pending!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //sending user Message
    private static void submitMissingProfile(final Activity activity, final String Reporter, final String Name, final String Gender,final String Race, final String Height, final String Built, final String LastSeenDate, final String LastSeenPlace, final String Additional){
        progressBar.setVisibility(View.VISIBLE);
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("userid", accessKeys.getDefaultUserId());
            user.put("reporter", Reporter);
            user.put("victimName", Name);
            user.put("victimGender", Gender);
            user.put("victimRace", Race);
            user.put("victimHeight", Height);
            user.put("victimBuilt", Built);
            user.put("victimLastSeenDate", LastSeenDate);
            user.put("victimLastSeenPlace", LastSeenPlace);
            user.put("submitterCoordinates",accessKeys.getLatitude() +","+accessKeys.getLongitude());
            user.put("victimCoordinates",accessKeys.getTargetLat() +","+accessKeys.getTargetLong());
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("distanceDifference",getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()),Double.parseDouble(accessKeys.getLongitude()),Double.parseDouble(accessKeys.getTargetLat()),Double.parseDouble(accessKeys.getTargetLong())));
            if(!Additional.isEmpty()){
                user.put("moreInfo", Additional);
            }
            // Add a new document with a generated ID
            db.collection(constants.missing)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.missing);
                            collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //set Realm user information;
                                        Loginfo("missing profile Details successfully added");
                                        saveProfileDocument(activity,selectedImage, document ,Name,Gender,Race,Height,Built,LastSeenDate,LastSeenPlace,Additional);
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

    //set profile picture to storage
    public static String saveProfileDocument(final Activity activity, final String Image, final String Document, final String Name, final String Gender,final String Race, final String Height, final String Built, final String LastSeenDate, final String LastSeenPlace, final String Additional){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.missing)
                .child("Images").child(Document);
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
                    updateProfileDocument(activity,downloadUri.toString(),Document,Name,Gender,Race,Height,Built,LastSeenDate,LastSeenPlace,Additional);
                } else {
                    // Handle failures
                    // ...
                    progressBar.setVisibility(View.GONE);
                    globalMethods.stopProgress = true;
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return url;
    }

    //update PicturePath
    public static void updateProfileDocument(final Activity activity, final String picture, final String documentRef, final String Name, final String Gender,final String Race, final String Height, final String Built, final String LastSeenDate, final String LastSeenPlace, final String Additional){
        try {
            Map<String, Object> user = new HashMap<>();
            user.put("missinImage", picture);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.missing );
            collectionReference.document(documentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentRef);
                        accessKeys.setTargetLat(null);
                        accessKeys.setTargetLong(null);
                        globalMethods.stopProgress = true;
                        selectedImage = null;
                        progressBar.setVisibility(View.GONE);
                        profile.setMyImage(picture);
                        profile.setMyName(Name);
                        profile.setMyRace(Race);
                        profile.setMyGender(Gender);
                        profile.setMyHeight(Height);
                        profile.setMyBuilt(Built);
                        profile.setMySeenTime(LastSeenDate);
                        profile.setMySeenPlace(LastSeenPlace);
                        profile.setMyInfo(Additional);
                        profile.setMyDocRef(documentRef);
                        MainActivity.returnToMissingHome = true;
                        methods.globalMethods.loadFragments(R.id.main, new profile(), activity);
                        //set location variable to notification
                        soloNotification.setNotificationType("missing");
                        soloNotification.setProfileImage(picture);
                        soloNotification.setProfileName(Name);
                        soloNotification.setProfileRace(Race);
                        soloNotification.setProfileGender(Gender);
                        soloNotification.setProfileHeight(Height);
                        soloNotification.setProfileBuilt(Built);
                        soloNotification.setProfileLastSeenDate(LastSeenDate);
                        soloNotification.setProfileLastSeenPlace(LastSeenPlace);
                        soloNotification.setProfileAdditional(Additional);
                        soloNotification.setProfileDocRef(documentRef);
                        //send notifications
                        getSendMissingNotifications(activity,Name,picture);
                    }else {
                        // Handle failures
                        // ...
                        progressBar.setVisibility(View.GONE);
                        globalMethods.stopProgress = true;
                        Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                    }
                }



                // Create a new user with a first and last name
                //MapView<String, Object> user = new HashMap<>();
                //user.put("userPhone", Phone);
                //user.put("userPicture", Picture);

                // Add a new document with a generated ID
                //db.collection(constants.users).document(userId)
                //        .update("userid", userId)
                //        .addOnCompleteListener(new OnCompleteListener<Void>() {
                //.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                //        public void onComplete(@NonNull Task<Void> task) {
                //            if (task.isSuccessful()) {
                //Toast.makeText(this, "Document created/updated", Toast.LENGTH_SHORT).show();
                //               Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                //               setPhoneNumber(activity, getDefaultUserEmail(), Phone, Picture);
                //           }
                //       }
                        /*@Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            //final String userid = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.users);
                            collectionReference.document(userId).update("userid", userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(this, "Document created/updated", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                                        setPhoneNumber(activity, getDefaultUserEmail(), Phone, Picture);
                                    }
                                }
                            });
                        }*/
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.w(Constraints.TAG, "Error adding document", e);
                    globalMethods.stopProgress = true;
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception exception){
            progressBar.setVisibility(View.GONE);
            exception.getMessage();
            exception.printStackTrace();
            globalMethods.stopProgress = true;
            Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
        }

    }


    public static DatePickerDialog Ddialog;
    public static Dialog dialog;
    public static DatePickerDialog.OnDateSetListener mDateSetListener;
    public static void InitiateDate(final Activity activity, final EditText editText){
        if (Ddialog == null) {
            //dialog = new Dialog(activity);
            //dialog.setContentView(R.layout.datepicker);
            //dialog.setCancelable(true);
            //final DatePicker simpleDatePicker = (DatePicker) dialog.findViewById(R.id.simpleDatePicker);
            // if DatePicker button is clicked, close the custom dialog
            final Calendar c = Calendar.getInstance();
            int maxYear = c.get(Calendar.YEAR); // this year ( 2011 ) - 20 = 1991
            int maxMonth = c.get(Calendar.MONTH);
            int maxDay = c.get(Calendar.DAY_OF_MONTH);
            Ddialog = new DatePickerDialog(
                    activity,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    maxYear ,maxMonth,maxDay
            );
            Ddialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    String dateOfBirth = "";
                    if (String.valueOf(i1).length() > 1){
                        if (String.valueOf(i2).length() > 1){
                            dateOfBirth = i2 + "/" + (i1+1) + "/" + i;
                        }else{
                            dateOfBirth = "0" + i2 + "/" + (i1+1) + "/" + i;
                        }
                    }else{
                        if (String.valueOf(i2).length() > 1){
                            dateOfBirth = i2 + "/0" + (i1+1) + "/" + i;
                        }else{
                            dateOfBirth = "0" + i2 + "/0" + (i1+1) + "/" + i;
                        }
                    }
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                    try {
                        Date now = new Date();
                        Date selected = dateformat.parse(dateOfBirth);
                        if(!selected.after(now)){
                            editText.setText(dateOfBirth);
                            Ddialog.dismiss();
                        }else{
                            Toast.makeText(activity, "Cannot select future dates", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            };

            Ddialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Ddialog = null;
                }
            });

            Ddialog.show();

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id==R.id.dateofLastSeen) {
            InitiateDate(getActivity(), dateofLastSeen);
        }else{
            MapView.setOnMissing(true);
            methods.globalMethods.loadFragments(R.id.main, new MapView(), getActivity());
        }
        return false;
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
                                    accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                                    accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                                }
                                    //connectionHandler.external.spas_.getAllDocuments(activity, recyclerView, progressBar,imageView);
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

    //Notifications section
    public static  void getSendMissingNotifications(final Activity activity,final String Name, final String Image){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.users)
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
                            getUsersTokens(activity,Name,Image);
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }
    //get users tokens;
    public static void getUsersTokens(final Activity activity,final String Name, final String Image){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("token")!=null){
                                    //set MessagingHelper variables
                                    messagingHelper.setIncludeLargeIcon(true);
                                    messagingHelper.setLargeIcon(Image);
                                    soloNotification.setNotificationType("missing");
                                    String Token = document.get("token").toString();
                                    soloNotification.serverKey = activity.getResources().getString(R.string.messaging_api_key);
                                    new soloNotification().execute("Missing person","Please help us find "+Name,Token);

                                }
                            }
                        }
                    }
                });
    }
}

