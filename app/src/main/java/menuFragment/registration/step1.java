package menuFragment.registration;


import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import authentication.auth;
import io.eyec.bombo.bothopele.R;
import methods.globalMethods;
import properties.accessKeys;

import static properties.accessKeys.setExitApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class step1 extends android.app.Fragment implements View.OnClickListener{
    View myview;
    CardView Next;
    EditText name;
    EditText surname;
    EditText email;
    EditText phone;
    EditText altphone;
    RadioButton Male;
    RadioButton Female;


    public step1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Initiate access variables
        accessKeys.setName(null);
        accessKeys.setSurname(null);
        accessKeys.setEmail(null);
        accessKeys.setPhone(null);
        accessKeys.setAltPhone(null);
        accessKeys.setGender(null);
        myview = inflater.inflate(R.layout.fragment_step1, container, false);
        name = myview.findViewById(R.id.name);
        surname = myview.findViewById(R.id.surname);
        email = myview.findViewById(R.id.email);
        phone = myview.findViewById(R.id.phone);
        altphone = myview.findViewById(R.id.altphone);
        Next = myview.findViewById(R.id.MyNext);
        Male = myview.findViewById(R.id.male);
        Female = myview.findViewById(R.id.female);
        name.requestFocus();
        Male.setChecked(true);
        Next.setOnClickListener(this);
        trackR_name();
        trackR_surname();
        trackR_phone();
        trackR_Altphone();
        trackR_email();
        setExitApplication(true);
        return myview;
    }

    private void trackR_name() {
        name.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.getText().toString().length() < 3) {
                    name.setTextColor(Color.RED);
                } else {
                    name.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (name.getText().toString().length() < 3) {
                    name.setTextColor(Color.RED);
                } else {
                    name.setTextColor(Color.BLACK);
                }

            }
        });

    }

    private void trackR_surname() {
        surname.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (surname.getText().toString().length() < 3) {
                    surname.setTextColor(Color.RED);
                } else {
                    surname.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (surname.getText().toString().length() < 3) {
                    surname.setTextColor(Color.RED);
                } else {
                    surname.setTextColor(Color.BLACK);
                }

            }
        });
    }

    private void trackR_phone() {
        phone.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone.getText().toString().length() < 10) {
                    phone.setTextColor(Color.RED);
                } else {
                    phone.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (phone.getText().toString().length() < 10) {
                    phone.setTextColor(Color.RED);
                } else {
                    phone.setTextColor(Color.BLACK);
                }

            }
        });
    }

    private void trackR_Altphone() {
        altphone.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (altphone.getText().toString().length() < 10) {
                    altphone.setTextColor(Color.RED);
                } else {
                    altphone.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (altphone.getText().toString().length() < 10) {
                    altphone.setTextColor(Color.RED);
                } else {
                    altphone.setTextColor(Color.BLACK);
                }

            }
        });
    }

    private  void trackR_email(){
        email.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!globalMethods.ValidateEmail(email.getText().toString()) ) {
                    email.setTextColor(Color.RED);
                }else{
                    email.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            public void afterTextChanged(Editable s) {
                if(!globalMethods.ValidateEmail(email.getText().toString()) ) {
                    email.setTextColor(Color.RED);
                }else{
                    email.setTextColor(Color.BLACK);
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        String Name = name.getText().toString();
        String Surname = surname.getText().toString();
        String Email = email.getText().toString();
        String Phone = phone.getText().toString();
        String AltPhone = altphone.getText().toString();
        String Gender = "";
        if (Name.length() < 3) {
            name.requestFocus();
        } else if (Surname.length() < 3) {
            surname.requestFocus();
        }else if (!globalMethods.ValidateEmail(Email)){
            email.requestFocus();
        } else if (Phone.length() < 10) {
            phone.requestFocus();
            Toast.makeText(getActivity(), "Incorrect phone number", Toast.LENGTH_LONG).show();
        } else if (AltPhone.length() < 10) {
            altphone.requestFocus();
            Toast.makeText(getActivity(), "Incorrect phone number", Toast.LENGTH_LONG).show();
        } else {
            if (globalMethods.isNetworkAvailable(getActivity())) {
                if (Male.isChecked()){
                    Gender = "Male";
                }else{
                    Gender = "Female";
                }
                accessKeys.setName(name.getText().toString());
                accessKeys.setSurname(surname.getText().toString());
                accessKeys.setEmail(email.getText().toString());
                accessKeys.setPhone(phone.getText().toString());
                accessKeys.setAltPhone(altphone.getText().toString());
                accessKeys.setGender(Gender);
                methods.globalMethods.loadFragmentWithTag(R.id.main, new step2(), getActivity(), "step2");
                //auth.InitiateAuth(getActivity(), accessKeys.getPhone());

            }else{
                globalMethods.networkerror(getActivity());
            }
        }
    }
}
