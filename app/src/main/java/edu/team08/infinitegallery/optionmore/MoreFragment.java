package edu.team08.infinitegallery.optionmore;

import static android.content.Intent.getIntent;
import static android.content.Intent.parseUri;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorite.FavoriteActivity;
import edu.team08.infinitegallery.favorite.FavoriteManager;
import edu.team08.infinitegallery.helpers.SquareImageButton;
import edu.team08.infinitegallery.optionalbums.SingleAlbumActivity;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;

public class MoreFragment extends Fragment {
    private static final int SETTINGS_REQUEST_CODE = 1;
    private Context context;
    private SquareImageButton btnTrashBin;
    private SquareImageButton btnFavorite;
    FavoriteManager favoriteManager;
    TextView favText, trashText;

    public MoreFragment(Context context) {
        this.context = context;
    }

    public static MoreFragment newInstance(Context context) {
        return new MoreFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbarMore);
        toolbar.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            int itemId = item.getItemId();
            if (itemId == R.id.menuMoreSettings) {
                Intent intent = new Intent(context, SettingsActivity.class);
//                getActivity().startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                startActivity(intent);
            } else {

            }
            return true;
        });

        btnTrashBin = rootView.findViewById(R.id.btn_trash_bin);
        btnTrashBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, TrashBinActivity.class);
                startActivity(myIntent);
            }
        });

        btnFavorite = rootView.findViewById(R.id.btn_favorites);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, FavoriteActivity.class);
                startActivity(myIntent, null);
            }
        });

        favText = rootView.findViewById(R.id.txtFavPhotos);
        trashText = rootView.findViewById(R.id.txtTrashBinPhotos);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        favText.setText(String.valueOf(getPhotosSize("favorite.db", "FAVORITE")) + " photos");
        trashText.setText(String.valueOf(getPhotosSize("trash_bin.db", "TRASH_BIN")) + " photos");
    }

    public int getPhotosSize(String dbName, String table) {
        int size = 0;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName), null);
        // Query the database to get all favorite file names
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            size = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return size;
    }
}