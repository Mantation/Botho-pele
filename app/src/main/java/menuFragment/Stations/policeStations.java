package menuFragment.Stations;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import properties.accessKeys;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class policeStations extends android.app.Fragment implements OnMapReadyCallback {
View myview;
    FrameLayout frameLayout;
    GoogleMap myMap;
    static FragmentManager fm;
    int PROXIMITY_RADIUS = 100000;
    double latitude,longitude;

    public policeStations() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_police_stations, container, false);
        frameLayout = myview.findViewById(R.id.map);
        fm = ((MainActivity) getActivity()).getSupportFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        Fragment oldFragment = fm.findFragmentById(R.id.map);
        if (oldFragment != null) {
            //getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
            try {
                //getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
            }catch (Exception e){
                e.printStackTrace();
                FragmentManager fragmentManager = ((MainActivity) getActivity()).getSupportFragmentManager();
                final Fragment current = fragmentManager.findFragmentById(R.id.map);
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(current).commitAllowingStateLoss();
                MainActivity.returnToRoadHome = false;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng Now = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
        googleMap.addMarker(new MarkerOptions().position(Now).title("You are here")).showInfoWindow();
        //Call Police Stations
        String stations = "police";
        String url = getURL(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()),stations);
        Object dataTransfer[] = new Object[3];
        dataTransfer[0] = myMap;
        dataTransfer[1] = url;
        dataTransfer[2] = getActivity();
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(dataTransfer);
        Toast.makeText(getActivity(),"Showing Nearby Police Stations", Toast.LENGTH_LONG).show();

    }

    private String getURL(double latitude, double longitude, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&keyword=police");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+getResources().getString(R.string.places_api_key));
        return googlePlaceUrl.toString();
    }


}
