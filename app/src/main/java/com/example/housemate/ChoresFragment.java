package com.example.housemate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChoresFragment extends Fragment {

    private Button myChoresButton;
    private Button houseChoresButton;
    private TextView textView;

    public ChoresFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_chores_2, container, false);


        myChoresButton = v.findViewById(R.id.choresMyChoresButton);
        houseChoresButton = v.findViewById(R.id.choresHouseChoresButton);
        textView = v.findViewById(R.id.choresTextView);

        myChoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("my chores");
            }
        });

        houseChoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("house chores");
            }
        });


        return v;
    }

}