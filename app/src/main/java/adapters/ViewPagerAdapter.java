package adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import menuFragment.Road.Locator;
import menuFragment.Road.roadComments;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "tag";
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        //return fragmentList.get(position);
        switch (position) {
            case 0:
                return new Locator();
            case 1:
                return new roadComments();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragmentTitle.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public  void addFragment(Fragment fragment,String title){
        fragmentList.add(fragment);
        fragmentTitle.add(title);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(TAG, "destroyItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
    }
    /*@Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }*/
}
