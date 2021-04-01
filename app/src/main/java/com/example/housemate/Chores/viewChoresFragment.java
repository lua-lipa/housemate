package com.example.housemate.Chores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.housemate.R;

public class viewChoresFragment extends Fragment {

    private Button myChoresButton;
    private Button houseChoresButton;
    private TextView textView;

    public viewChoresFragment() {
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
