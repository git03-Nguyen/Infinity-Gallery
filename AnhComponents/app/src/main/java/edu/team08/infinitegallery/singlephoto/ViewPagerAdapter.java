package edu.team08.infinitegallery.singlephoto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;

public class ViewPagerAdapter extends PagerAdapter {
    static final int MAX_CACHE_SIZE = 16;
    Context context;
    File[] photoFiles;
    LayoutInflater inflater;
    ZoomImageView imageView;
    static HashMap<String, Drawable> cache = new HashMap<String, Drawable>();

    public ViewPagerAdapter(Context context, File[] photoFiles){
        this.photoFiles = photoFiles;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static private Drawable getDrawable(String key){
        if(!cache.containsKey(key)){
            cache.put(key, Drawable.createFromPath(key));
        }

        if(cache.size() >= MAX_CACHE_SIZE){
            cache.clear();
        }

        return cache.get(key);
    }

    public ZoomImageView getImageView(){ return imageView; }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = inflater.inflate(R.layout.item_pager_single_photo, container, false);
        ZoomImageView view = itemView.findViewById(R.id.zoomableImageView);
        view.setImageDrawable((getDrawable(photoFiles[position].getAbsolutePath())));
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public int getCount() {
        return photoFiles.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object){
        super.setPrimaryItem(container, position, object);
        imageView = ((View)object).findViewById(R.id.zoomableImageView);
        imageView.setImageDrawable(getDrawable(photoFiles[position].getAbsolutePath()));
    }
}
