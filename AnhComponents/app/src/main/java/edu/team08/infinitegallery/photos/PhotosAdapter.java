package edu.team08.infinitegallery.photos;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.singlephoto.SinglePhotoActivity;
import edu.team08.infinitegallery.trashbin.SingleTrashActivity;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private final Context context;
    private SparseBooleanArray selectedItemsIds;
    private List<File> allPhotos;
    private final int spanCount;
    private boolean isTrash;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageItem;
        private CheckBox checkbox;
        private TextView txtNameImage;
        private TextView txtSizeAndDateImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if (spanCount != 1) {
                imageItem = itemView.findViewById(R.id.itemPhoto);
                checkbox = itemView.findViewById(R.id.itemPhotoCheckBox);
            }
            else {
                imageItem = itemView.findViewById(R.id.listItemPhoto);
                checkbox = itemView.findViewById(R.id.checkListBox);
                txtNameImage = itemView.findViewById(R.id.txtNamePhoto);
                txtSizeAndDateImage = itemView.findViewById(R.id.txtSizeAndDatePhoto);
            }
        }
    }

    public PhotosAdapter(Context context, List<File> allPhotos, int spanCount) {
        this.context = context;
        this.allPhotos = allPhotos;
        selectedItemsIds = new SparseBooleanArray();
        this.spanCount = spanCount;
        this.isTrash = context.toString().contains("TrashBinActivity");
    }

    @NonNull
    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView;
        if (spanCount != 1)
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        else
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_photo, parent, false);

        Glide.with(context).clear(rootView);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get item path at current position
        File photo = allPhotos.get(position);
        Log.e("onBind", photo.getAbsolutePath());
        // Set item to the ImageView using Glide library
        // holder.imageItem.setImageDrawable(Drawable.createFromPath(picturePath));
        Glide.with(context)
                .load(photo)
                .into(holder.imageItem);
        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, position + ": " + photo.getName(), Toast.LENGTH_SHORT).show();
                // TODO: start fullScreenPhoto activity, sending the photo's absolutePath.
                // TODO: (!) remember to check if it's exist or not
                String[] photoPaths = new String[allPhotos.size()];
                for (int i = 0; i < photoPaths.length; i++) {
                    photoPaths[i] = allPhotos.get(i).getAbsolutePath();
                }
                Intent myIntent;
                if (isTrash) myIntent = new Intent(context, SingleTrashActivity.class);
                else myIntent = new Intent(context, SinglePhotoActivity.class);
                myIntent.putExtra("photoPaths", photoPaths);
                myIntent.putExtra("currentPosition", position);
                startActivity(context, myIntent, null);
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        // Set width and height of ImageView
        if (this.isTrash) {
            ((TrashBinActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        } else {
            ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        }
        // Depend on how many columns of images are displayed in view
        if (spanCount != 1) {
            int size = displaymetrics.widthPixels / spanCount;
            holder.imageItem.setLayoutParams(new RelativeLayout.LayoutParams(size, size));
        }
        else {
            // Set image size to display
            int size = displaymetrics.widthPixels / 4;
            holder.imageItem.setLayoutParams(new RelativeLayout.LayoutParams(size, size));

            // Set the information of image
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);

            int lastSlash = photo.getAbsolutePath().lastIndexOf('/');
            String imageName = photo.getAbsolutePath().substring(lastSlash + 1);
            holder.txtNameImage.setText(imageName);
            holder.txtSizeAndDateImage.setText(Math.round(photo.length() * 1.0 / 1000) + " KB");
            holder.txtSizeAndDateImage.append(", ");
            holder.txtSizeAndDateImage.append(sdf.format(photo.lastModified()));
        }


        if(selectedItemsIds.get(position))
        {
            holder.itemView.setBackgroundColor(0x9934B5E4);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(true);
        }
        else
        {
            holder.checkbox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return allPhotos.size();
    }
}
