package com.android.edgarsjanovskis.adlus;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import static com.android.edgarsjanovskis.adlus.R.string;

public class MoreInfoDialog extends DialogFragment{

    private String msg = " LR ";

    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)

                .setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })

                .setPositiveButton(string.show_map, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MoreInfoDialog.this.getActivity().finish();
                        startActivity(new Intent(getActivity(),GeofencingActivity.class));
                    }
                });

        return builder.create();
    }
}
