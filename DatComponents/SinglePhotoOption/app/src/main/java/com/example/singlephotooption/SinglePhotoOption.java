package com.example.singlephotooption;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class SinglePhotoOption extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo_option);

        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        View view = getSupportActionBar().getCustomView();
//        TextView name = view.findViewById(R.id.name);
    }
}