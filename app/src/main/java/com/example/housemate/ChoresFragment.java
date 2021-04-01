package com.example.housemate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ChoresFragment extends Fragment {

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
        View v =  inflater.inflate(R.layout.fragment_chores, container, false);

        Button myChoresButton = (Button) v.findViewById(R.id.myChoresButton);
        myChoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = v.findViewById(R.id.textViewTest);
                txt.setText("My Chores");
            }
        });

        Button houseChoresButton = (Button) v.findViewById(R.id.houseChoresButton);
        houseChoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = v.findViewById(R.id.textViewTest);
                txt.setText("House Chores");
            }
        });

        return v;
    }

}