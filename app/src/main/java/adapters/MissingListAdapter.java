package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.profile;

public class MissingListAdapter extends RecyclerView.Adapter<missingViewHolder> {
    Activity activity;
    public String[] Image;
    public String []Name;
    public String []Race;
    public String []Gender;
    public String []Height;
    public String []Built;
    public String[] Dates;
    public String[] Times;
    public String []LastSeenPlace;
    public String []LastSeenDay;
    public String []Info;
    public String []Document;

    public String[] getImage() {
        return Image;
    }

    public void setImage(String[] image) {
        Image = image;
    }

    public String[] getName() {
        return Name;
    }

    public void setName(String[] name) {
        Name = name;
    }

    public String[] getRace() {
        return Race;
    }

    public void setRace(String[] race) {
        Race = race;
    }

    public String[] getGender() {
        return Gender;
    }

    public void setGender(String[] gender) {
        Gender = gender;
    }

    public String[] getHeight() {
        return Height;
    }

    public void setHeight(String[] height) {
        Height = height;
    }

    public String[] getBuilt() {
        return Built;
    }

    public void setBuilt(String[] built) {
        Built = built;
    }

    public String[] getDates() {
        return Dates;
    }

    public void setDates(String[] dates) {
        Dates = dates;
    }

    public String[] getTimes() {
        return Times;
    }

    public void setTimes(String[] times) {
        Times = times;
    }

    public String[] getLastSeenPlace() {
        return LastSeenPlace;
    }

    public void setLastSeenPlace(String[] lastSeenPlace) {
        LastSeenPlace = lastSeenPlace;
    }

    public String[] getLastSeenDay() {
        return LastSeenDay;
    }

    public void setLastSeenDay(String[] lastSeenDay) {
        LastSeenDay = lastSeenDay;
    }

    public String[] getInfo() {
        return Info;
    }

    public void setInfo(String[] info) {
        Info = info;
    }

    public String[] getDocument() {
        return Document;
    }

    public void setDocument(String[] document) {
        Document = document;
    }

    public MissingListAdapter(Activity activity) {
        this.activity = activity;
    }
    @NonNull
    @Override
    public missingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.missinlist, parent, false);
        return new missingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final missingViewHolder holder, int position) {
        Glide.with(activity).load(Image[position]).into(holder.Image);
        holder.Name.setText(Name[position]);
        holder.Info.setText(Info[position]);
        //Image
        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                profile.setMyImage(Image[position]);
                profile.setMyName(Name[position]);
                profile.setMyRace(Race[position]);
                profile.setMyGender(Gender[position]);
                profile.setMyHeight(Height[position]);
                profile.setMyBuilt(Built[position]);
                profile.setMySeenTime(LastSeenDay[position]);
                profile.setMySeenPlace(LastSeenPlace[position]);
                profile.setMyInfo(Info[position]);
                profile.setMyDocRef(Document[position]);
                MainActivity.returnToMissingHome = false;
                MainActivity.returnToMain = false;
                methods.globalMethods.loadFragments(R.id.main, new profile(), activity);
            }
        });


    }

    @Override
    public int getItemCount() {
        return Image.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return position;
    }
}

class missingViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout MainLayout;
    public CircleImageView Image;
    public TextView Name;
    public TextView Info;

    public missingViewHolder(View view) {
        super(view);
        MainLayout = view.findViewById(R.id.misingMain);
        Image = view.findViewById(R.id.victimpic);
        Name = view.findViewById(R.id.name);
        Info = view.findViewById(R.id.Info);
    }
}
