package com.ixuea.courses.mymusic.component.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ixuea.courses.mymusic.util.Constant;

public class UserActivity extends AppCompatActivity {
    public static void start(Context context, int style) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(Constant.STYLE, style);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
