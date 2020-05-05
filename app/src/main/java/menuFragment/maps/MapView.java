package menuFragment.maps;


import android.app.Activity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;
import properties.accessKeys;

import static methods.globalMethods.getStringAddress;
import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapView extends android.app.Fragment implements OnMapReadyCallback,View.OnClickListener, FragmentManager.OnBackStackChangedListener {
    View myview;
    TextView Loc;
    FrameLayout frameLayout;
    GoogleMap myMap;
    CardView MySet;
    static FragmentManager fm;
    public static boolean onAnon;
    public static boolean onRoad;
    public static boolean onMissing;

    public static boolean isOnAnon() {
        return onAnon;
    }

    public static void setOnAnon(boolean onAnon) {
        MapView.onAnon = onAnon;
        MapView.onRoad = false;
        MapView.onMissing = false;
    }

    public static boolean isOnRoad() {
        return onRoad;
    }

    public static void setOnRoad(boolean onRoad) {
        MapView.onAnon = false;
        MapView.onRoad = onRoad;
        MapView.onMissing = false;
    }

    public static boolean isOnMissing() {
        return onMissing;
    }

    public static void setOnMissing(boolean onMissing) {
        MapView.onAnon = false;
        MapView.onRoad = false;
        MapView.onMissing = onMissing;
    }

    public static void initiateMapContent(){
        MapView.onAnon = false;
        MapView.onRoad = false;
        MapView.onMissing = false;
    }

    public MapView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_map, container, false);
        MySet = myview.findViewById(R.id.MySet);
        frameLayout = myview.findViewById(R.id.map);
        Loc = myview.findViewById(R.id.location);
        MySet.setVisibility(View.GONE);
        fm = ((MainActivity) getActivity()).getSupportFragmentManager();/// getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        MySet.setOnClickListener(this);
        ((MainActivity)getActivity()).getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
            }
        });
        MainActivity.onMap = true;
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    boolean locationLabel;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng Now = new LatLng(Double.parseDouble(accessKeys.getLatitude()), Double.parseDouble(accessKeys.getLongitude()));
        googleMap.addMarker(new MarkerOptions().position(Now)
                .title("Your Location")).showInfoWindow();
        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        Now,11f));
        myMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                double CameraLat = myMap.getCameraPosition().target.latitude;
                double CameraLong = myMap.getCameraPosition().target.longitude;
                if (locationLabel){
                    Loc.setText(getStringAddress(getActivity(),CameraLat,CameraLong));
                }else{
                    locationLabel = true;
                }

                MySet.setVisibility(View.VISIBLE);
            }
        });
        myMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                MySet.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        LatLng center = myMap.getCameraPosition().target;
        accessKeys.setTargetLat(String.valueOf(center.latitude));
        accessKeys.setTargetLong(String.valueOf(center.longitude));
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
        MainActivity.onMap = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.onMap = false;
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        Fragment oldFragment = fm.findFragmentById(R.id.map);
        if (oldFragment != null) {
            //getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
            ((MainActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        getFragmentManager().popBackStack();
        getFragmentManager().popBackStack();
        MainActivity.onMap = true;
    }

}
