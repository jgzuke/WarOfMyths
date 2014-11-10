package com.example.magegame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
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
	protected boolean stickOnRight = true;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 10;
	private byte[] savedData = new byte[savePoints];
	private MediaPlayer backMusic;
	private SoundPool spool;
	private AudioManager audioManager;
	private float volume;
	private int soundID;
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
			if(menuRun != null)
			{
				if(menuRun.gameRunning)
				{
					menuRun.frameCall();
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
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
    private void changeView(AllViews view)
    {
    	View old = viewSwitcher.getNextView();
    	viewSwitcher.removeView(old);
    	viewSwitcher.removeView(view);
    	viewSwitcher.addView(view);
    	viewSwitcher.showNext();
    }
    public void startTutorial()
    {
        read();
    }
    public void startMenu()
    {
    	read();
    	if(savedData[0] == 0)
    	{
    		startTutorial();
    		savedData[0] = 1;
    		write();
    		menuRun.currentScreen = "tutorial0001";
    	} else
    	{
	    	menuRun.currentScreen = "main";
    	}
    	menuRun.now = menuRun.loadImage("main");
    	changeView(menuRun);
    	currentView = menuRun;
    	//stickOnRight = !stickOnRight;
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
	public void startMusic()
	{
        backMusic= MediaPlayer.create((Context)this, R.raw.backsound);
        backMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer backMusic) {
                backMusic.start();
            }
        });
        backMusic.setLooping(true);
	}
	public void playEffect(int ID)
	{
		soundID = spool.load((Context)this, ID, 1);
		spool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
				spool.play(soundID, 1, 1, 1, 0, 1f);
		    }
		});
	}
	public void stopMusic()
	{
		if(backMusic != null)
		{
			backMusic.stop();
        	backMusic.release();
        	backMusic = null;
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
        startMusic();
    }

   @Override
    public void onResume() {
        super.onResume();
        currentView.gameRunning = true;
        startMusic();
    }

    @Override
    public void onPause() {
        super.onPause();
        currentView.gameRunning = false;
        stopMusic();
    }

    @Override
    public void onStop() {
        super.onStop();
        currentView.gameRunning = false;
        stopMusic();
    }

   @Override
    public void onDestroy() {
        super.onDestroy();
        currentView.gameRunning = false;
        stopMusic();
    }
   private void read()
   {
	   openRead();
	   try {
		   fileRead.read(savedData, 0, savePoints);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   closeRead();
   }
   private void write()
   {
	   openWrite();
	   	try {
	   		fileWrite.write(savedData, 0, savePoints);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	closeWrite();
   }
   private void openRead()
   {
   	try {
			fileRead = openFileInput("ProjectSaveData");
		} catch (FileNotFoundException e) {
			openWrite();
			closeWrite();
			openRead();
		}
   }
   private void openWrite()
   {
   	try {
			fileWrite = openFileOutput("ProjectSaveData", Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}
   }
   private void closeRead()
   {
   	try {
			fileRead.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   private void closeWrite()
   {
   	try {
			fileWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   
}
