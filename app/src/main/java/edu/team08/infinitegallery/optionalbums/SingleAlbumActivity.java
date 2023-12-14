package edu.team08.infinitegallery.optionalbums;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.privacy.PrivacyManager;
import edu.team08.infinitegallery.settings.SettingsActivity;
import edu.team08.infinitegallery.singlephoto.edit.EditPhotoActivity;
import edu.team08.infinitegallery.singlephoto.edit.FileSaveHelper;
import edu.team08.infinitegallery.slideshow.SlideShowActivity;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

public class SingleAlbumActivity extends AppCompatActivity implements MainCallbacks {
    static int spanCount = 4;
    List<File> photoFiles;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;
    Toolbar toolbar;
    Toolbar toolbarPhotosSelection;
    MaterialButton btnTurnOffSelectionMode;
    TextView txtNumberOfSelected;
    MaterialCheckBox checkBoxAll;
    BottomNavigationView bottomNavigationView;
    String albumName;
    String folderPath;
    boolean firstTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_album);

        this.firstTime = true;

        Intent intent = getIntent();
        if (intent.hasExtra("albumName")) {
            this.albumName = intent.getStringExtra("albumName");
        }
        if (intent.hasExtra("folderPath")) {
            this.folderPath = intent.getStringExtra("folderPath");
        }

        getAllPhotos();

        this.toolbar = findViewById(R.id.topToolbarAlbum);
        this.toolbar.setTitle(this.albumName);
        String formatText=getResources().getString(R.string.num_photos,this.photoFiles.size());
//        this.toolbar.setSubtitle(this.photoFiles.size() + " photos");
        this.toolbar.setSubtitle(formatText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photosRecView = findViewById(R.id.recViewPhotos);
        showAllPhotos();

        this.toolbarPhotosSelection = findViewById(R.id.toolbarPhotosSelection);
        this.btnTurnOffSelectionMode = findViewById(R.id.btnTurnOffSelectionMode);
        this.btnTurnOffSelectionMode.setOnClickListener(v -> {
            toggleSelectionMode();
        });
        this.txtNumberOfSelected = findViewById(R.id.txtNumberOfSelected);
        this.checkBoxAll = findViewById(R.id.checkboxAll);
        this.bottomNavigationView = findViewById(R.id.selectionBottomBar);
        this.bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        this.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            File[] files = getSelectedFiles();

            if (itemId == R.id.multipleHide) {
                hideMultiplePhotos(files);
            } else if (itemId == R.id.multipleMoveTrash) {
                trashMultiplePhotos(files);
            } else if (itemId == R.id.multipleShare) {
                shareMultiplePhotos(files);
            } else {
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private File[] getSelectedFiles() {
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

    private void trashMultiplePhotos(File[] files) {
        if (files.length == 0) return;
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getString(R.string.confirm_deletion_title),
                getString(R.string.confirm_deletion_message,files.length),
                new Runnable() {

                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(SingleAlbumActivity.this, "Deleting ...",
                                () -> {
                                    try {
                                        TrashBinManager trashBinManager = new TrashBinManager(SingleAlbumActivity.this);
                                        for (File file: files) {
                                            trashBinManager.moveToTrash(file);
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    toggleSelectionMode();
                                    onResume();
                                });

                    }
                },
                null);
    }

    private void hideMultiplePhotos(File[] files) {
        if (files.length == 0) return;
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getString(R.string.confirm_hiding_title),
                getString(R.string.confirm_hiding_list_photos_message,files.length),
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(SingleAlbumActivity.this, "Hiding ...", () -> {
                                    try {
                                        PrivacyManager privacyManager = new PrivacyManager(SingleAlbumActivity.this);
                                        for (File file: files) {
                                            privacyManager.hideToPrivacy(file);
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    toggleSelectionMode();
                                    onResume();
                                });

                    }
                },
                null);
    }

    private void shareMultiplePhotos(File[] files) {
        if (files.length == 0) return;

        ArrayList<Uri> uris = new ArrayList<>();
        for(File file: files){
            Uri photoURI = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".fileprovider", file);

            uris.add(buildFileProviderUri(photoURI));
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("image/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivityForResult(Intent.createChooser(shareIntent, getString(R.string.msg_share_image)), 1002);

    }

    private Uri buildFileProviderUri(Uri uri) {
        if (FileSaveHelper.isSdkHigherThan28()) {
            return uri;
        }

        String path = uri.getPath();
        if (path == null) {
            throw new IllegalArgumentException("URI Path Expected");
        }

        return FileProvider.getUriForFile(
                this,
                EditPhotoActivity.FILE_PROVIDER_AUTHORITY,
                new File(path)
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            toggleSelectionMode();
        }
    }

    private void deleteAlbum() {
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getString(R.string.confirm_deletion_title),
                getString(R.string.are_you_sure_to_delete_this_album_to_the_trash),
                new Runnable() {

                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(SingleAlbumActivity.this, "Deleting ...",
                                () -> {
                                    try {
                                        TrashBinManager trashBinManager = new TrashBinManager(SingleAlbumActivity.this);
                                        for (File file: photoFiles) {
                                            trashBinManager.moveToTrash(file);
                                        }
                                        new File(folderPath).delete();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    finish();
                                });

                    }
                },
                null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstTime) {
            firstTime = false;
        } else {
            getAllPhotosOfFolder(folderPath);
            showAllPhotos();
            String formatText=getResources().getString(R.string.num_photos,this.photoFiles.size());
            this.toolbar.setSubtitle(formatText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_single_album, menu);
        if (!new File(folderPath).getParentFile().getName().equals("Infinity-Albums")) {
            menu.removeItem(R.id.rename);
            menu.removeItem(R.id.deleteAlbum);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.settings) {
            Intent myIntent = new Intent(SingleAlbumActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else if (itemId == R.id.addPhotos) {
            Intent myIntent = new Intent(SingleAlbumActivity.this, AddPhotoActivity.class);
            myIntent.putExtra("folderPath", folderPath);
            startActivity(myIntent, null);
        } else if (itemId == R.id.column_2) {
            spanCount = 2;
            showAllPhotos();
        } else if (itemId == R.id.column_3) {
            spanCount = 3;
            showAllPhotos();
        } else if (itemId == R.id.column_4) {
            spanCount = 4;
            showAllPhotos();
        } else if (itemId == R.id.column_5) {
            spanCount = 5;
            showAllPhotos();
        } else if (itemId == R.id.slideshow) {
            if (photoFiles.size() > 0) {
                Intent myIntent = new Intent(SingleAlbumActivity.this, SlideShowActivity.class);
                myIntent.putExtra("folderPath", folderPath);
                startActivity(myIntent, null);
            }
        } else if (itemId == R.id.select) {
            if (photoFiles.size() > 0) {
                toggleSelectionMode();
            }
        } else if (itemId == R.id.deleteAlbum) {
            deleteAlbum();
        } else if (itemId == R.id.rename) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SingleAlbumActivity.this);
            builder.setTitle("Rename");

            EditText input = new EditText(SingleAlbumActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(albumName);
            builder.setView(input);
            input.selectAll();

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String rename = input.getText().toString();
                    File currentFile = new File(folderPath);
                    File newFile = new File(currentFile.getParent(), rename);
                    if(newFile.exists()){
                        Toast.makeText(SingleAlbumActivity.this, rename + " file is existed", Toast.LENGTH_SHORT);
                    } else {
                        if(currentFile.renameTo(newFile)){
                            folderPath = newFile.getAbsolutePath();
                            albumName = rename;
                            toolbar.setTitle(albumName);
                        }else{
                            Toast.makeText(SingleAlbumActivity.this,"Rename failed", Toast.LENGTH_SHORT);
                        }
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
        } else if (itemId == R.id.deleteAlbum) {
            // TODO: should delete recursively before delete directory
        } else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void toggleSelectionMode() {
        photosAdapter.toggleSelectionMode();
        if (photosAdapter.getSelectionMode()) {
            this.toolbar.setVisibility(View.GONE);
            this.toolbarPhotosSelection.setVisibility(View.VISIBLE);
            String formattedText=getResources().getString(R.string.selected_photos,0);
            this.txtNumberOfSelected.setText(formattedText);
            this.bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            this.toolbar.setVisibility(View.VISIBLE);
            this.toolbarPhotosSelection.setVisibility(View.GONE);
            this.bottomNavigationView.setVisibility(View.GONE);
        }
        this.checkBoxAll.setChecked(false);
    }

    private void getAllPhotosOfFolder(String folderPath) {
        this.photoFiles = new ArrayList<File>();
        File folder = new File(folderPath);
        if (folder == null || !folder.isDirectory()) return;
        this.photoFiles = filterImageFiles(folder.listFiles());
    }

    private List<File> filterImageFiles(File[] files) {
        List<File> imageFiles = new ArrayList<>();
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif"};

        Arrays.stream(files)
                .filter(file -> file.isFile() && hasValidExtension(file, validExtensions))
                .forEach(imageFiles::add);

        return imageFiles;
    }

    private boolean hasValidExtension(File file, String[] validExtensions) {
        return Arrays.stream(validExtensions)
                .anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
    }

    private void getAllPhotos() {
        Intent intent = getIntent();
        String[] photosPaths = null;
        if (intent.hasExtra("photosList")) {
            photosPaths = intent.getStringArrayExtra("photosList");
        }
        this.photoFiles = new ArrayList<>();
        if (photosPaths != null) {
            for (String path: photosPaths) {
                this.photoFiles.add(new File(path));
            }
        }
    }

    private void showAllPhotos() {
        photosAdapter = new PhotosAdapter(this, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        switch(sender) {
            case "NUMBER OF SELECTIONS":
                int selectionsCount = Integer.parseInt(request);
                this.setNumberOfSelectedFiles(selectionsCount);
                break;

            default: break;
        }
    }

    private void setNumberOfSelectedFiles(int number) {
        String formattedText=getResources().getString(R.string.selected_photos, number);
        this.txtNumberOfSelected.setText(formattedText);
    }

    List<String> imagePathList;
    String imagePath;

    @SuppressLint("Range")
    public void getImageFilePath(Uri uri) {

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePathList.add(imagePath);
            cursor.close();
        }
    }
}