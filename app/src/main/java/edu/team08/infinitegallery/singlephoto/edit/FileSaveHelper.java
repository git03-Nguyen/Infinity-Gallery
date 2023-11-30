package edu.team08.infinitegallery.singlephoto.edit;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSaveHelper implements LifecycleObserver {
    private ContentResolver contentResolver;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private MutableLiveData<FileMeta> fileCreatedResult = new MutableLiveData<>();
    private OnFileCreateResult resultListener;
    private Observer<FileMeta> observer = new Observer<FileMeta>() {
        @Override
        public void onChanged(FileMeta fileMeta) {
            if (resultListener != null) {
                resultListener.onFileCreateResult(
                        fileMeta.isCreated(),
                        fileMeta.getFilePath(),
                        fileMeta.getError(),
                        fileMeta.getUri()
                );
            }
        }
    };

    public FileSaveHelper(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public FileSaveHelper(AppCompatActivity activity) {
        this(activity.getContentResolver());
        addObserver(activity);
    }

    private void addObserver(LifecycleOwner lifecycleOwner){
        fileCreatedResult.observe(lifecycleOwner, observer);
        lifecycleOwner.getLifecycle().addObserver(this);
    }


    private Uri getEditedImageUri(String fileNameToSave, ContentValues newImageDetails, Uri imageCollection) throws IOException {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
        Uri editedImageUri = contentResolver.insert(imageCollection, newImageDetails);

        if (editedImageUri != null) {
            OutputStream outputStream = contentResolver.openOutputStream(editedImageUri);
            if (outputStream != null) {
                outputStream.close();
            }
        }

        return editedImageUri;
    }

    public void createFile(String fileNameToSave, OnFileCreateResult listener) {
        resultListener = listener;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = null;
                try {
                    // Build the edited image URI for the MediaStore
                    ContentValues newImageDetails = new ContentValues();
                    Uri imageCollection = buildUriCollection(newImageDetails);
                    Uri editedImageUri = getEditedImageUri(fileNameToSave, newImageDetails, imageCollection);

                    // Query the MediaStore for the image file path from the image Uri
                    cursor = contentResolver.query(
                            editedImageUri,
                            new String[]{MediaStore.Images.Media.DATA},
                            null,
                            null,
                            null
                    );
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String filePath = cursor.getString(columnIndex);

                    // Post the file created result with the resolved image file path
                    updateResult(true, filePath, null, editedImageUri, newImageDetails);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    updateResult(false, null, ex.getMessage(), null, null);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }

    @SuppressLint("InlinedApi")
    public void notifyThatFileIsNowPubliclyAvailable(ContentResolver contentResolver) {
        if (isSdkHigherThan28()) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    FileMeta value = fileCreatedResult.getValue();
                    if (value != null) {
                        value.getImageDetails().clear();
                        value.getImageDetails().put(MediaStore.Images.Media.IS_PENDING, 0);
                        contentResolver.update(value.getUri(), value.getImageDetails(), null, null);
                    }
                }
            });
        }
    }


    @SuppressLint("InlinedApi")
    private Uri buildUriCollection(ContentValues newImageDetails) {
        Uri imageCollection;
        if (isSdkHigherThan28()) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        return imageCollection;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void release(){
        if(executor != null){
            executor.shutdownNow();
        }
    }


    private class FileMeta{
        public boolean isCreated;
        public String filePath;
        public Uri uri;
        public String error;
        public ContentValues imageDetails;

        public FileMeta(boolean isCreated, String filePath, Uri uri, String error, ContentValues imageDetails) {
            this.isCreated = isCreated;
            this.filePath = filePath;
            this.uri = uri;
            this.error = error;
            this.imageDetails = imageDetails;
        }

        public boolean isCreated() {
            return isCreated;
        }

        public String getFilePath() {
            return filePath;
        }

        public Uri getUri() {
            return uri;
        }

        public String getError() {
            return error;
        }

        public ContentValues getImageDetails() {
            return imageDetails;
        }
    }

    public interface OnFileCreateResult{
        void onFileCreateResult(boolean created, String filePath, String error, Uri uri);
    }

    private void updateResult(boolean result, String filePath, String error, Uri uri, ContentValues newImageDetails) {
        fileCreatedResult.postValue(new FileMeta(result, filePath, uri, error, newImageDetails));
    }

    public static boolean isSdkHigherThan28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
