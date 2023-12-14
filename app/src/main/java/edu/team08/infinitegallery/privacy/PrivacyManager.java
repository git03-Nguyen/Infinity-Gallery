package edu.team08.infinitegallery.privacy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PrivacyManager {

    //Properties and attributes
    private Context context;

    private static final String PRIVACY_TABLENAME = "PRIVACY";
    private final String PRIVACY_DBNAME = "privacy.db";
    private String PRIVACY_FOLDER_PATH;

    //constructor
    public PrivacyManager(Context context) {
        this.context = context;
        try {
            initPrivacyOption();
        } catch (IOException e) {
            //make a runtimeException instead in order to ignoring it if happen
            throw new RuntimeException(e);
        }
    }

    //functional methods
    private void initPrivacyOption() throws IOException { //IOException thrown by createNewFile()
        File internalStorage = context.getFilesDir();
        File privacyFolder = new File(internalStorage, "privacy_folder");

        //make new folder for privacy storage if not exist
        if (!privacyFolder.exists()){
            privacyFolder.mkdir();
        }

        //preventing the items inside the privacy folder to be scanned by other media applications
        File nomediaFile = new File(privacyFolder, ".nomedia");
        if (!nomediaFile.exists()){
            nomediaFile.createNewFile();
        }
        this.PRIVACY_FOLDER_PATH = privacyFolder.getAbsolutePath();

        //initialize the database for privacy
        this.initializeDatabase();
    }

    private void initializeDatabase() {
        if (this.checkIfExistingDB(PRIVACY_DBNAME)) {
            return;
        }

        // Create the database
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath(PRIVACY_DBNAME),
                null
        );

        // Create the "TRASH" table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PRIVACY_TABLENAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ORIGINAL_PATH TEXT, PHOTO_NAME TEXT, HIDE_TIME INTEGER)"
        );

        // Close the database
        db.close();
    }

    public File[] getAllPrivacyFiles() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(PRIVACY_DBNAME), null);
        //set projection - the columns of data wanted to retrieve
        String[] projection = {"PHOTO_NAME"};

        // Query the database to get all privacy file names
        Cursor cursor = db.query(PRIVACY_TABLENAME, projection, null, null, null, null, null);

        List<String> privacy_filename_list = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String privacyFileName = cursor.getString(cursor.getColumnIndex("PHOTO_NAME"));
                privacy_filename_list.add(privacyFileName);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        // Convert file names to File objects using the privacy bin directory path
        File privacyDirectory = new File(PRIVACY_FOLDER_PATH);
        File[] privacyList = new File[privacy_filename_list.size()];

        for (int i = 0; i < privacy_filename_list.size(); i++) {
            privacyList[i] = new File(privacyDirectory, privacy_filename_list.get(i));
        }

        Arrays.sort(privacyList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                // Compare based on the last modified date in descending order (newest first)
                return Long.compare(file2.lastModified(), file1.lastModified());
            }
        });

        return privacyList;
    }

    private void moveFile(File src, File dest) throws IOException { //IOException thrown by FileInputStream / FileOutputStream constructors, transferTo()
        if (dest.exists()) {
            // If the destination file already exists, rename it with a postfix
            dest = makeNewDestination(dest);
        }

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dest).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
        src.delete();
    }

    private File makeNewDestination(File origin_dest) {
        File dest = origin_dest;
        int clone_num = 1;

        while (dest.exists()) {
            String origin_name = origin_dest.getName();
            String fileName = origin_name.substring(0, origin_name.lastIndexOf('.'));
            String fileExtension = origin_name.substring(origin_name.lastIndexOf('.') + 1);

            String newName = fileName + " (" + clone_num + ")." + fileExtension;
            dest = new File(origin_dest.getParent(), newName);
            clone_num++;
        }

        return dest;
    }

    public void hideToPrivacy(File photo) throws IOException { //IOException thrown by this.moveFile()
        File privateFile = null;
        do {
            String fileName = UUID.randomUUID().toString();
            privateFile = new File(PRIVACY_FOLDER_PATH, fileName);
        } while (privateFile.exists());

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(PRIVACY_DBNAME), null);

        ContentValues values = new ContentValues();
        values.put("ORIGINAL_PATH", photo.getAbsolutePath());
        values.put("PHOTO_NAME", privateFile.getName());
        values.put("HIDE_TIME", System.currentTimeMillis());

        db.insert(PRIVACY_TABLENAME, null, values);

        this.moveFile(photo, privateFile);
    }

    @SuppressLint("Range") //handle cursor.getColumnIndex() returning -1
    public String revealPhoto(File photo) throws IOException { //IOException thrown by this.moveFile()
        if (photo == null || !photo.exists()) {
            return "";
        }

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(PRIVACY_DBNAME), null);

        Cursor cursor = db.query(
                PRIVACY_TABLENAME,
                new String[]{"ORIGINAL_PATH"},
                "PHOTO_NAME = ?",
                new String[]{photo.getName()},
                null,
                null,
                null
        );

        String originalPath = null;
        if (cursor != null && cursor.moveToFirst()) {
            originalPath = cursor.getString(cursor.getColumnIndex("ORIGINAL_PATH"));
            cursor.close();
        }

        db.delete(PRIVACY_TABLENAME, "PHOTO_NAME = ?", new String[]{photo.getName()});
        db.close();

        if (originalPath != null) {
            File original = new File(originalPath);
            this.moveFile(photo, original);
        }

        return originalPath;
    }

    public void deletePermanent(File privateFile) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(PRIVACY_DBNAME), null);
        db.delete(PRIVACY_TABLENAME,
                "PHOTO_NAME = ?", new String[]{privateFile.getName()}
        );
        db.close();

        if (privateFile == null || !privateFile.exists()) {
            return;
        }

        privateFile.delete();
    }


    //getters - setters

    private boolean checkIfExistingDB(String dbName) {
        SQLiteDatabase db_connection = null;
        try {
            db_connection = SQLiteDatabase.openDatabase(
                    context.getDatabasePath(dbName).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY
            );
            db_connection.close();
        } catch (Exception e) {
            // Database does not exist
        }
        return db_connection != null;
    }

}
