package edu.team08.infinitegallery.optionalbums;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;

public class AlbumsFragment extends Fragment {
    private Context context;
    private RecyclerView albumRecView;
    private List<String> albumPaths;

    public AlbumsFragment(Context context) {
        this.context = context;
    }

    public static AlbumsFragment newInstance(Context context) {
        return new AlbumsFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbarAlbums);

        toolbar.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            int itemId = item.getItemId();
            if (itemId == R.id.menuAlbumsSettings) {
                Intent myIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(myIntent, null);
            }
            return true;
        });

        this.albumRecView = rootView.findViewById(R.id.recViewAlbums);
        this.albumPaths = new ArrayList<>();

        readFolders();

        return rootView;
    }

    private void readFolders() {

    }


}