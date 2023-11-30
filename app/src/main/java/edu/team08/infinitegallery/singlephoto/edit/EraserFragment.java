package edu.team08.infinitegallery.singlephoto.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.team08.infinitegallery.R;


public class EraserFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {
    private Properties properties;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eraser_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SeekBar sbEraserSize = view.findViewById(R.id.eraserSize);

        sbEraserSize.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        properties.onEraserSizeChange(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Your onStartTrackingTouch implementation here
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Your onStopTrackingTouch implementation here
    }

    public void setPropertiesChangeListener(Properties properties){
        this.properties = properties;
    }


    public interface Properties {
        void onEraserSizeChange(int size);
    }
}