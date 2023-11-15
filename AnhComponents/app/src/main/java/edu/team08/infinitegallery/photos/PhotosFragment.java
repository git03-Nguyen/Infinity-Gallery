package edu.team08.infinitegallery.photos;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import edu.team08.infinitegallery.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment {
    int spanCount = 4;
    Context context;
    List<File> photoFiles;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;

    public PhotosFragment(Context context) {

        this.context = context;
    }

    public static PhotosFragment newInstance(Context context) {
        return new PhotosFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View photosFragment = inflater.inflate(R.layout.fragment_photos, container, false);
        photosRecView = photosFragment.findViewById(R.id.recViewPhotos);

        // TODO: update functionalities in toolbar
        Toolbar toolbar = photosFragment.findViewById(R.id.toolbarPhotos);
        toolbar.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        

        return photosFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        readAllImages();
        showAllPictures();
    }

    private void readAllImages() {
        // TODO: maybe other thread, and only reload if have some changes in files
        photoFiles = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            }

            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                String photoPath = cursor.getString(columnIndex);
                photoFiles.add(new File(photoPath));
            }
        } finally {
            // Close the cursor to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }

        }
        Toast.makeText(context, "Number of photos: " + photoFiles.size(), Toast.LENGTH_SHORT).show();

    }

    void showAllPictures() {
        photosAdapter = new PhotosAdapter(context, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
    }


}