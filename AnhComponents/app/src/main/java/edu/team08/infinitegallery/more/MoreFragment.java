package edu.team08.infinitegallery.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.settings.SettingsActivity;
import edu.team08.infinitegallery.singlephoto.SinglePhotoActivity;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;

public class MoreFragment extends Fragment {
    private static final int SETTINGS_REQUEST_CODE = 1;
    private Context context;
    private Button btnTrashBin;

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

        btnTrashBin = rootView.findViewById(R.id.btnOpenTrashBin);
        btnTrashBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, TrashBinActivity.class);
                startActivity(myIntent);
            }
        });

        return rootView;
    }
}