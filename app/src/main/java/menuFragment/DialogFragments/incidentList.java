package menuFragment.DialogFragments;


import android.app.DialogFragment;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.eyec.bombo.bothopele.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class incidentList extends DialogFragment {
    View myview;
    RecyclerView recyclerView;


    public incidentList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview =  inflater.inflate(R.layout.fragment_incident_list, container, false);
        recyclerView = (RecyclerView) myview.findViewById(R.id.recycler_incidents);
        connectionHandler.external.categories_.incidentsCategory_.getAllDocuments(getActivity(),recyclerView);
        this.getDialog().setTitle("incidents");
        return myview;
    }

}
