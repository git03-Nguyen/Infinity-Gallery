package edu.team08.infinitegallery.singlephoto.edit;

import static edu.team08.infinitegallery.InfinityGalleryApp.getApp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Integer> colorPickerColors;
    private OnColorClickListener onColorClickListener;

    public ColorAdapter(Context context, List<Integer> colorPickerColors) {
        this.context = context;
        this.colorPickerColors = colorPickerColors;
        this.inflater = LayoutInflater.from(context);
    }

    public ColorAdapter(Context context) {
        this(context, getDefaultColors(context));
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.color_picker_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GradientDrawable gradientDrawable = new GradientDrawable();

        // Set the shape to a rectangle
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);

        // Set the corner radius
        gradientDrawable.setCornerRadius(10);

        // Set the background color
        gradientDrawable.setColor(colorPickerColors.get(position));

        // Set the border color and width
        gradientDrawable.setStroke(3, ContextCompat.getColor(context, R.color.border_color_picker));

        // Set the created drawable as the background for the view
        holder.colorPickerView.setBackground(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    public void setOnColorPickerClickListener(OnColorClickListener onColorClickListener) {
        this.onColorClickListener = onColorClickListener;
    }


    public interface OnColorClickListener {
        void onColorClickListener(Integer colorCode);
    }

    public static List<Integer> getDefaultColors(Context context) {
        List<Integer> colorPickerColors = new ArrayList<>();
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.black));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.white));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker));
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_green_color_picker));
        return colorPickerColors;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View colorPickerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorPickerView = itemView.findViewById(R.id.color_picker_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onColorClickListener != null) {
                        onColorClickListener.onColorClickListener(colorPickerColors.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

}
