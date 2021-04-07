package com.example.housemate.family;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import androidx.fragment.app.DialogFragment;

import com.example.housemate.R;
import com.example.housemate.util.HousemateAPI;

public class FamilyAdminConfirmationDialog extends DialogFragment {
    private HousemateAPI housemateAPI = HousemateAPI.getInstance();
    private FamilyMember familyMember;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        familyMember = housemateAPI.getSelectedMember();
        String familyMemberName = familyMember.getName();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml("Make <b>" + familyMemberName + "</b> an admin?"))
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
