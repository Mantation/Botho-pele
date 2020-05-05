package adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.profile;
import menuFragment.Road.Locator;
import menuFragment.Road.overview;
import menuFragment.Road.roadComments;

import static methods.globalMethods.getBitmapFromURL;

public class RoadListAdapter extends RecyclerView.Adapter <roadViewHolder> {
    Activity activity;
    public String []Image;
    public String []File;
    public String []Incident;
    public boolean []isVideo;
    public String []Info;
    public String []Document;

    public String[] getImage() {
        return Image;
    }

    public void setImage(String[] image) {
        Image = image;
    }

    public String[] getFile() {
        return File;
    }

    public void setFile(String[] file) {
        File = file;
    }

    public String[] getIncident() {
        return Incident;
    }

    public void setIncident(String[] incident) {
        Incident = incident;
    }

    public boolean[] getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(boolean[] isVideo) {
        this.isVideo = isVideo;
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

    public RoadListAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public roadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roadlist, parent, false);
        return new roadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final roadViewHolder holder, int position) {
        Glide.with(activity).load(Image[position]).centerCrop().crossFade().into(holder.Image);
            //==MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //give YourVideoUrl below
            //==retriever.setDataSource(File[position], new HashMap<String, String>());
            // this gets frame at 2nd second
            //==Bitmap image = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            /*try {
                Bitmap bitmap = retriveVideoFrameFromVideo(File[position]);
                if (bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false);
                    holder.Image.setImageBitmap(bitmap);
                    //Glide.with(activity).load(bitmap).asBitmap().centerCrop().into(holder.Image);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }*/
            //use this bitmap image
            //Glide.with(activity).load(Uri.fromFile(new File(File[position]))).asBitmap().centerCrop().into(holder.Image);
            //Glide.with(activity).load(getBitmapFromURL(File[position])).asBitmap().centerCrop().into(holder.Image);
            //Glide.with(activity).load(getBitmapFromURL(File[position])).centerCrop().crossFade().into(holder.Image);
            //==holder.Image.setImageBitmap(image);
            //downloadImage down = new downloadImage();
            //down.execute(File[position],String.valueOf(position));
            //Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(File[position], MediaStore.Video.Thumbnails.MICRO_KIND);
            //Glide.with(activity).load(bitmap).asBitmap().centerCrop().into(holder.Image);
            /*int id = MediaStore.Video.Thumbnails.FULL_SCREEN_KIND;
            ContentResolver crThumb = activity.getContentResolver().;
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
            holder.Image.setImageBitmap(curThumb);
            Glide.with(activity).load(curThumb).asBitmap().centerCrop().into(holder.Image);
            //Glide.with(activity).load(curThumb).centerCrop().crossFade().into(holder.Image);*/
        holder.Incident.setText(Incident[position]);
        holder.Info.setText(Info[position]);
        //Image
        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Locator.setMyDocRef(Document[position]);
                roadComments.setVideo(isVideo[position]);
                roadComments.setMyDocRef(Document[position]);
                roadComments.setMyIncident(Incident[position]);
                roadComments.setMyInfo(Info[position]);
                roadComments.setMyFile(File[position]);
                //MainActivity.returnToRoadHome = false;
                methods.globalMethods.loadFragments(R.id.main, new overview(), activity);
            }
        });


    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return Info.length;
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

    class downloadImage extends AsyncTask<String, Void, Bitmap> {
        roadViewHolder holder;
        int position;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(String... arg0)
        {
            try{
                position = Integer.parseInt(arg0[1]);
                //Prepare to download image
                URL url = new URL(arg0[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                //return total.toString();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            //ImageView imageView = (ImageView) roadHolder.MainLayout.getChildAt(position).findViewById(R.id.roadImage);
            //Glide.with(activity).load(result).asBitmap().centerCrop().into(imageView);
            //Glide.with(activity).load(Uri.fromFile(new File(result))).centerCrop().crossFade().into(imageView);
            super.onPostExecute(result);

        }
    }

    private Bitmap downloadImages(String path, ImageView imageView)
    {
        try{
            //Prepare to download image
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Glide.with(activity).load(bitmap).centerCrop().crossFade().into(imageView);
            return bitmap;

        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }

        return null;
    }
}

class roadViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout MainLayout;
    public static ImageView Image;
    public TextView Incident;
    public TextView Info;

    public roadViewHolder(View view) {
        super(view);
        MainLayout = view.findViewById(R.id.misingMain);
        Image = view.findViewById(R.id.roadImage);
        Incident = view.findViewById(R.id.incident);
        Info = view.findViewById(R.id.Info);
    }
}
