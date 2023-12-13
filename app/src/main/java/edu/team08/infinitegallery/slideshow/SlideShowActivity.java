package edu.team08.infinitegallery.slideshow;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.settings.AppConfig;

public class SlideShowActivity extends AppCompatActivity {
    private String folderPath;
    private String albumName;
    ViewPager2 viewPager2;
    TextView count;
    private List<File> allPhotos;
    private Toolbar toolbar;
    private Handler slideHandler = new Handler();

    int photoSize, currentIndex;
    int delayedValue = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        Intent intent = getIntent();
        toolbar = findViewById(R.id.toolbarSlideshow);

        if (intent.hasExtra("folderPath")) {
            folderPath = intent.getStringExtra("folderPath");
            albumName = new File(folderPath).getName();
            getAllPhotosOfFolder(folderPath);
            toolbar.setTitle("Slide of ${AlbumName}".replace("${AlbumName}", albumName));
        }

        if(intent.hasExtra("photoPaths")){
            String[] photoPaths = intent.getStringArrayExtra("photoPaths");
            allPhotos = new ArrayList<File>();
            for(int i = 0; i < photoPaths.length; i++){
                allPhotos.add(new File(photoPaths[i]));
            }

            toolbar.setTitle("Slideshow");
        }

        delayedValue = AppConfig.getInstance(this).getTimeLapse();

        allPhotos.add(0, allPhotos.get(allPhotos.size() - 1));
        allPhotos.add(allPhotos.get(1));
        photoSize = allPhotos.size();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        count = findViewById(R.id.count);
        count.setText("1/" + (photoSize - 2));

        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        viewPager2.setAdapter(new SlideShowAdapter(this, viewPager2, allPhotos));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPager2.setCurrentItem(1, false);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setScaleY(1);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int position) {
                super.onPageScrollStateChanged(position);
                if (position == ViewPager2.SCROLL_STATE_IDLE) {
                    if (viewPager2.getCurrentItem() == 0) {
                        viewPager2.setCurrentItem(allPhotos.size() - 2, false);
                    }
                    if (viewPager2.getCurrentItem() == allPhotos.size() - 1) {
                        viewPager2.setCurrentItem(1, false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
                slideHandler.postDelayed(sliderRunnable, delayedValue * 1000L);

                currentIndex = (viewPager2.getCurrentItem() - 1) % (photoSize - 2) + 1;
                count.setText(currentIndex + "/" + (photoSize - 2));
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, delayedValue * 1000L);
    }

    private void getAllPhotosOfFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder == null || !folder.isDirectory()) return;
        allPhotos = filterImageFiles(folder.listFiles());
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
}
