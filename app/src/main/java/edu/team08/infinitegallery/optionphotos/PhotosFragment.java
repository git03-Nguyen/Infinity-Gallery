package edu.team08.infinitegallery.optionphotos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;

public class PhotosFragment extends Fragment {
    int spanCount = 4;
    Context context;
    List<File> photoFiles;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;
    ViewSwitcher viewSwitcher;
    Toolbar toolbar;
    Toolbar selectionToolbar;
    MaterialCheckBox checkBoxAll;
    MaterialButton btnTurnOffSelectionMode;
    TextView txtNumberOfSelectedFiles;

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
        viewSwitcher = photosFragment.findViewById(R.id.viewSwitcher);

        // TODO: update functionalities in toolbar
        toolbar = photosFragment.findViewById(R.id.toolbarPhotos);
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menuPhotosSettings) {
                Intent myIntent = new Intent(context, SettingsActivity.class);
                startActivity(myIntent, null);
            } else if (itemId == R.id.menuPhotosSelect) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
                toggleSelectionMode();
                
            } else if (itemId == R.id.column_2) {
                spanCount = 2;
                setSpanSize();
            } else if (itemId == R.id.column_3) {
                spanCount = 3;
                setSpanSize();
            } else if (itemId == R.id.column_4) {
                spanCount = 4;
                setSpanSize();
            } else if (itemId == R.id.column_5) {
                spanCount = 5;
                setSpanSize();
            } else {
                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        selectionToolbar = photosFragment.findViewById(R.id.toolbarPhotosSelection);
        txtNumberOfSelectedFiles = photosFragment.findViewById(R.id.txtNumberOfSelected);
        btnTurnOffSelectionMode = photosFragment.findViewById(R.id.btnTurnOffSelectionMode);
        btnTurnOffSelectionMode.setOnClickListener(v -> {
            toggleSelectionMode();
        });
        checkBoxAll = photosFragment.findViewById(R.id.checkboxAll);
        // TODO: set onclick for checkBoxAll

        return photosFragment;
    }

    public void toggleSelectionMode() {
        photosAdapter.toggleSelectionMode();
        if (photosAdapter.getSelectionMode()) {
            // Change the layout (toolbar)
            toggleToolbarForSelection();
            // Call activity to change layout (nav bar)
            ((MainCallbacks) context).onEmitMsgFromFragToMain("SELECTION MODE", "1");
        } else {
            // Change the layout (top bar)
            toggleToolbarForSelection();
            // Call activity to change layout (nav bar)
            ((MainCallbacks) context).onEmitMsgFromFragToMain("SELECTION MODE", "0");
            onResume();
        }
    }

    private void toggleToolbarForSelection() {
        if (photosAdapter.getSelectionMode()) {
            this.toolbar.setVisibility(View.GONE);
            this.selectionToolbar.setVisibility(View.VISIBLE);
            this.txtNumberOfSelectedFiles.setText("Selected " + 0);
        } else {
            this.toolbar.setVisibility(View.VISIBLE);
            this.selectionToolbar.setVisibility(View.GONE);
        }
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

    }

    void showAllPictures() {
        if (photoFiles.size() > 0) {
            if (photosRecView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
            photosAdapter = new PhotosAdapter(context, photoFiles, spanCount);
            photosRecView.setAdapter(photosAdapter);
            photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
            setSpanSize();
            toolbar.setTitle("November 29, 2023"); // TODO: set by the first image on the window view
        } else {
            if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
            toolbar.setTitle("");
        }
    }

    public File[] getSelectedFiles() {
        if (photosAdapter.getSelectionMode()) {
            List<File> list = new ArrayList<>();
            SparseBooleanArray selectedItemsId = photosAdapter.getSelectedIds();
            for (int i = 0; i < selectedItemsId.size(); i++) {
                if (selectedItemsId.valueAt(i)) list.add(photoFiles.get(selectedItemsId.keyAt(i)));
            }
            return list.toArray(new File[0]);
        } else {
            return new File[0];
        }
    }

    public void setNumberOfSelectedFiles(int number) {
        this.txtNumberOfSelectedFiles.setText("Selected " + photosAdapter.getSelectionsCount());
    }

    private void setSpanSize() {
        photosAdapter = new PhotosAdapter(context, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
    }
}