package edu.team08.infinitegallery.optionphotos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.team08.infinitegallery.helpers.FileLastModifiedComparator;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.main.MainActivity;
import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.settings.AppConfig;
import edu.team08.infinitegallery.settings.SettingsActivity;
import edu.team08.infinitegallery.trashbin.TrashBinManager;


public class PhotosFragment extends Fragment {
    int spanCount = 4;
    public static int MAX_DIFF = 1;
    Context context;
    List<File> photoFiles;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;
    ViewSwitcher viewSwitcher;
    Toolbar toolbar;
    TextView txtPhotosTitle;
    Toolbar selectionToolbar;
    MaterialCheckBox checkBoxAll;
    MaterialButton btnTurnOffSelectionMode;
    TextView txtNumberOfSelectedFiles;
    FrameLayout frameLayoutToolbar;
    private Parcelable recyclerViewState;
    boolean firstTime;
  
    String[] permissions = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
    boolean permit_storage_image = false;
    boolean permit_camera_access = false;

    private ActivityResultLauncher<String> storageImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    permit_storage_image = true;
                }
                else {
                    permit_storage_image = false;
                }
                requestPermissionCameraAccess();
            });
    private ActivityResultLauncher<String> cameraAccessLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    permit_camera_access = true;
                }
                else {
                    permit_camera_access = false;
                }
            });
    private ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getData() != null) {
                                Bundle extras = result.getData().getExtras();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                saveImage(imageBitmap);
                            }
                        }
                    }
            );

    public PhotosFragment() {}

    public static PhotosFragment newInstance() {
        PhotosFragment fragment = new PhotosFragment();
        return fragment;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        firstTime = true;
//        ((MainActivity) context).getWindow().setStatusBarColor(Color.TRANSPARENT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View photosFragment = inflater.inflate(R.layout.fragment_photos, container, false);
        photosRecView = photosFragment.findViewById(R.id.recViewPhotos);
        photosRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = ((GridLayoutManager)photosRecView.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                // TODO: get the photo and set the time
                setTimeline(firstVisiblePosition);
            }
        });

        viewSwitcher = photosFragment.findViewById(R.id.viewSwitcher);

        // TODO: update functionalities in toolbar
        txtPhotosTitle = photosFragment.findViewById(R.id.txtPhotosTitle);
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
                if (!photoFiles.isEmpty()) {
                    toggleSelectionMode();
                }
                
            } else if (itemId == R.id.menuPhotosCamera) {
                if (permit_camera_access) {
                    openCamera();
                }
                else {
                    requestPermissionCameraAccess();
                }

            } else if (itemId == R.id.menuPhotosDeleteDup) {
                Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(context, "Deleting o...", () -> {
                            // Delete duplicate images
                            try {
                                deleteDuplicates(context);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        () -> {
                            Toast.makeText(context, "Deleted duplicates successfully", Toast.LENGTH_SHORT).show();
                            readAllImages();
                            setSpanSize();
                        });

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
                //Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        frameLayoutToolbar = photosFragment.findViewById(R.id.toolbarFrameLayout);
        // status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        frameLayoutToolbar.setPadding(0, statusBarHeight, 0, 0);
//        Toast.makeText(context, statusBarHeight, Toast.LENGTH_SHORT).show();
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameLayoutToolbar.getLayoutParams();
//        params.(0, statusBarHeight, 0, 0);
        selectionToolbar = photosFragment.findViewById(R.id.toolbarPhotosSelection);
        txtNumberOfSelectedFiles = photosFragment.findViewById(R.id.txtNumberOfSelected);
        btnTurnOffSelectionMode = photosFragment.findViewById(R.id.btnTurnOffSelectionMode);
        btnTurnOffSelectionMode.setOnClickListener(v -> {
            toggleSelectionMode();
        });
        checkBoxAll = photosFragment.findViewById(R.id.checkboxAll);
        checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                photosAdapter.selectAll();
                setNumberOfSelectedFiles(photoFiles.size());
            } else {
                photosAdapter.unSelectAll();
                setNumberOfSelectedFiles(0);
            }
        });

        return photosFragment;
    }

    public void toggleSelectionMode() {
        photosAdapter.toggleSelectionMode();
        // Change the layout (toolbar)
        toggleToolbarForSelection();
        if (photosAdapter.getSelectionMode()) {

            // Call activity to change layout (nav bar)
            ((MainCallbacks) context).onEmitMsgFromFragToMain("SELECTION MODE", "1");
        } else {

            // Call activity to change layout (nav bar)
            ((MainCallbacks) context).onEmitMsgFromFragToMain("SELECTION MODE", "0");
        }
    }

    public void toggleToolbarForSelection() {
        if (photosAdapter.getSelectionMode()) {
            this.toolbar.setVisibility(View.GONE);
            this.selectionToolbar.setVisibility(View.VISIBLE);
            String formattedText=getResources().getString(R.string.selected_photos,0);
            this.txtNumberOfSelectedFiles.setText(formattedText);
        } else {
            this.toolbar.setVisibility(View.VISIBLE);
            this.selectionToolbar.setVisibility(View.GONE);
        }
        this.checkBoxAll.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        readAllImages();
        showAllPictures();
        if (firstTime) {
            firstTime = false;
            photosRecView.scrollToPosition(photoFiles.size() - 1);
        } else {
            photosRecView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        ((MainActivity) context).changeStatusBar();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (photosRecView.getLayoutManager() != null) {
            recyclerViewState = photosRecView.getLayoutManager().onSaveInstanceState();
        }
        ((MainActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

            cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED);
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

        if (photoFiles != null) {
            Collections.sort(photoFiles, new FileLastModifiedComparator());
        }

    }

    void showAllPictures() {
        if (photoFiles.size() > 0) {
            if (photosRecView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
            photosAdapter = new PhotosAdapter(context, photoFiles, spanCount);
            photosRecView.setAdapter(photosAdapter);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, spanCount);
            photosRecView.setLayoutManager(gridLayoutManager);
            setSpanSize();

            GridLayoutManager layoutManager = ((GridLayoutManager)photosRecView.getLayoutManager());
            int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            setTimeline(firstVisiblePosition);
        } else {
            if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
            this.txtPhotosTitle.setText("");
        }
    }

    private void setTimeline(int position) {
        if (position < 0 || position >= photoFiles.size()) return;
        File photo = photoFiles.get(position);
        Date date = new Date(photo.lastModified());

        if (AppConfig.getInstance(context).getSelectedLanguage()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format), new Locale("vi"));
            this.txtPhotosTitle.setText(dateFormat.format(date));
        }
        else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format), new Locale("en"));
            this.txtPhotosTitle.setText(dateFormat.format(date));
        }

    }

    public File[] getSelectedFiles() {
        if (!photosAdapter.getSelectionMode()) {
            return new File[0];
        }

        if (photosAdapter.selectedAll) {
            return photoFiles.toArray(new File[0]);
        }

        List<File> list = new ArrayList<>();
        SparseBooleanArray selectedItemsId = photosAdapter.getSelectedIds();
        for (int i = 0; i < selectedItemsId.size(); i++) {
            if (selectedItemsId.valueAt(i)) list.add(photoFiles.get(selectedItemsId.keyAt(i)));
        }
        return list.toArray(new File[0]);
    }

    public void setNumberOfSelectedFiles(int number) {
        String formattedText=getResources().getString(R.string.selected_photos, number);
        this.txtNumberOfSelectedFiles.setText(formattedText);
    }

    private void setSpanSize() {
        photosAdapter = new PhotosAdapter(context, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
    }

    public void requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (storageImagesLauncher != null) {
                storageImagesLauncher.launch(permissions[0]);
            }
        }
        else {
            permit_storage_image = true;
            requestPermissionCameraAccess();
        }
    }

    public void requestPermissionCameraAccess() {
        if (ContextCompat.checkSelfPermission(context, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            if (cameraAccessLauncher != null) {
                cameraAccessLauncher.launch(permissions[1]);
            }
            Toast.makeText(context, "Permission for camera requested", Toast.LENGTH_SHORT).show();
        }
        else {
            permit_camera_access = true;
            openCamera();
        }
    }

    public void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraLauncher != null) {
            cameraLauncher.launch(cameraIntent);
        }
    }

    private void saveImage(Bitmap bitmap) {
        File cameraDir = new File(Environment.getExternalStorageDirectory(), "Camera");
        if (!cameraDir.exists()) {
            cameraDir.mkdir();
        }
        Log.e("CameraDirectory", cameraDir.getAbsolutePath());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File imageFile = new File(cameraDir, "IMG_" + timeStamp + ".jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            context.sendBroadcast(mediaScanIntent);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void scanMediaOnStorage(@Nullable Runnable runnable) {
        MediaScannerConnection.scanFile(context, new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, new String[] {"image/*"}, new MediaScannerConnection.OnScanCompletedListener()  {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
                if (runnable != null) runnable.run();
            }
        });
    }

    public void deleteDuplicates(Context context) throws IOException {
        List<PhotoFingerprint> photoFPs = new ArrayList<>();
        TrashBinManager trashBinManager = new TrashBinManager(context);
        // Create photoFPs to get all fingerprints
        for (File file : photoFiles) {
            String filePath = file.getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            PhotoFingerprint photo = new PhotoFingerprint(filePath);
            photo.setFinger(bitmap);
            photoFPs.add(photo);
        }
        // Sort photoFPs ascending based on fingerprint value
        Collections.sort(photoFPs);
        for (int i = 1; i < photoFPs.size(); i++) {
            int diff = photoFPs.get(i).hammingDist(photoFPs.get(i - 1).getFinger());
            if (diff <= MAX_DIFF) {
                // Delete older photo
                if (photoFPs.get(i).isNewer(photoFPs.get(i - 1))) {
                    trashBinManager.moveToTrash(new File(photoFPs.get(i - 1).getPath()));
                }
                else {
                    trashBinManager.moveToTrash(new File(photoFPs.get(i).getPath()));
                }
            }
        }
    }
}