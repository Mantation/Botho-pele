package menuFragment.Missing;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.eyec.bombo.bothopele.R;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class mainMissing extends android.app.Fragment implements View.OnClickListener{
    View myview;
    FloatingActionButton Initiate;
    RecyclerView recyclerView;
    static ProgressBar progressBar;


    public mainMissing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_main_missing, container, false);
        recyclerView = myview.findViewById(R.id.recycler_missing);
        progressBar = myview.findViewById(R.id.progress);
        Initiate = myview.findViewById(R.id.myInitiate);
        Initiate.setVisibility(View.GONE);
        connectionHandler.external.missing_.getAllDocuments(getActivity(), recyclerView, progressBar,Initiate);
        Initiate.setOnClickListener(this);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.myInitiate){
            methods.globalMethods.loadFragmentWithTag(R.id.main, new initiate(), getActivity(),"initiate");
        }
    }
}
