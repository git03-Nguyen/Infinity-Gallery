package edu.team08.infinitegallery.optionsearch;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import edu.team08.infinitegallery.R;

public class SearchFragment extends Fragment {
    private Context context;
    private SearchView searchView;

    public SearchFragment(Context context) {
        this.context = context;
    }

    public static SearchFragment newInstance(Context context) {
        return new SearchFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = rootView.findViewById(R.id.searchView);
        return rootView;
    }
}