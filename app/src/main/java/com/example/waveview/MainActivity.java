package com.example.waveview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.jack.widget.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll = findViewById(R.id.ll);
        mWaveView = new WaveView(this);
        mWaveView.setWaveColor(Color.CYAN);
        mWaveView.setAnimDelay(350);
        mWaveView.setWaveCount(6);
        mWaveView.setWaveWidth(30);
        mWaveView.setWaveGravity(WaveView.Gravity.BOTTOM);
        mWaveView.setWaveMargin(50);
        mWaveView.setAnimDuration(1000);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
        ll.addView(mWaveView, layoutParams);
    }

    public void set(View view) {
        mWaveView.setWaveWidth(15);
        mWaveView.setAnimDuration(240);
        mWaveView.setAnimDelay(1500);
        mWaveView.setWaveMinRatio(0.1f);
    }
}