package edu.team08.infinitegallery.trashbin;

import static androidx.core.app.ActivityCompat.startIntentSenderForResult;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TrashBinManager {
    int spanCount = 4;
    private Context context;
    private static String TRASH_BIN_PATH;
//    private static final String METADATA_FILE = "/path/to/your/metadata.txt";

    public TrashBinManager(Context context) {
        this.context = context;
        try {
            initTrashBin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initTrashBin() throws IOException {
        File internalStorage = context.getFilesDir();
        File trashBinDir = new File(internalStorage, "trash_bin");
        if (!trashBinDir.exists()) trashBinDir.mkdir();
        File nomediaFile = new File(trashBinDir, ".nomedia");
        if (!nomediaFile.exists()) nomediaFile.createNewFile();
        this.TRASH_BIN_PATH = trashBinDir.getAbsolutePath();
    }

    private void moveFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            deleteFile(src);
            if (src.exists()) {
                Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    private void deleteFile(@NonNull File file) {
        if (!file.exists()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {//SDK>=30
                int id;
                String[] projection = {MediaStore.Images.Media._ID};
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                }
                Cursor cursor=context.getContentResolver().query(uri, projection,"_data=?",new String[] {file.getAbsolutePath()},null);
                if (cursor.getCount()>0){
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    id = cursor.getInt(columnIndex);
                }else id = 0;
                Uri request = ContentUris.withAppendedId(uri, id);
                try {
                    final int REQUEST_PERM_DELETE = 8;
                    List<Uri> list = new ArrayList<>();
                    list.add(request);
                    PendingIntent editPendingIntent = MediaStore.createDeleteRequest(context.getContentResolver(), list);
                    startIntentSenderForResult((Activity) context, editPendingIntent.getIntentSender(), REQUEST_PERM_DELETE, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    Log.i("zakaria_mediaD","   requestDeletePermission error:\n"+e.getMessage());

                }
            } else {//SDK<=29
                if (!file.delete()) throw new Exception("Cannot delete, I don't know");
            }
        } catch (Exception e) {
            Log.i("DeleteError", e.getMessage());
        }

    }

    public String getTrashBinPath() {
        return TRASH_BIN_PATH;
    }

    public File[] getTrashFiles() {
        if (TRASH_BIN_PATH != null) {
            File trashBinDir = new File(TRASH_BIN_PATH);

            if (trashBinDir.exists() && trashBinDir.isDirectory()) {
                File[] allFiles = trashBinDir.listFiles();

                // Exclude the .nomedia file from the list
                List<File> filteredFiles = new ArrayList<>();
                for (File file : allFiles) {
                    if (!file.getName().equalsIgnoreCase(".nomedia")) {
                        filteredFiles.add(file);
                    }
                }

                return filteredFiles.toArray(new File[0]);
            }
        }

        return null;
    }

    public void moveToTrash(File photo) throws IOException {
        // TODO: implement the db
//        String randomFileName = UUID.randomUUID().toString();
        File trash = new File(TRASH_BIN_PATH, photo.getName());
        moveFile(photo, trash);
    }

    public void restorePhoto(File trash) throws IOException {
        // TODO: implement the db
        File destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photo = new File(destinationDirectory, trash.getName());
        moveFile(trash, photo);
    }

}
