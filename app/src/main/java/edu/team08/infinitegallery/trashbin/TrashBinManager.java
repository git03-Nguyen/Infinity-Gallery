package edu.team08.infinitegallery.trashbin;

import android.content.Context;
import android.os.Environment;

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

        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
        src.delete();
        // TODO: đại loại ở file đã làm đc rồi, tuy nhiên cần thêm db để check
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
