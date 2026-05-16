package com.ixuea.courses.mymusic;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ixuea.courses.mymusic.component.ad.model.Ad;

/**
 * Public slim launcher. Selected feature Activities remain available from
 * source; frozen product areas are removed from this branch.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("MuseFlow Android public slim build");
        view.setPadding(48, 96, 48, 48);
        setContentView(view);
    }

    public void processAdClick(Ad data) {
        // Ads are not part of the public slim feature set.
    }
}
