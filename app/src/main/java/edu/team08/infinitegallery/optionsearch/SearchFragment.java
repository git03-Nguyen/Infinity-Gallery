package edu.team08.infinitegallery.optionsearch;

import android.content.Context;
import android.database.Cursor;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.ViewSwitcher;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.StringUtils;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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

        if (photoList == null) {
            readAllImages();
        }

        resultOfSearching = new ArrayList<>();

        if (query.isEmpty()) {
            showAllPictures();
            return;
        }

        for (PhotoInfo photoInfo : photoList) {
            if (photoInfo.getFile().getName().toLowerCase().contains(query.toLowerCase())) {
                resultOfSearching.add(photoInfo);
                //Log.d("Đã tìm thấy file photo: ",photoInfo.getFile().getName());
                //  break statement to stop adding the same photoInfo multiple times
            }
            String address= StringUtils.removeAccent(photoInfo.getAddress());
            Log.d("Address no accent: ",address);
            if (address.toLowerCase().contains(query.toLowerCase())) {
                resultOfSearching.add(photoInfo);
                //Log.d("Đã tìm thấy vị trí: ",photoInfo.getAddress());

            }
        }


        // Convert PhotoInfo objects to File objects
        tempResult = new ArrayList<>();
        for (PhotoInfo photoInfo : resultOfSearching) {
            tempResult.add(photoInfo.getFile());
        }

        showAllPictures();
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
//                        String address="";
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
                            Log.d("Photo infomation:",photoInfo.getAddress());
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




}
