package connectionHandler.external;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import adapters.MissingSlinder;
import constants.constants;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.profile;
import menuFragment.main;

import static methods.globalMethods.clearList;

public class missingSlider_ extends Application {
    static MissingSlinder sliderAdapter;
    static List<String> Images = new ArrayList<String>();
    static List<String> Document = new ArrayList<String>();
    static List<String> name = new ArrayList<String>();
    static List<String> race = new ArrayList<String>();
    static List<String> gender = new ArrayList<String>();
    static List<String> height = new ArrayList<String>();
    static List<String> built = new ArrayList<String>();
    static List<String> time = new ArrayList<String>();
    static List<String> date = new ArrayList<String>();
    static List<String> seenPlace = new ArrayList<String>();
    static List<String> seenDate = new ArrayList<String>();
    static List<String> info = new ArrayList<String>();
    private static int total;

    public static int getTotal() {
        return total;
    }

    public static void setTotal(int total) {
        missingSlider_.total = total;
    }
    public static void getDBDetails(final Activity activity, final ViewPager viewPager){
        //gets all documents from firestore
        clearList(Images);
        clearList(Document);
        clearList(name);
        clearList(race);
        clearList(gender);
        clearList(height);
        clearList(built);
        clearList(seenPlace);
        clearList(seenDate);
        clearList(info);
        clearList(Document);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(constants.missing)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Images.add(document.get("missinImage").toString());
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
                            }
                            //Name.add(count - 1, CreationName);
                            //Images.add(count - 1, CreationImage);
                            final String[] doc = new String[Document.size()];
                            final String[] image = new String[Images.size()];
                            final String []MyName = new String [name.size()];
                            final String []MyRace = new String [race.size()];
                            final String []MyGender = new String [gender.size()];
                            final String []MyHeight = new String [height.size()];
                            final String []MyBuilt = new String [built.size()];
                            final String []MyLastSeenPlace = new String [seenPlace.size()];
                            final String []myLastSeenDay = new String [seenDate.size()];
                            final String []MyInfo = new String [info.size()];
                            for (int i = 0; i < Images.size(); i++) {
                                doc[i] = Document.get(i);
                                image[i] = Images.get(i);
                                MyName[i] = name.get(i);
                                MyRace[i] = race.get(i);
                                MyGender[i] = gender.get(i);
                                MyHeight[i] = height.get(i);
                                MyBuilt[i] = built.get(i);
                                MyLastSeenPlace[i] = seenPlace.get(i);
                                myLastSeenDay[i] = seenDate.get(i);
                                MyInfo[i] = info.get(i);
                            }
                            setTotal(image.length);
                            final int Total = image.length;
                            //Pager adapter
                            /*PagerAdapter adapter = new PagerAdapter() {
                                LayoutInflater layoutInflater;
                                private String[] Images;
                                private String[] Document;
                                public String []Name;
                                public String []Race;
                                public String []Gender;
                                public String []Height;
                                public String []Built;
                                public String []LastSeenPlace;
                                public String []LastSeenDay;
                                public String []Info;
                                @Override
                                public int getCount() {
                                    return Total;
                                }

                                @Override
                                public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                                    return view.equals(object);
                                }

                                @Override
                                public Object instantiateItem(ViewGroup container, final  int position) {
                                    layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                                    View view = layoutInflater.inflate(R.layout.slide,container,false);
                                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
                                    TextView textView = (TextView) view.findViewById(R.id.document);
                                    Glide.with(context).load(Images[position]).into(imageView);
                                    textView.setText(Document[position]);
                                    container.addView(view,0);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            profile.setMyImage(Images[position]);
                                            profile.setMyName(Name[position]);
                                            profile.setMyRace(Race[position]);
                                            profile.setMyGender(Gender[position]);
                                            profile.setMyHeight(Height[position]);
                                            profile.setMyBuilt(Built[position]);
                                            profile.setMySeenTime(LastSeenDay[position]);
                                            profile.setMySeenPlace(LastSeenPlace[position]);
                                            profile.setMyInfo(Info[position]);
                                            profile.setMyDocRef(Document[position]);
                                            MainActivity.returnToMain = true;
                                            methods.globalMethods.loadFragments(R.id.main, new profile(), context);
                                        }
                                    });
                                    return view;
                                }

                            };*/
                            //sliderAdapter = (MissingSlinder) new InfinitePagerAdapter(new MissingSlinder(context, adapter));
                            sliderAdapter = new MissingSlinder(activity);
                            sliderAdapter.setImages(image);
                            sliderAdapter.setDocument(doc);
                            sliderAdapter.setName(MyName);
                            sliderAdapter.setRace(MyRace);
                            sliderAdapter.setGender(MyGender);
                            sliderAdapter.setHeight(MyHeight);
                            sliderAdapter.setBuilt(MyBuilt);
                            sliderAdapter.setLastSeenPlace(MyLastSeenPlace);
                            sliderAdapter.setLastSeenDay(myLastSeenDay);
                            sliderAdapter.setInfo(MyInfo);
                            viewPager.setAdapter(sliderAdapter);
                            //get screen width & height  missing sliders is 100
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            ((MainActivity) activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                            int height = displayMetrics.heightPixels;
                            int width = displayMetrics.widthPixels;
                            double shift = width * 0.63;
                            int calcValue = (int) shift;
                            //String[] total = String.valueOf(shift).split("\\.");
                            viewPager.setPadding(0,0,calcValue,0);
                            main.addDots(activity,0);
                        }
                    }
                });
    }
}
