package connectionHandler.external;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapters.MissingListAdapter;
import constants.constants;
import properties.accessKeys;

import static methods.globalMethods.clearList;

public class missing_ extends Application {
    public static List<String> image = new ArrayList<String>();
    public static List<String> name = new ArrayList<String>();
    public static List<String> race = new ArrayList<String>();
    public static List<String> gender = new ArrayList<String>();
    public static List<String> height = new ArrayList<String>();
    public static List<String> built = new ArrayList<String>();
    public static List<String> time = new ArrayList<String>();
    public static List<String> date = new ArrayList<String>();
    public static List<String> seenPlace = new ArrayList<String>();
    public static List<String> seenDate = new ArrayList<String>();
    public static List<String> info = new ArrayList<String>();
    public static List<String> Document = new ArrayList<String>();
    public static List<String> dateTime = new ArrayList<String>();
    static MissingListAdapter missinglistAdapter;

    public static void getAllDocuments(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final ProgressBar progressBar, FloatingActionButton Initiate) {
        clearList(image);
        clearList(name);
        clearList(race);
        clearList(gender);
        clearList(height);
        clearList(built);
        clearList(time);
        clearList(date);
        clearList(seenPlace);
        clearList(seenDate);
        clearList(info);
        clearList(Document);
        clearList(dateTime);
        //gets all documents from firestore
        getFirestoreSpaDetails(activity,recyclerView,progressBar,Initiate);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }

    //get patients
    public static void getSpaDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final ProgressBar progressBar, final FloatingActionButton Initiate){
        clearList(image);
        clearList(name);
        clearList(race);
        clearList(gender);
        clearList(height);
        clearList(built);
        clearList(time);
        clearList(date);
        clearList(seenPlace);
        clearList(seenDate);
        clearList(info);
        clearList(Document);
        clearList(dateTime);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.missing)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("date")!=null  && document.get("time")!=null &&  document.get("document ref")!=null && document.get("missinImage")!=null && document.get("userid")!=null &&  document.get("reporter")!=null && document.get("victimBuilt")!=null &&
                                        document.get("victimGender")!=null  && document.get("victimHeight")!=null &&  document.get("victimLastSeenDate")!=null && document.get("victimLastSeenPlace")!=null && document.get("submitterCoordinates")!=null &&  document.get("victimName")!=null && document.get("victimRace")!=null) {
                                    image.add(document.get("missinImage").toString());
                                    name.add(document.get("victimName").toString());
                                    race.add(document.get("victimRace").toString());
                                    gender.add(document.get("victimGender").toString());
                                    height.add(document.get("victimHeight").toString());
                                    built.add(document.get("victimBuilt").toString());
                                    date.add(document.get("date").toString());
                                    time.add(document.get("time").toString());
                                    seenPlace.add(document.get("victimLastSeenPlace").toString());
                                    seenDate.add(document.get("victimLastSeenDate").toString());
                                    Document.add(document.get("document ref").toString());
                                    if(document.get("moreInfo")!=null){
                                        info.add(document.get("moreInfo").toString());
                                    }else{
                                        info.add("");
                                    }
                                    dateTime.add(document.get("date").toString()+" "+ document.get("time").toString()+"~"+count);
                                    count++;
                                }
                            }
                            final String []Image = new String [image.size()];
                            final String []MyName = new String [name.size()];
                            final String []MyRace = new String [race.size()];
                            final String []MyGender = new String [gender.size()];
                            final String []MyHeight = new String [height.size()];
                            final String []MyBuilt = new String [built.size()];
                            final String []myDate = new String [date.size()];
                            final String []myTime = new String [time.size()];
                            final String []MyLastSeenPlace = new String [seenPlace.size()];
                            final String []myLastSeenDay = new String [seenDate.size()];
                            final String []MyInfo = new String [info.size()];
                            final String []MyDocument = new String [Document.size()];
                            Collections.sort(dateTime,Collections.reverseOrder());
                            int counter = 0;
                            for (String datetime:dateTime) {
                                String []getDate = datetime.split("~");
                                int index = Integer.parseInt(getDate[1]);
                                Image[counter] = image.get(index);
                                MyName[counter] = name.get(index);
                                MyRace[counter] = race.get(index);
                                MyGender[counter] = gender.get(index);
                                MyHeight[counter] = height.get(index);
                                MyBuilt[counter] = built.get(index);
                                myDate[counter] = date.get(index);
                                myTime[counter] = time.get(index);
                                MyLastSeenPlace[counter] = seenPlace.get(index);
                                myLastSeenDay[counter] = seenDate.get(index);
                                MyInfo[counter] = info.get(index);
                                MyDocument[counter] = Document.get(index);
                                counter++;
                            }
                            missinglistAdapter = new MissingListAdapter(activity);
                            missinglistAdapter.setImage(Image);
                            missinglistAdapter.setName(MyName);
                            missinglistAdapter.setRace(MyRace);
                            missinglistAdapter.setGender(MyGender);
                            missinglistAdapter.setHeight(MyHeight);
                            missinglistAdapter.setBuilt(MyBuilt);
                            missinglistAdapter.setDates(myDate);
                            missinglistAdapter.setTimes(myTime);
                            missinglistAdapter.setLastSeenPlace(MyLastSeenPlace);
                            missinglistAdapter.setLastSeenDay(myLastSeenDay);
                            missinglistAdapter.setInfo(MyInfo);
                            missinglistAdapter.setDocument(MyDocument);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(missinglistAdapter);
                            progressBar.setVisibility(View.GONE);
                            Initiate.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    //get all documents from firestore
    public static  void getFirestoreSpaDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final ProgressBar progressBar, final FloatingActionButton Initiate){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.missing).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.missing)
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
                            getSpaDetails(activity,recyclerView,progressBar,Initiate);
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }


}
