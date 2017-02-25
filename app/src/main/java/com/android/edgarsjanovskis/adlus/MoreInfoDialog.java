package com.android.edgarsjanovskis.adlus;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Edgars on 24.02.17.
 */

public class MoreInfoDialog extends DialogFragment{


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.app_name)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })

                .setPositiveButton(R.string.show_map, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MoreInfoDialog.this.getActivity().finish();
                        startActivity(new Intent(getActivity(),MapActivity.class));
                    }
                });

        return builder.create();
    }


}
