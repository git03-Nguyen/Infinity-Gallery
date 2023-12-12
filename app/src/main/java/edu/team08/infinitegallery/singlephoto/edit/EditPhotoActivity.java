package edu.team08.infinitegallery.singlephoto.edit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.Manifest;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.singlephoto.edit.base.BaseActivity;
import edu.team08.infinitegallery.singlephoto.edit.filters.FilterListener;
import edu.team08.infinitegallery.singlephoto.edit.filters.ViewFilterAdapter;
import edu.team08.infinitegallery.singlephoto.edit.tools.EditToolAdapter;
import edu.team08.infinitegallery.singlephoto.edit.tools.ToolType;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;

public class EditPhotoActivity extends BaseActivity implements ShapeFragment.Properties, View.OnClickListener,
        EditToolAdapter.OnItemSelected, FilterListener, EmojiFragment.EmojiListener, StickerFragment.StickerListener, EraserFragment.Properties{
    private PhotoEditorView photoEditorView;
    private PhotoEditor photoEditor;
    private String photoPath;
    private ShapeFragment shapeFragment;
    private ShapeBuilder shapeBuilder;
    private TextView txtCurrentTool;
    private EditToolAdapter editToolAdapter;
    private RecyclerView rvTools;
    private Boolean isFilterVisible;
    private Boolean isBrush;
    private Boolean isErase;
    private ConstraintSet constraintSet = new ConstraintSet();
    private ConstraintLayout rootView;
    private RecyclerView rvFilters;
    private ViewFilterAdapter viewFilterAdapter = new ViewFilterAdapter(this);
    private EmojiFragment emojiFragment;
    private StickerFragment stickerFragment;
    private FileSaveHelper saveFileHelper;
    private ImageView imageClose;
    private ImageView imageCheck;
    private Uri saveImageUri;
    private Toolbar topToolbarEditPhoto;
    private EraserFragment eraserFragment;
    public static final String TAG = "EditImageActivity";
    public static final String FILE_PROVIDER_AUTHORITY = "edu.team08.infinitegallery.fileprovider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);



        Intent intent = getIntent();
        photoPath = null;
        if(intent.hasExtra("photoPath")){
            photoPath = intent.getStringExtra("photoPath");
        }

        initViews();

        setSupportActionBar(topToolbarEditPhoto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_white);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        topToolbarEditPhoto.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isFilterVisible) {
//                    displayFilter(false);
//                    txtCurrentTool.setText(R.string.app_name);
//                }
                if (!photoEditor.isCacheEmpty()) {
                    showSaveDialog();
                } else {
                    onBackPressed();
                }
            }
        });
    }

    private void initViews(){
        photoEditorView = findViewById(R.id.photoEditorView);
        txtCurrentTool = findViewById(R.id.txtCurrentTool);
        rvTools = findViewById(R.id.rvConstraintTools);
        rvFilters = findViewById(R.id.rvFilterView);
        rootView = findViewById(R.id.rootView);
        topToolbarEditPhoto = findViewById(R.id.topToolbarEditPhoto);

        shapeFragment = new ShapeFragment();
        shapeBuilder = new ShapeBuilder();
        editToolAdapter = new EditToolAdapter(getBaseContext(),this);
        emojiFragment = new EmojiFragment();
        stickerFragment = new StickerFragment();
        saveFileHelper = new FileSaveHelper(this);
        eraserFragment = new EraserFragment();

        emojiFragment.setEmojiListener(this);
        stickerFragment.setStickerListener(this);
        shapeFragment.setPropertiesChangeListener(this);
        eraserFragment.setPropertiesChangeListener(this);

        rvTools.setAdapter(editToolAdapter);
        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFilters.setLayoutManager(llmFilters);
        rvFilters.setAdapter(viewFilterAdapter);

        Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
        photoEditorView.getSource().setImageBitmap(imageBitmap);

        //Use custom font using latest support library
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

        //loading font from asset
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setClipSourceImage(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

        ImageView imageUndo = findViewById(R.id.imgUndo);
        imageUndo.setOnClickListener(this);

        ImageView imageRedo = findViewById(R.id.imgRedo);
        imageRedo.setOnClickListener(this);

        ImageView imageCamera = findViewById(R.id.imgCamera);
        imageCamera.setOnClickListener(this);

        ImageView imageGallery = findViewById(R.id.imgGallery);
        imageGallery.setOnClickListener(this);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        ImageView imageShare = findViewById(R.id.imgShare);
        imageShare.setOnClickListener(this);

        imageClose = findViewById(R.id.imgClose);
        imageClose.setOnClickListener(this);
        imageClose.setVisibility(View.INVISIBLE);

//        imageCheck = findViewById(R.id.imgCheck);
//        imageCheck.setOnClickListener(this);
//        imageCheck.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onColorChanged(int colorCode) {
        photoEditor.setShape(shapeBuilder.withShapeColor(colorCode));
        txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        photoEditor.setShape(shapeBuilder.withShapeOpacity(opacity));
        txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapeSizeChanged(int shapeSize) {
        shapeFragment.setSizeBrush(shapeSize);
        photoEditor.setShape(shapeBuilder.withShapeSize(shapeSize));
        txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapePicked(ShapeType shapeType) {
        photoEditor.setShape(shapeBuilder.withShapeType(shapeType));
        txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        if(toolType == ToolType.SHAPE){
            isBrush = true;
            photoEditor.setBrushDrawingMode(true);
            photoEditor.setShape(shapeBuilder.withShapeSize(shapeFragment.getSizeBrush()));
            imageClose.setVisibility(View.VISIBLE);
            txtCurrentTool.setText(R.string.label_shape);
            showBottomSheetDialogFragment(shapeFragment);
        }else if(toolType == ToolType.TEXT){
            TextEditorFragment textEditorFragment = TextEditorFragment.show(this, "", ContextCompat.getColor(this, R.color.white));
            textEditorFragment.setOnTextEditorListener(new TextEditorFragment.TextEditorListener() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    txtCurrentTool.setText(R.string.label_text);
                    TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
                    textStyleBuilder.withTextColor(colorCode);
                    photoEditor.addText(inputText, textStyleBuilder);
                }
            });
        }else if(toolType == ToolType.ERASER){
            isErase = true;
            photoEditor.setBrushDrawingMode(true);
            photoEditor.setShape(shapeBuilder.withShapeSize(eraserFragment.getSizeEraser()));
            photoEditor.brushEraser();
            imageClose.setVisibility(View.VISIBLE);
            txtCurrentTool.setText(R.string.label_eraser_mode);
            showBottomSheetDialogFragment(eraserFragment);
        }else if(toolType == ToolType.FILTER){
            imageClose.setVisibility(View.VISIBLE);
            txtCurrentTool.setText(R.string.label_filter);
            displayFilter(true);
        }else if(toolType == ToolType.EMOJI){
            imageClose.setVisibility(View.INVISIBLE);
            showBottomSheetDialogFragment(emojiFragment);
        }else if(toolType == ToolType.STICKER){
            imageClose.setVisibility(View.INVISIBLE);
            showBottomSheetDialogFragment(stickerFragment);
        }

        if(toolType != ToolType.SHAPE && toolType != ToolType.ERASER){
            photoEditor.setBrushDrawingMode(false);
        }
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment){
        if(fragment != null && !fragment.isAdded()) fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private void displayFilter(boolean isVisible) {
        isFilterVisible = isVisible;
        constraintSet.clone(rootView);

        int rvFilterId = rvFilters.getId();

        if (isVisible) {
            constraintSet.clear(rvFilterId, ConstraintSet.START);
            constraintSet.connect(
                    rvFilterId, ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START
            );
            constraintSet.connect(
                    rvFilterId, ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END
            );
        } else {
            constraintSet.connect(
                    rvFilterId, ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END
            );
            constraintSet.clear(rvFilterId, ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(rootView, changeBounds);

        constraintSet.applyTo(rootView);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        photoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        photoEditor.addEmoji(emojiUnicode);
        txtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        photoEditor.addImage(bitmap);
        txtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    @SuppressLint({"NonConstantResourceId", "MissingPermission"})
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.imgUndo) {
            photoEditor.undo();
        } else if (viewId == R.id.imgRedo) {
            photoEditor.redo();
        } else if (viewId == R.id.btnSave) {
            saveImage();
        } else if (viewId == R.id.imgClose) {
            if(isBrush != null && isBrush){
                isBrush = false;
                txtCurrentTool.setText(R.string.app_name);
                photoEditor.setBrushDrawingMode(false);
            }
            if(isErase != null && isErase){
                isErase = false;
                txtCurrentTool.setText(R.string.app_name);
                photoEditor.setBrushDrawingMode(false);
            }
            if(isFilterVisible != null && isFilterVisible){
                displayFilter(false);
                txtCurrentTool.setText(R.string.app_name);
            }

            imageClose.setVisibility(View.INVISIBLE);
        } else if (viewId == R.id.imgShare) {
            shareImage();
        } else if (viewId == R.id.imgCamera) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else if (viewId == R.id.imgGallery) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    photoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) (data != null ? data.getExtras().get("data") : null);
                    //Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, photoEditorView.getWidth(), photoEditorView.getHeight(), false);
                    photoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    photoEditor.clearAllViews();
                    try {
                        Uri uri = (data != null) ? data.getData() : null;
                        if (uri != null) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), uri
                            );
                            photoEditorView.getSource().setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void isPermissionGranted(Boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    @SuppressLint("MissingPermission")
    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void saveImage() {
        String fileName = System.currentTimeMillis() + ".png";
        boolean hasStoragePermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()) {
            showLoading("Saving...");

            saveFileHelper.createFile(fileName, new FileSaveHelper.OnFileCreateResult() {
                @Override
                @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
                public void onFileCreateResult(boolean created, String filePath, String error, Uri uri) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                if (created && filePath != null) {
                                    SaveSettings saveSettings = new SaveSettings.Builder()
                                            .setClearViewsEnabled(true)
                                            .setTransparencyEnabled(true)
                                            .build();

                                    photoEditor.saveAsFile(filePath, new PhotoEditor.OnSaveListener() {
                                        @Override
                                        public void onSuccess(@NonNull String s) {
                                            saveFileHelper.notifyThatFileIsNowPubliclyAvailable(getContentResolver());
                                            hideLoading();
                                            displaySnackBar("Image Saved Successfully");
                                            saveImageUri = uri;
                                            photoEditorView.getSource().setImageURI(saveImageUri);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hideLoading();
                                            displaySnackBar("Failed to save Image");
                                        }
                                    });
                                } else {
                                    hideLoading();
                                    if (error != null) {
                                        displaySnackBar(error);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();
                }
            });
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void shareImage() {
        Uri saveImageUri = this.saveImageUri;
        if (saveImageUri == null) {
            displaySnackBar(getString(R.string.msg_save_image_to_share));
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(saveImageUri));
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)));
    }

    public Uri buildFileProviderUri(Uri uri) {
        if (FileSaveHelper.isSdkHigherThan28()) {
            return uri;
        }

        String path = uri.getPath();
        if (path == null) {
            throw new IllegalArgumentException("URI Path Expected");
        }

        return FileProvider.getUriForFile(
                this,
                FILE_PROVIDER_AUTHORITY,
                new File(path)
        );
    }

    @Override
    public void onEraserSizeChange(int size) {
        eraserFragment.setSizeEraser(size);
        photoEditor.setShape(shapeBuilder.withShapeSize(size));
    }
}
