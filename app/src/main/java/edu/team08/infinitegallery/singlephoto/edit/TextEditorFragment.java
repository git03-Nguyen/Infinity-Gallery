package edu.team08.infinitegallery.singlephoto.edit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.team08.infinitegallery.R;

public class TextEditorFragment extends DialogFragment {
    private EditText addTextEditText;
    private TextView addTextDoneTextView;
    private InputMethodManager inputMethodManager;
    private int colorCode;
    private TextEditorListener textEditorListener;

    private static final String TAG = TextEditorFragment.class.getSimpleName();
    private static final String EXTRA_INPUT_TEXT = "extra_input_text";
    private static final String EXTRA_COLOR_CODE = "extra_color_code";



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        // Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();

        addTextEditText = view.findViewById(R.id.add_text_edit_text);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        addTextDoneTextView = view.findViewById(R.id.add_text_done_tv);

        // Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorAdapter colorAdapter = new ColorAdapter(context);

        // This listener will change the text color when clicked on any color from picker
        colorAdapter.setOnColorPickerClickListener(new ColorAdapter.OnColorClickListener() {
            @Override
            public void onColorClickListener(Integer colorCode) {
                TextEditorFragment.this.colorCode = colorCode;
                addTextEditText.setTextColor(colorCode);
            }
        });

        addTextColorPickerRecyclerView.setAdapter(colorAdapter);

        Bundle arguments = requireArguments();

        addTextEditText.setText(arguments.getString(EXTRA_INPUT_TEXT));
        colorCode = arguments.getInt(EXTRA_COLOR_CODE);
        addTextEditText.setTextColor(colorCode);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        // Make a callback on activity when the user is done with text editing
        addTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View onClickListenerView) {
                inputMethodManager.hideSoftInputFromWindow(onClickListenerView.getWindowToken(), 0);
                dismiss();
                String inputText = addTextEditText.getText().toString();
                TextEditorListener textEditorListener = TextEditorFragment.this.textEditorListener;
                if (inputText != null && !inputText.isEmpty() && textEditorListener != null) {
                    textEditorListener.onDone(inputText, colorCode);
                }
            }
        });
    }

    // Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditorListener textEditorListener) {
        this.textEditorListener = textEditorListener;
    }

    public interface TextEditorListener {
        void onDone(String inputText, int colorCode);
    }

    public static TextEditorFragment show(
            AppCompatActivity appCompatActivity,
            String inputText,
            @ColorInt int colorCode
    ) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        TextEditorFragment fragment = new TextEditorFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }
}
