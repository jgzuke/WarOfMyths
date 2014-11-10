package com.example.magegame;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ViewSwitcher;
public class StartActivity extends Activity
{
	protected AllViews currentView;
	protected Controller control;
	protected LoadingScreen loading;
	protected MenuRunner menuRun;
	protected ImageLibrary imageLibrary;
	protected double screenDimensionMultiplier;
	protected int screenMinX;
	protected int screenMinY;
	private ViewSwitcher viewSwitcher;
	private Handler mHandler = new Handler();
	private Runnable frameCaller = new Runnable()
	{
		public void run()
		{
			if(control != null)
			{
				if(control.gameRunning)
				{
					control.frameCall();
				}
			}
			if(loading != null)
			{
				if(loading.gameRunning)
				{
					loading.frameCall();
				}
			}
			mHandler.postDelayed(this, 50);
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		frameCaller.run();
        viewSwitcher = new ViewSwitcher(this);
    	setScreenDimensions();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		loading = new LoadingScreen(this, this);
		loading.setBackgroundColor(Color.BLACK);
		loading.gameRunning = true;
		changeView(loading);
    	currentView = loading;
        setContentView(viewSwitcher);
		imageLibrary = new ImageLibrary(this, this);
		menuRun = new MenuRunner(this, this);
    	menuRun.setBackgroundColor(Color.BLACK);
    	control = new Controller(this, this, imageLibrary);
    	control.setBackgroundColor(Color.BLACK);
    	control.primeFighting();
		loading.incrementPercentLoaded(30);
    }
    private void changeView(AllViews view)
    {
    	View old = viewSwitcher.getNextView();
    	viewSwitcher.removeView(old);
    	viewSwitcher.removeView(view);
    	viewSwitcher.addView(view);
    	viewSwitcher.showNext();
    }
    public void startMenu()
    {
    	menuRun.currentScreen = "main";
    	changeView(menuRun);
    	currentView = menuRun;
    	loading.gameRunning = false;
    	menuRun.gameRunning = true;
    	control.gameRunning = false;
    	control.primeFighting();
    }
    public void startFight(int playerTypeSet, int levelSet, int difficultySet)
    {
    	control.startFighting(playerTypeSet, levelSet, difficultySet);
    	currentView = control;
    	changeView(control);
    	loading.gameRunning = false;
    	menuRun.gameRunning = false;
    	control.gameRunning = true;
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
        currentView.gameRunning = true;
    }

   @Override
    public void onResume() {
        super.onResume();
        currentView.gameRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        currentView.gameRunning = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        currentView.gameRunning = false;
    }

   @Override
    public void onDestroy() {
        super.onDestroy();
        currentView.gameRunning = false;
    }
}
