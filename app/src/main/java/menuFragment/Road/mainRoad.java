package menuFragment.Road;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.eyec.bombo.bothopele.R;
import menuFragment.Missing.initiate;
import methods.globalMethods;
import properties.accessKeys;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class mainRoad extends android.app.Fragment implements View.OnClickListener{
    View myview;
    FloatingActionButton Initiate;
    RecyclerView recyclerView;
    static ProgressBar progressBar;
    public static LocationManager locationManager;
    public static LocationListener locationListener;

    public mainRoad() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_main_road, container, false);
        recyclerView = myview.findViewById(R.id.recycler_road);
        progressBar = myview.findViewById(R.id.progress);
        Initiate = myview.findViewById(R.id.myInitiate);
        Initiate.setVisibility(View.GONE);
        connectionHandler.external.road_.getAllDocuments(getActivity(), recyclerView, progressBar,Initiate);
        Initiate.setOnClickListener(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //if(!isLocationGrabbed) {
                boolean LocationEmpty = false;
                if (accessKeys.getLongitude() == null && accessKeys.getLatitude() == null) {
                    LocationEmpty = true;
                }
                System.out.println(location.getLatitude() + ": " + location.getLongitude());
                accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                if (LocationEmpty) {
                    accessKeys.setLongitude(String.valueOf(location.getLongitude()));
                    accessKeys.setLatitude(String.valueOf(location.getLatitude()));
                }
                //connectionHandler.external.spas_.getAllDocuments(getActivity(), recyclerView, progressBar,imageView);
                //    isLocationGrabbed = true;
                //}
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        globalMethods.geRoadLocationPermission(getActivity(), locationManager, locationListener);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.myInitiate){
            methods.globalMethods.loadFragmentWithTag(R.id.main, new road(), getActivity(),"road");
        }
    }
}
