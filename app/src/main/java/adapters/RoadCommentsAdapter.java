package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.R;

public class RoadCommentsAdapter extends RecyclerView.Adapter <commentsViewHolder>{
    Activity activity;
    public String[] Name;
    public String[] Comment;
    public String[] Image;
    public String[] CommentImage;
    public String[] Dates;
    public String[] Times;
    public String[] IncidentTime;
    public String[] IncidentPlace;
    public boolean[] Locator;

    public String[] getName() {
        return Name;
    }

    public void setName(String[] name) {
        Name = name;
    }

    public String[] getComment() {
        return Comment;
    }

    public void setComment(String[] comment) {
        Comment = comment;
    }

    public String[] getImage() {
        return Image;
    }

    public void setImage(String[] image) {
        Image = image;
    }

    public String[] getCommentImage() {
        return CommentImage;
    }

    public void setCommentImage(String[] commentImage) {
        CommentImage = commentImage;
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

    public String[] getIncidentTime() {
        return IncidentTime;
    }

    public void setIncidentTime(String[] incidentTime) {
        IncidentTime = incidentTime;
    }

    public String[] getIncidentPlace() {
        return IncidentPlace;
    }

    public void setIncidentPlace(String[] incidentPlace) {
        IncidentPlace = incidentPlace;
    }

    public boolean[] getLocator() {
        return Locator;
    }

    public void setLocator(boolean[] locator) {
        Locator = locator;
    }

    public RoadCommentsAdapter(Activity activity) {
        this.activity = activity;
    }
    @NonNull
    @Override
    public commentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagelist, parent, false);
        return new commentsViewHolder(view);
    }

    int defaultColor;
    @Override
    public void onBindViewHolder(final @NonNull commentsViewHolder holder, int position) {
        //Convert Date
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd", Locale.UK);
        SimpleDateFormat fullDateformat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.UK);
        Date sysDate = new Date(Dates[position]);
        String Today = dateformat.format(sysDate);
        Date fullDate = new Date(Today);
        String finalDate = fullDateformat.format(fullDate);
        if(position == 0){
            holder.dateTop.setText(finalDate);
            defaultColor = holder.comment.getCurrentTextColor();
        }else{
            if(!Dates[position - 1].equalsIgnoreCase(Dates[position])){
                holder.dateTop.setText(finalDate);
                holder.DateLayout.setVisibility(View.VISIBLE);
            }else{
                holder.DateLayout.setVisibility(View.GONE);
            }
        }
        if(Locator[position]){
            String text = "Last seen : "+IncidentTime[position]+" on "+IncidentPlace[position] +" - <font color=#cc0000>"+Times[position].substring(0,5)+"</font>";
            holder.comment.setText(Html.fromHtml(text));
            holder.comment.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }else{
            holder.comment.setText(Comment[position]);
        }
        Glide.with(activity).load(Image[position]).into(holder.profile);
        holder.name.setText(Name[position]);
        if(Times[position].equalsIgnoreCase("sending...")||Times[position].equalsIgnoreCase("Failed")){
            holder.time.setText(Times[position]);
            holder.time.setTextColor(Color.RED);
        }else{
            holder.time.setText(Times[position].substring(0,5));
            holder.time.setTextColor(defaultColor);
        }

        if(!CommentImage[position].equalsIgnoreCase("none")){
            Glide.with(activity).load(CommentImage[position]).into(holder.commentImage);
        }else{
            holder.CommentLayout.setVisibility(View.GONE);
        }
        int Header = 0;
        if(holder.CommentLayout.getVisibility() == View.GONE){
            ViewGroup.LayoutParams params = holder.CommentLayout.getLayoutParams();
            Header += params.height; // for space under commentPicture
        }
        if(holder.DateLayout.getVisibility() == View.GONE){
            ViewGroup.LayoutParams params = holder.DateLayout.getLayoutParams();
            Header += params.height;
        }
        //set Header Layout
        ViewGroup.LayoutParams params = holder.Header.getLayoutParams();
        int defaultHeight = params.height;
        params.height = defaultHeight - Header;
        params.width = holder.Header.getLayoutParams().width;
        holder.Header.setLayoutParams(params);
        holder.Header.invalidate();
        //Image
        holder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                PreviewPicture(activity,CommentImage[position]);
            }
        });
    }

    public static void PreviewPicture(final Activity Myactivity, final String Image){
        final Dialog dialog = new Dialog(Myactivity);
        dialog.setContentView(R.layout.previewimage);
        dialog.setCancelable(true);
        ImageView picture = (ImageView) dialog.findViewById(R.id.image);
        Glide.with(Myactivity).load(Image).into(picture);
        dialog.show();
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

