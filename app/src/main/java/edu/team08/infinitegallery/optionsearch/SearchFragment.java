package edu.team08.infinitegallery.optionsearch;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.StringUtils;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.singlephoto.FaceOnLiveService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SearchFragment extends Fragment {

    private Context context;
    private SearchView searchView;

    int spanCount = 4;
    ViewSwitcher viewSwitcher;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;

    List<PhotoInfo> photoList;
    List<PhotoInfo> resultOfSearching;
    List<File> tempResult;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    public SearchFragment(Context context) {
        this.context = context;
    }

    public static SearchFragment newInstance(Context context) {
        return new SearchFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultOfSearching = new ArrayList<>();
        tempResult = new ArrayList<>();

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
    public void onPause() {
        super.onPause();
        executor.shutdown();
    }
    @Override
    public void onResume() {
        super.onResume();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                readAllImages();
//                String path = "/storage/emulated/0/Pictures/cccd.jpg";
//                File photoFile = new File(path);
//                postCurrentImage("cJnXPgk0ICnuhRKvxU9noCzpF8OGkV3P", photoFile);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showAllPictures();
                    }
                });
            }
        });
    }
    private void readAllImages() {

        photoList = new ArrayList<>();
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
                File photoFile = new File(photoPath);

                float[] coordinates = LocationHelper.readCoordinatesFromExif(photoPath);

                float latitude = 0;
                float longitude = 0;
                PhotoInfo photoInfo = new PhotoInfo(photoFile, latitude, longitude,"");
                if (coordinates != null && coordinates.length == 2) {
                    latitude = coordinates[0];
                    longitude = coordinates[1];
                    Log.d("ExifCoordinates", "Latitude: " + latitude + ", Longitude: " + longitude);
                    LocationHelper.reverseCoordinatesToAddress(latitude, longitude, new LocationHelper.ReverseGeocodeCallback() {
                        @Override
                        public void onSuccess(String formattedAddress,PhotoInfo photoInfo) {
                            // Use the formatted address here
                            Log.d("Reverse Geocoding Result", "Formatted Address: " + formattedAddress);
//                            address=formattedAddress;
                            photoInfo.setAddress(formattedAddress);
                            Log.d("Photo infomation:",photoInfo.getAddress());
                            photoList.add(photoInfo);

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("Photo information:",photoInfo.getAddress());
                            photoList.add(photoInfo);
                            // Handle failure here
                            t.printStackTrace();
                        }
                    },photoInfo);
                }
                else {
                    photoList.add(photoInfo);
                }
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

                photosAdapter = new PhotosAdapter(context, tempResult, spanCount);
                photosRecView.setAdapter(photosAdapter);
                photosRecView.setLayoutManager(new GridLayoutManager(context, spanCount));
            }
        } else {
            if (viewSwitcher != null && viewSwitcher.getNextView() != null && R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        }
    }

    private void performSearch(String query) {


        if (photoList == null) {
            readAllImages();
        }

        resultOfSearching = new ArrayList<>();

        if (query.isEmpty()) {
            showAllPictures();
            return;
        }

        for (PhotoInfo photoInfo : photoList) {
            String fileName = photoInfo.getFile().getName().toLowerCase();
            String address = StringUtils.removeAccent(photoInfo.getAddress()).toLowerCase();

            if (fileName.contains(query.toLowerCase()) || address.contains(query.toLowerCase())) {
                resultOfSearching.add(photoInfo);
            }
        }

        // Convert PhotoInfo objects to File objects
        tempResult = new ArrayList<>();
        for (PhotoInfo photoInfo : resultOfSearching) {
            tempResult.add(photoInfo.getFile());
            Log.d("Path of file",photoInfo.getFile().getPath());
        }

        showAllPictures();
    }

}