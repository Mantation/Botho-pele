package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.bumptech.glide.Glide;

import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.profile;

public class MissingSlinder extends PagerAdapter {

    Context context;
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

    /*public MissingSlinder(Context context,PagerAdapter adapter) {
        super(adapter);
        this.context = context;
    }*/
    public MissingSlinder(Context context){

        this.context = context;
    }
    public MissingSlinder(){

        this.context = context;
    }


    public  void setImages(String[] images) {
        Images = images;
    }

    public void setDocument(String[] document) {
        Document = document;
    }

    public void setName(String[] name) {
        Name = name;
    }

    public void setRace(String[] race) {
        Race = race;
    }

    public void setGender(String[] gender) {
        Gender = gender;
    }

    public void setHeight(String[] height) {
        Height = height;
    }

    public void setBuilt(String[] built) {
        Built = built;
    }

    public void setLastSeenPlace(String[] lastSeenPlace) {
        LastSeenPlace = lastSeenPlace;
    }

    public void setLastSeenDay(String[] lastSeenDay) {
        LastSeenDay = lastSeenDay;
    }

    public void setInfo(String[] info) {
        Info = info;
    }

    @Override
    public int getCount() {
        return Images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        //return view == (ConstraintLayout) object;
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container,final  int position) {
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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout)object);
    }

}
