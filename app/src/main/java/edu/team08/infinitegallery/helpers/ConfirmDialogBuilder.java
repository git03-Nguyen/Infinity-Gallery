package edu.team08.infinitegallery.helpers;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class ConfirmDialogBuilder {
    public static void showConfirmDialog(Context context, String title, String message, Runnable onConfirm, Runnable onRefuse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, execute the onConfirm action
                if (onConfirm != null) {
                    onConfirm.run();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User refused, execute the onRefuse action
                if (onRefuse != null) {
                    onRefuse.run();
                }
            }
        });

        builder.show();
    }

}
