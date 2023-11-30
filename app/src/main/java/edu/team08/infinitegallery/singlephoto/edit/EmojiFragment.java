package edu.team08.infinitegallery.singlephoto.edit;

import static edu.team08.infinitegallery.InfinityGalleryApp.getApp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;


import java.util.ArrayList;

import edu.team08.infinitegallery.R;

public class EmojiFragment extends BottomSheetDialogFragment {
    private EmojiListener emojiListener = null;
    private BottomSheetCallback bsBehaviorCallback;
    private static ArrayList<String> emojisList = getEmojis(getApp());

    public EmojiFragment(){
        bsBehaviorCallback = new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);

        ViewGroup parentView = (ViewGroup) contentView.getParent();
        if (parentView != null) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parentView.getLayoutParams();
            CoordinatorLayout.Behavior<?> behavior = params.getBehavior();
            if (behavior != null && behavior instanceof BottomSheetBehavior<?>) {
                BottomSheetBehavior<?> bottomSheetBehavior = (BottomSheetBehavior<?>) behavior;
                bottomSheetBehavior.setBottomSheetCallback(bsBehaviorCallback);
            }

            parentView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        rvEmoji.setLayoutManager(gridLayoutManager);

        EmojiAdapter emojiAdapter = new EmojiAdapter();
        rvEmoji.setAdapter(emojiAdapter);
        rvEmoji.setHasFixedSize(true);
        rvEmoji.setItemViewCacheSize(emojisList.size());
    }

    public void setEmojiListener(EmojiListener emojiListener){
        this.emojiListener = emojiListener;
    }

    public static ArrayList<String> getEmojis(Context context) {
        ArrayList<String> convertedEmojiList = new ArrayList<>();
        String[] emojiList = context.getResources().getStringArray(R.array.photo_editor_emoji);
        for (String emojiUnicode : emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode));
        }
        return convertedEmojiList;
    }

    private static String convertEmoji(String emoji) {
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            return new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            return "";
        }
    }

    interface EmojiListener{
        void onEmojiClick(String emojiUnicode);
    }

    public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emoji_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtEmoji.setText(emojisList.get(position));
        }

        @Override
        public int getItemCount() {
            return emojisList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtEmoji;

            public ViewHolder(View itemView) {
                super(itemView);
                txtEmoji = itemView.findViewById(R.id.txtEmoji);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emojiListener != null) {
                            emojiListener.onEmojiClick(emojisList.get(getLayoutPosition()));
                        }
                        dismiss();
                    }
                });
            }
        }
    }
}
