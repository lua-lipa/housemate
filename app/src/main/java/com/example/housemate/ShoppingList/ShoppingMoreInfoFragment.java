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

    public ShoppingMoreInfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_more_info, container, false);
        gotItButton = view.findViewById(R.id.shopping_more_info_got_it_button);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle);
    }

}