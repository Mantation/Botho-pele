package helperClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import menuFragment.Road.roadComments;

import static android.content.Context.WINDOW_SERVICE;

public class FullScreenMediaController extends MediaController implements View.OnTouchListener{
    public ImageButton fullScreen;
    public String isFullScreen;
    Context context;
    Activity activity;

    public FullScreenMediaController(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton (super.getContext());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        //params.bottomMargin = 80;
        params.rightMargin = 20;
        params.topMargin = 30;
        addView(fullScreen, params);

        //fullscreen indicator from intent
        isFullScreen =  ((Activity)getContext()).getIntent().
                getStringExtra("fullScreenInd");

        if("y".equals(isFullScreen)){
            //fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
        }else{
            //fullScreen.setImageResource(R.drawable.ic_fullscreen);
        }

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                int rotation = display.getRotation();
                if(rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) {
                    roadComments.RestoreLayout(activity);
                }else {
                    roadComments.RedefineLayout(activity);
                    roadComments.hideStatusBar(activity);
                }
                /*Intent intent = new Intent(getContext(),FullScreenVideoActivity.class);


                if("y".equals(isFullScreen)){
                    intent.putExtra("fullScreenInd", "");
                }else{
                    intent.putExtra("fullScreenInd", "y");
                }
                ((Activity)getContext()).startActivity(intent);*/
                //methods.globalMethods.loadFragments(R.id.home, new videoPlayer(), context);
            }
        });
    }

    private boolean isLandScape(){
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();

        if (rotation == Surface.ROTATION_90
                || rotation == Surface.ROTATION_270) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if(rotation != Surface.ROTATION_90
                || rotation != Surface.ROTATION_270) {
            roadComments.hideStatusBar(activity);
        }
        return false;
    }
}
