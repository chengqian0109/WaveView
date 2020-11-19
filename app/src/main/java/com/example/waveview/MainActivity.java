package com.example.waveview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.jack.widget.WaveView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll = findViewById(R.id.ll);
        WaveView waveView = new WaveView(this);
        waveView.setWaveColor(Color.CYAN);
        waveView.setAnimDelay(350);
        waveView.setWaveCount(6);
        waveView.setWaveWidth(30);
        waveView.setWaveMargin(50);
        waveView.setAnimDuration(1000);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
        ll.addView(waveView, layoutParams);
    }
}