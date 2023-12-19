package edu.team08.infinitegallery.singlephoto.edit.tools;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.settings.AppConfig;

public class EditToolAdapter extends RecyclerView.Adapter<EditToolAdapter.ViewHolder>{
    private OnItemSelected mOnItemSelected;
    private List<ToolModel> mToolList = new ArrayList();
    Context context;

    public EditToolAdapter(Context context,OnItemSelected onItemSelected){
        mOnItemSelected = onItemSelected;

        // Original ToolModel names
        String shapeName = "Shape";
        String textName = "Text";
        String eraserName = "Eraser";
        String filterName = "Filter";
        String emojiName = "Emoji";
        String stickerName = "Sticker";

        if (AppConfig.getInstance(context).getSelectedLanguage()) {
            // Vietnamese names
            shapeName = "Hình dạng";
            textName = "Văn bản";
            eraserName = "Cục tẩy";
            filterName = "Bộ lọc";
            emojiName = "Cảm xúc";
            stickerName = "Nhãn dán";
        }

        mToolList.add(new ToolModel(shapeName, R.drawable.ic_oval, ToolType.SHAPE));
        mToolList.add(new ToolModel(textName, R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel(eraserName, R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel(filterName, R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel(emojiName, R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel(stickerName, R.drawable.ic_sticker, ToolType.STICKER));
    }

    public interface OnItemSelected{
        public void onToolSelected(ToolType toolType);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    public class ToolModel{
        public String mToolName;
        public int mToolIcon;
        public ToolType mToolType;

        public ToolModel(String toolName, int toolIcon, ToolType toolType){
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgToolIcon;
        public TextView txtTool;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
                }
            });

            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
        }
    }
}

