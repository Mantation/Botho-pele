package menuFragment.Road;


import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import adapters.RoadCommentsAdapter;
import constants.constants;
import helperClasses.CustomEditText;
import interfaces.interface_;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import methods.globalMethods;
import properties.accessKeys;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;
import static methods.globalMethods.InitializeFirstLetter;
import static methods.globalMethods.Time;
import static methods.globalMethods.ToDate;
import static methods.globalMethods.getCameraPermissions;
import static methods.globalMethods.getReadWritePermissions;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class roadComments extends Fragment implements View.OnClickListener, View.OnTouchListener , interface_.DrawableClickListener{
    View myview;
    ImageView Imageview;
    static TextView IncidentType;
    static CardView vidFrame;
    static VideoView vidplay;
    static ProgressBar vidProgressbar;
    private MediaController mediaController;
    private ImageButton fullScreen;
    static ConstraintLayout MainLayout;
    CardView incidentpicLayout;
    TextView Info;
    CustomEditText Message;
    ImageButton Send;
    static LinearLayout typeSection;
    static View Separator;
    static ImageView Image;
    static ImageView closeImage;
    static ConstraintLayout MainPreviewLayer;
    int Height;
    static RecyclerView recyclerView;
    public static RoadCommentsAdapter myAdapter;
    static ProgressBar progressBar;
    public static boolean onRoadComments;
    public static String selectedImage;

    static boolean Video;
    static String myFile;
    static String myIncident;
    static String myInfo;
    static String myDocRef;
    static int vidFrameHeight;
    static int vidFrameWidth;
    static float CardViewRadius;
    MediaPlayer mediaPlayer;

    static ViewPager viewPager;

    public static boolean IsVideo() {
        return Video;
    }

    public static void setVideo(boolean isVideo) {
        roadComments.Video = isVideo;
    }

    public static String getMyFile() {
        return myFile;
    }

    public static void setMyFile(String myFile) {
        roadComments.myFile = myFile;
    }

    public static String getMyIncident() {
        return myIncident;
    }

    public static void setMyIncident(String myIncident) {
        roadComments.myIncident = myIncident;
    }

    public static String getMyInfo() {
        return myInfo;
    }

    public static void setMyInfo(String myInfo) {
        roadComments.myInfo = myInfo;
    }

    public static String getMyDocRef() {
        return myDocRef;
    }

    public static void setMyDocRef(String myDocRef) {
        roadComments.myDocRef = myDocRef;
    }

    //set selected image on imageview
    public static void setCommentImage(Activity activity, String myImage) {
        Glide.with(activity).load(myImage).into(Image);
        selectedImage = myImage;
        MainPreviewLayer.setVisibility(View.VISIBLE);
    }

    public roadComments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview =  inflater.inflate(R.layout.fragment_road_comments, container, false);
        Imageview = myview.findViewById(R.id.incidentImage);
        IncidentType = myview.findViewById(R.id.incidenttype);
        vidFrame = myview.findViewById(R.id.vidFrame);
        vidplay = myview.findViewById(R.id.vidplay);
        vidProgressbar = myview.findViewById(R.id.MediaProgressBar);
        incidentpicLayout = myview.findViewById(R.id.incidentpicLayout);
        Info = myview.findViewById(R.id.Info);
        Message = myview.findViewById(R.id.initiateText);
        Send = myview.findViewById(R.id.imageButton);
        progressBar = myview.findViewById(R.id.progress);
        recyclerView = myview.findViewById(R.id.recyclerview_messages);
        Image = myview.findViewById(R.id.selectedImage);
        closeImage = myview.findViewById(R.id.closeImage);
        MainPreviewLayer = myview.findViewById(R.id.displayLayer);
        MainLayout = myview.findViewById(R.id.main);
        typeSection = myview.findViewById(R.id.messageLayoutSec);
        Separator = myview.findViewById(R.id.view);
        progressBar.setVisibility(View.VISIBLE);
        MainPreviewLayer.setVisibility(View.GONE);
        vidProgressbar.setVisibility(View.GONE);
        connectionHandler.external.roadComments_.getAllDocuments(getActivity(), recyclerView,getMyDocRef(), progressBar);
        recyclerView.setOnTouchListener(this);
        Message.setOnTouchListener(this);
        closeImage.setOnTouchListener(this);
        Message.setDrawableClickListener(this);
        //scroll
        scroll = true;
        MonitorScroll();
        //initialize
        if(IsVideo()){
            vidFrame.setVisibility(View.VISIBLE);
            incidentpicLayout.setVisibility(View.GONE);
            //vidProgressbar.setVisibility(View.VISIBLE);
            //vidplay.setVideoURI(Uri.parse(getMyFile()));
            vidplay.setVideoURI(Uri.parse(getMyFile()));
            //vidplay.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Vids/20200319_211557.mp4"));
            mediaController = new helperClasses.FullScreenMediaController(getActivity(),getActivity());
            mediaController.setAnchorView(vidplay);
            ViewGroup.LayoutParams frameLayout = vidplay.getLayoutParams();
            vidFrameHeight = frameLayout.height;
            vidFrameWidth = frameLayout.width;
            CardViewRadius = vidFrame.getRadius();
            //mediaController.setMediaPlayer(vidplay);
            /*ViewGroup.LayoutParams frameLayout = vidFrame.getLayoutParams();
            ViewGroup.LayoutParams videoLayout = vidplay.getLayoutParams();
            videoLayout.width = frameLayout.width;
            videoLayout.height = frameLayout.height;
            vidFrameHeight = frameLayout.height;
            vidFrameWidth = frameLayout.width;
            vidplay.setLayoutParams(videoLayout);
            vidplay.invalidate();
            //fullScreen = new ImageButton (mediaController.getContext());*/
            //fullScreen = new ImageButton (super.getContext());
            //mediaController.setForegroundGravity(Gravity.CENTER);
            vidplay.setMediaController(mediaController);
            vidProgressbar.setVisibility(View.VISIBLE);
            //vidplay.start();
            vidplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    vidProgressbar.setVisibility(View.GONE);
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                            vidProgressbar.setVisibility(View.GONE);
                            //mp.start();
                        }
                    });


                }
            });
        }else{
            incidentpicLayout.setVisibility(View.VISIBLE);
            vidFrame.setVisibility(View.GONE);
            //vidProgressbar.setVisibility(View.GONE);
            Glide.with(getActivity()).load(getMyFile()).into(Imageview);
        }
        IncidentType.setText(getMyIncident());
        if (getMyInfo().length() > 95){
            Info.setText(getMyInfo().substring(0,90)+"...");
        }else{
            Info.setText(getMyInfo());
        }

        trackR_TextArea();
        Message.requestFocus();
        Height = Message.getLayoutParams().height;
        Send.setOnClickListener(this);
        setExitApplication(false);
        return myview;
    }

    private boolean isLandScape(){
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();

        if (rotation == Surface.ROTATION_90
                || rotation == Surface.ROTATION_270) {
            return true;
        }
        return false;
    }

    public static boolean isMaximized;
    public static void RedefineLayout(final Activity activity){
        //vidFrame.setBackgroundColor(Color.BLACK);
        /*DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) vidplay.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        vidplay.setLayoutParams(params);*/
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        TabLayout tbLayout=(TabLayout) activity.findViewById(R.id.tab);
        tbLayout.setVisibility(View.GONE);
        vidFrame.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
        MainLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT));
        vidplay.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,0,0,0);
        vidFrame.setLayoutParams(params);
        vidFrame.invalidate();
        vidFrame.setRadius(0);
        isMaximized = true;
        scroll = false;
        typeSection.setVisibility(View.GONE);
        Separator.setVisibility(View.GONE);
        /*/ConstraintLayout.LayoutParams Mainparams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams vidparams = vidFrame.getLayoutParams();
        vidparams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT);
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;// - 30;
        int x = ViewGroup.LayoutParams.MATCH_PARENT;
        int y = ViewGroup.LayoutParams.MATCH_PARENT;
        //params.width =width;//200;
        //params.height =height;//200;
        //vidparams.width =params.height;//200;
        //vidparams.height =params.width;//200;
        //params.width = height;//200;
        //params.height = width ;//200;
        params.width = x;//200;
        params.height = y ;//200;
        //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        vidFrame.setLayoutParams(vidparams);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/
    }

    public static void RestoreLayout(final Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TabLayout tbLayout=(TabLayout) activity.findViewById(R.id.tab);
        tbLayout.setVisibility(View.VISIBLE);
        vidFrame.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT));
        MainLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        vidplay.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,vidFrameHeight));
        //set initial attributes for mainLayout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8,8,8,8);
        params.topToBottom = R.id.incidenttype;
        //params.bottomToTop = R.id.linearLayout;
        vidFrame.setLayoutParams(params);
        vidFrame.invalidate();
        vidFrame.setRadius(CardViewRadius);
        isMaximized = false;
        typeSection.setVisibility(View.VISIBLE);
        Separator.setVisibility(View.VISIBLE);
    }

    public static void hideStatusBar(final Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onDestroy() {
        onRoadComments = false;
        handler.removeCallbacks(myRunnable);
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(myRunnable);
        handler.removeMessages(0);
        super.onDestroy();
    }

    private  void trackR_TextArea(){
        Message.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                ViewGroup.LayoutParams params = Message.getLayoutParams();
                if(Message.getLineCount() > 2) {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    Message.setLayoutParams(params);
                    Message.invalidate();
                    Message.setMaxLines(5);
                }else{
                    if(Message.getLineCount() <= 2) {
                        params.height = Height;
                        Message.setLayoutParams(params);
                        Message.invalidate();
                        Message.setMaxLines(5);
                    }
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                //if(imm.isActive()){
                //    scrollView.arrowScroll(ScrollView.FOCUS_DOWN);
                //}
            }
            public void afterTextChanged(Editable s) {
                ViewGroup.LayoutParams params = Message.getLayoutParams();
                if(Message.getLineCount() > 2) {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    Message.setLayoutParams(params);
                    Message.invalidate();
                    Message.setMaxLines(5);
                }else{
                    if(Message.getLineCount() <= 2) {
                        params.height = Height;
                        Message.setLayoutParams(params);
                        Message.invalidate();
                        Message.setMaxLines(5);
                    }
                }
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        scroll = false;
        if(id == R.id.closeImage){
            MainPreviewLayer.setVisibility(View.GONE);
            selectedImage = null;
            Image.setImageDrawable(null);
        }
        return false;
    }

    public static void InitiateCameraForCommentPic(final Activity Myactivity){
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
        if(accessKeys.isApproval()){
            String messageContent = Message.getText().toString();
            String Picure;
            if(!messageContent.isEmpty()) {
                Message.setText("");
                if(selectedImage == null || selectedImage.equalsIgnoreCase(" ")) {
                    Picure = "none";
                }else{
                    Picure = selectedImage;
                    MainPreviewLayer.setVisibility(View.GONE);
                    selectedImage = null;
                    Image.setImageDrawable(null);
                }
                int count = myAdapter.getItemCount();
                updateRecycler(getActivity(),false,count,messageContent,Picure);
            }else{
                Message.requestFocus();
            }
        }else{
            Toast.makeText(getActivity(), "Cannot perform transaction, your verification status is pending!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(DrawablePosition target) {
        switch (target) {
            case LEFT:
                //Launch camera
                onRoadComments = true;
                InitiateCameraForCommentPic(getActivity());
                break;
            case RIGHT:
                //Do something here
                break;
            case TOP:
                //Do something here
                break;
            case BOTTOM:
                //Do something here
                break;

            default:
                break;
        }

    }

    // update recyclerview
    private static void updateRecycler(final Activity activity,final boolean updateId, final int count, final String messageContent,final String pictureContent){
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
        myAdapter.Times[count] = "sending...";
        myAdapter.Comment[count] = messageContent;
        myAdapter.Image[count] = accessKeys.getUserImage();
        myAdapter.Name[count] = InitializeFirstLetter(accessKeys.getName())+" "+InitializeFirstLetter(accessKeys.getSurname());
        myAdapter.CommentImage[count] = pictureContent;
        myAdapter.IncidentTime[count] = Time();
        myAdapter.IncidentPlace[count] = "none";
        myAdapter.Locator[count] = false;
        myAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(myAdapter.getItemCount()-1);
        sendUserComment(activity,updateId,accessKeys.getDefaultUserId(),InitializeFirstLetter(accessKeys.getName())+" "+InitializeFirstLetter(accessKeys.getSurname()),getMyDocRef(),messageContent,accessKeys.getUserImage(),pictureContent,myAdapter.Times, count);
    }

    //sending user Message
    private static void sendUserComment(final Activity activity,final boolean updateId, final String userId, final String Name, final String Doc, final String Message,final String Image,final String commentImage,final String []TimeLayout, final int index){
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            final String time = Time();
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("document ref", defaultvalue);
            user.put("userid", userId);
            user.put("name", Name);
            user.put("parentDocument",Doc);
            user.put("date", ToDate());
            user.put("time", time);
            user.put("comment", Message);
            user.put("Image", Image);
            user.put("commentImage", commentImage);
            user.put("incidentTime", "none");
            user.put("incidentLastSeenPlace", "none");
            user.put("locator", false);

            // Add a new document with a generated ID
            db.collection(constants.roadComments)
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            final  String document = documentReference.getId();
                            CollectionReference collectionReference = db.collection(constants.roadComments);
                            collectionReference.document(documentReference.getId()).update("document ref", document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //set Realm user information;
                                        Loginfo("user Personal Details successfully added");
                                        if(updateId){
                                            Loginfo("user Personal Details successfully added");
                                            TimeLayout[index] = time.substring(0,5);
                                            myAdapter.notifyDataSetChanged();
                                            recyclerView.smoothScrollToPosition(myAdapter.getItemCount());
                                        }else{
                                            //update time on recyclerview
                                            TimeLayout[index] = time.substring(0,5);
                                            myAdapter.notifyDataSetChanged();
                                            recyclerView.smoothScrollToPosition(myAdapter.getItemCount());
                                        }
                                        if(!commentImage.equalsIgnoreCase("none")){
                                            saveCommentImage(activity,commentImage, document);
                                        }
                                    }else{
                                        globalMethods.stopProgress = true;
                                        Logerror("unable to update user ID Details, ");
                                        //update time on recyclerview
                                        TimeLayout[index] = "Failed";
                                        myAdapter.notifyDataSetChanged();
                                        recyclerView.smoothScrollToPosition(myAdapter.getItemCount());
                                    }
                                }

                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            globalMethods.stopProgress = true;
                            Logerror("unable to add user Personal Details, " + e.getMessage());
                            //update time on recyclerview
                            TimeLayout[index] = "Failed";
                            myAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(myAdapter.getItemCount());
                        }
                    });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
        }
    }

    //set profile picture to storage
    public static String saveCommentImage(final Activity activity, final String Image, final String Document){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.roadComments)
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
                    updateProfileDocument(activity,downloadUri.toString(),Document);
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
    public static void updateProfileDocument(final Activity activity, final String picture, final String documentRef){
        try {
            Map<String, Object> user = new HashMap<>();
            user.put("commentImage", picture);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.roadComments );
            collectionReference.document(documentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Constraints.TAG, "DocumentSnapshot added with ID: " + documentRef);
                    }else {
                        // Handle failures
                        // ...
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
                    Log.w(Constraints.TAG, "Error adding document", e);
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
            Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean scroll;
    public static final Handler handler = new Handler();
    final static int delay = 500; //milliseconds
    public static Runnable myRunnable;
    public static void MonitorScroll(){
        handler.postDelayed(myRunnable = new Runnable(){
            public void run(){
                if(scroll) {
                    if (myAdapter != null)
                        if(myAdapter.getItemCount()!= 0)
                            recyclerView.smoothScrollToPosition(myAdapter.getItemCount()-1);
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
