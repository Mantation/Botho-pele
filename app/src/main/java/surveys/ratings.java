package surveys;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import constants.constants;
import io.eyec.bombo.bothopele.R;
import properties.accessKeys;

import static logHandler.Logging.Loginfo;

public class ratings extends Application {
    //check if ratings is opened
    public static void checkRatings(final Activity activity) {
        //gets all documents from firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.survey).whereEqualTo("survey", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.get("date") != null) {
                            String mySurveyDate = document.get("date").toString();
                            Date surveyDate = new Date(mySurveyDate);
                            Date currentDate = new Date();
                            if(currentDate.getTime()< surveyDate.getTime()){
                                checkUserRatingsResults(activity,mySurveyDate);
                            }
                        }
                    }
                }
            }
        });

    }
    //check if user has rated
    public static void checkUserRatingsResults(final Activity activity,final String myDate) {
        //gets all documents from firestore
        final String surveyField = "survey-"+myDate.replace("/","_");
        final String ratingsField = "rating-"+myDate.replace("/","_");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.users).whereEqualTo(surveyField, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int count = 0;
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        count++;
                        if (document.get(surveyField) == null) {
                            RateApp(activity,surveyField,ratingsField);
                        }
                    }
                    if(count==0){
                        RateApp(activity,surveyField,ratingsField);
                    }
                }
            }
        });

    }
    // rate the actual app
    static int Rrated;
    public static void RateApp(final Activity activity,final String surveyField,final String rateField) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.ratingslayout);
        dialog.setCancelable(true);
        final TextView Rate = (TextView) dialog.findViewById(R.id.rate);
        final ImageView star1 = (ImageView) dialog.findViewById(R.id.rate1);
        final ImageView star2 = (ImageView) dialog.findViewById(R.id.rate2);
        final ImageView star3 = (ImageView) dialog.findViewById(R.id.rate3);
        final ImageView star4 = (ImageView) dialog.findViewById(R.id.rate4);
        final ImageView star5 = (ImageView) dialog.findViewById(R.id.rate5);

        star1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_unrated);
                star3.setImageResource(R.drawable.star_unrated);
                star4.setImageResource(R.drawable.star_unrated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 20;
                return false;
            }
        });
        star2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_unrated);
                star4.setImageResource(R.drawable.star_unrated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 40;
                return false;
            }
        });
        star3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_rated);
                star4.setImageResource(R.drawable.star_unrated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 60;
                return false;
            }
        });
        star4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_rated);
                star4.setImageResource(R.drawable.star_rated);
                star5.setImageResource(R.drawable.star_unrated);
                Rrated = 80;
                return false;
            }
        });
        star5.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                star1.setImageResource(R.drawable.star_rated);
                star2.setImageResource(R.drawable.star_rated);
                star3.setImageResource(R.drawable.star_rated);
                star4.setImageResource(R.drawable.star_rated);
                star5.setImageResource(R.drawable.star_rated);
                Rrated = 100;
                return false;
            }
        });

        Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Rrated > 0) {
                    SubmitRatings(surveyField,rateField);
                    Rrated = 0;
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Kindly rate this App", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
    //submit the actual rating
    public static void SubmitRatings(final String surveyField, final String ratingField){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //update user details
        CollectionReference collectionReference = db.collection(constants.users);
        collectionReference.document(accessKeys.getDefaultDocument()).update(surveyField,true,ratingField, Rrated).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //success
                    Loginfo("user Items successfully added to cart");
                }
            }

        });

    }

}
