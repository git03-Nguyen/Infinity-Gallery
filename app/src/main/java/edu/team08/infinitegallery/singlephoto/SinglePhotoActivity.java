
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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorites.FavoriteManager;

import edu.team08.infinitegallery.settings.AppConfig;
import edu.team08.infinitegallery.singlephoto.RecognizeCard.CardInfo;
import edu.team08.infinitegallery.singlephoto.RecognizeCard.DriverLicenseCard;
import edu.team08.infinitegallery.singlephoto.RecognizeCard.IDCard;
import edu.team08.infinitegallery.singlephoto.RecognizeCard.PassportCard;
import edu.team08.infinitegallery.singlephoto.edit.EditPhotoActivity;
import edu.team08.infinitegallery.singlephoto.edit.FileSaveHelper;

import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;

import edu.team08.infinitegallery.privacy.PrivacyManager;

import edu.team08.infinitegallery.trashbin.TrashBinManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SinglePhotoActivity extends AppCompatActivity implements MainCallbacks {
    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private Toolbar topToolbarPhoto;
    private String[] photoPaths;
    private CheckBox favoriteBox;
    private WallpaperManager wallpaperManager;
    private int currentPosition;
    private PhotoView view;
    private Bitmap imageBitmap;

    private PopupMenu morePopupMenu;

    private CheckBox cardBox;
    private static final String API_KEY_INFO="cJnXPgk0ICnuhRKvxU9noCzpF8OGkV3P";

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

        singlePhotoFragment = new SinglePhotoFragment(this, photoPaths, currentPosition);
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

        cardBox=findViewById(R.id.cbCard);
        cardBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean isChecked = cardBox.isChecked();
                if (isChecked) {
                    Log.d("CardBox", "Clicked: " + cardBox.isChecked());
                    Log.d("PhotoPaths",photoPaths[currentPosition]);
                    File photoFile = new File(photoPaths[currentPosition]);
                    postCurrentImage(photoFile);
                }
            }
        });
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
                } else if(itemId == R.id.more_setAsWallpaper){
//                    view = singlePhotoFragment.getImageView();
//                    try {
//                        wallpaperManager.setBitmap(viewToBitmap(view, view.getWidth(), view.getHeight()));
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }

//                    try{
//                        imageBitmap = BitmapFactory.decodeFile(photoPaths[currentPosition]);
//                        DisplayMetrics displayMetrics = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                        int width = displayMetrics.widthPixels;
//                        int height = displayMetrics.heightPixels;
//                        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, true);
//                        wallpaperManager.setBitmap(imageBitmap);
//                    }catch(IOException e){
//                        Log.e("Error set as wallpaper: ", e.getMessage());
//                    }

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int height = metrics.heightPixels;
                    int width = metrics.widthPixels;
                    Bitmap tempbitMap = BitmapFactory.decodeFile(photoPaths[currentPosition]);
                    Bitmap bitmap = Bitmap.createScaledBitmap(tempbitMap,width,height, true);
                    wallpaperManager.setWallpaperOffsetSteps(1, 1);
                    wallpaperManager.suggestDesiredDimensions(width, height);
                    try {
                        wallpaperManager.setBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(SinglePhotoActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
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

    private Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        view.draw(canvas);
        return bm;
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
                if (AppConfig.getInstance(this).getSelectedLanguage()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format), new Locale("vi"));
                    SimpleDateFormat timeFormat = new SimpleDateFormat(getResources().getString(R.string.time_format), new Locale("vi"));
                    topToolbarPhoto.setTitle(dateFormat.format(date));
                    topToolbarPhoto.setSubtitle(timeFormat.format(date));
                }
                else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format));
                    SimpleDateFormat timeFormat = new SimpleDateFormat(getResources().getString(R.string.time_format));
                    topToolbarPhoto.setTitle(dateFormat.format(date));
                    topToolbarPhoto.setSubtitle(timeFormat.format(date));
                }
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
                getString(R.string.confirm_deletion_title),
                getString(R.string.confirm_deletion_one_photo_message),
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
                getString(R.string.confirm_hiding_title),
                getString(R.string.confirm_hiding_message),
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

    private void showBottomSheet(CardInfo card) {
        // Tạo và hiển thị BottomSheetFragment
        BottomSheetFragment bottomSheetFragment=BottomSheetFragment.newInstance(card);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void postCurrentImage(File photoFile) {
        File currentFile = photoFile;
        if (currentFile != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.faceonlive.com/")
                    .build();

            FaceOnLiveService service = retrofit.create(FaceOnLiveService.class);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), currentFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", currentFile.getName(), reqFile);

            Call<ResponseBody> call = service.postImage(API_KEY_INFO, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful())    {
                        try {
                            String jsonString = response.body().string();
                            CardInfo cardInfo= handleJsonResponse(jsonString);
                            if (cardInfo == null || cardInfo.getCardType() == null) {
                                Toast.makeText(getBaseContext(), "This is not a valid card, unable to retrieve information.", Toast.LENGTH_SHORT).show();
                            } else {
                                showBottomSheet(cardInfo);
                            }
                            cardBox.setChecked(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), "An error occurred while processing the response.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Info Card id", "Response not successful: " + response.code() + " " + response.message());
                        Toast.makeText(getBaseContext(), "This is not a valid card, unable to retrieve information.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle the failure
                }
            });
        }

    }
    public CardInfo handleJsonResponse(String jsonString)
    {
        CardInfo objCard=null;
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject dataObject = jsonObject.getJSONObject("data");

            String countryName = dataObject.getString("countryName");
            String documentName = dataObject.getString("documentName");
            Log.d("Info Card id", "Document Name: " + documentName);
            if ("Id Card".equals(documentName))
            {
                JSONObject nationObject = dataObject.getJSONObject("nation");
                JSONObject ocrObject=dataObject.getJSONObject("ocr");
                String name = nationObject.optString("name","undefined");
                if ("undefined".equals(name)){
                    name=ocrObject.optString("name","undefined");
                }
                Log.d("Name person: ",name);
                String address=nationObject.optString("address","undefined");
                String gender=nationObject.optString("sex","undefined");
                String nationality=nationObject.optString("nationality","undefined");
                if ("undefined".equals(nationality)){
                    nationality=ocrObject.optString("nationality","undefined");
                }
                String dob=ocrObject.optString("dateOfBirth","undefined");
                String dateExpired=ocrObject.optString("dateOfExpiry","undefined");
                String documentIDNumber=ocrObject.optString("documentNumber","undefined");

                 objCard=new IDCard(documentIDNumber,countryName,name,dob,nationality,dateExpired,address,gender);


                Log.d("Info Card id", "Country Name: " + countryName);
                Log.d("Info Card id", "Document Name: " + documentName);
                Log.d("Info Card id", "Name: " + name);
                Log.d("Date of birth: ",dob);

                Log.d("Document Number: ",documentIDNumber);
            }
           else if ("Passport".equals(documentName))
            {
                JSONObject ocrObject=dataObject.getJSONObject("ocr");
                String  name=ocrObject.optString("name","undefined");
                String dob=ocrObject.optString("dateOfBirth","undefined");
                String dateExpired=ocrObject.optString("dateOfExpiry","undefined");
                String documentIDNumber=ocrObject.optString("documentNumber","undefined");
                String nationality=ocrObject.optString("nationality","undefined");
                String issueStateCode=ocrObject.optString( "issuingStateCode","undefined");

                objCard=new PassportCard(documentIDNumber,countryName,name,dob,nationality,dateExpired,issueStateCode);
                Log.d("Info Card id", "Country Name: " + countryName);
                Log.d("Info Card id", "Document Name: " + documentName);
                Log.d("Info Card id", "Name: " + name);
                Log.d("Date of birth: ",dob);
                Log.d("Document Number: ",documentIDNumber);
            }
           else if ("Driver Licence".equals(documentName))
            {
                JSONObject nationObject = dataObject.optJSONObject("nation");
                JSONObject ocrObject=dataObject.getJSONObject("ocr");
                String name, address,nationality;
                if (nationObject!=null)
                {
                    name= nationObject.optString("name","undefined");
                    nationality=nationObject.optString("nationality","undefined");
                    address=nationObject.optString("address","undefined");
                }
                else {
                    name=ocrObject.optString("name","undefined");
                    nationality=ocrObject.optString("nationality","undefined");
                    address=ocrObject.optString("address","undefined");
                }


                String dob=ocrObject.optString("dateOfBirth","undefined");
                String dateExpired=ocrObject.optString("dateOfExpiry","undefined");
                String documentIDNumber=ocrObject.optString("documentNumber","undefined");
                String driverLicenseClass=ocrObject.optString("dlClass","undefined");

                objCard=new DriverLicenseCard(documentIDNumber,countryName,name,dob,nationality,dateExpired,driverLicenseClass,address);
                Log.d("Driver class: ",driverLicenseClass);

            }
        }
        catch ( JSONException e) {
            e.printStackTrace();
        }
        return objCard;
    }
}