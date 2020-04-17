package methods;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import expressions.regular;
import io.eyec.bombo.bothopele.MainActivity;
import constants.constants;
import io.eyec.bombo.bothopele.R;
import io.eyec.bombo.bothopele.backgroundLocationTracker;

public class globalMethods {
    //Double format
    final public static DecimalFormat f = new DecimalFormat("#.00");
    //checks network availability
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    static boolean result = false;
    static boolean executed;
    public static boolean isNetworkAvailableWithTimeouts(final Activity activity) {
        final int timeout = 5000;
        if (isNetworkAvailable(activity)) {
            if(!executed) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            HttpURLConnection urlc = null;
                            try {
                                urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                                urlc.setRequestProperty("User-Agent", "Test");
                                urlc.setRequestProperty("Connection", "close");
                                urlc.setConnectTimeout(timeout);
                                urlc.connect();
                            } catch (java.net.SocketTimeoutException d) {
                                Toast.makeText(activity, "Time out error, please reconnect and try again!", Toast.LENGTH_SHORT).show();
                                result = false;
                                executed = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                                result = false;
                                executed = true;
                            }
                            //return (urlc.getResponseCode() == 200);
                            if (urlc.getResponseCode() == 200) {
                                result = true;
                                executed = true;
                                isNetworkAvailableWithTimeouts(activity);
                            }
                        } catch (IOException e) {
                            Log.e("NetTag", "Error checking internet connection", e);
                            networkerror(activity);
                            result = false;
                        }
                    }
                });
            }else{
                executed = false;
            }
        }else {
            Log.d("NetTag", "No network available!");
            networkerror(activity);
            result = false;
        }
        return result;
    }
    //Handles loading of a fragment in an activity
    public static void loadFragmentWithTagNavBack(int fragment, android.app.Fragment newFragment, Context context, String tag) {
        //FragmentManager fragmentManager = getFragmentManager();
        try {

            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(fragment,newFragment,tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        } catch (Exception e) {
            //Logging.Logerror(e.getMessage());
        }
    }

    //Handles loading of a fragment in an activity
    public static void loadFragmentWithTag(int fragment, android.app.Fragment newFragment, Context context, String tag) {
        //FragmentManager fragmentManager = getFragmentManager();
        try {
            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragment, newFragment,tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            //Logging.Logerror(e.getMessage());
        }
    }

    //Handles loading of a fragment in an activity
    public static void loadFragments(int fragment, android.app.Fragment newFragment, Context context) {
        //FragmentManager fragmentManager = getFragmentManager();
        try {
            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragment, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            //Logging.Logerror(e.getMessage());
        }
    }
    //Handles loading of a fragment in an activity at back stack
    public static void loadFragmentsOnBackStack(int fragment, android.app.Fragment newFragment, Context context, String tagName) {
        //FragmentManager fragmentManager = getFragmentManager();
        try {
            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(fragment,newFragment, tagName);
            fragmentTransaction.show(newFragment);
            //fragmentTransaction.replace(fragment, newFragment);
            fragmentTransaction.addToBackStack(tagName);
            fragmentTransaction.commit();
        } catch (Exception e) {
            //Logging.Logerror(e.getMessage());
        }
    }

    //Handles navigating back to a fragment that was already created
    public static void showFragmentTemp(int fragment, Fragment newFragment, Context context) {
        //FragmentManager fragmentManager = getFragmentManager();
        try {
            androidx.fragment.app.FragmentManager manager = ((MainActivity) context).getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction =  manager.beginTransaction();
            //fragmentTransaction.replace(fragment, newFragment);
            manager.getBackStackEntryCount();
            manager.getFragments();
            fragmentTransaction.show(newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } catch (Exception e) {
            //Logging.Logerror(e.getMessage());
        }
    }


    //Handles navigating back to a fragment that was already created
    public static void loadFragmentTemp(int fragment, Fragment newFragment, Context context, String tagName) {
        //FragmentManager fragmentManager = getFragmentManager();
        try {
            androidx.fragment.app.FragmentManager manager = ((MainActivity) context).getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction =  manager.beginTransaction();
            //fragmentTransaction.replace(fragment, newFragment);
            manager.getBackStackEntryCount();
            manager.getFragments();
            fragmentTransaction.add(fragment, newFragment,tagName);
            fragmentTransaction.addToBackStack(tagName);
            fragmentTransaction.replace(fragment,newFragment,tagName);
            fragmentTransaction.commit();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } catch (Exception e) {
            //Logging.Logerror(e.getMessage());
        }
    }



    //Displays when the user choose no option
    public static void ConfirmResolution(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    //empties a list
    public static void clearList(List list) {
        final int i = 0;
        if (list.size() > 0) {
            for (; list.size() > 0; ) {
                list.remove(list.size() - 1);
            }
            list.clear();
        }
    }

    //Initialize first letter
    public static String InitializeFirstLetter(String text) {
        String FirstLetter = text.substring(0, 1).toUpperCase();
        String OtherLetters = text.substring(1, text.length()).toLowerCase();
        return FirstLetter + OtherLetters;
    }

    //run toast on UI threat
    public static void runSuccessfulToast(final Activity activity, final String DocuName) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, DocuName + " file Successfully downloaded!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //validate email
    public static boolean ValidateEmail(String value) {
        boolean IsValid;
        String strRegPtr = regular.email;
        Pattern pt = Pattern.compile(strRegPtr);
        String input = value;
        Matcher matcher = pt.matcher(input);
        if (matcher.matches()) {
            IsValid = true;
        } else {
            IsValid = false;
        }
        return IsValid;
    }

    //Validate password

    public static boolean ValidatePassword(String value) {
        boolean IsValid;
        String strRegPtr = regular.password;
        Pattern pt = Pattern.compile(strRegPtr);
        String input = value;
        Matcher matcher = pt.matcher(input);
        if (matcher.matches()) {
            IsValid = true;
        } else {
            IsValid = false;
        }
        return IsValid;
    }

    //Toast network connectivity
    public static void networkerror(Activity activity) {
        Toast.makeText(activity, "No Internet Connection!", Toast.LENGTH_SHORT).show();
    }


    //Date
    public static String ToDate() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd", Locale.UK);
        Date now = new Date();
        String Today = dateformat.format(now);
        return Today;

    }

    //Time
    public static String Time() {
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss", Locale.UK);
        Date now = new Date();
        String Today = dateformat.format(now);
        return Today;
    }

    //Device Name
    public static String Device() {
        return Build.MODEL;
    }

    //get the phone id
    public synchronized static String getPhoneId(Context context) {
        if (constants.uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(constants.PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            constants.uniqueID = sharedPrefs.getString(constants.PREF_UNIQUE_ID, null);
            if (constants.uniqueID == null) {
                constants.uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(constants.PREF_UNIQUE_ID, constants.uniqueID);
                editor.apply();
            }
        }
        return constants.uniqueID;
    }

    //get image from phone
    public static Intent getFileChooserIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    //launch camera
    public static Intent getCameraChooserIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*String FileName = "IMG_"+ToDate().replace("/","-")+Time().replace(":","_")+".jpg";
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + constants.AppName + "/" + constants.forumImages);
        File photo = new File(folder,  FileName);
        if (!folder.exists()) {
            folder.mkdirs();
                        /*CopyOption[] options = new CopyOption[]{
                                StandardCopyOption.COPY_ATTRIBUTES,
                                StandardCopyOption.COPY_ATTRIBUTES
                        };
        }
        //intent.putExtra(MediaStore.EXTRA_OUTPUT,
                //Uri.fromFile(photo));
        //        Uri.fromFile(new File(folder.getPath()+"/"+FileName)));
        cameraPic = Uri.fromFile(photo);
        Dashboard.MyFile = folder.getPath()+"/"+FileName;*/
        return intent;
    }

    //launch camera
    public static Intent getVideoChooserIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        /*String FileName = "IMG_"+ToDate().replace("/","-")+Time().replace(":","_")+".jpg";
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + constants.AppName + "/" + constants.forumImages);
        File photo = new File(folder,  FileName);
        if (!folder.exists()) {
            folder.mkdirs();
                        /*CopyOption[] options = new CopyOption[]{
                                StandardCopyOption.COPY_ATTRIBUTES,
                                StandardCopyOption.COPY_ATTRIBUTES
                        };
        }
        //intent.putExtra(MediaStore.EXTRA_OUTPUT,
                //Uri.fromFile(photo));
        //        Uri.fromFile(new File(folder.getPath()+"/"+FileName)));
        cameraPic = Uri.fromFile(photo);
        Dashboard.MyFile = folder.getPath()+"/"+FileName;*/
        return intent;
    }

    //get image & pdf from phone
    public static Intent getMultipleFileChooserIntent() {
        String[] mimeTypes = {"image/*", "application/pdf"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            System.out.println("Mimetype is " + mimeTypesStr);
            System.out.println("Mimetype is " + mimeTypes[0]);

        }
        return intent;
    }

    //get video from phone
    public static Intent getImageVideoChooserIntent() {
        String[] mimeTypes = {"image/*", "video/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            System.out.println("Mimetype is " + mimeTypesStr);
            System.out.println("Mimetype is " + mimeTypes[0]);

        }
        return intent;
    }

    //Animations
    //hide view from right to left
    public static void hideView(final View view, final Activity activity) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });

        view.startAnimation(animation);
    }

    //show view from left to right
    public static void showView(final View view, final Activity activity) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        view.startAnimation(animation);
    }

    //hide view from left to right
    public static void rehideView(final View view, final Activity activity) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_left);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });

        view.startAnimation(animation);
    }

    //show view from right to left
    public static void reshowView(final View view, final Activity activity) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        view.startAnimation(animation);
    }

    //chat date format

    public static String chatDate(String date) {
        String result = "";
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss", Locale.ENGLISH);
            Date now = new Date();
            String Today = dateformat.format(now);
            Date myToday = dateformat.parse(Today);
            String[] Time = date.split(" ");
            String[] newDate = Time[0].split("/");
            String Date = newDate[2] + "-" + newDate[1] + "-" + newDate[0] + " " + Time[1];
            Date myBefore = dateformat.parse(Date);
            String Before = dateformat.format(myBefore);

            Date firstDate = dateformat.parse(Today);
            Date secondDate = dateformat.parse(Before);

            long diffInMillies = Math.abs(firstDate.getTime() - secondDate.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            int day = (int) diff;
            //Days before yesterday
            SimpleDateFormat Myformat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);

            switch (day) {
                case 0:
                    result = "Today".toUpperCase();
                    break;
                case 1:
                    result = "Yesterday".toUpperCase();
                    break;
                case 2:
                    result = CheckDayOfTheWeek(Time[0]);
                    break;
                case 3:
                    result = CheckDayOfTheWeek(Time[0]);
                    break;
                case 4:
                    result = CheckDayOfTheWeek(Time[0]);
                    break;
                case 5:
                    result = CheckDayOfTheWeek(Time[0]);
                    break;
                case 6:
                    result = CheckDayOfTheWeek(Time[0]);
                    break;
                case 7:
                    result = CheckDayOfTheWeek(Time[0]);
                    break;
                default:
                    Date myDate = dateformat.parse(newDate[2] + "-" + newDate[1] + "-" + newDate[0] + " " + Time[1]);
                    String dd = Myformat.format(myDate);
                    result = dd.replace("-", " ");
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;


    }

    //returns full worded date
    public static String fullDate(String date){
        String results = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        try {
            Date myDate = format.parse(date);
            SimpleDateFormat Myformat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
            results = Myformat.format(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return results;

    }

    public static String CheckDayOfTheWeek(String date) {
        String[] myDate = date.split("/");
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(myDate[0]), (Integer.valueOf(myDate[1]) - 1), Integer.valueOf(myDate[2]));
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    public static boolean stopProgress;
    public static final Handler handler = new Handler();
    final static int delay = 500; //milliseconds
    public static Runnable myRunnable;
    public static Dialog Mydialog;


    public static void ShowDialog(final Activity activity){
        try{
            if(Mydialog == null ){
                stopProgress = false;
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.custom_progress);
                dialog.setCancelable(false);
                pl.droidsonroids.gif.GifImageView gifImageView = (pl.droidsonroids.gif.GifImageView) dialog.findViewById(R.id.imageview);
                dialog.show();
                Mydialog = dialog;
                MonitorProgress();
            }

        }catch (Exception e){

        }


    }

    public static void MonitorProgress(){
        handler.postDelayed(new Runnable(){
            public void run(){
                if(!stopProgress){
                    System.out.println("Monitor progress");
                    myRunnable = this;
                }else{
                    if(Mydialog !=null && stopProgress) {
                        Mydialog.dismiss();
                        handler.removeCallbacks(myRunnable);
                        handler.removeCallbacksAndMessages(null);
                        Mydialog = null;
                    }
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    //get Resd/write permissions from user
    public static void getCameraPermissions(Activity activity) {
        //permissions read/write external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,},
                    1);
        }
    }

    //permissions read/write external storage
    public static void getReadWritePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,},
                    1);
        }
    }

    //check if ArrayList contains a value
    public static boolean isAvailable(final List arrayList, final String textValue){
        boolean results = false;
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).toString().equals(textValue)){
                results = true;
                break;
            }
        }
        return results;
    }

    //get String location
    public static String getStringAddress(final Activity activity, double Lat, double Long){
        String result = "";
        String address = "";
        String city = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity,Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(Lat,Long,1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            if(city!=null){
                result = address + " " + city;
            }else{
                result = address;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }

    //get String location
    public static String getDistanceBetween(final double LatA, final double LongA, final double LatB, final double LongB){
        String result = "";
        Location locationA = new Location("point A");
        locationA.setLatitude(LatA);
        locationA.setLongitude(LongA);

        Location locationB = new Location("point B");

        locationB.setLatitude(LatB);
        locationB.setLongitude(LongB);
        try{
            float distance = locationA.distanceTo(locationB);
            float Killometers = distance/1000;
            if(Killometers < 1){
                result = "0"+f.format(Killometers);
            }else{
                result = f.format(Killometers);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }

    public static void getMissingLocationPermission(final Activity activity, final LocationManager locationManager, final LocationListener locationListener){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MainActivity.permissionfor = constants.locationMissing;
            MainActivity.permissionfor = constants.locationMissing;
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "You need these permissions to optimally use this app.", Toast.LENGTH_LONG).show();
                }
            });
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            //connectionHandler.external.spas_.getAllDocuments(getActivity(), recyclerView);
        }

    }

    public static void geRoadLocationPermission(final Activity activity, final LocationManager locationManager, final LocationListener locationListener){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MainActivity.permissionfor = constants.locationRoadSide;
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "You need these permissions to optimally use this app.", Toast.LENGTH_LONG).show();
                }
            });
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            //fire off my service
            if (!isMyServiceRunning(activity,backgroundLocationTracker.class)) {
                backgroundLocationTracker.setActivity(activity);
                activity.startService(new Intent(activity, backgroundLocationTracker.class));
                //connectionHandler.external.spas_.getAllDocuments(getActivity(), recyclerView);
            }
        }

    }

    public static boolean isMyServiceRunning(final Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    static Bitmap bitmap;
    public static String convertVideoFrameToBitmap(Bitmap bitmap){
        String results = "";
        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(vidPath, new HashMap<String, String>());
        Bitmap image = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(activity, Uri.parse(vidPath));
        bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
        retriever.release();
        /*File folder = new File(Environment.getExternalStorageDirectory() + "/" + constants.AppName + "/" + constants.roadfiles);
        if (Environment.getExternalStorageDirectory().exists()) {
            folder.mkdirs();
        }
        File papersFile = new File(folder + "/" + ToDate().replace("/","-")+"_"+Time().replace("/","-") + ".png");
        FileUtils.copyFileToDirectory(BitMapToString(bitmap), papersFile);*/
        //Compress Bitmap
        File file = new File(BitMapToString(bitmap));
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            //FileUtils.copyFileToDirectory(bitmap.compress(Bitmap.CompressFormat.PNG, 100, os), papersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        results = BitMapToString(bitmap);


        return results;
    }

    public static Bitmap getBitmapFromURL(String path){
        try{
            URL url = new URL(path);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            /*HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);*/
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

