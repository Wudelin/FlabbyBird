package com.wdl.flabbybird;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.wdl.flabbybird.view.GameFlabbyBirdView;

public class MainActivity extends AppCompatActivity
{

    private GameFlabbyBirdView mGame;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGame = new GameFlabbyBirdView(this);
        setContentView(mGame);
    }
}
