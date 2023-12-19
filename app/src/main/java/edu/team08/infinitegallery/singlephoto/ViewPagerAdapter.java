package edu.team08.infinitegallery.singlephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.trashbin.SingleTrashActivity;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

public class ViewPagerAdapter extends PagerAdapter {
    static final int MAX_CACHE_SIZE = 16;
    static Context context;
    File[] photoFiles;
    LayoutInflater inflater;
    PhotoView zoomViewImage;
    static HashMap<String, Drawable> cache = new HashMap<String, Drawable>();

    public ViewPagerAdapter(Context context, File[] photoFiles){
        this.photoFiles = photoFiles;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static private Drawable getDrawable(String key){
        if(!cache.containsKey(key)){
            if (context instanceof SingleTrashActivity) {
                cache.put(key, new BitmapDrawable(context.getResources(), new TrashBinManager(context).decryptPhoto(new File(key))));
            } else {
                cache.put(key, Drawable.createFromPath(key));
            }
        }

        if(cache.size() >= MAX_CACHE_SIZE){
            cache.clear();
        }

        return cache.get(key);
    }

    public PhotoView getZoomViewImage(){ return zoomViewImage; }
    
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = inflater.inflate(R.layout.item_pager_single_photo, container, false);
        PhotoView view = itemView.findViewById(R.id.photoView);
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
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        zoomViewImage = ((View) object).findViewById(R.id.photoView);
        zoomViewImage.setImageDrawable(getDrawable(photoFiles[position].getAbsolutePath()));
    }

    public void rotate(int angle){
        zoomViewImage.setRotationBy(angle);
    }

    private Bitmap decodeWebPFile(String filePath) {
        try {
            // Use BitmapFactory to decode the WebP file
            InputStream inputStream = new FileInputStream(filePath);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
