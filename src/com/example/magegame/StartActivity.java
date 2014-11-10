package com.example.magegame;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
public class StartActivity extends Activity
{
	private Controller control;
	private Game game;
	private double screenDimensionMultiplier;
	private int screenMinX;
	private int screenMinY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setScreenDimensions();
		game = new Game(this);
    	control = new Controller(this, game, 2, 3, 4, screenDimensionMultiplier, screenMinX, screenMinY);
    	control.setBackgroundColor(Color.WHITE);
        setContentView(control);
    }

	public void setScreenDimensions()
	{
		int dimension1 = getResources().getDisplayMetrics().heightPixels;
		int dimension2 = getResources().getDisplayMetrics().widthPixels;
		double screenWidthstart;
		double screenHeightstart;
		double ratio;
		if(dimension1 > dimension2)
		{
			screenWidthstart = dimension1;
			screenHeightstart = dimension2;
		} else
		{
			screenWidthstart = dimension2;
			screenHeightstart = dimension1;
		}
		ratio = (double)(screenWidthstart/screenHeightstart);
		if(ratio > 1.5)
		{
			screenMinX = (int)(screenWidthstart - (screenHeightstart*1.5))/2;
			screenMinY = 0;
			screenDimensionMultiplier = ((screenHeightstart*1.5)/480);
		} else
		{
			screenMinY = (int)(screenHeightstart - (screenWidthstart/1.5))/2;
			screenMinX = 0;
			screenDimensionMultiplier = ((screenWidthstart/1.5)/320);
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        control.gameRunning = true;
    }

   @Override
    public void onResume() {
        super.onResume();
        control.gameRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        control.gameRunning = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        control.gameRunning = false;
    }

   @Override
    public void onDestroy() {
        super.onDestroy();
        control.gameRunning = false;
    }
   
}
