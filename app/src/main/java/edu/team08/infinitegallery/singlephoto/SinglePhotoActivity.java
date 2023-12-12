
package edu.team08.infinitegallery.singlephoto;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorites.FavoriteManager;

import edu.team08.infinitegallery.singlephoto.edit.EditPhotoActivity;
import edu.team08.infinitegallery.singlephoto.edit.FileSaveHelper;

import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;

import edu.team08.infinitegallery.privacy.PrivacyManager;

import edu.team08.infinitegallery.trashbin.TrashBinManager;
public class SinglePhotoActivity extends AppCompatActivity implements MainCallbacks {
    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private Toolbar topToolbarPhoto;
    private String[] photoPaths;
    private CheckBox favoriteBox;
    private WallpaperManager wallpaperManager;
    private int currentPosition;
    private PopupMenu morePopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

        Intent intent = getIntent();
        if (intent.hasExtra("photoPaths")) {
            this.photoPaths = intent.getStringArrayExtra("photoPaths");
        }

        currentPosition = 0;
        if (intent.hasExtra("currentPosition")) {
            currentPosition = intent.getIntExtra("currentPosition", 0);
        }

        singlePhotoFragment = SinglePhotoFragment.newInstance(photoPaths, currentPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, singlePhotoFragment)
                .commit();

        favoriteBox = findViewById(R.id.cbFavorite);
        favoriteBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = favoriteBox.isChecked();
                if (isChecked) {
                    addToFavorite();
                } else {
                    removeFromFavorite();
                }
            }
        });

        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        // TODO: implementations for bottom nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        morePopupMenu = new PopupMenu(SinglePhotoActivity.this, bottomNavigationView.findViewById(R.id.more));
        morePopupMenu.inflate(R.menu.menu_single_photo_more);
        morePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.more_copyTo){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                } else if(itemId == R.id.more_moveTo){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                } else if(itemId == R.id.more_slideshow){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                }else if(itemId == R.id.rotateLeft){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                }else if(itemId == R.id.rotateRight){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                }else if(itemId == R.id.rotate180){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                }else if(itemId == R.id.more_rename){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                } else if(itemId == R.id.more_setAsHomeScreen){
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;
                            Bitmap bitmap = BitmapFactory.decodeFile(photoPaths[currentPosition]);
                            wallpaperManager.setWallpaperOffsetSteps(1, 1);
                            wallpaperManager.suggestDesiredDimensions(width, height);
                            bitmap = centerCropWallpaper(bitmap, wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight());
                            try {
                                wallpaperManager.setBitmap(bitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };

                    thread.start();
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                } else if(itemId == R.id.more_setAsLockScreen){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                DisplayMetrics metrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                int height = metrics.heightPixels;
                                int width = metrics.widthPixels;
                                Bitmap bitmap = BitmapFactory.decodeFile(photoPaths[currentPosition]);
                                wallpaperManager.setWallpaperOffsetSteps(1, 1);
                                wallpaperManager.suggestDesiredDimensions(width, height);
                                bitmap = centerCropWallpaper(bitmap, wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight());
                                try {
                                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };

                        thread.start();
                        Toast.makeText(SinglePhotoActivity.this, "Set as Lockscreen", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SinglePhotoActivity.this, "Lock screen wallpaper not supported", Toast.LENGTH_SHORT).show();
                    }
                }else if(itemId == R.id.more_details){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                }else if(itemId == R.id.more_displayFilename){
                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.moveTrash) {
                moveToTrash();
            } else if(itemId == R.id.hide) {
                hideToPrivacy();
            } else if(itemId == R.id.edit){
                Intent myIntent = new Intent(this, EditPhotoActivity.class);
                myIntent.putExtra("photoPath", photoPaths[currentPosition]);
                startActivity(myIntent);
            } else if(itemId == R.id.share){
                try {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            this.getApplicationContext().getPackageName() + ".fileprovider", new File(photoPaths[currentPosition]));
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(photoURI));
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.msg_share_image)));
                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if(itemId == R.id.more){
                morePopupMenu.show();
            }

            return true;
        });

        topToolbarPhoto = findViewById(R.id.topToolbarPhoto);
        setDateForToolbar(photoPaths[currentPosition]);
        setFavoriteForToolbar(photoPaths[currentPosition]);

        setSupportActionBar(topToolbarPhoto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = getIntent();
        intent.putExtra("currentPosition", currentPosition);
    }

    private Bitmap centerCropWallpaper(Bitmap wallpaper, int desiredWidth, int desiredHeight){
        float scale = (float) desiredHeight / wallpaper.getHeight();
        int scaledWidth = (int) (scale * wallpaper.getWidth());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int imageCenterWidth = scaledWidth /2;
        int widthToCut = imageCenterWidth - deviceWidth / 2;
        int leftWidth = scaledWidth - widthToCut;
        Bitmap scaledWallpaper = Bitmap.createScaledBitmap(wallpaper, scaledWidth, desiredHeight, false);
        Bitmap croppedWallpaper = Bitmap.createBitmap(
                scaledWallpaper,
                widthToCut,
                0,
                leftWidth,
                desiredHeight
        );
        return croppedWallpaper;
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

    private void setDateForToolbar(String filePath){
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(filePath));
            ExifSubIFDDirectory exifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            FileSystemDirectory fileDir = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
            Date date = null;
            if(exifDir != null) {
                date = exifDir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }else if(fileDir != null){
                date = fileDir.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
            }else{
                //TODO: how to handle in case the photo don't have Exif tag and File tag.
            }

            if(date != null && topToolbarPhoto != null){
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                topToolbarPhoto.setTitle(dateFormat.format(date));
                topToolbarPhoto.setSubtitle(timeFormat.format(date));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    private void addToFavorite() {
        int currentPosition = singlePhotoFragment.getCurrentPosition();
        new FavoriteManager(this).addToFavorite(photoPaths[currentPosition]);
        Toast.makeText(this, "Add to favorites", Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite() {
        int currentPosition = singlePhotoFragment.getCurrentPosition();
        new FavoriteManager(this).removeFromFavorite(photoPaths[currentPosition]);
        Toast.makeText(this, "Remove from favorites", Toast.LENGTH_SHORT).show();
    }

    private void moveToTrash() {
        // Get the current position
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        // Build a confirmation dialog with a progress bar
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Deletion",
                "Are you sure to move this photo to the trash?",
                new Runnable() {

                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(SinglePhotoActivity.this, "Deleting ...", () -> {
                                    try {
                                        new TrashBinManager(SinglePhotoActivity.this).moveToTrash(new File(photoPaths[currentPosition]));
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

    private void hideToPrivacy() {
        // Get the current position
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Hiding",
                "Are you sure to move this photo to the privacy list ?",
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(SinglePhotoActivity.this, "Hiding ...", () -> {
                                    try {
                                        new PrivacyManager(SinglePhotoActivity.this).hideToPrivacy(new File(photoPaths[currentPosition]));
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

    private void setFavoriteForToolbar(String photoPath) {
        boolean isFavorite = new FavoriteManager(this).isFavorite(photoPath);
        this.favoriteBox.setChecked(isFavorite);
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        // Do not care who sender?
        // Get information about current picture => current position.
        currentPosition = Integer.parseInt(request);
        setDateForToolbar(photoPaths[currentPosition]);
        setFavoriteForToolbar(photoPaths[currentPosition]);
    }

}