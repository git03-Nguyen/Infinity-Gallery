package com.example.singlephotooption;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DisplaySinglePhotoFragment extends Fragment {
    private Context context = null;
    private File[] photoFiles;
    public static DisplaySinglePhotoFragment newInstance(){
        DisplaySinglePhotoFragment fragment = new DisplaySinglePhotoFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            context = getActivity();
        }
        catch (IllegalStateException e){
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout displayLayout = (FrameLayout) inflater.inflate(R.layout.fragment_display_single_layout, null);
        ViewPager viewPager = displayLayout.findViewById(R.id.viewPager);

        photoFiles = convertPathsToFiles(getAllImagesPaths(context)).toArray(new File[0]);

        Log.e("Bug", "bug in onCreateView");

        ViewPagerAdapter adapter = new ViewPagerAdapter(context, photoFiles);
        viewPager.setAdapter(adapter);
        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();


        return displayLayout;
    }

    private List<String> getAllImagesPaths(Context context) {
        List<String> paths = new ArrayList<String>();

        // The data you want to retrieve
        String[] projection = {MediaStore.Images.Media.DATA};

        // Get the cursor
        Cursor cursor = null;
        try {
            // For Android Q and above, use MediaStore.Images.Media.getContentUri
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            }

            // Query the content resolver
            cursor = context.getContentResolver().query(uri, projection, null, null, null);

            // Get the column index of the data in the cursor
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            // Iterate through the cursor and print the paths
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(columnIndex);
                paths.add(imagePath);
            }
        } finally {
            // Close the cursor to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }

            return paths;
        }
    }

    private List<File> convertPathsToFiles(List<String> paths){
        List<File> files = new ArrayList<File>();
        for(int i = 0; i < paths.size(); i++){
            files.add(new File(paths.get(i)));
        }

        return files;
    }
}
