package edu.team08.infinitegallery.singlephoto;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SinglePhotoFragment extends Fragment {

    private Context context;
    private File[] photoFiles;
    private int currentPosition;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public SinglePhotoFragment(Context context, String[] photoPaths, int currentPosition) {
        this.context = context;
        photoFiles = new File[photoPaths.length];
        for (int i = 0; i < photoPaths.length; i++) {
            this.photoFiles[i] = new File(photoPaths[i]);

        }
        this.currentPosition = currentPosition;
    }

    public SinglePhotoFragment(){}

    public static SinglePhotoFragment newInstance(Context context, String[] photoPaths, int currentPosition) {
        return new SinglePhotoFragment(context, photoPaths, currentPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout fragmentView = (FrameLayout) inflater.inflate(R.layout.fragment_single_photo, null);
        viewPager = fragmentView.findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(context, photoFiles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                ((MainCallbacks) context).onEmitMsgFromFragToMain("SinglePhotoFragment", Integer.toString(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return fragmentView;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public PhotoView getImageView(){
        return adapter.getZoomViewImage();
    }
}