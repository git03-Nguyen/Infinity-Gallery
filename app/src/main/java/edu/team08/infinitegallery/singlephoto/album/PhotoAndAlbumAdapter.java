package edu.team08.infinitegallery.singlephoto.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.SquareImageButton;
import edu.team08.infinitegallery.optionalbums.AlbumFolder;
import edu.team08.infinitegallery.optionalbums.SingleAlbumActivity;

public class PhotoAndAlbumAdapter extends BaseAdapter {

    private Context context;
    private AlbumFolder[] albumFolders;
    private String photoPath;

    public PhotoAndAlbumAdapter(Context context, AlbumFolder[] albumFolders, String photoPath) {
        this.context = context;
        this.albumFolders = albumFolders;
        this.photoPath = photoPath;
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
        txtNumOfPhotos.setText(formatText);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoAndAlbumsActivity activity = (PhotoAndAlbumsActivity) context;
                File folder = new File(currentFolder.getFolder().getAbsolutePath());
                File src = new File(photoPath);
                File dst = new File(folder, src.getName());
                boolean flag = false;

                if(activity.getOption().equals("more_copyTo")){
                    flag = false;
                }else if(activity.getOption().equals("more_moveTo")){
                    flag = true;
                }

                try {
                    transferFile(src, dst, flag);
                    activity.recreate();
                } catch (IOException e) {
                    Toast.makeText(context, "There are some errors!", Toast.LENGTH_SHORT).show();
                }

                Intent returnIntent = new Intent();
                activity.setResult(Activity.RESULT_CANCELED, returnIntent);
                activity.finish();
            }
        });

        return convertView;
    }

    // transfer data from src file to dst file, flag: true => delete src; false: remain src.
    public void transferFile(File src, File dst, boolean flag) throws IOException {
        if (dst.exists()) {
            // If the destination file already exists, rename it with a postfix
            dst = getUniqueDestination(dst);
        }

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }

        if(flag){
            src.delete();
        }
    }

    private File getUniqueDestination(File originalDst) {
        File dst = originalDst;
        int postfix = 1;

        // Keep incrementing the postfix until a unique destination is found
        while (dst.exists()) {
            String originalName = originalDst.getName();
            String nameWithoutExtension = originalName.substring(0, originalName.lastIndexOf('.'));
            String extension = originalName.substring(originalName.lastIndexOf('.') + 1);

            String newName = nameWithoutExtension + " (" + postfix + ")." + extension;
            dst = new File(originalDst.getParent(), newName);
            postfix++;
        }

        return dst;
    }
}
