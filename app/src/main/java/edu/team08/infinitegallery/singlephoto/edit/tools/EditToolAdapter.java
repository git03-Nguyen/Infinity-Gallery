package edu.team08.infinitegallery.singlephoto.edit.tools;


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

public class EditToolAdapter extends RecyclerView.Adapter<EditToolAdapter.ViewHolder>{
    private OnItemSelected mOnItemSelected;
    private List<ToolModel> mToolList = new ArrayList();

    public EditToolAdapter(OnItemSelected onItemSelected){
        mOnItemSelected = onItemSelected;

        mToolList.add(new ToolModel("Shape", R.drawable.ic_oval, ToolType.SHAPE));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));
    }

    public interface OnItemSelected{
        public void onToolSelected(ToolType toolType);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_editing_tools_item, parent, false);
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

