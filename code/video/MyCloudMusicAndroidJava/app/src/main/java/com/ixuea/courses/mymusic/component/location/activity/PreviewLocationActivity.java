package com.ixuea.courses.mymusic.component.location.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PreviewLocationActivity extends AppCompatActivity {
    public static void start(Context context, Object data) {
        // Location preview is intentionally stubbed in the public slim branch.
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
