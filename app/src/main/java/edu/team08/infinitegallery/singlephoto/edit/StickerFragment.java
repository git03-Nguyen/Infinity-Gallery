package edu.team08.infinitegallery.singlephoto.edit;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;

import edu.team08.infinitegallery.R;
public class StickerFragment extends BottomSheetDialogFragment {
    private StickerListener stickerListener;

    private final BottomSheetCallback bsBehaviorCallback = new BottomSheetCallback() {
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

    public void setStickerListener(StickerListener stickerListener) {
        this.stickerListener = stickerListener;
    }



    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams params = ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) params;
        BottomSheetBehavior<?> behavior = (BottomSheetBehavior<?>) layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            behavior.setBottomSheetCallback(bsBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);
        StickerAdapter stickerAdapter = new StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
        rvEmoji.setHasFixedSize(true);
        rvEmoji.setItemViewCacheSize(stickerPathList.length);
    }

    public static final String[] stickerPathList = {
            "https://cdn-icons-png.flaticon.com/256/4392/4392452.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392455.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392459.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392462.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392465.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392467.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392469.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392471.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392522.png"
    };

    public interface StickerListener {
        void onStickerClick(Bitmap bitmap);
    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(requireContext())
                    .asBitmap()
                    .load(stickerPathList[position])
                    .into(holder.imgSticker);
        }

        @Override
        public int getItemCount() {
            return stickerPathList.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(v -> {
                    if (stickerListener != null) {
                        Glide.with(requireContext())
                                .asBitmap()
                                .load(stickerPathList[getLayoutPosition()])
                                .into(new CustomTarget<Bitmap>(256, 256) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                                        stickerListener.onStickerClick(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(Drawable placeholder) {
                                    }
                                });
                    }
                    dismiss();
                });
            }
        }
    }
}