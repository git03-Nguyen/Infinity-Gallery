package edu.team08.infinitegallery.optionalbums;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.SquareImageButton;

// TODO: implement albums adapter
public class AlbumsAdapter extends BaseAdapter {

    private Context context;
    private AlbumFolder[] albumFolders;

    public AlbumsAdapter(Context context, AlbumFolder[] albumFolders) {
        this.context = context;
        this.albumFolders = albumFolders;
    }

    @Override
    public int getCount() {
        if (this.albumFolders == null) return 0;
        return this.albumFolders.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        }

        SquareImageButton btnAlbum = convertView.findViewById(R.id.squareBtnAlbum);
        TextView txtAlbumName = convertView.findViewById(R.id.albumName);
        TextView txtNumOfPhotos = convertView.findViewById(R.id.numberOfPhotos);

        AlbumFolder currentFolder = this.albumFolders[position];

        if (currentFolder.getNumberOfPhotos() == 0) {
            btnAlbum.setImageResource(R.drawable.img_image_error);
        } else {
            Bitmap myBitmap = BitmapFactory.decodeFile(currentFolder.getPhotos()[currentFolder.getNumberOfPhotos()-1].getAbsolutePath());
            btnAlbum.setImageBitmap(myBitmap);
        }
        btnAlbum.setBackgroundResource(R.drawable.sl_bg_button_private_album);
        txtAlbumName.setText(currentFolder.getFolder().getName());
        String formatText=context.getResources().getString(R.string.num_photos,currentFolder.getNumberOfPhotos());
//        txtNumOfPhotos.setText(currentFolder.getNumberOfPhotos() + " photos");
        txtNumOfPhotos.setText(formatText);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, SingleAlbumActivity.class);
                myIntent.putExtra("albumName", currentFolder.getFolder().getName());
                String[] photosStr = new String[currentFolder.getNumberOfPhotos()];
                File[] photosFiles = currentFolder.getPhotos();
                for (int i = 0; i < photosStr.length; i++) {
                    photosStr[i] = photosFiles[i].getAbsolutePath();
                }
                myIntent.putExtra("photosList", photosStr);
                myIntent.putExtra("folderPath", currentFolder.getFolder().getAbsolutePath());
                context.startActivity(myIntent, null);
            }
        });

        return convertView;
    }
}
