package edu.team08.infinitegallery.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.concurrent.atomic.AtomicReference;

import edu.team08.infinitegallery.R;

public class ConfirmDialogBuilder {
    public static void showConfirmDialog(Context context, String title, String message, Runnable onConfirm, Runnable onRefuse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        String optionYes=context.getString(edu.team08.infinitegallery.R.string.yes);
        String optionNo=context.getString(edu.team08.infinitegallery.R.string.no);
        builder.setPositiveButton(optionYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, execute the onConfirm action
                if (onConfirm != null) {
                    onConfirm.run();
                }
            }
        });

        builder.setNegativeButton(optionNo, new DialogInterface.OnClickListener() {
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

    public static void showAddPhotoDialog(Context context, int numberOfSelected, String albumName, Runnable movePhotos, Runnable copyPhotos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AtomicReference<String> title = new AtomicReference<>(context.getResources().getString(R.string.copy_photos));
        AtomicReference<String> message = new AtomicReference<>(context.getResources().getString(R.string.confirm_copy_photos, numberOfSelected, albumName));
        builder.setTitle(title.get());
        builder.setMessage(message.get());

        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(edu.team08.infinitegallery.R.string.move_selected_photos);
        checkBox.setPadding(10, 0, 0, 0);
        builder.setView(checkBox);

        String optionYes = context.getString(edu.team08.infinitegallery.R.string.yes);
        String optionNo = context.getString(edu.team08.infinitegallery.R.string.no);
        builder.setPositiveButton(optionYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, execute the onConfirm action
                if (checkBox.isChecked()) {
                    movePhotos.run();
                } else {
                    copyPhotos.run();
                }
            }
        });

        builder.setNegativeButton(optionNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User refused, execute the onRefuse action

            }
        });

        AlertDialog alertDialog = builder.show();
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                title.set(context.getResources().getString(R.string.move_photos));
                message.set(context.getResources().getString(R.string.confirm_move_photos, numberOfSelected, albumName));
            } else {
                title.set(context.getResources().getString(R.string.copy_photos));
                message.set(context.getResources().getString(R.string.confirm_copy_photos, numberOfSelected, albumName));
            }
            alertDialog.setTitle(title.get());
            alertDialog.setMessage(message.get());
        });
    }


}
