package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.apache.commons.lang3.ArrayUtils;

import constants.constants;
import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.profile;
import menuFragment.Movement.Friends;
import methods.globalMethods;
import properties.accessKeys;

import static logHandler.Logging.Loginfo;

public class FriendslistAdapter extends RecyclerView.Adapter <friendsViewHolder>{
    Activity activity;
    public String[] User;
    public String[] Name;
    public String[] Status;

    public String[] getUser() {
        return User;
    }

    public void setUser(String[] user) {
        User = user;
    }

    public String[] getName() {
        return Name;
    }

    public void setName(String[] name) {
        Name = name;
    }

    public String[] getStatus() {
        return Status;
    }

    public void setStatus(String[] status) {
        Status = status;
    }

    public FriendslistAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public friendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslist, parent, false);
        return new friendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final friendsViewHolder holder, int position) {
        if (Status[position].equalsIgnoreCase("pending")) {
            //Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.process)).into(holder.Status);
            holder.Status.setImageDrawable(activity.getResources().getDrawable(R.drawable.process));
            holder.Profile.setText(Name[position]);
            holder.UserId.setText(User[position]);
        }else if(Status[position].equalsIgnoreCase("accepted")){
            //Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.on)).into(holder.Status);
            holder.Status.setImageDrawable(activity.getResources().getDrawable(R.drawable.on));
            holder.Profile.setText(Name[position]);
            holder.UserId.setText(User[position]);
        }
        holder.Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity,"press and hold to remove : "+Name[position],Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        holder.Profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef;
                final int position = holder.getAdapterPosition();
                docRef = db.document(constants.friends+"/"+ accessKeys.getDefaultUserId());
                docRef.update("status", "rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //success
                            Loginfo("user Personal Details successfully added");
                            globalMethods.stopProgress = true;
                            User = ArrayUtils.remove(User, position);
                            Name = ArrayUtils.remove(Name, position);
                            Status = ArrayUtils.remove(Status, position);
                            notifyDataSetChanged();
                        }
                    }
                });
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return Name.length;
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

class friendsViewHolder extends RecyclerView.ViewHolder {
    public TextView Profile;
    public ImageView Status;
    public TextView UserId;

    public friendsViewHolder(View view) {
        super(view);
        Status = view.findViewById(R.id.status);
        Profile = view.findViewById(R.id.name);
        UserId = view.findViewById(R.id.userid);
    }
}

