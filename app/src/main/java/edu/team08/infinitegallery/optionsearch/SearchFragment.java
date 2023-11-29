package edu.team08.infinitegallery.optionsearch;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.ViewSwitcher;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;

public class SearchFragment extends Fragment {
    private Context context;
    private SearchView searchView;

    int spanCount = 4;
    ViewSwitcher viewSwitcher;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;

    List<File> photoFiles;
    List<File> resultOfSearching;

    public SearchFragment(Context context) {
        this.context = context;
    }

    public static SearchFragment newInstance(Context context) {
        return new SearchFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = rootView.findViewById(R.id.searchView);
        viewSwitcher = rootView.findViewById(R.id.viewSwitcher);
        photosRecView = rootView.findViewById(R.id.recViewPhotos);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        readAllImages();
    }

    private void performSearch(String query) {

        if (photoFiles == null) {
            readAllImages();
        }

        resultOfSearching = new ArrayList<>();

        if (query.isEmpty()) {
            showAllPictures();
            return;
        }

        for (File file : photoFiles) {
            if (file.getName().toLowerCase().contains(query.toLowerCase())) {
                resultOfSearching.add(file);
            }
        }

        showAllPictures();
    }

    private void readAllImages() {
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
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void showAllPictures() {
        if (resultOfSearching.size() > 0) {
            if (viewSwitcher != null && photosRecView != null) {
                if (photosRecView.getId() == viewSwitcher.getNextView().getId()) {
                    viewSwitcher.showNext();
                }

                photosAdapter = new PhotosAdapter(context, resultOfSearching, spanCount);
                photosRecView.setAdapter(photosAdapter);
                photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
            }
        } else {
            if (viewSwitcher != null && viewSwitcher.getNextView() != null && R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        }
    }
}
