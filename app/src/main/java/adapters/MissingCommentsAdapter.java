package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
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

public class MissingCommentsAdapter extends RecyclerView.Adapter <commentsViewHolder>{
    Activity activity;
    public String[] Name;
    public String[] Comment;
    public String[] Image;
    public String[] CommentImage;
    public String[] Dates;
    public String[] Times;

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

    public MissingCommentsAdapter(Activity activity) {
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
        Glide.with(activity).load(Image[position]).into(holder.profile);
        holder.name.setText(Name[position]);
        if(Times[position].equalsIgnoreCase("sending...")||Times[position].equalsIgnoreCase("Failed")){
            holder.time.setText(Times[position]);
            holder.time.setTextColor(Color.RED);
        }else{
            holder.time.setText(Times[position].substring(0,5));
            holder.time.setTextColor(defaultColor);
        }
        holder.comment.setText(Comment[position]);
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

class commentsViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout DateLayout;
    public ConstraintLayout CommentLayout;
    public TextView dateTop;
    public CircleImageView profile;
    public ImageView commentImage;
    public TextView name;
    public TextView time;
    public TextView comment;
    public RelativeLayout Header;

    public commentsViewHolder(View view) {
        super(view);
        DateLayout = view.findViewById(R.id.dateTitle);
        dateTop = view.findViewById(R.id.dateTop);
        profile = view.findViewById(R.id.Image);
        name = view.findViewById(R.id.name);
        time = view.findViewById(R.id.time);
        comment = view.findViewById(R.id.comment);
        commentImage = view.findViewById(R.id.commentImage);
        Header = view.findViewById(R.id.header);
        CommentLayout = view.findViewById(R.id.commentLayout);
    }
}
