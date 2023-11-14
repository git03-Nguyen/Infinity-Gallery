package edu.team08.infinitegallery.singlephoto;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.service.controls.templates.ControlTemplate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;

import edu.team08.infinitegallery.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SinglePhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SinglePhotoFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private Context context;
    private File[] photoFiles;
    private int currentPosition;
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
        ViewPager viewPager = fragmentView.findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(context, photoFiles);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);

        return fragmentView;
    }
}