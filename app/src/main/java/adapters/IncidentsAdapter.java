package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.eyec.bombo.bothopele.R;
import menuFragment.Anonymous.mainAnon;
import menuFragment.Road.road;

public class IncidentsAdapter extends RecyclerView.Adapter <incidentViewHolder> implements View.OnClickListener{
    Context context;
    Activity activity;
    private String[] Incident;

    public void setIncident(String[] incident) {
        Incident = incident;
    }

    public IncidentsAdapter(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
    }

    public IncidentsAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public incidentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.incidentlist, viewGroup, false);
        view.setOnClickListener(this);
        view.findViewById(R.id.incident).setOnClickListener(this);
        return new incidentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull incidentViewHolder subViewHolder, int i) {
        subViewHolder.incident.setText(Incident[i]);
    }

    @Override
    public int getItemCount() {
        return Incident.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return 1;
    }


    @Override
    public void onClick(View view) {
        TextView MyText = view.findViewById(R.id.incident);
        String value = MyText.getText().toString();
        if (road.onRoad) {
            road.setIncidentInfo(activity, value);
            road.incident.dismiss();
        }else{
            mainAnon.setIncidentInfo(activity, value);
            mainAnon.incident.dismiss();
        }
    }
}

class incidentViewHolder extends RecyclerView.ViewHolder {
    public TextView incident;
    public incidentViewHolder(View view) {
        super(view);
        incident = view.findViewById(R.id.incident);
    }
}
