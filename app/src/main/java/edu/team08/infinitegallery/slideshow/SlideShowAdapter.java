package edu.team08.infinitegallery.slideshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;

public class SlideShowAdapter extends RecyclerView.Adapter<SlideShowAdapter.SliderViewHolder>{

    private final List<File> photos;
    private ViewPager2 viewPager2;
    Context context;

    SlideShowAdapter(Context context, ViewPager2 viewPager2, List<File> allPhotos) {
        this.context = context;
        this.viewPager2 = viewPager2;
        this.photos = allPhotos;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pager_single_photo, parent, false);
        return new SliderViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        File photoFile = photos.get(position % photos.size());
        Glide.with(context).asBitmap().load(photoFile).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoView);
        }
    }
}
