package edu.team08.infinitegallery.singlephoto.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.team08.infinitegallery.R;
import ja.burhanrashid52.photoeditor.shape.ShapeType;

public class ShapeFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {
    private Properties properties;
    private int sizeBrush = 25;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shape_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvColor = view.findViewById(R.id.shapeColors);
        SeekBar sbOpacity = view.findViewById(R.id.shapeOpacity);
        SeekBar sbBrushSize = view.findViewById(R.id.shapeSize);
        RadioGroup shapeGroup = view.findViewById(R.id.shapeRadioGroup);

        shapeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.lineRadioButton){
                properties.onShapePicked(ShapeType.Line.INSTANCE);
            } else  if(checkedId == R.id.arrowRadioButton){
                properties.onShapePicked(new ShapeType.Arrow());
            } else  if(checkedId == R.id.ovalRadioButton){
                properties.onShapePicked(ShapeType.Oval.INSTANCE);
            } else  if(checkedId == R.id.rectRadioButton){
                properties.onShapePicked(ShapeType.Rectangle.INSTANCE);
            } else {
                properties.onShapePicked(ShapeType.Brush.INSTANCE);
            }
        });

        sbOpacity.setOnSeekBarChangeListener(this);
        sbBrushSize.setOnSeekBarChangeListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(layoutManager);
        rvColor.setHasFixedSize(true);

        ColorAdapter colorAdapter = new ColorAdapter(requireActivity());
        colorAdapter.setOnColorPickerClickListener(colorCode -> {
            if (properties != null) {
                dismiss();
                properties.onColorChanged(colorCode);
            }
        });

        rvColor.setAdapter(colorAdapter);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (properties != null && seekBar.getId() == R.id.shapeOpacity) {
            properties.onOpacityChanged(progress);
        }
        if (properties != null && seekBar.getId() == R.id.shapeSize) {
            properties.onShapeSizeChanged(progress);
        }
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

    public int getSizeBrush(){
        return sizeBrush;
    }

    public void setSizeBrush(int size){
        sizeBrush = size;
    }
    public interface Properties {
        void onColorChanged(int colorCode);

        void onOpacityChanged(int opacity);

        void onShapeSizeChanged(int shapeSize);

        void onShapePicked(ShapeType shapeType);
    }
}