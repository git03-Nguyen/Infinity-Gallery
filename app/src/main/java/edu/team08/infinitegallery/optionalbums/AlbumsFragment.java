package edu.team08.infinitegallery.optionalbums;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;

public class AlbumsFragment extends Fragment {
    private Context context;
    private GridView gridView;
    private AlbumFolder[] albumFolders;
    private AlbumsAdapter albumsAdapter;
    private ViewSwitcher viewSwitcher;

    public AlbumsFragment(Context context) {
        this.context = context;
    }

    public static AlbumsFragment newInstance(Context context) {
        return new AlbumsFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbarAlbums);

        toolbar.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            int itemId = item.getItemId();
            if (itemId == R.id.menuAlbumsSettings) {
                Intent myIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(myIntent, null);
            }
            return true;
        });

        this.viewSwitcher = rootView.findViewById(R.id.viewSwitcher);
        this.gridView = rootView.findViewById(R.id.gridView);
        this.albumFolders = getAllAlbumFolders();
        displayFolderAlbums();

        return rootView;
    }

    public AlbumFolder[] getAllAlbumFolders() {
        File[] allPhotos = readAllImages();

        // Organize photos into folders
        Map<File, List<File>> folderMap = new HashMap<>();

        for (File photo : allPhotos) {
            File folder = photo.getParentFile();

            // If the folder is not already in the map, add it
            if (!folderMap.containsKey(folder)) {
                folderMap.put(folder, new ArrayList<>());
            }

            // Add the photo to the corresponding folder
            folderMap.get(folder).add(photo);
        }

        // Create AlbumFolder objects from the map entries
        List<AlbumFolder> albumFolders = new ArrayList<>();
        for (Map.Entry<File, List<File>> entry : folderMap.entrySet()) {
            File folder = entry.getKey();
            List<File> photos = Arrays.asList(entry.getValue().toArray(new File[0]));
            albumFolders.add(new AlbumFolder(photos.toArray(new File[0]), folder));
        }

        // Convert the list to an array and return
        return albumFolders.toArray(new AlbumFolder[0]);
    }

    private void displayFolderAlbums() {
       if (this.albumFolders.length > 0) {
            if (gridView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
           albumsAdapter = new AlbumsAdapter(context, this.albumFolders);
           gridView.setAdapter(albumsAdapter);
        } else {
            if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        }
    }

    private File[] readAllImages() {
        ArrayList<File> photoFiles = new ArrayList<>();
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
            if (cursor != null) {
                cursor.close();
            }
        }

        return photoFiles.toArray(new File[0]);
    }


}