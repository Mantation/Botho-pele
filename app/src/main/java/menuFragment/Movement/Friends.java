package menuFragment.Movement;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.profile;
import menuFragment.main;
import methods.globalMethods;
import properties.accessKeys;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static logHandler.Logging.Logerror;
import static logHandler.Logging.Loginfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class Friends extends android.app.Fragment implements View.OnClickListener{
    View myview;
    EditText Phone;
    CardView Submit;
    TextView Info;
    static ProgressBar progressBar;
    public static RecyclerView recyclerView;


    public Friends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = myview.findViewById(R.id.recycler_friends);
        progressBar = myview.findViewById(R.id.progress);
        Phone = myview.findViewById(R.id.phone);
        Submit = myview.findViewById(R.id.MySubmit);
        Info = myview.findViewById(R.id.info);
        Info.setVisibility(View.GONE);
        connectionHandler.external.friendslist_.getAllDocuments(getActivity(), recyclerView,Info);
        progressBar.setVisibility(View.GONE);
        Submit.setOnClickListener(this);
        return myview;
    }

    //Determining existing user
    public static void getClientValidityInformation(final Activity activity,final View view, final String Phone,final ProgressBar progressBar, final EditText editText) {
        //gets all documents from firestore
        final String phone = "+27"+Phone.substring(1,Phone.length());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users).whereEqualTo("phone", phone).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.get("name") != null && document.get("surname") != null) {
                            String Name = document.get("name").toString();
                            String Surname = document.get("surname").toString();
                            String id = document.get("userid").toString();
                            String gender = "";
                            if(document.get("gender").toString().equalsIgnoreCase("Male")){
                                gender = "Mr, ";
                            }else{
                                gender = "Ms, ";
                            }
                            if(id.equals(accessKeys.getDefaultUserId())){
                                //Toast.makeText(activity, "You cannot send an invitation to self!", Toast.LENGTH_SHORT).show();
                                globalMethods.ConfirmResolution(view,"You cannot send an invitation to self!");
                            }else{
                                sendInvite(activity,view,id,gender+Name,Surname,progressBar,editText);
                            }
                        }
                    }
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    public static void sendInvite(final Activity activity,final View primaryView, final String userId, final String Name, final String Surname,final ProgressBar progressBar, final EditText editText){
        //initiate
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.friendlayout);
        dialog.setCancelable(true);
        final TextView name = (TextView) dialog.findViewById(R.id.name);
        final TextView surname = (TextView) dialog.findViewById(R.id.surname);
        final CardView confirm = (CardView) dialog.findViewById(R.id.MyConfirm);
        name.setText(Name);
        surname.setText(Surname);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                requestFriendship(activity,primaryView,userId,progressBar,editText);
            }
        });
        dialog.show();
    }

    //update PicturePath
    public static void requestFriendship(final Activity activity,final View view, final String userid, final ProgressBar progressBar, final EditText editText){
        try {
            String defaultvalue = "n/a";
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(constants.friends).document(userid)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.get("user")!=null){
                                    DocumentReference docRef = db.document(constants.friends+"/"+userid);
                                    docRef.update("user", FieldValue.arrayUnion(accessKeys.getDefaultUserId()+"~pending"));
                                    editText.getText().clear();
                                    globalMethods.ConfirmResolution(view,"User successfully added!");
                                    globalMethods.stopProgress = true;
                                    progressBar.setVisibility(View.GONE);
                                }else{
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("user", Arrays.asList(accessKeys.getDefaultUserId()+"~pending"));
                                    user.put("live", false);
                                    db.collection(constants.friends).document(userid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //set Realm user information;
                                            editText.getText().clear();
                                            Loginfo("user Personal Details successfully added");
                                            globalMethods.stopProgress = true;
                                            progressBar.setVisibility(View.GONE);
                                            activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    globalMethods.ConfirmResolution(view,"User successfully added!");
                                                }
                                            });

                                            editText.requestFocus();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                            globalMethods.stopProgress = true;
                                            progressBar.setVisibility(View.GONE);
                                            activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(activity, "unable to add selected user!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            Logerror("unable to add selected user!, " + e.getMessage());
                                        }
                                    });

                                }
                            }
                        });
        }catch (Exception exception){
            Log.w(TAG, "Error writing document", exception);
            globalMethods.stopProgress = true;
            progressBar.setVisibility(View.GONE);
            exception.getMessage();
            exception.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (accessKeys.isApproval()) {
            if (!Phone.getText().toString().isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                getClientValidityInformation(getActivity(), v, Phone.getText().toString().trim(), progressBar, Phone);
            }
        }else{
            Toast.makeText(getActivity(), "Cannot perform transaction, your verification status is pending!", Toast.LENGTH_SHORT).show();
        }
    }
}
