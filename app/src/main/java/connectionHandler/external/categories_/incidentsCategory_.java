package connectionHandler.external.categories_;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapters.IncidentsAdapter;
import constants.constants;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static methods.globalMethods.clearList;

public class incidentsCategory_ extends Application {
    public static List<String> Incident = new ArrayList<String>();
    static IncidentsAdapter incidentsAdapter;

    public static void getAllDocuments(final Activity activity, final RecyclerView recyclerView) {
        clearList(Incident);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.crimes)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                while(document.get("name"+count)!=null){
                                    String name = document.get("name"+count).toString();
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Incident.add(name);
                                    count++;
                                }
                            }
                            String []Name = new String [Incident.size()];
                            for (int i = 0; i < Incident.size(); i++) {
                                Name[i] = Incident.get(i);
                            }
                            incidentsAdapter = new IncidentsAdapter(activity,activity);
                            incidentsAdapter.setIncident(Name);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(incidentsAdapter);


                        }
                    }
                });
        //getCommentIssues(activity, context, view, recyclerView); - should be like this
    }
}
