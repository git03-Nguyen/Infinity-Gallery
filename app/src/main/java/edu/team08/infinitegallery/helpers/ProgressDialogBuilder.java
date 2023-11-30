package edu.team08.infinitegallery.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.team08.infinitegallery.R;

public class ProgressDialogBuilder {

    public static Dialog buildProgressDialog(Context context, String title, Runnable backgroundWork, Runnable onDismiss) {
        Dialog progressDialog = new Dialog(context);
        progressDialog.setContentView(R.layout.dialog_progress_bar);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);

        ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);
        TextView textViewMessage = progressDialog.findViewById(R.id.textViewMessage);

        // Set the work to be done when the progress dialog is shown
        progressDialog.setOnShowListener(dialog -> {
            if (backgroundWork != null) {
                // Execute the background work in a separate thread
                try {
                    new Thread(() -> {
                        backgroundWork.run();
                        // Dismiss the dialog on the UI thread after the work is done
                        new Handler(Looper.getMainLooper()).post(() -> {
                            progressBar.setProgress(100);
                            progressDialog.dismiss();
                        });
                    }).start();
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                    // Dismiss the dialog in case of an exception
                    progressDialog.dismiss();
                }
            }
        });

        // Set the OnDismissListener for handling actions after the dialog is dismissed
        progressDialog.setOnDismissListener(dialog -> {
            if (onDismiss != null) {
                // Execute the onDismiss action on the UI thread
                new Handler(Looper.getMainLooper()).post(onDismiss);
            }
        });

        progressDialog.show();

        return progressDialog;
    }

    public static void updateProgressMessage(Dialog progressDialog, String message) {
        if (progressDialog != null) {
            TextView textViewMessage = progressDialog.findViewById(R.id.textViewMessage);
            if (textViewMessage != null) {
                textViewMessage.setText(message);
            }
        }
    }
}
