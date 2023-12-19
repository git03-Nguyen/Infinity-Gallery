package edu.team08.infinitegallery.trashbin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TrashBinManager {
    int spanCount = 4;
    private Context context;
    private static String TRASH_BIN_PATH;
    private static final String TRASH_BIN_DB_NAME = "trash_bin.db";
    private static final String TRASH_BIN_TABLE_NAME = "TRASH_BIN";

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

        testDatabaseOperations();
    }

    public void moveFile(File src, File dst) throws IOException {
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }

        if (dst.exists()) {
            // If the destination file already exists, rename it with a postfix
            dst = getUniqueDestination(dst);
        }

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
        src.delete();
    }

    private void encrypt(File src, File dst) throws GeneralSecurityException, IOException {
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }

        if (dst.exists()) {
            // If the destination file already exists, rename it with a postfix
            dst = getUniqueDestination(dst);
        }

        MasterKey mainKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        EncryptedFile encryptedFile = new EncryptedFile.Builder(context,
                dst,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

        InputStream inputStream = new FileInputStream(src);
        OutputStream outputStream = encryptedFile.openFileOutput();
        // Write data from source file to encrypted output stream
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();

    }

    private void decrypt(final File src, File dst) throws GeneralSecurityException, IOException {
        dst = getUniqueDestination(dst);

        MasterKey mainKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        EncryptedFile encryptedFile = new EncryptedFile.Builder(context,
                src,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

        InputStream inputStream = encryptedFile.openFileInput();
        OutputStream outputStream = new FileOutputStream(dst);

        // Write data from encrypted source file to output stream
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();

    }

    public void copyFile(File src, File dst) throws IOException {
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }

        if (dst.exists()) {
            // If the destination file already exists, rename it with a postfix
            dst = getUniqueDestination(dst);
        }

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    private File getUniqueDestination(File originalDst) {
        File dst = originalDst;
        int postfix = 1;

        // Keep incrementing the postfix until a unique destination is found
        while (dst.exists()) {
            String originalName = originalDst.getName();
            String nameWithoutExtension = originalName.substring(0, originalName.lastIndexOf('.'));
            String extension = originalName.substring(originalName.lastIndexOf('.') + 1);

            String newName = nameWithoutExtension + " (" + postfix + ")." + extension;
            dst = new File(originalDst.getParent(), newName);
            postfix++;
        }

        return dst;
    }

    public File[] getTrashFiles() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TRASH_BIN_DB_NAME), null);
        String[] projection = {"TRASH_NAME"};
        String orderBy = "DELETE_DATE DESC"; // Sort by DELETE_DATE in descending order
        // Query the database to get all trash file names
        Cursor cursor = db.query(TRASH_BIN_TABLE_NAME, projection, null, null, null, null, orderBy);

        List<String> trashFileNames = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String trashFileName = cursor.getString(cursor.getColumnIndex("TRASH_NAME"));
                trashFileNames.add(trashFileName);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        // Convert file names to File objects using the trash bin directory path
        File trashBinDir = new File(TRASH_BIN_PATH);
        File[] trashFiles = new File[trashFileNames.size()];
        for (int i = 0; i < trashFileNames.size(); i++) {
            trashFiles[i] = new File(trashBinDir, trashFileNames.get(i));
        }

        return trashFiles;
    }

    public void moveToTrash(File photo) throws IOException {
        File trash = null;
        do {
            String trashName = UUID.randomUUID().toString();
            trash = new File(TRASH_BIN_PATH, trashName);
        } while (trash.exists());

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TRASH_BIN_DB_NAME), null);
        ContentValues values = new ContentValues();
        values.put("ORIGINAL_PATH", photo.getAbsolutePath());
        values.put("TRASH_NAME", trash.getName());
        values.put("DELETE_DATE", System.currentTimeMillis());
        db.insert(TRASH_BIN_TABLE_NAME, null, values);

        //moveFile(photo, trash);
        try {
            encrypt(photo, trash);
            photo.delete();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

    }

    public void permanentDelete(File trash) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TRASH_BIN_DB_NAME), null);
        db.delete(TRASH_BIN_TABLE_NAME, "TRASH_NAME = ?", new String[]{trash.getName()});
        db.close();

        if (trash == null || !trash.exists()) { return; }
        trash.delete();
    }

    @SuppressLint("Range")
    public String restorePhoto(File trash) throws IOException {
        if (trash == null || !trash.exists()) { return ""; }

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TRASH_BIN_DB_NAME), null);

        Cursor cursor = db.query(
                TRASH_BIN_TABLE_NAME,
                new String[]{"ORIGINAL_PATH"},
                "TRASH_NAME = ?",
                new String[]{trash.getName()},
                null,
                null,
                null
        );

        String originalPath = null;
        if (cursor != null && cursor.moveToFirst()) {
            originalPath = cursor.getString(cursor.getColumnIndex("ORIGINAL_PATH"));
            cursor.close();
        }

        db.delete(TRASH_BIN_TABLE_NAME, "TRASH_NAME = ?", new String[]{trash.getName()});
        db.close();

        if (originalPath != null) {
            File original = new File(originalPath);
            try {
                decrypt(trash, original);
                trash.delete();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        } else {}

        return originalPath;
    }

    private boolean databaseExists(String dbName) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(
                    context.getDatabasePath(dbName).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (Exception e) {
            // Database does not exist
        }
        return checkDB != null;
    }

    private void testDatabaseOperations() {

        if (databaseExists(TRASH_BIN_DB_NAME)) { return; }

        // Create the database
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath(TRASH_BIN_DB_NAME), null);

        // Create the "TRASH" table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TRASH_BIN_TABLE_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ORIGINAL_PATH TEXT, TRASH_NAME TEXT, DELETE_DATE INTEGER)");

        // Close the database
        db.close();
    }

    public int[] getDaysRemain(File[] trashFiles) {
        int[] daysRemain = new int[trashFiles.length];
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(TRASH_BIN_DB_NAME), null);
        for (int i = 0; i < trashFiles.length; i++) {
            Cursor cursor = db.query(TRASH_BIN_TABLE_NAME, new String[]{"DELETE_DATE"}, "TRASH_NAME = ?", new String[]{trashFiles[i].getName()}, null, null, null);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") long deleteDate = cursor.getLong(cursor.getColumnIndex("DELETE_DATE"));
                long currentDate = System.currentTimeMillis();
                long difference = currentDate - deleteDate;
                long daysPassed = TimeUnit.MILLISECONDS.toDays(difference);
                daysRemain[i] = 30 - (int)daysPassed;
            }
            cursor.close();
        }
        db.close();
        return daysRemain;
    }


    public void checkAndCleanTrashBin() {
        File[] trashFiles = getTrashFiles();

        int[] daysRemain = getDaysRemain(trashFiles);

        for (int i = 0; i < daysRemain.length; i++) {
            if (daysRemain[i] < 0) {
                permanentDelete(trashFiles[i]);
                trashFiles[i] = null;
            }
        }

    }
}
