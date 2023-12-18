package edu.team08.infinitegallery.optionalbums;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.SquareImageButton;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    private Context context;
    private AlbumFolder[] albumFolders;
    private int spanCount;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView;
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);

        Glide.with(context).clear(rootView);
        return new AlbumsAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlbumFolder currentFolder = this.albumFolders[position];

        if (currentFolder.getNumberOfPhotos() == 0) {
            Glide.with(context)
                    .load(R.drawable.img_image_error)
                    .placeholder(R.drawable.img_image_placeholder)
                    .into(holder.btnAlbum);
        } else {
            Glide.with(context)
                    .load(currentFolder.getPhotos()[currentFolder.getNumberOfPhotos()-1])
                    .placeholder(R.drawable.img_image_placeholder)
                    .into(holder.btnAlbum);
        }

        holder.btnAlbum.setBackgroundResource(R.drawable.sl_bg_button_private_album);
        holder.txtAlbumName.setText(currentFolder.getFolder().getName());
        String formatText=context.getResources().getString(R.string.num_photos,currentFolder.getNumberOfPhotos());
//        txtNumOfPhotos.setText(currentFolder.getNumberOfPhotos() + " photos");
        holder.txtNumOfPhotos.setText(formatText);
        holder.btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, SingleAlbumActivity.class);
                myIntent.putExtra("albumName", currentFolder.getFolder().getName());
                myIntent.putExtra("folderPath", currentFolder.getFolder().getAbsolutePath());
                context.startActivity(myIntent, null);
            }
        });

//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int size = displaymetrics.widthPixels / spanCount;
//        holder.btnAlbum.setLayoutParams(new LinearLayout.LayoutParams(size, size));
    }

    @Override
    public int getItemCount() {
        if (this.albumFolders == null) return 0;
        return this.albumFolders.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SquareImageButton btnAlbum;
        private TextView txtAlbumName;
        private TextView txtNumOfPhotos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAlbum = itemView.findViewById(R.id.squareBtnAlbum);
            txtAlbumName = itemView.findViewById(R.id.albumName);
            txtNumOfPhotos = itemView.findViewById(R.id.numberOfPhotos);
        }
    }

    public AlbumsAdapter(Context context, AlbumFolder[] albumFolders, int spanCount) {
        this.context = context;
        this.albumFolders = albumFolders;
        this.spanCount = spanCount;
    }

}
