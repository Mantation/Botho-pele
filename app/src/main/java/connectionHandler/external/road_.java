package connectionHandler.external;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
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
import java.util.Date;
import java.util.List;

import adapters.MissingListAdapter;
import adapters.RoadListAdapter;
import constants.constants;

import static methods.globalMethods.clearList;

public class road_ extends Application {
    public static List<String> image = new ArrayList<String>();
    public static List<String> dateTime = new ArrayList<String>();
    public static List<String> File = new ArrayList<String>();
    public static List<String> Incident = new ArrayList<String>();
    public static List<String> Info = new ArrayList<String>();
    public static List<String> Document = new ArrayList<String>();
    public static List<Boolean> isVideo = new ArrayList<Boolean>();
    static RoadListAdapter roadlistAdapter;

    public static void getAllDocuments(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final ProgressBar progressBar, FloatingActionButton Initiate) {
        clearList(image);
        clearList(dateTime);
        clearList(File);
        clearList(Incident);
        clearList(Info);
        clearList(isVideo);
        clearList(Document);
        //gets all documents from firestore
        getFirestoreSpaDetails(activity,recyclerView,progressBar,Initiate);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }

    //get patients
    public static void getSpaDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final ProgressBar progressBar, final FloatingActionButton Initiate){
        clearList(image);
        clearList(dateTime);
        clearList(File);
        clearList(Incident);
        clearList(Info);
        clearList(isVideo);
        clearList(Document);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.road)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("date")!=null  && document.get("time")!=null &&  document.get("document ref")!=null && document.get("userid")!=null &&  document.get("reporter")!=null && document.get("incidentTime")!=null
                                        && document.get("incidentType")!=null &&  document.get("incidentLastSeenPlace")!=null && document.get("submitterCoordinates")!=null  &&  document.get("distanceDifference")!=null && document.get("moreInfo")!=null) {
                                    image.add(document.get("imageFile").toString());
                                    dateTime.add(document.get("date").toString()+" "+ document.get("time").toString()+"~"+count);
                                    isVideo.add(Boolean.parseBoolean(document.get("isVideo").toString()));
                                    if(Boolean.parseBoolean(document.get("isVideo").toString())){
                                        File.add(document.get("videoFile").toString());
                                    }else{
                                        File.add(document.get("imageFile").toString());
                                    }
                                    Incident.add(document.get("incidentType").toString());
                                    Info.add(document.get("moreInfo").toString());
                                    Document.add(document.get("document ref").toString());
                                    count++;
                                }
                            }

                            final String []Image = new String [image.size()];
                            final String []MyFile = new String [File.size()];
                            final String []MyIncident = new String [Incident.size()];
                            final String []MyInfo = new String [Info.size()];
                            final boolean []MyVideo = new boolean [isVideo.size()];
                            final String []MyDocument = new String [Document.size()];
                            Collections.sort(dateTime,Collections.reverseOrder());
                            int counter = 0;
                            for (String date:dateTime) {
                                String []getDate = date.split("~");
                                int index = Integer.parseInt(getDate[1]);
                                Image[counter] = image.get(index);
                                MyFile[counter] = File.get(index);
                                MyIncident[counter] = Incident.get(index);
                                MyInfo[counter] = Info.get(index);
                                MyVideo[counter] = isVideo.get(index);
                                MyDocument[counter] = Document.get(index);
                                counter++;
                            }
                            roadlistAdapter = new RoadListAdapter(activity);
                            roadlistAdapter.setImage(Image);
                            roadlistAdapter.setFile(MyFile);
                            roadlistAdapter.setIncident(MyIncident);
                            roadlistAdapter.setInfo(MyInfo);
                            roadlistAdapter.setIsVideo(MyVideo);
                            roadlistAdapter.setDocument(MyDocument);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(roadlistAdapter);
                            progressBar.setVisibility(View.GONE);
                            Initiate.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    //get all documents from firestore
    public static  void getFirestoreSpaDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final ProgressBar progressBar, final FloatingActionButton Initiate){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.road).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.road)
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
