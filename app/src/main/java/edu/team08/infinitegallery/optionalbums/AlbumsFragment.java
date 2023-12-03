package edu.team08.infinitegallery.optionalbums;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AlertDialog;
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
import edu.team08.infinitegallery.main.MainActivity;
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
            int itemId = item.getItemId();
            if (itemId == R.id.menuAlbumsSettings) {
                Intent myIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(myIntent, null);
            } else if (itemId == R.id.menuAlbumsAdd) {
                addNewAlbum();
            } else {
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        this.viewSwitcher = rootView.findViewById(R.id.viewSwitcher);
        this.gridView = rootView.findViewById(R.id.gridView);
        this.albumFolders = getAllAlbumFolders();
        displayFolderAlbums();

        return rootView;
    }

    private void addNewAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create New Album");

        // Set up the input field
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the positive and negative buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString().trim();

                // Perform actions with the entered albumName
                if (!TextUtils.isEmpty(albumName)) {
                    // Add your logic here, e.g., create a new album with the entered name
                    createNewAlbum(albumName);
                } else {
                    // Handle the case where the input is empty
                    Toast.makeText(context, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewAlbum(String albumName) {
        File albumsFolder = new File(Environment.getExternalStorageDirectory(), "Infinity-Albums");

        // Create the Infinity-Albums folder if it doesn't exist
        if (!albumsFolder.exists()) albumsFolder.mkdirs();

        // Check if the albumName already exists in Infinity-Albums
        File newAlbumFolder = new File(albumsFolder, albumName);
        if (newAlbumFolder.exists()) {
            Toast.makeText(context, "Album " + albumName + " already exists!", Toast.LENGTH_SHORT).show();
        } else {
            // Create the new album folder
            if (newAlbumFolder.mkdirs()) {
                Toast.makeText(context, "Album " + albumName + " created!", Toast.LENGTH_SHORT).show();
                onResume();
            } else {
                Toast.makeText(context, "Failed to create album " + albumName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.albumFolders = getAllAlbumFolders();
        displayFolderAlbums();
        ((MainActivity) context).changeStatusBar();
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

        // Read all user-album-folders and check if they exist in hashmap or not
        File userAlbumsFolder = new File(Environment.getExternalStorageDirectory(), "Infinity-Albums");

        if (userAlbumsFolder.exists() && userAlbumsFolder.isDirectory()) {
            File[] userAlbumFolders = userAlbumsFolder.listFiles();

            if (userAlbumFolders != null) {
                for (File userAlbumFolder : userAlbumFolders) {
                    // If the user album folder is not already in the map, add it with no photos
                    if (!folderMap.containsKey(userAlbumFolder)) {
                        folderMap.put(userAlbumFolder, new ArrayList<>());
                    }
                }
            }
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