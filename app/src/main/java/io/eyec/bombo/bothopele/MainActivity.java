package io.eyec.bombo.bothopele;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import constants.constants;
import menuFragment.Movement.mainMovement;
import menuFragment.Road.Locator;
import menuFragment.Road.mainRoad;
import menuFragment.Road.overview;
import menuFragment.Road.road;
import menuFragment.main;
import menuFragment.Missing.initiate;
import menuFragment.Missing.profile;
import menuFragment.noConnection;
import menuFragment.registration.step1;
import menuFragment.registration.step2;
import menuFragment.Road.roadComments;
import methods.globalMethods;
import properties.accessKeys;

import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static constants.constants.CHOOSE_FILE_REQUESTCODE;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.getCameraChooserIntent;
import static methods.globalMethods.getFileChooserIntent;
import static methods.globalMethods.getImageVideoChooserIntent;
import static methods.globalMethods.getVideoChooserIntent;
import static properties.accessKeys.isExitApplication;

public class MainActivity extends AppCompatActivity {
    Fragment mContent;
    SharedPreferences sharedPreferences;
    public static LocationManager locManager;
    public static  LocationListener locListener;
    public static final String TAG = "firebaseConnection";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Key = "lastFragment" ;
    static Activity activity;
    ProgressBar progressBar;
    ConstraintLayout Main;
    public static boolean infoDisplayed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        Main = findViewById(R.id.main);
        activity = this;
        //int versionCode = BuildConfig.VERSION_CODE;
        //String versionName = BuildConfig.VERSION_NAME;
        if(savedInstanceState == null) {
            if(!accessKeys.isNetworkUnAvailable()) {
                progressBar.setVisibility(View.GONE);
                Main.setBackground(null);
                if (getIntent().getExtras() != null ){
                    try {
                        try {
                            String Extras = getIntent().getExtras().getString("extras");
                            JSONObject extras = new JSONObject(Extras);
                            String notificationType = extras.get("NotificationType").toString();
                            if (notificationType.equalsIgnoreCase("movement")){
                                methods.globalMethods.loadFragmentWithTag(R.id.main, new mainMovement(), this, "mainMovement");
                            }else if(notificationType.equalsIgnoreCase("locator")){
                                Locator.setMyDocRef(extras.get("DocumentRef").toString());
                                roadComments.setVideo(Boolean.parseBoolean(extras.get("isVideo").toString()));
                                roadComments.setMyDocRef(extras.get("DocumentRef").toString());
                                roadComments.setMyIncident(extras.get("Incident").toString());
                                roadComments.setMyInfo(extras.get("Info").toString());
                                roadComments.setMyFile(extras.get("File").toString());
                                methods.globalMethods.loadFragments(R.id.main, new overview(), activity);
                            }else{
                                profile.setMyImage(extras.get("Image").toString());
                                profile.setMyName(extras.get("Name").toString());
                                profile.setMyRace(extras.get("Race").toString());
                                profile.setMyGender(extras.get("Gender").toString());
                                profile.setMyHeight(extras.get("Height").toString());
                                profile.setMyBuilt(extras.get("Built").toString());
                                profile.setMySeenTime(extras.get("LastSeenDate").toString());
                                profile.setMySeenPlace(extras.get("LastSeenPlace").toString());
                                profile.setMyInfo(extras.get("Info").toString());
                                profile.setMyDocRef(extras.get("DocumentRef").toString());
                                methods.globalMethods.loadFragments(R.id.main, new profile(), activity);
                            }
                        }catch (Exception e){
                            String notificationType = getIntent().getExtras().getString("NotificationType");
                            if (notificationType.equalsIgnoreCase("movement")){
                                methods.globalMethods.loadFragmentWithTag(R.id.main, new mainMovement(), this, "mainMovement");
                            }else if(notificationType.equalsIgnoreCase("locator")){
                                Locator.setMyDocRef(getIntent().getExtras().getString("DocumentRef"));
                                roadComments.setVideo(Boolean.parseBoolean(getIntent().getExtras().getString("isVideo")));
                                roadComments.setMyDocRef(getIntent().getExtras().getString("DocumentRef"));
                                roadComments.setMyIncident(getIntent().getExtras().getString("Incident"));
                                roadComments.setMyInfo(getIntent().getExtras().getString("Info"));
                                roadComments.setMyFile(getIntent().getExtras().getString("File"));
                                methods.globalMethods.loadFragments(R.id.main, new overview(), activity);
                            }else{
                                profile.setMyImage(getIntent().getExtras().getString("Image"));
                                profile.setMyName(getIntent().getExtras().getString("Name"));
                                profile.setMyRace(getIntent().getExtras().getString("Race"));
                                profile.setMyGender(getIntent().getExtras().getString("Gender"));
                                profile.setMyHeight(getIntent().getExtras().getString("Height"));
                                profile.setMyBuilt(getIntent().getExtras().getString("Built"));
                                profile.setMySeenTime(getIntent().getExtras().getString("LastSeenDate"));
                                profile.setMySeenPlace(getIntent().getExtras().getString("LastSeenPlace"));
                                profile.setMyInfo(getIntent().getExtras().getString("Info"));
                                profile.setMyDocRef(getIntent().getExtras().getString("DocumentRef"));
                                methods.globalMethods.loadFragments(R.id.main, new profile(), activity);
                            }

                        }

                        //check permissions if they are granted
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            //fire off my service
                            if (!globalMethods.isMyServiceRunning(activity,backgroundLocationTracker.class)) {
                                backgroundLocationTracker.setActivity(this);
                                this.startService(new Intent(this, backgroundLocationTracker.class));
                                //connectionHandler.external.spas_.getAllDocuments(getActivity(), recyclerView);
                            }
                        }
                    returnToMain = true;
                    returnToMissingHome= false;
                    returnToRoadHome=false;
                    onMap=false;
                    } catch (Exception  e) {
                        if (accessKeys.isLoggedin()) {
                            methods.globalMethods.loadFragmentWithTag(R.id.main, new main(), this, "main");
                            //methods.globalMethods.loadFragmentWithTag(R.id.main, new splash(), this,"splash");
                        } else {
                            methods.globalMethods.loadFragmentWithTag(R.id.main, new step1(), this, "step1");
                        }
                        e.printStackTrace();
                    }
                    //setOnNotification(false);
                }else {
                    if (accessKeys.isLoggedin()) {
                        methods.globalMethods.loadFragmentWithTag(R.id.main, new main(), this, "main");
                        //methods.globalMethods.loadFragmentWithTag(R.id.main, new splash(), this,"splash");
                    } else {
                        methods.globalMethods.loadFragmentWithTag(R.id.main, new step1(), this, "step1");
                    }
                }
            }else{
                methods.globalMethods.loadFragmentWithTag(R.id.main, new noConnection(), this, "connection");
            }
        }else{
            mContent = getFragmentManager().getFragment(savedInstanceState, Key);
            methods.globalMethods.loadFragmentWithTag(R.id.main, mContent, this,Key);
        }
    }

    public static String permissionfor;

    //Handle permission to read/write on internal storage/phone
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (permissionfor.equals(constants.camera)) {
                        startActivityForResult(getCameraChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                    }else if (permissionfor.equals(constants.videoRecorder)){
                        //startActivityForResult(getFileChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                        startActivityForResult(getVideoChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                    }else if (permissionfor.equals(constants.choosefile)){
                        //startActivityForResult(getFileChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                        startActivityForResult(getFileChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                    }else if (permissionfor.equals(constants.choosefileVideo)){
                        //startActivityForResult(getFileChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                        startActivityForResult(getImageVideoChooserIntent(), constants.CHOOSE_FILE_REQUESTCODE);
                    }else if(permissionfor.equals(constants.locationMissing)) {
                        initiate.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, initiate.locationListener);
                    }else if(permissionfor.equals(constants.locationRoadSide)) {
                        if(accessKeys.isMovementMonitor()){
                            main.runnablelocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, main.runnablelocationListener);
                        }
                        if(mainRoad.locationManager != null &&  mainRoad.locationListener!=null){
                            mainRoad.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mainRoad.locationListener);
                        }
                        main.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, main.locationListener);
                        //survey.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, survey.locationListener);
                    }else{
                        this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "You need these permissions to optimally use this app.", Toast.LENGTH_LONG).show();
                            }
                        });
                        Intent permisionsSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                        permisionsSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        permisionsSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(permisionsSettings);
                        this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "You need these permissions to optimally use this app.", Toast.LENGTH_LONG).show();
                            }
                        });
                        //checkPermissions();
                        this.finish();
                        System.exit(0);
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static boolean selectPic;
    public static boolean capturePic;
    public static boolean captureVid;
    public static boolean takeVideo;
    public static String capturedfile;


    @Override
    protected void onDestroy() {
        getIntent().removeExtra("extras");
        super.onDestroy();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_FILE_REQUESTCODE:
                //for a chosen picture
                if (resultCode == RESULT_OK && selectPic) {
                    selectPic = false;
                    /*multiple files
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            String path = getRealPathFromURI(this, uri);
                            MyFilePath.add(Myposition+"~"+uri);
                        }

                    } else {*/
                    String FilePath = data.getData().getPath();
                    Uri myfile = data.getData();
                    String[] getName = FilePath.split("/");
                    String Name = getName[getName.length - 1];
                    if(step2.onRegistration){
                        if(step2.forID){
                            step2.setID(String.valueOf(myfile));
                        }else{
                            step2.setProfilePicture(String.valueOf(myfile));
                        }
                    }else if(initiate.onMissing){
                        initiate.setVictimImage(this,String.valueOf(myfile));
                    }else if(profile.onMissingComments){
                        profile.setCommentImage(this,String.valueOf(myfile));
                    }else if (road.onRoad) {
                        road.setRoadImage(this, String.valueOf(myfile));
                    }else if (roadComments.onRoadComments){
                        roadComments.setCommentImage(this, String.valueOf(myfile));
                    }else if(main.onMain){
                        main.setProfileImage(this, String.valueOf(myfile));
                    }
                    //notes.setMyImage(String.valueOf(myfile));
                    capturedfile = FilePath;
                    Toast.makeText(this, "File '" + Name + "' successfully attached!", Toast.LENGTH_LONG).show();
                    //methods.globalMethods.loadFragments(R.id.main, new cancelPreview(),this);
                    //for a taken picture
                } else if (resultCode == RESULT_OK && capturePic) {
                    try {
                        capturePic = false;
                        Bitmap photos = (Bitmap) data.getExtras().get("data");
                        String FilePath = data.getData().getPath();
                        Uri myfile = data.getData();
                        if (step2.onRegistration) {
                            if (step2.forID) {
                                step2.setID(String.valueOf(myfile));
                            } else {
                                step2.setProfilePicture(String.valueOf(myfile));
                            }

                        } else if (initiate.onMissing) {
                            initiate.setVictimImage(this, String.valueOf(myfile));
                        } else if (profile.onMissingComments) {
                            profile.setCommentImage(this, String.valueOf(myfile));
                        }else if (road.onRoad) {
                            road.setRoadImage(this, String.valueOf(myfile));
                        } else if (roadComments.onRoadComments){
                            roadComments.setCommentImage(this, String.valueOf(myfile));
                        }else if(main.onMain){
                            main.setProfileImage(this, String.valueOf(myfile));
                        }
                        //notes.setMyImage(String.valueOf(myfile));
                        capturedfile = FilePath;
                        Toast.makeText(this, "File successfully attached!", Toast.LENGTH_LONG).show();
                        //methods.globalMethods.loadFragments(R.id.main, new cancelPreview(),this);
                    /*Uri selectedImage = cameraPic;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap photo;
                    try {
                        photo = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);
                        //Uri tempUri = getImageUri(getApplicationContext(), photo);
                        //File finalFile = new File(getRealPathFromURI(tempUri));


                        //setScreenshotname(photo.toString());
                        setScreenshotname(String.valueOf(Uri.fromFile(new File(MyFile))));
                        //--setScreenshotname(String.valueOf(myfile));
                        //setCamScreenshot(cameraPic);
                        //setCamScreenshotfilePath(String.valueOf(cameraPic));
                        setCamScreenshot(String.valueOf(String.valueOf(Uri.fromFile(new File(MyFile)))));
                        //setCamScreenshot(String.valueOf(tempUri));
                        setCamScreenshotfilePath(String.valueOf(Uri.fromFile(new File(MyFile))));
                        //setCamScreenshotfilePath(String.valueOf(tempUri));
                        setScreenshotfilePath(null);
                        setCameraScreenShot();*/
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                    Toast.makeText(this, "File successfully attached!", Toast.LENGTH_LONG).show();
                }else if(resultCode == RESULT_OK && captureVid){
                    try {
                        captureVid = false;
                        //Bitmap photos = (Bitmap) data.getExtras().get("data");
                        //Determine file type
                        String FilePath = data.getData().getPath();
                        String[] getName = FilePath.split("/");
                        String Name = getName[getName.length - 1];
                        String FolderType = getName[getName.length - 3];
                        String []searchfile = Name.split("\\.mp4");
                        String searchvid = Name.substring(Name.length() - 4, Name.length()).toLowerCase();
                        Uri myfile = data.getData();
                        //Determine File size
                        InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(myfile);
                        int dataSize = (fileInputStream.available()/1024)/1024;

                        if (dataSize <= constants.videoSize){
                            if (road.onRoad) {
                                road.setRoadImage(this, String.valueOf(myfile));
                                if(searchvid.equalsIgnoreCase(".mp4")|| Name.contains("video") || FolderType.equals("video") || takeVideo){
                                    road.setVid(true);
                                }else{
                                    road.setVid(false);
                                }
                                Toast.makeText(this, "File successfully attached!", Toast.LENGTH_LONG).show();
                            }
                            takeVideo = false;
                        }else{
                            Toast.makeText(this, "File too large, must be at least 5mb", Toast.LENGTH_LONG).show();
                        }
                        //notes.setMyImage(String.valueOf(myfile));
                        capturedfile = FilePath;
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                    Toast.makeText(this, "File successfully attached!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Oops, unable to get File details, please retry!", Toast.LENGTH_LONG).show();
                    if (capturePic) {
                        removeContent(capturedfile);
                    }
                    if (captureVid) {
                        removeContent(capturedfile);
                    }
                    selectPic = false;
                    capturePic = false;
                    takeVideo = false;

                }
        }
    }

    public static void removeContent(String camPath){
        if (camPath !=null){
            File sourceDirectory = new File(camPath);
            FileUtils.deleteQuietly(sourceDirectory);
            capturedfile = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getFragmentManager().putFragment(outState, "Fragment", mContent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mContent = getFragmentManager().getFragment(savedInstanceState, "Fragment");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.main);
        //FragmentManager fragmentManager = getSupportFragmentManager();
        editor.putString(Key, fragment.getTag());
        editor.apply();
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String Tag = sharedPreferences.getString(Key, "");
        Fragment fragment = getFragmentManager().findFragmentByTag(Key);
        methods.globalMethods.loadFragmentWithTag(R.id.main, fragment, this, Tag);
    }

    boolean backpressed = false;
    public static boolean returnToMain = false;
    public static boolean returnToMissingHome = false;
    public static boolean returnToRoadHome = false;
    public static boolean onMap = false;
    @Override
    public void onBackPressed() {
        if (isExitApplication()) {// || isAdminFragment.isVisible()){
            if (backpressed) {
                //super.onBackPressed();
                getIntent().removeExtra("extras");
                finish();
                System.exit(0);
            }
            this.backpressed = true;
            Toast.makeText(this, "double tap to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backpressed = false;
                }
            }, 2000);

        } else {
            if(returnToMissingHome) {
                super.onBackPressed();
                super.onBackPressed();
                returnToMissingHome = false;
                returnToMain = true;
            }else if(returnToRoadHome){
                if(roadComments.isMaximized){
                    roadComments.RestoreLayout(this);
                }else{
                    super.onBackPressed();
                    returnToRoadHome = false;
                    returnToMain = true;
                }
            }else if(onMap){
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                onMap = false;
            }else if(returnToMain) {
                methods.globalMethods.loadFragments(R.id.main, new main(), this);
                returnToMain = false;
            }else if(roadComments.onRoadComments){
                roadComments.RestoreLayout(activity);
                roadComments.onRoadComments = false;
            }else{
                super.onBackPressed();
            }
        }
    }
}
