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
import constants.constants;
import menuFragment.Missing.profile;

import static methods.globalMethods.clearList;

public class missingComments_ extends Application {
    public static List<String> image = new ArrayList<String>();
    public static List<String> name = new ArrayList<String>();
    public static List<String> date = new ArrayList<String>();
    public static List<String> time = new ArrayList<String>();
    public static List<String> message = new ArrayList<String>();
    public static List<String> commentImage = new ArrayList<String>();

    static MissingCommentsAdapter missingCommentsAdapter;

    public static void getAllDocuments(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView,final String DocumentRef, final ProgressBar progressBar) {
        clearList(image);
        clearList(name);
        clearList(date);
        clearList(time);
        clearList(message);
        clearList(commentImage);
        //gets all documents from firestore
        getFirestoreSpaMessagesDetails(activity,recyclerView,DocumentRef,progressBar);
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }

    //get all documents from firestore
    public static  void getFirestoreSpaMessagesDetails(final Activity activity, final androidx.recyclerview.widget.RecyclerView recyclerView,final String DocumentRef, final ProgressBar progressBar){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.missingComments).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        dbs.collection(constants.missingComments)
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
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.missingComments)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if(document.get("parentDocument")!=null  && document.get("date")!=null &&  document.get("time")!=null && document.get("comment")!=null && document.get("name")!=null && document.get("document ref")!=null &&
                                        document.get("Image")!=null  && document.get("commentImage")!=null && document.get("userid")!=null) {
                                    if(DocumentRef.equals(document.get("parentDocument").toString())) {
                                        image.add(document.get("Image").toString());
                                        name.add(document.get("name").toString());
                                        date.add(document.get("date").toString());
                                        time.add(document.get("time").toString());
                                        message.add(document.get("comment").toString());
                                        commentImage.add(document.get("commentImage").toString());
                                    }
                                }
                            }
                            final String []MyName = new String [name.size()];
                            final String []MyMessage = new String [message.size()];
                            final String []MyImage = new String [image.size()];
                            final String []Mydate = new String [date.size()];
                            final String []Mytime = new String [time.size()];
                            final String []MyCommentImage = new String [time.size()];
                            for (int i = 0; i < name.size(); i++) {
                                MyName[i] = name.get(i);
                                MyMessage[i] = message.get(i);
                                MyImage[i] = image.get(i);
                                Mydate[i] = date.get(i);
                                Mytime[i] = time.get(i);
                                MyCommentImage[i] = commentImage.get(i);
                            }
                            //sort ascending
                            String tempName = "";
                            String tempImage = "";
                            String tempMessage= "";
                            String tempDate = "";
                            String tempTime = "";
                            String tempCommentImage = "";
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
                                            }
                                        }
                                    }
                                }
                            }
                            missingCommentsAdapter = new MissingCommentsAdapter(activity);
                            missingCommentsAdapter.setComment(MyMessage);
                            missingCommentsAdapter.setName(MyName);
                            missingCommentsAdapter.setImage(MyImage);
                            missingCommentsAdapter.setCommentImage(MyCommentImage);
                            missingCommentsAdapter.setTimes(Mytime);
                            missingCommentsAdapter.setDates(Mydate);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(missingCommentsAdapter);
                            recyclerView.smoothScrollToPosition(message.size());
                            profile.myAdapter = missingCommentsAdapter;
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
