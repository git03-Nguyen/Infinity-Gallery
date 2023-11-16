package edu.team08.infinitegallery.singlephoto;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.controls.templates.ControlTemplate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.trashbin.SingleTrashActivity;

public class SinglePhotoFragment extends Fragment {

    // TODO: Rename and change types of parameters
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

    // TODO: Rename and change types and number of parameters
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
            public void onPageSelected(int position) { currentPosition = position; }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return fragmentView;
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }


}