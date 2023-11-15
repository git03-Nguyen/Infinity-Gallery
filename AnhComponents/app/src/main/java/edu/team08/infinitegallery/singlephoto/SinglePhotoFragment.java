package edu.team08.infinitegallery.singlephoto;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.controls.templates.ControlTemplate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.trashbin.SingleTrashActivity;

public class SinglePhotoFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private Context context;
    private File[] photoFiles;
    private int currentPosition;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public SinglePhotoFragment(Context context, String[] photoPaths, int currentPosition) {
        this.context = context;
        photoFiles = new File[photoPaths.length];
        for (int i = 0; i < photoPaths.length; i++) {
            this.photoFiles[i] = new File(photoPaths[i]);
        }
        this.currentPosition = currentPosition;
    }

    // TODO: Rename and change types and number of parameters
    public static SinglePhotoFragment newInstance(Context context, String[] photoPaths, int currentPosition) {
        return new SinglePhotoFragment(context, photoPaths, currentPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout fragmentView = (FrameLayout) inflater.inflate(R.layout.fragment_single_photo, null);
        viewPager = fragmentView.findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(context, photoFiles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // unnecessary
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // unnecessary
            }
        });

        return fragmentView;
    }

    public void moveToTrash() {
        Toast.makeText(context, "Del: " + photoFiles[currentPosition].getName(), Toast.LENGTH_SHORT).show();
        File currentPhoto = photoFiles[currentPosition];

        List<File> newPhotoFiles = new ArrayList<>(Arrays.asList(photoFiles));
        newPhotoFiles.remove(currentPhoto);
        photoFiles = newPhotoFiles.toArray(new File[0]);

        moveToTrashHelper(currentPhoto);

//        viewPager.removeAllViews();
        adapter.updateData(photoFiles);
//        int cur = currentPosition;
//        viewPager.setCurrentItem(0);
//
//        if (photoFiles.length == 0) {
//            // TODO: after deleting, it will return to MainActivity and Photos tab
//
//
//        } else if (photoFiles.length > 0) {
//            // TODO: after deleting, it will return to SinglePhotoActivity of previous photo
//            cur--;
//            viewPager.setCurrentItem(cur, true);
//        }

        Intent myIntent = new Intent(context, MainActivity.class);
        startActivity(myIntent, null);


    }

    private void moveToTrashHelper(File photo) {
        File internalStorage = context.getFilesDir();
        File trashBinDir = new File(internalStorage, "trash_bin");

        File destinationFile = new File(trashBinDir, photo.getName());
        ContentValues contentValues = new ContentValues();
        String extension = MimeTypeMap.getFileExtensionFromUrl(photo.getAbsolutePath());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        contentValues.put(MediaStore.Images.Media.DATA, destinationFile.getAbsolutePath());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        // Notify the media scanner that a new file has been added
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destinationFile)));

        if (photo.delete()) {
        }
        Toast.makeText(context, "Deleted: "+photo.getName(), Toast.LENGTH_SHORT).show();
    }
}