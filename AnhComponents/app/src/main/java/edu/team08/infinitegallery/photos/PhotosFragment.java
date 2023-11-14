package edu.team08.infinitegallery.photos;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
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
        
        readAllImages();

        return photosFragment;
    }

    private void readAllImages() {
        // TODO: maybe other thread, and only reload if have some changes in files
        photoFiles = new ArrayList<>();
        addImagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
        addImagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        addImagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)));
        // TODO: default sorting
        showAllPictures();
    }

    private void addImagesFrom(String dirPath){
        final File dir = new File(dirPath);
        final FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return !s.toLowerCase(Locale.ROOT).startsWith(".trashed") &&
                        !s.toLowerCase(Locale.ROOT).startsWith(".hide") &&
                        (s.toLowerCase().endsWith(".png") || s.toLowerCase(Locale.ROOT).endsWith(".jpg")
                        || s.toLowerCase().endsWith(".jpeg") || s.toLowerCase().endsWith(".gif"));
            }
        };

        File[] files = dir.listFiles(filter);
        for(File file : files){
            photoFiles.add(file);
        }
    }

    void showAllPictures() {
        // Send a string path to the adapter. The adapter will create everything from the provided path
        // This implementation is not permanent
        photosAdapter = new PhotosAdapter(context, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
    }


}