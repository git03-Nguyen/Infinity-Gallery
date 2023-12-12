package edu.team08.infinitegallery.optionmore;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorites.FavoriteActivity;
import edu.team08.infinitegallery.favorites.FavoriteManager;
import edu.team08.infinitegallery.helpers.SquareImageButton;

import edu.team08.infinitegallery.main.MainActivity;
import edu.team08.infinitegallery.privacy.PrivacyActivity;
import edu.team08.infinitegallery.privacy.PrivacyLoginActivity;

import edu.team08.infinitegallery.settings.SettingsActivity;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;

public class MoreFragment extends Fragment {
    private static final int SETTINGS_REQUEST_CODE = 1;
    private Context context;
    private SquareImageButton btnTrashBin;
    private SquareImageButton btnPrivacy;
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

        btnPrivacy = rootView.findViewById(R.id.btn_privacy);
        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHavingPassword()) {
                    Intent myIntent = new Intent(context, PrivacyLoginActivity.class);
                    startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(context, PrivacyActivity.class);
                    startActivity(myIntent);
                }

            }
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

    boolean isHavingPassword() {
        SharedPreferences mPref = context.getSharedPreferences("PASSWORD", Context.MODE_PRIVATE);

        String password = mPref.getString(PrivacyLoginActivity.PREF_PASS_NAME, null);
//        return (null != password);
        boolean state = mPref.contains("PASS")
                || (password != null)
                ;
        Toast.makeText(this.context, "Password state: " + state, Toast.LENGTH_SHORT).show();
        Log.d("PASSWORD_STATE", "The password: " + mPref.getString("PASS", null));
        return state;
    }

    @Override
    public void onResume() {
        super.onResume();
        String numFavPhoto=getResources().getString(R.string.num_photos,getPhotosSize("favorite.db", "FAVORITE"));
        favText.setText(numFavPhoto );
        String numbTrashPhoto=getResources().getString(R.string.num_photos,getPhotosSize("trash_bin.db", "TRASH_BIN"));
        trashText.setText(numbTrashPhoto);
        ((MainActivity) context).changeStatusBar();
    }

    public int getPhotosSize(String dbName, String table) {
        int size = 0;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName), null);

        // Check if the table exists
        if (tableExists(db, table)) {
            // Query the database to get the count of all records in the table
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table, null);

            // Move to the first row of the result set
            if (cursor.moveToFirst()) {
                size = cursor.getInt(0); // Get the count from the first column
            }
            cursor.close();
        }
        db.close();
        return size;
    }

    // Helper method to check if a table exists in the database
    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

}