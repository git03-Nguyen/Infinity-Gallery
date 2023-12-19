package edu.team08.infinitegallery.optionphotos;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import edu.team08.infinitegallery.helpers.DataBridge;
import edu.team08.infinitegallery.main.MainActivity;
import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorites.FavoriteActivity;
import edu.team08.infinitegallery.optionalbums.AddPhotoActivity;
import edu.team08.infinitegallery.optionalbums.SingleAlbumActivity;
import edu.team08.infinitegallery.privacy.PrivacyActivity;
import edu.team08.infinitegallery.privacy.SinglePrivacyActivity;
import edu.team08.infinitegallery.singlephoto.SinglePhotoActivity;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private final Context context;
    private SparseBooleanArray selectedItemsIds;
    private List<File> allPhotos;
    private final int spanCount;
    private boolean selectionMode;
    public boolean selectedAll;

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
        this.selectionMode = false;
        this.selectedAll = false;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PhotosAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get item path at current position
        File photo = allPhotos.get(position);

        // Set item to the ImageView using Glide library
        // holder.imageItem.setImageDrawable(Drawable.createFromPath(picturePath));
        Glide.with(context)
                .load(photo)
                .placeholder(R.drawable.img_image_placeholder)
                .into(holder.imageItem);

        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectionMode) {
                    String[] photoPaths = new String[allPhotos.size()];
                    for (int i = 0; i < photoPaths.length; i++) {
                        photoPaths[i] = allPhotos.get(i).getAbsolutePath();
                    }
                    Intent myIntent = null;
                    if (context instanceof MainActivity) {
                        myIntent = new Intent(context, SinglePhotoActivity.class);
                    } else if (context instanceof PrivacyActivity) {
                        myIntent = new Intent(context, SinglePrivacyActivity.class);
                    } else if (context instanceof SingleAlbumActivity) {
                        myIntent = new Intent(context, SinglePhotoActivity.class);
                    } else if (context instanceof FavoriteActivity) {
                        myIntent = new Intent(context, SinglePhotoActivity.class);
                    }
                    if (myIntent != null) {
//                        myIntent.putExtra("photoPaths", photoPaths);
                        DataBridge.photoStrings = photoPaths;
                        myIntent.putExtra("currentPosition", position);
                        startActivity(context, myIntent, null);
                    }
                } else {
                    toggleSelection(position);
                }

            }
        });

        holder.imageItem.setOnLongClickListener(v -> {
            if (!selectionMode) {
                toggleSelectionMode();
                ((MainCallbacks) context).onEmitMsgFromFragToMain("SELECTION MODE", "2");
                holder.imageItem.performClick();
            }
            return true;
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        // Set width and height of ImageView
        if (context instanceof TrashBinActivity) {
            ((TrashBinActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        } else if (context instanceof PrivacyActivity) {
            ((PrivacyActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        } else if (context instanceof MainActivity) {
            ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        } else if (context instanceof SingleAlbumActivity) {
            ((SingleAlbumActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        } else if (context instanceof FavoriteActivity) {
            ((FavoriteActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        } else if (context instanceof AddPhotoActivity) {
            ((AddPhotoActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
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

        if (selectedAll) {
            selectedItemsIds.put(position, true);
        }

        if(selectedItemsIds.get(position)) {
            holder.itemView.setBackgroundColor(0x993BF566);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(true);
            // make the holder.imageItem a bit darker
            //holder.imageItem.setColorFilter(Color.argb(150, 54, 171, 81));
            holder.imageItem.setColorFilter(Color.argb(150, 100, 100, 100));
        }
        else {
            holder.checkbox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.imageItem.clearColorFilter();
        }



    }

    @Override
    public int getItemCount() {
        return allPhotos.size();
    }

    public void toggleSelectionMode() {
        this.selectionMode = !this.selectionMode;
        removeAllSelections();
    }

    public boolean getSelectionMode() {
        return this.selectionMode;
    }

    public void toggleSelection(int position) {
        if (selectedItemsIds.get(position)) {
            selectedItemsIds.delete(position);
        } else {
            selectedItemsIds.put(position, true);
        }
        notifyItemChanged(position);
        ((MainCallbacks) context).onEmitMsgFromFragToMain("NUMBER OF SELECTIONS", String.valueOf(getSelectionsCount()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll() {
        selectedItemsIds.clear();
        selectedAll = true;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void unSelectAll() {
        selectedItemsIds.clear();
        selectedAll = false;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAllSelections() {
        selectedItemsIds.clear();
        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectionsCount() {
        return selectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }


}
