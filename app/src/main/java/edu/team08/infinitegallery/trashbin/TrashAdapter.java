package edu.team08.infinitegallery.trashbin;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import edu.team08.infinitegallery.R;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.ViewHolder> {
    private final Context context;
    private SparseBooleanArray selectedItemsIds;
    private List<File> trashPhotos;
    private final int spanCount;
    private TrashBinManager trashBinManager;
    private int[] dayRemains;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageItem;
        private CheckBox checkbox;
        private TextView itemDaysText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (spanCount != 1) {
                imageItem = itemView.findViewById(R.id.itemPhoto);
                checkbox = itemView.findViewById(R.id.itemPhotoCheckBox);
                itemDaysText = itemView.findViewById(R.id.itemDaysText);
            }
        }
    }

    public TrashAdapter(Context context, List<File> trashPhotos, int spanCount, TrashBinManager trashBinManager) {
        this.context = context;
        this.trashPhotos = trashPhotos;
        selectedItemsIds = new SparseBooleanArray();
        this.spanCount = spanCount;
        this.trashBinManager = trashBinManager;
        this.dayRemains = trashBinManager.getDaysRemain(trashPhotos.toArray(new File[0]));
    }

    @NonNull
    @Override
    public TrashAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = null;
        if (spanCount != 1)
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trash, parent, false);

        Glide.with(context).clear(rootView);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrashAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get item path at current position
        File trash = trashPhotos.get(position);
        int daysRemain = this.dayRemains[position];
        // Set item to the ImageView using Glide library
        // holder.imageItem.setImageDrawable(Drawable.createFromPath(picturePath));
        Glide.with(context)
                .load(trashBinManager.decryptPhoto(trash))
                .placeholder(R.drawable.img_image_placeholder)
                .into(holder.imageItem);
        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start fullScreenPhoto activity, sending the photo's absolutePath.
                // TODO: (!) remember to check if it's exist or not
                String[] photoPaths = new String[trashPhotos.size()];
                for (int i = 0; i < photoPaths.length; i++) {
                    photoPaths[i] = trashPhotos.get(i).getAbsolutePath();
                }
                Intent myIntent = null;
                myIntent = new Intent(context, SingleTrashActivity.class);
                if (myIntent != null) {
                    myIntent.putExtra("photoPaths", photoPaths);
                    myIntent.putExtra("currentPosition", position);
                    startActivity(context, myIntent, null);
                }
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        // Set width and height of ImageView
        ((TrashBinActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        // Depend on how many columns of images are displayed in view
        if (spanCount != 1) {
            int size = displaymetrics.widthPixels / spanCount;
            holder.imageItem.setLayoutParams(new FrameLayout.LayoutParams(size, size));
            holder.itemDaysText.setText(Integer.toString(daysRemain) + " days");
        }

        if(selectedItemsIds.get(position)) {
            holder.itemView.setBackgroundColor(0x9934B5E4);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(true);
        }
        else {
            holder.checkbox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return trashPhotos.size();
    }
}
