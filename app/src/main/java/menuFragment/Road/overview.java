package menuFragment.Road;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import adapters.ViewPagerAdapter;
import io.eyec.bombo.bothopele.MainActivity;
import io.eyec.bombo.bothopele.R;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class overview extends android.app.Fragment {
    View myview;
    ViewPager viewPager;
    private TabLayout tabLayout;
    androidx.fragment.app.Fragment fragment1;
    androidx.fragment.app.Fragment fragment2;
    ViewPagerAdapter adapter;
    FragmentManager fragmentManager;
    int savedImagePosition = 0;


    public overview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_overview, container, false);
        viewPager = myview.findViewById(R.id.pager);
        tabLayout = myview.findViewById(R.id.tab);
        fragmentManager = ((MainActivity) getActivity()).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        //adapter = new ViewPagerAdapter(fragmentManager);
        //adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new Locator(), "Locator");
        adapter.addFragment(new roadComments(), "Comments");
        fragment1 =  new Locator();
        fragment2 =  new roadComments();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(savedImagePosition,true);
        viewPager.setSaveFromParentEnabled(false);
        viewPager.setOffscreenPageLimit(2);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        FragmentManager fm = ((MainActivity) getActivity()).getSupportFragmentManager();
        Fragment oldFragment = fm.findFragmentById(R.id.map);
        if (oldFragment != null) {
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

}
