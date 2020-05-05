package menuFragment.registration;


import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import authentication.auth;
import constants.constants;
import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.main;
import methods.globalMethods;
import properties.accessKeys;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static methods.globalMethods.getCameraPermissions;
import static methods.globalMethods.getReadWritePermissions;
import static methods.globalMethods.showView;

/**
 * A simple {@link Fragment} subclass.
 */
public class step2 extends android.app.Fragment implements View.OnClickListener{
    View myview;
    CardView Login;
    static ImageView ID;
    static CircleImageView ProfilePicture;
    static ProgressBar progressBar;
    static Activity activity;
    public static String selectedID;
    public static String selectedImage;
    public static boolean forID;
    public static boolean onRegistration;

    //set selected image on imageview
    public static void setID(String myImage) {
        Glide.with(activity).load(myImage).into(ID);
        selectedID = myImage;
    }
    //set selected image on imageview
    public static void setProfilePicture(String myImage) {
        Glide.with(activity).load(myImage).into(ProfilePicture);
        selectedImage = myImage;
    }

    public step2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_step2, container, false);
        activity = getActivity();
        ID = myview.findViewById(R.id.idpic);
        ProfilePicture = myview.findViewById(R.id.profilepic);
        Login = myview.findViewById(R.id.MyLogin);
        progressBar = myview.findViewById(R.id.progress);
        Login.setOnClickListener(this);
        ProfilePicture.setOnClickListener(this);
        ID.setOnClickListener(this);
        onRegistration = true;
        progressBar.setVisibility(View.GONE);
        return myview;
    }

    public static void InitiateCameraForID(final Activity Myactivity){
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
                forID = true;
                MainActivity.capturePic = true;
                MainActivity.permissionfor = constants.camera;
                getCameraPermissions(Myactivity);
            }
        });
        dialogfromPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forID = true;
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
    public static void InitiateCameraForProPic(final Activity Myactivity){
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
        forID = false;
        onRegistration = true;
        int id = v.getId();
        if(id==R.id.idpic) {
            InitiateCameraForID(getActivity());
        }else if (id==R.id.profilepic) {
            InitiateCameraForProPic(getActivity());
        }else{
            progressBar.setVisibility(View.VISIBLE);
            onRegistration = false;
            auth.InitiateAuth(getActivity(), accessKeys.getPhone(), progressBar);

        }
    }

    //set id picture to storage
    public static String saveIDDocument(final Activity activity, final String Image, final String Document){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.users)
                .child("idImage").child(Document);
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
                    updateIDDocument(activity,downloadUri.toString(),Document);
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
    public static void updateIDDocument(final Activity activity, final String picture, final String DocumentRef){
        try {
            Map<String, Object> user = new HashMap<>();
            user.put("ID", picture);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.users );
            collectionReference.document(DocumentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + DocumentRef);
                        globalMethods.stopProgress = true;
                        selectedID = null;
                        accessKeys.setIdImage(picture);
                    }else {
                        // Handle failures
                        // ...
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
                    Log.w(TAG, "Error adding document", e);
                    globalMethods.stopProgress = true;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
            progressBar.setVisibility(View.GONE);
            globalMethods.stopProgress = true;
            Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
        }

    }

    //set profile picture to storage
    public static String saveProfileDocument(final Activity activity, final String Image, final String Document){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference ref = storageRef.child(constants.users)
                .child("ProfileImage").child(Document);
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
            user.put("profileImage", picture);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection(constants.users );
            collectionReference.document(documentRef).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentRef);
                        globalMethods.stopProgress = true;
                        selectedImage = null;
                        progressBar.setVisibility(View.GONE);
                        accessKeys.setUserImage(picture);
                        methods.globalMethods.loadFragments(R.id.main, new main(), activity);
                    }else {
                        // Handle failures
                        // ...
                        globalMethods.stopProgress = true;
                        Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
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
                    Log.w(TAG, "Error adding document", e);
                    globalMethods.stopProgress = true;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception exception){
            exception.getMessage();
            exception.printStackTrace();
            globalMethods.stopProgress = true;
            progressBar.setVisibility(View.GONE);
            Toast.makeText(activity, "Error adding sending details to server", Toast.LENGTH_SHORT).show();
        }

    }
}
