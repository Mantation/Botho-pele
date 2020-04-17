package connectionHandler.external;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import adapters.FriendslistAdapter;
import adapters.MissingListAdapter;
import constants.constants;
import properties.accessKeys;

import static methods.globalMethods.clearList;

public class friendslist_ extends Application {

    public static List<String> name = new ArrayList<String>();
    public static List<String> user = new ArrayList<String>();
    public static List<String> status = new ArrayList<String>();
    static FriendslistAdapter friendslistAdapter;

    public static void getAllDocuments(final Activity activity, final RecyclerView recyclerView, final TextView Info) {
        clearList(name);
        clearList(user);
        clearList(status);
        //gets all documents from firestore
        getFirestoreFriendsDetails(activity,recyclerView,Info);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }
    //get users friends list
    public static void getFriendsDetailsList(final Activity activity, final RecyclerView recyclerView, final TextView Info){
        clearList(name);
        clearList(user);
        clearList(status);
        friendsCount = 0;
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.friends)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                friendsCount++;
                                if(document.get("user")!=null){
                                    List<String> users = Arrays.asList(document.get("user").toString());
                                    String []allValues = users.get(0).split(",");
                                    for (int i = 0; i < allValues.length; i++) {
                                        String []Info = allValues[i].split("~");
                                        String Status = Info[1].replace("]","").trim();
                                        String User = Info[0].replace("[","").trim();
                                        if (Status.equalsIgnoreCase("accepted") || Status.equalsIgnoreCase("pending")) {
                                            if(User.equals(accessKeys.getDefaultUserId())){
                                                user.add(document.getId());
                                                status.add(Status);
                                            }
                                        }
                                    }
                                }
                            }
                            if (friendsCount == totalList)
                                getFriends(activity, recyclerView,Info);
                        }
                    }
                });
    }
    static int friendsCount;
    public static void getFriends(final Activity activity, final RecyclerView recyclerView, final TextView Info){
        for (int i = 0; i < user.size(); i++) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(constants.users).whereEqualTo("userid", user.get(i)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    boolean isFound = false;
                    String Name = "";
                    String Surname = "";
                    String UserId = "";
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.get("gender").toString().equalsIgnoreCase("male")) {
                                Name = "Mr, " + document.get("name").toString();
                            } else {
                                Name = "Ms, " + document.get("name").toString();
                            }
                            Surname = document.get("surname").toString();
                            name.add(Name + " " + Surname);
                        }
                        //friendsCount++;
                        if(friendsCount == totalList){
                            final String []myUser = new String [user.size()];
                            final String []MyName = new String [name.size()];
                            final String []MyStatus = new String [status.size()];
                            for (int x = 0; x < name.size(); x++) {
                                myUser[x] = user.get(x);
                                MyName[x] = name.get(x);
                                MyStatus[x] = status.get(x);
                            }
                            friendslistAdapter = new FriendslistAdapter(activity);
                            friendslistAdapter.setUser(myUser);
                            friendslistAdapter.setName(MyName);
                            friendslistAdapter.setStatus(MyStatus);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(friendslistAdapter);
                            if(myUser.length > 0) {
                                Info.setVisibility(View.GONE);
                            }else{
                                Info.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }
    }

    static int totalList ;
    //get all documents from firestore
    public static  void getFirestoreFriendsDetails(final Activity activity, final RecyclerView recyclerView, final TextView Info){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.friends).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.friends)
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
                            getFriendsDetailsList(activity,recyclerView,Info);
                            totalList = myListOfDocuments.size();
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }
}
