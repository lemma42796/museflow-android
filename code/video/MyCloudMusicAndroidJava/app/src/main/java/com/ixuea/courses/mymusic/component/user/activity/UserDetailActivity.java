package com.ixuea.courses.mymusic.component.user.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserDetailActivity extends AppCompatActivity {
    public static void startWithId(Context context, String id) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    public static void startWithNickname(Context context, String nickname) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra("nickname", nickname);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }
}
