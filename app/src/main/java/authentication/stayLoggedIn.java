package authentication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import properties.accessKeys;

import static properties.accessKeys.setAuthenticated;
import static properties.accessKeys.setNetworkUnAvailable;

public class stayLoggedIn extends MultiDexApplication {
    public static FirebaseUser firebaseUser;
    public static Context context;
    public static boolean networkRecoveryAttempt;

    @Override
    public void onCreate() {
        super.onCreate();
        if(!networkRecoveryAttempt)
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //get firebase data
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(context == null)
            context = this.getApplicationContext();
        //firebaseUser = null;
        //firebaseAuth.signOut();
        if (Availability(context)) {
            //enable offline capabilities
            if(firebaseUser != null) {
                setNetworkUnAvailable(false);
                accessKeys.setLoggedin(true);
                accessKeys.setDefaultUserId(firebaseUser.getUid());
                getUserDetails(firebaseUser.getUid());
            }
        }else{
            setNetworkUnAvailable(true);
            accessKeys.setLoggedin(false);
        }
    }

    public static void getUserDetails(final String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.get("userid")!=null){
                                String UserId = document.get("userid").toString();
                                    if (UserId.equals(userId)) {
                                        accessKeys.setLoggedin(true);
                                        setAuthenticated(true);
                                        accessKeys.setDefaultUserId(userId);
                                        accessKeys.setName(String.valueOf(document.get("name")));
                                        accessKeys.setSurname(String.valueOf(document.get("surname")));
                                        accessKeys.setEmail(String.valueOf(document.get("email")));
                                        accessKeys.setPhone(String.valueOf(document.get("phone")));
                                        accessKeys.setAltPhone(String.valueOf(document.get("altphone")));
                                        accessKeys.setGender(String.valueOf(document.get("gender")));
                                        accessKeys.setApproval(Boolean.valueOf(String.valueOf(document.get("approved"))));
                                        accessKeys.setDefaultDocument(String.valueOf(document.get("document ref")));
                                        accessKeys.setUserImage(String.valueOf(document.get("profileImage")));
                                        accessKeys.setMessagingToken(String.valueOf(document.get("token")));
                                        accessKeys.setIdImage(String.valueOf(document.get("ID")));
                                        //if(document.get("id number")!=null){
                                        //    accessKeys.setIdnumber(String.valueOf(document.get("id number")));
                                        //    accessKeys.setIdType(String.valueOf(document.get("Id type")));
                                        // }
                                        //if network is recovered
                                        if (networkRecoveryAttempt) {
                                            Intent intent = new Intent(context, MainActivity.class);
                                            context.startActivity(intent);
                                        }
                                        break;
                                    }

                                }
                            }
                        }
                    }
                });
    }

    //checks network availability
    public boolean Availability(final Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

