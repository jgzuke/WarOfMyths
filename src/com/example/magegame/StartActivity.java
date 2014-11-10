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
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
	private int gameCurrency;
	private int realCurrency;
	protected int wApollo = 1;
	protected int wPoseidon = 1;
	protected int wZues = 1;
	protected int wHades = 1;
	protected int wHephaestus = 1;
	protected int wAres = 1;
	protected int wAthena = 1;
	protected int wHermes = 1;
	protected int uAmbrosia = 1;
	protected int uArtemisArrow = 1;
	protected int uDionysusWine = 1;
	protected int uHestiasBlessing = 1;
	
	private ViewSwitcher viewSwitcher;
	private Handler mHandler = new Handler();
	protected boolean stickOnRight = false;
	protected boolean shootTapScreen = false;
	protected boolean shootTapDirectional = false;
	protected byte levelBeaten = 0;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 5;
	protected byte[] savedData = new byte[savePoints];
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
		changeView(loading);
    	currentView = loading;
        setContentView(viewSwitcher);
        viewSwitcher.setKeepScreenOn(true);
		imageLibrary = new ImageLibrary(this, this);
		menuRun = new MenuRunner(this, this);
    	menuRun.setBackgroundColor(Color.BLACK);
    	control = new Controller(this, this, imageLibrary);
    	control.setBackgroundColor(Color.WHITE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		read();
    	if(savedData[0] == 0)
    	{
    		savedData[0] = 1;
    		write();
    		menuRun.changeScreen("tutorial0001");
    	} else
    	{
    		menuRun.changeScreen("main");
    	}
    	changeView(menuRun);
    	currentView = menuRun;
    	menuRun.gameRunning = true;
    	startStoreMusic();
		loading = null;
    	control.primeFighting();
    }
    private void changeView(AllViews view)
    {
    	View old = viewSwitcher.getNextView();
    	viewSwitcher.removeView(old);
    	viewSwitcher.removeView(view);
    	viewSwitcher.addView(view);
    	viewSwitcher.showNext();
    }
    protected void startMenu(boolean wonRound)
    {
    	imageLibrary.recycleImages();
    	if(!wonRound)
    	{
        	menuRun.changeScreen("wonround");
    	} else
    	{
    		menuRun.changeScreen("lostround");
    	}
    	changeView(menuRun);
    	currentView = menuRun;
    	menuRun.gameRunning = true;
    	control.gameRunning = false;
    	startStoreMusic();
    	control.primeFighting();
    }
    protected void startFight(int playerTypeSet, int levelSet, int difficultySet)
    {
    	imageLibrary.loadAllImages();
    	control.startFighting(playerTypeSet, levelSet, difficultySet);
    	currentView = control;
    	changeView(control);
    	menuRun.gameRunning = false;
    	control.gameRunning = true;
    	startMusic();
    }
	protected void setScreenDimensions()
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
	protected void startMusic()
	{
		stopMusic();
        backMusic= MediaPlayer.create((Context)this, R.raw.archangel);
        backMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer backMusic) {
                backMusic.start();
            }
        });
        backMusic.setLooping(true);
	}
	protected void startStoreMusic()
	{
		stopMusic();
        backMusic= MediaPlayer.create((Context)this, R.raw.heart);
        backMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer backMusic) {
                backMusic.start();
            }
        });
        backMusic.setLooping(true);
	}
	protected void playEffect(int ID)
	{
		soundID = spool.load((Context)this, ID, 1);
		spool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
				spool.play(soundID, 1, 1, 1, 0, 1f);
		    }
		});
	}
	protected void stopMusic()
	{
		if(backMusic != null)
		{
			backMusic.stop();
        	backMusic.release();
        	backMusic = null;
		}
	}
    protected void buyGame(String toBuy)
    {
    	gameCurrency -= buy(toBuy, gameCurrency);
    }
    protected void buyReal(String toBuy)
    {
    	realCurrency -= buy(toBuy, realCurrency);
    }
	protected int buy(String toBuy, int currency)
    {
    	int ID = getItemID(toBuy);
    	double cost = 0;
    	switch (ID)
    	{
    	case 1:
    		cost = Math.pow(4*wApollo, 2);
    		if(currency>=cost)
    		{
    			wApollo += 0.1;
    		}
    		break;
    	case 2:
    		cost = Math.pow(4*wPoseidon, 2);
    		if(currency>=cost)
    		{
    			wPoseidon += 0.1;
    		}
    		break;
    	case 3:
    		cost = Math.pow(4*wZues, 2);
    		if(currency>=cost)
    		{
    			wZues += 0.1;
    		}
    		break;
    	case 4:
    		cost = Math.pow(4*wHades, 2);
    		if(currency>=cost)
    		{
    			wHades += 0.1;
    		}
    		break;
    	case 5:
    		cost = Math.pow(4*wHephaestus, 2);
    		if(currency>=cost)
    		{
    			wHephaestus += 0.1;
    		}
    		break;
    	case 6:
    		cost = Math.pow(4*wAres, 2);
    		if(currency>=cost)
    		{
    			wAres += 0.1;
    		}
    		break;
    	case 7:
    		cost = Math.pow(4*wAthena, 2);
    		if(currency>=cost)
    		{
    			wAthena += 0.1;
    		}
    		break;
    	case 8:
    		cost = Math.pow(4*wHermes, 2);
    		if(currency>=cost)
    		{
    			wHermes += 0.1;
    		}
    		break;
    	case 9:
    		cost = 20;
    		if(currency>=cost)
    		{
    			uAmbrosia ++;
    		}
    		break;
    	case 10:
    		cost = 20;
    		if(currency>=cost)
    		{
    			uArtemisArrow ++;
    		}
    		break;
		case 11:
			cost = 20;
    		if(currency>=cost)
    		{
    			uDionysusWine ++;
    		}
			break;
		case 12:
			cost = 20;
    		if(currency>=cost)
    		{
    			uHestiasBlessing ++;
    		}
			break;
    	}
    	if(cost == 0)
    	{
    		//playEffect(R.raw.nomoney);
    	}
    	return (int)cost;
    }
    protected int getItemID(String toBuy)
    {
    	int ID = 0;
    	if(toBuy.equals("wApollo"))
    	{
    		ID = 1;
    	} else if(toBuy.equals("wPoseidon"))
    	{
    		ID = 2;
    	} else if(toBuy.equals("wZues"))
    	{
    		ID = 3;
    	} else if(toBuy.equals("wHades"))
    	{
    		ID = 4;
    	} else if(toBuy.equals("wHephaestus"))
    	{
    		ID = 5;
    	} else if(toBuy.equals("wAres"))
    	{
    		ID = 6;
    	} else if(toBuy.equals("wAthena"))
    	{
    		ID = 7;
    	} else if(toBuy.equals("wHermes"))
    	{
    		ID = 8;
    	} else if(toBuy.equals("uAmbrosia"))
    	{
    		ID = 9;
    	} else if(toBuy.equals("uArtemisArrow"))
    	{
    		ID = 10;
    	} else if(toBuy.equals("uDionysusWine"))
    	{
    		ID = 11;
    	} else if(toBuy.equals("uHestiasBlessing"))
    	{
    		ID = 12;
    	}
    	return ID;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    public void winFight(int difficultyLevel)
    {
    	
    }
    @Override
    public void onStart() {
        super.onStart();
    }

   @Override
    public void onResume() {
        super.onResume();
        read();
        stickOnRight = !(savedData[1] == 1);
    	shootTapScreen = savedData[2] == 1;
    	shootTapDirectional = !(savedData[3] == 1);
    	levelBeaten = savedData[4];
        currentView.gameRunning = true;
        if(control != null)
        {
        	if(control.gameRunning)
        	{
        		startMusic();
        		imageLibrary.loadAllImages();
        	} else
        	{
        		startStoreMusic();
        	}
        } else
        {
        	startStoreMusic();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        savedData[1] = 1;
        savedData[2] = 0;
        savedData[3] = 1;
        if(stickOnRight) savedData[1] = 0;
        if(shootTapScreen) savedData[2] = 1;
        if(shootTapDirectional) savedData[3] = 0;
        savedData[4] = levelBeaten;
    	write();
        currentView.gameRunning = false;
        stopMusic();
        imageLibrary.recycleImages();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

   @Override
    public void onDestroy() {
        super.onDestroy();
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
