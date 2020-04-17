package menuFragment.Road;


import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import constants.constants;
import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.DialogFragments.incidentList;
import menuFragment.Missing.profile;
import menuFragment.maps.MapView;
import methods.globalMethods;
import properties.accessKeys;

import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.BitMapToString;
import static methods.globalMethods.InitializeFirstLetter;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.convertVideoFrameToBitmap;
import static methods.globalMethods.getCameraPermissions;
import static methods.globalMethods.getDistanceBetween;
import static methods.globalMethods.getReadWritePermissions;
import static methods.globalMethods.getStringAddress;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class road extends android.app.Fragment implements View.OnClickListener, View.OnTouchListener {
    View myview;
    static CircleImageView incidentpic;
    RadioButton Now;
    RadioButton Hour;
    RadioButton Day;
    RadioButton Past;
    static EditText Additional;
    static EditText placeofLastSeen;
    CardView Type;
    static TextView type;
    CardView Submit;
    ConstraintLayout mainLayer;
    static ProgressBar progressBar;
    private static boolean locationFound;
    public static String selectedImage;
    public static String incidentType;
    public static boolean onMissing;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    public static incidentList incident;
    public static boolean onRoad;
    public static boolean Vid;

    public static void setIncidentInfo(final Activity activity, final String text) {
        type.setText(text);
        type.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        incidentType = text;
    }

    public static boolean isVid() {
        return Vid;
    }

    public static void setVid(boolean vid) {
        Vid = vid;
    }

    //set selected image/video on imageview
    public static void setRoadImage(Activity activity, String myImage) {
        //Load into incident pic
        Glide.with(activity).load(myImage).into(incidentpic);
        selectedImage = myImage;
    }

    public road() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_road, container, false);
        incidentpic = myview.findViewById(R.id.incidentpic);
        Now = myview.findViewById(R.id.now);
        Hour = myview.findViewById(R.id.hour);
        Day = myview.findViewById(R.id.day);
        Past = myview.findViewById(R.id.past);
        type = myview.findViewById(R.id.incidentview);
        Type = myview.findViewById(R.id.type);
        Additional = myview.findViewById(R.id.body);
        Submit = myview.findViewById(R.id.MySubmit);
        progressBar = myview.findViewById(R.id.progress);
        mainLayer = myview.findViewById(R.id.mainLayer);
        placeofLastSeen = myview.findViewById(R.id.placeofLastSeen);
        Now.setChecked(true);
        progressBar.setVisibility(View.GONE);
        incidentpic.setOnClickListener(this);
        Submit.setOnClickListener(this);
        Type.setOnTouchListener(this);
        placeofLastSeen.setOnTouchListener(this);
        type.setEnabled(false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                //if(!isLocationGrabbed) {
                boolean LocationEmpty = false;
                if (accessKeys.getLongitude() == null && accessKeys.getLatitude() == null) {
                    LocationEmpty = true;
                }
                System.out.println(location.getLatitude() + ": " + location.getLongitude());
                accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                if (LocationEmpty) {
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
        globalMethods.geRoadLocationPermission(getActivity(), locationManager, locationListener);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accessKeys.getTargetLat() != null && accessKeys.getTargetLong() != null && !MainActivity.returnToRoadHome) {
            if (selectedImage != null) {
                Glide.with(getActivity()).load(selectedImage).into(incidentpic);
            }
            if (incidentType != null) {
                type.setText(incidentType);
                type.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }
            placeofLastSeen.setText(getStringAddress(getActivity(), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())) + " km");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (accessKeys.getTargetLat() != null && accessKeys.getTargetLong() != null && !MainActivity.returnToRoadHome) {
            if (selectedImage != null) {
                Glide.with(getActivity()).load(selectedImage).into(incidentpic);
            }
            if (incidentType != null) {
                type.setText(incidentType);
                type.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }
            placeofLastSeen.setText(getStringAddress(getActivity(), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())) + " km");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (accessKeys.getTargetLat() != null && accessKeys.getTargetLong() != null && !MainActivity.returnToRoadHome) {
            if (selectedImage != null) {
                Glide.with(getActivity()).load(selectedImage).into(incidentpic);
            }
            if (incidentType != null) {
                type.setText(incidentType);
                type.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            }
            placeofLastSeen.setText(getStringAddress(getActivity(), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getStringAddress(getActivity(), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            System.out.println(getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())) + " km");
        }
    }

    @Override
    public void onDestroy() {
        onRoad = false;
        super.onDestroy();
    }

    public static void InitiateCameraForRoadPic(final Activity Myactivity) {
        final Dialog dialog = new Dialog(Myactivity);
        dialog.setContentView(R.layout.camvid_custom_layout);
        dialog.setCancelable(true);
        TextView dialogCamera = (TextView) dialog.findViewById(R.id.camera);
        TextView dialogfromPhone = (TextView) dialog.findViewById(R.id.fromphone);
        TextView dialogVideo = (TextView) dialog.findViewById(R.id.video);
        TextView dialogCancel = (TextView) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        dialogCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.capturePic = true;
                MainActivity.permissionfor = constants.camera;
                getCameraPermissions(Myactivity);
                setVid(false);
            }
        });
        dialogfromPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.captureVid = true;
                MainActivity.permissionfor = constants.choosefileVideo;
                getReadWritePermissions(Myactivity);

            }
        });
        dialogVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.captureVid = true;
                MainActivity.takeVideo = true;
                MainActivity.permissionfor = constants.videoRecorder;
                //getReadWritePermissions(Myactivity);
                //Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                getReadWritePermissions(Myactivity);

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
        if (id == R.id.incidentpic) {
            onRoad = true;
            InitiateCameraForRoadPic(getActivity());
        } else {
            if (accessKeys.isApproval()) {
                onMissing = false;
                String IncidentTime = "";
                String Reporter = "";
                String myInfo = Additional.getText().toString();
                String myPlace = placeofLastSeen.getText().toString();
                String myType = type.getText().toString();

                if (selectedImage == null) {
                    Toast.makeText(getActivity(), "Please provide incident proof", Toast.LENGTH_LONG).show();
                    incidentpic.requestFocus();
                } else if (myType.isEmpty()) {
                    Toast.makeText(getActivity(), "Please select an incident type", Toast.LENGTH_LONG).show();
                    type.requestFocus();
                } else if (myPlace.isEmpty()) {
                    placeofLastSeen.requestFocus();
                    Toast.makeText(getActivity(), "Please indicate where last was the victim seen", Toast.LENGTH_LONG).show();
                } else if (myInfo.isEmpty() || myInfo.length() < 3) {
                    Toast.makeText(getActivity(), "Please provide a detailed description", Toast.LENGTH_LONG).show();
                    Additional.requestFocus();
                } else {
                    if (globalMethods.isNetworkAvailable(getActivity())) {
                        //Indicent time
                        if (Now.isChecked()) {
                            IncidentTime = "now";
                        } else if (Hour.isChecked()) {
                            IncidentTime = "an hour ago";
                        } else if (Day.isChecked()) {
                            IncidentTime = "yesterday";
                        } else {
                            IncidentTime = "in the past";
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
                        submitRoadProfile(getActivity(), Reporter, IncidentTime, myType, myPlace, myInfo, isVid());
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
    private static void submitRoadProfile(final Activity activity, final String Reporter, final String IncidentTime, final String IncidentType, final String LastSeenPlace, final String Additional, final boolean fileVid) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("userid", accessKeys.getDefaultUserId());
            user.put("reporter", Reporter);
            user.put("incidentTime", IncidentTime);
            user.put("incidentType", IncidentType);
            user.put("incidentLastSeenPlace", LastSeenPlace);
            user.put("incidentCoordinates", accessKeys.getTargetLat() + "," + accessKeys.getTargetLong());
            user.put("submitterCoordinates", accessKeys.getLatitude() + "," + accessKeys.getLongitude());
            user.put("isVideo", fileVid);
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("Image", accessKeys.getUserImage());
            user.put("distanceDifference", getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
            user.put("moreInfo", Additional);
            // Add a new document with a generated ID
            db.collection(constants.road).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    final String document = documentReference.getId();
                    CollectionReference collectionReference = db.collection(constants.road);
                    collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //set Realm user information;
                                Loginfo("missing profile Details successfully added");
                                saveImageDocument(activity, selectedImage, document, IncidentTime, IncidentType, LastSeenPlace, Additional, fileVid);
                                reportIncidentTimePlace(activity, Reporter,selectedImage, IncidentTime, LastSeenPlace, document,fileVid);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                globalMethods.stopProgress = true;
                                Logerror("unable to add missing profile Details, ");
                            }
                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.w(Constraints.TAG, "Error adding document", e);
                    globalMethods.stopProgress = true;
                    Logerror("unable to add missing profile Details, " + e.getMessage());
                }
            });
        } catch (Exception exception) {
            exception.getMessage();
            exception.printStackTrace();
        }
    }

    //Report incident Location Time
    public static void reportIncidentTimePlace(final Activity activity, final String Reporter, final String Image, final String IncidentTime, final String LastSeenPlace, final String Document, final boolean fileVid) {
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
            user.put("incidentCoordinates", accessKeys.getTargetLat() + "," + accessKeys.getTargetLong());
            user.put("submitterCoordinates", accessKeys.getLatitude() + "," + accessKeys.getLongitude());
            user.put("name", InitializeFirstLetter(accessKeys.getName())+" "+InitializeFirstLetter(accessKeys.getSurname()));
            user.put("date", ToDate());
            user.put("time", Time());
            user.put("Image", accessKeys.getUserImage());
            user.put("comment", "none");
            user.put("commentImage", "none");
            user.put("locator", true);
            user.put("distanceDifference", getDistanceBetween(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()), Double.parseDouble(accessKeys.getTargetLat()), Double.parseDouble(accessKeys.getTargetLong())));
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
                                if(fileVid) {
                                    GlideBitmapDrawable drawable = (GlideBitmapDrawable) incidentpic.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    saveVideoThumbNail(activity, bitmap, Document);
                                }
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

    //set profile picture to storage
    public static String saveImageDocument(final Activity activity, final String Image, final String Document,final String IncidentTime, final String IncidentType , final String LastSeenPlace, final String Additional,final boolean fileVid){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        String Location = "";
        if(fileVid){
            Location = "Video";
        }else{
            Location = "Images";
        }
        final StorageReference ref = storageRef.child(constants.road)
                .child(Location).child(Document);
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
                    updateProfileDocument(activity,downloadUri.toString(),Document,IncidentTime,IncidentType,LastSeenPlace,Additional,fileVid);
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

    //set audio file
    private static String saveVidDocument(final Activity activity, final String Video, final String Document,final String IncidentTime, final String IncidentType , final String LastSeenPlace, final String Additional,final boolean fileVid){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.road)
                .child("Videos").child(Document);
        InputStream stream = null;
        try {
            stream = new FileInputStream(Video);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        UploadTask uploadTask = ref.putStream(stream);
        //UploadTask uploadTask = ref.putFile(Uri.parse(audio));
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
                    updateProfileDocument(activity,downloadUri.toString(),Document,IncidentTime,IncidentType,LastSeenPlace,Additional,fileVid);
                    //updateAudio(activity,issueno,pictureUrl,description,text_description,downloadUri.toString());
                    //SaveIssue(activity, description, downloadUri.toString(), pictureUrl,issueno,audio);// for audio
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        return url;
    }

    //update PicturePath
    public static void updateProfileDocument(final Activity activity, final String picture, final String documentRef, final String IncidentTime, final String IncidentType , final String LastSeenPlace, final String Additional,final boolean fileVid){
        try {
            Map<String, Object> user = new HashMap<>();
            if(fileVid){
                user.put("videoFile", picture);
            }else{
                user.put("imageFile", picture);
            }
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.road);
            collectionReference.document(documentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentRef);
                        globalMethods.stopProgress = true;
                        road.Additional.setText("");
                        road.type.setText("");
                        road.placeofLastSeen.setText("");
                        selectedImage = null;
                        progressBar.setVisibility(View.GONE);
                        Locator.setMyDocRef(documentRef);
                        roadComments.setVideo(fileVid);
                        roadComments.setMyDocRef(documentRef);
                        roadComments.setMyIncident(IncidentType);
                        roadComments.setMyInfo(Additional);
                        roadComments.setMyFile(picture);
                        MainActivity.returnToRoadHome = true;
                        globalMethods.loadFragments(R.id.main, new overview(), activity);
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

    //set profile picture to storage
    public static String saveVideoThumbNail(final Activity activity, final Bitmap bitmap, final String Document){
        //get Bitmap from preview
        FileOutputStream outStream = null;
        File Internalfile = Environment.getExternalStorageDirectory();
        final File dir = new File(Internalfile + "/" + constants.AppName + "/" + constants.roadfiles);
        dir.mkdirs();
        final String fileName = "Image.jpg";
        try {
            File outFile = new File(dir, fileName);
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File imageFile = new File(dir+"/"+fileName);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.road)
                .child("Images").child(Document);
        UploadTask uploadTask = ref.putFile(Uri.fromFile(imageFile));
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
                    MainActivity.removeContent(dir+"/"+fileName);
                    Uri downloadUri = task.getResult();
                    updateThumbnailDocument(activity,downloadUri.toString(),Document);
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
    public static void updateThumbnailDocument(final Activity activity, final String picture, final String documentRef){
        try {
            Map<String, Object> user = new HashMap<>();
            user.put("imageFile", picture);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.road);
            collectionReference.document(documentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentRef);
                        globalMethods.stopProgress = true;
                    } else {
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

                @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if(id==R.id.type){
            final FragmentManager fragmentManager = getActivity().getFragmentManager();
            incident = new incidentList();
            incident.show(fragmentManager, "so");
        }else{
            methods.globalMethods.loadFragments(R.id.main, new MapView(), getActivity());
        }

        return false;
    }

}

