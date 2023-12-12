package edu.team08.infinitegallery.singlephoto;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;

public class SinglePhotoFragment extends Fragment {

    private Context context;
    private File[] photoFiles;
    private int currentPosition;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public SinglePhotoFragment(){
        // Required empty public constructor
    }

    public static SinglePhotoFragment newInstance(String[] photoPaths, int currentPosition) {
        SinglePhotoFragment fragment = new SinglePhotoFragment();
        Bundle args = new Bundle();
        args.putStringArray("photoPaths", photoPaths);
        args.putInt("currentPosition", currentPosition);
        fragment.setArguments(args);
        return fragment;
    }

//    public SinglePhotoFragment(Context context, String[] photoPaths, int currentPosition) {
//        this.context = context;
//        photoFiles = new File[photoPaths.length];
//        for (int i = 0; i < photoPaths.length; i++) {
//            this.photoFiles[i] = new File(photoPaths[i]);
//        }
//        this.currentPosition = currentPosition;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        if(getArguments() != null){
            String[] photoPaths = getArguments().getStringArray("photoPaths");
            int currentPosition = getArguments().getInt("currentPosition");

            this.photoFiles = new File[photoPaths.length];
            for(int i = 0; i < photoFiles.length; i++){
                this.photoFiles[i] = new File(photoPaths[i]);
            }
            this.currentPosition = currentPosition;
        }
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

    @Override
    public void onPause() {
        super.onPause();
        if(getArguments() != null){
            Bundle oldArgs = getArguments();
            Bundle newArgs = new Bundle();
            newArgs.putStringArray("photoPaths", oldArgs.getStringArray("photoPaths"));
            newArgs.putInt("currentPosition", currentPosition);
            setArguments(newArgs);
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public PhotoView getImageView(){
        return adapter.getZoomViewImage();
    }
}