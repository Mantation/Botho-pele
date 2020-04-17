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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapters.MissingCommentsAdapter;
import adapters.RoadCommentsAdapter;
import constants.constants;
import menuFragment.Missing.profile;
import menuFragment.Road.Locator;
import menuFragment.Road.road;
import menuFragment.Road.roadComments;

import static methods.globalMethods.clearList;

public class roadComments_ extends Application {
    public static List<String> image = new ArrayList<String>();
    public static List<String> name = new ArrayList<String>();
    public static List<String> date = new ArrayList<String>();
    public static List<String> time = new ArrayList<String>();
    public static List<String> message = new ArrayList<String>();
    public static List<String> commentImage = new ArrayList<String>();
    public static List<String> IncidentTime = new ArrayList<String>();
    public static List<String> IncidentPlace = new ArrayList<String>();
    public static List<Boolean> locator = new ArrayList<Boolean>();

    static RoadCommentsAdapter roadCommentsAdapter;

    public static void getAllDocuments(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView, final String DocumentRef, final ProgressBar progressBar) {
        clearList(image);
        clearList(name);
        clearList(date);
        clearList(time);
        clearList(message);
        clearList(commentImage);
        clearList(IncidentTime);
        clearList(IncidentPlace);
        clearList(locator);
        //gets all documents from firestore
        getFirestoreSpaMessagesDetails(activity,recyclerView,DocumentRef,progressBar);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }

    //get all documents from firestore
    public static  void getFirestoreSpaMessagesDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView,final String DocumentRef, final ProgressBar progressBar){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.roadComments).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.roadComments)
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
                            getSpaMessagesDetails(activity,recyclerView,DocumentRef, progressBar);
                            //getIssues(activity,context, view, recyclerView); //ammended
                        }
                    }
                });
    }

    //get patients
    public static void getSpaMessagesDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView,final String DocumentRef, final ProgressBar progressBar){
        clearList(image);
        clearList(name);
        clearList(date);
        clearList(time);
        clearList(message);
        clearList(commentImage);
        clearList(IncidentTime);
        clearList(IncidentPlace);
        clearList(locator);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.roadComments)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("parentDocument")!=null  && document.get("date")!=null &&  document.get("time")!=null && document.get("comment")!=null && document.get("name")!=null && document.get("document ref")!=null &&
                                        document.get("Image")!=null  && document.get("commentImage")!=null && document.get("userid")!=null &&  document.get("incidentTime")!=null && document.get("incidentLastSeenPlace")!=null &&  document.get("locator")!=null) {
                                    if(DocumentRef.equals(document.get("parentDocument").toString())) {
                                        if(!Boolean.parseBoolean(document.get("locator").toString())){
                                            message.add(document.get("comment").toString());
                                            commentImage.add(document.get("commentImage").toString());
                                            IncidentTime.add("none");
                                            IncidentPlace.add("none");

                                        }else{
                                            message.add("none");
                                            commentImage.add("none");
                                            IncidentTime.add(document.get("incidentTime").toString());
                                            IncidentPlace.add(document.get("incidentLastSeenPlace").toString());
                                        }
                                        image.add(document.get("Image").toString());
                                        name.add(document.get("name").toString());
                                        date.add(document.get("date").toString());
                                        time.add(document.get("time").toString());
                                        locator.add(Boolean.parseBoolean(document.get("locator").toString()));
                                    }
                                }
                            }
                            final String []MyName = new String [name.size()];
                            final String []MyMessage = new String [message.size()];
                            final String []MyImage = new String [image.size()];
                            final String []Mydate = new String [date.size()];
                            final String []Mytime = new String [time.size()];
                            final String []MyCommentImage = new String [time.size()];
                            final String []MyIncTime = new String [IncidentTime.size()];
                            final String []MyIncPlace = new String [IncidentPlace.size()];
                            final boolean []MyLocator = new boolean[locator.size()];
                            for (int i = 0; i < name.size(); i++) {
                                MyName[i] = name.get(i);
                                MyMessage[i] = message.get(i);
                                MyImage[i] = image.get(i);
                                Mydate[i] = date.get(i);
                                Mytime[i] = time.get(i);
                                MyCommentImage[i] = commentImage.get(i);
                                MyIncTime[i] = IncidentTime.get(i);
                                MyIncPlace[i] = IncidentPlace.get(i);
                                MyLocator[i] = locator.get(i);
                            }
                            //sort ascending
                            String tempName = "";
                            String tempImage = "";
                            String tempMessage= "";
                            String tempDate = "";
                            String tempTime = "";
                            String tempCommentImage = "";
                            String tempIncTime = "";
                            String tempIncPlace = "";
                            boolean tempLocator = false;
                            for (int i = 0; i < MyMessage.length; i++) {
                                for (int j = i; j < MyMessage.length - 1; j++) {
                                    int MyDate = Integer.parseInt(Mydate[j+1].replace("/",""));
                                    int MyTime = Integer.parseInt(Mytime[j+1].replace(":",""));
                                    int MyOldDate = Integer.parseInt(Mydate[i].replace("/",""));
                                    int MyOldTime = Integer.parseInt(Mytime[i].replace(":",""));
                                    if(MyOldDate>MyDate)
                                    {

                                        tempImage = MyImage[j+1];
                                        MyImage[j+1] = MyImage[i];
                                        MyImage[i] = tempImage;

                                        tempDate = Mydate[j+1];
                                        Mydate[j+1] = Mydate[i];
                                        Mydate[i] = tempDate;

                                        tempTime = Mytime[j+1];
                                        Mytime[j+1] = Mytime[i];
                                        Mytime[i] = tempTime;

                                        tempMessage = MyMessage[j+1];
                                        MyMessage[j+1] = MyMessage[i];
                                        MyMessage[i] = tempMessage;

                                        tempName = MyName[j+1];
                                        MyName[j+1] = MyName[i];
                                        MyName[i] = tempName;

                                        tempCommentImage = MyCommentImage[j+1];
                                        MyCommentImage[j+1] = MyCommentImage[i];
                                        MyCommentImage[i] = tempCommentImage;

                                        tempIncTime = MyIncTime[j+1];
                                        MyIncTime[j+1] = MyIncTime[i];
                                        MyIncTime[i] = tempIncTime;

                                        tempIncPlace = MyIncPlace[j+1];
                                        MyIncPlace[j+1] = MyIncPlace[i];
                                        MyIncPlace[i] = tempIncPlace;

                                        tempLocator = MyLocator[j+1];
                                        MyLocator[j+1] = MyLocator[i];
                                        MyLocator[i] = tempLocator;

                                    }else{
                                        if (MyOldDate==MyDate){
                                            if(MyOldTime>MyTime)
                                            {
                                                tempImage = MyImage[j+1];
                                                MyImage[j+1] = MyImage[i];
                                                MyImage[i] = tempImage;

                                                tempDate = Mydate[j+1];
                                                Mydate[j+1] = Mydate[i];
                                                Mydate[i] = tempDate;

                                                tempTime = Mytime[j+1];
                                                Mytime[j+1] = Mytime[i];
                                                Mytime[i] = tempTime;

                                                tempMessage = MyMessage[j+1];
                                                MyMessage[j+1] = MyMessage[i];
                                                MyMessage[i] = tempMessage;

                                                tempName = MyName[j+1];
                                                MyName[j+1] = MyName[i];
                                                MyName[i] = tempName;

                                                tempCommentImage = MyCommentImage[j+1];
                                                MyCommentImage[j+1] = MyCommentImage[i];
                                                MyCommentImage[i] = tempCommentImage;

                                                tempIncTime = MyIncTime[j+1];
                                                MyIncTime[j+1] = MyIncTime[i];
                                                MyIncTime[i] = tempIncTime;

                                                tempIncPlace = MyIncPlace[j+1];
                                                MyIncPlace[j+1] = MyIncPlace[i];
                                                MyIncPlace[i] = tempIncPlace;

                                                tempLocator = MyLocator[j+1];
                                                MyLocator[j+1] = MyLocator[i];
                                                MyLocator[i] = tempLocator;
                                            }
                                        }
                                    }
                                }
                            }
                            roadCommentsAdapter = new RoadCommentsAdapter(activity);
                            roadCommentsAdapter.setComment(MyMessage);
                            roadCommentsAdapter.setName(MyName);
                            roadCommentsAdapter.setImage(MyImage);
                            roadCommentsAdapter.setCommentImage(MyCommentImage);
                            roadCommentsAdapter.setTimes(Mytime);
                            roadCommentsAdapter.setDates(Mydate);
                            roadCommentsAdapter.setIncidentTime(MyIncTime);
                            roadCommentsAdapter.setIncidentPlace(MyIncPlace);
                            roadCommentsAdapter.setLocator(MyLocator);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(roadCommentsAdapter);
                            recyclerView.smoothScrollToPosition(message.size());
                            roadComments.myAdapter = roadCommentsAdapter;
                            Locator.myAdapter = roadCommentsAdapter;
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
