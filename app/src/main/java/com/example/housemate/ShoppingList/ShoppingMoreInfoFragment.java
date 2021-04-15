package com.example.housemate.ShoppingList;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.housemate.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ShoppingMoreInfoFragment extends BottomSheetDialogFragment {
    private Button gotItButton;

    public ShoppingMoreInfoFragment() {
        //required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_more_info, container, false);

        //got it button set here
        gotItButton = view.findViewById(R.id.shopping_more_info_got_it_button);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //close the fragment once we click it
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    //this is just a design aspect, allows us to override the style from the theme.xml of the Bottom Sheet Dialog 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle);
    }

}