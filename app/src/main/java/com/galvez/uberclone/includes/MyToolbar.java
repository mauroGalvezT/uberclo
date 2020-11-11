package com.galvez.uberclone.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.galvez.uberclone.R;

import java.util.Objects;

public class MyToolbar {
    public static void show(AppCompatActivity activity,String tittle,boolean upButton){

        Toolbar mtoolbar=activity.findViewById(R.id.toolbarA);
        activity.setSupportActionBar(mtoolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(tittle);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }
}
