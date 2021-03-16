package com.example.housemate.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housemate.R;

public class customToast {
    public static void createToast(Context context, String message, boolean error){
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.customtoast, null);

        TextView toastText = view.findViewById(R.id.textViewToast);

        Spannable spanString = new SpannableString(message);
        spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);

        toastText.setText(spanString);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);

        if(error){
            toastText.setTextColor(Color.parseColor("F4F4F4"));
        }else{
            toastText.setTextColor(Color.parseColor("G5G5G5"));
        }

        toast.setGravity(Gravity.BOTTOM,32,32);
        toast.show();

    }
}
