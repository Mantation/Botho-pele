package menuFragment.Departments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.eyec.bombo.bothopele.R;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class Departments extends android.app.Fragment implements View.OnClickListener{
    View myview;
    ImageView Corona;
    ImageView Saps;
    ImageView Trauma;
    ImageView Adhd;
    ImageView Depression;
    ImageView SocialDev;
    ImageView Suicide;
    ImageView Aids;
    ImageView Mental;
    ImageView Kids;
    ImageView Health;
    ImageView Driving;
    ImageView Er24;
    ImageView Netcare;
    ImageView Fire;
    ImageView Consumer;


    public Departments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_departments, container, false);
        Corona = myview.findViewById(R.id.phoneCOCO);
        Saps = myview.findViewById(R.id.phoneSAPS);
        Trauma = myview.findViewById(R.id.phoneTRAUMA);
        Adhd = myview.findViewById(R.id.phoneADHD);
        Depression = myview.findViewById(R.id.phoneDEPP);
        SocialDev = myview.findViewById(R.id.phoneSOCIAL);
        Suicide = myview.findViewById(R.id.phoneSUICIDE);
        Aids = myview.findViewById(R.id.phoneAIDS);
        Mental = myview.findViewById(R.id.phoneMENTAL);
        Kids = myview.findViewById(R.id.phoneKIDS);
        Health = myview.findViewById(R.id.phoneHEALTH);
        Driving = myview.findViewById(R.id.phoneDRIVING);
        Er24 = myview.findViewById(R.id.phoneER24);
        Netcare = myview.findViewById(R.id.phoneNETCARE);
        Fire = myview.findViewById(R.id.phoneFIRE);
        Consumer = myview.findViewById(R.id.phoneCONSUMER);
        Corona.setOnClickListener(this);
        Saps.setOnClickListener(this);
        Trauma.setOnClickListener(this);
        Adhd.setOnClickListener(this);
        Depression.setOnClickListener(this);
        SocialDev.setOnClickListener(this);
        Suicide.setOnClickListener(this);
        Aids.setOnClickListener(this);
        Mental.setOnClickListener(this);
        Kids.setOnClickListener(this);
        Health.setOnClickListener(this);
        Driving.setOnClickListener(this);
        Er24.setOnClickListener(this);
        Netcare.setOnClickListener(this);
        Fire.setOnClickListener(this);
        Consumer.setOnClickListener(this);
        setExitApplication(false);
        return myview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        /* Create the Intent */
        Intent intent = new Intent(Intent.ACTION_DIAL);
        switch (id){
            case R.id.phoneCOCO:intent.setData(Uri.parse("tel:0800 029 999".trim()));
            break;
            case R.id.phoneSAPS:intent.setData(Uri.parse("tel:10111".trim()));
            break;
            case R.id.phoneTRAUMA:intent.setData(Uri.parse("tel:0800 20 50 26".trim()));
            break;
            case R.id.phoneADHD:intent.setData(Uri.parse("tel:0800 55 44 33".trim()));
            break;
            case R.id.phoneDEPP:intent.setData(Uri.parse("tel:0800 70 80 90".trim()));
            break;
            case R.id.phoneSOCIAL:intent.setData(Uri.parse("tel:0800 12 13 14".trim()));
            break;
            case R.id.phoneSUICIDE:intent.setData(Uri.parse("tel:0800 567 567".trim()));
            break;
            case R.id.phoneAIDS:intent.setData(Uri.parse("tel:0800 012 322".trim()));
            break;
            case R.id.phoneMENTAL:intent.setData(Uri.parse("tel:0800 567 567".trim()));
            break;
            case R.id.phoneKIDS:intent.setData(Uri.parse("tel:0800 333 0555".trim()));
            break;
            case R.id.phoneHEALTH:intent.setData(Uri.parse("tel:0800 20 14 14".trim()));
            break;
            case R.id.phoneDRIVING:intent.setData(Uri.parse("tel:081 410 6338".trim()));
            break;
            case R.id.phoneER24:intent.setData(Uri.parse("tel:086 108 4124".trim()));
            break;
            case R.id.phoneNETCARE:intent.setData(Uri.parse("tel:0860 638 227".trim()));
            break;
            case R.id.phoneFIRE:intent.setData(Uri.parse("tel:012 848 4602".trim()));
            break;
            case R.id.phoneCONSUMER:intent.setData(Uri.parse("tel:0800 014 880".trim()));
            break;
            default:intent.setData(Uri.parse("tel:0800 029 999".trim()));
                break;
        }
        getActivity().startActivity(intent);
    }
}
