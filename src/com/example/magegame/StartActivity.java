package com.example.magegame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ViewSwitcher;
public class StartActivity extends Activity
{
	protected Controller control;
	protected double screenDimensionMultiplier;
	protected int screenMinX;
	protected int screenMinY;
	protected int gameCurrency=5;
	protected int realCurrency=5;
	protected byte wApollo = 10;
	protected byte wPoseidon = 10;
	protected byte wZues = 10;
	protected byte wHades = 10;
	protected byte wHephaestus = 10;
	protected byte wAres = 10;
	protected byte wAthena = 10;
	protected byte wHermes = 10;
	protected byte pHeal = 1;
	protected byte pCool = 1;
	protected byte pWater = 1;
	protected byte pEarth = 1;
	protected byte pAir = 1;
	protected byte pFire = 1;
	protected boolean stickOnRight = false;
	protected boolean shootTapScreen = false;
	protected boolean shootTapDirectional = true;
	protected byte levelBeaten = 0;
	protected boolean gameRunning = true;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 26;
	protected byte[] savedData = new byte[savePoints];
	protected MediaPlayer backMusic;
	private SoundPool spool;
	private int[] soundPoolMap = new int[13];
	protected AudioManager audioManager;
	protected byte playerType = 0;
	protected double volumeMusic = 127;
	protected double volumeEffect = 127;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		read();
		boolean firstTime = false;
		if(savedData[0] == 0)
    	{
    		savedData[0] = 1;
    		setSaveData();
    		write();
    		firstTime = true;
    	} else
    	{
    		readSaveData();
    	}
        setWindowAndAudio();
        setScreenDimensions();
		control = new Controller(this, this);
        setContentView(control);
        startMusic();
    	if(firstTime)
    	{
    		startFight(2);
    	}
    	control.changePlayOptions();
    }
    protected void setWindowAndAudio()
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    	soundPoolMap[0] = spool.load(this, R.raw.shoot_burn, 1);
    	soundPoolMap[1] = spool.load(this, R.raw.shoot_electric, 1);
    	soundPoolMap[2] = spool.load(this, R.raw.shoot_water, 1);
    	soundPoolMap[3] = spool.load(this, R.raw.shoot_earth, 1);
    	soundPoolMap[4] = spool.load(this, R.raw.shoot_burst, 1);
    	soundPoolMap[5] = spool.load(this, R.raw.shoot_shoot, 1);
    	soundPoolMap[6] = spool.load(this, R.raw.shoot_teleport, 1);
    	soundPoolMap[7] = spool.load(this, R.raw.enemy_sword1, 1);
    	soundPoolMap[8] = spool.load(this, R.raw.enemy_sword2, 1);
    	soundPoolMap[9] = spool.load(this, R.raw.enemy_swordmiss, 1);
    	soundPoolMap[10] = spool.load(this, R.raw.enemy_arrowhit, 1);
    	soundPoolMap[11] = spool.load(this, R.raw.enemy_arrowrelease, 1);
    	soundPoolMap[12] = spool.load(this, R.raw.effect_pageflip, 1);
    }
    protected void startMenu()
    {
    	control.drainHp = false;
    	control.lowerHp = false;
    	control.limitSpells = false;
    	control.enemyRegen = false;
    	control.changeDifficulty(10);
    	control.imageLibrary.directionsTutorial = control.imageLibrary.loadImage("menu_directions", 235, 140);
    	control.startFighting(10);
    	control.gamePaused = false;
    }
    protected void loseFight()
    {
    	control.gamePaused = true;
    	control.currentPause = "lost";
    	control.invalidate();
    }
    protected void winFight()
    {
        if((int)(control.levelNum/10)-2 == levelBeaten)
       	{
        	levelBeaten++;
       	}
        control.gamePaused = true;
    	control.currentPause = "won";
    	control.invalidate();
    }
    protected void startFight(int levelSet)
    {
    	control.startFighting(levelSet*10);
    	control.gamePaused = false;
    	if(levelSet==2)
    	{
    		control.player.getPowerUp(2);
    	}
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
        backMusic= MediaPlayer.create((Context)this, R.raw.busqueda);
        backMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer backMusic) {
                backMusic.start();
            }
        });
        backMusic.setLooping(true);
        float systemVolume= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float) (systemVolume*volumeMusic/127);
		backMusic.setVolume(systemVolume, systemVolume);
	}
	protected void playEffect(String toPlay)
	{
		float newV= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		newV = (float)(newV*volumeEffect/127);
		if(toPlay.equals("burn"))spool.play(soundPoolMap[0], newV, newV, 1, 0, 1f);
		if(toPlay.equals("electric"))spool.play(soundPoolMap[1], newV, newV, 1, 0, 1f);
		if(toPlay.equals("water"))spool.play(soundPoolMap[2], newV, newV, 1, 0, 1f);
		if(toPlay.equals("earth"))spool.play(soundPoolMap[3], newV, newV, 1, 0, 1f);
		if(toPlay.equals("burst"))spool.play(soundPoolMap[4], newV, newV, 1, 0, 1f);
		if(toPlay.equals("shoot"))spool.play(soundPoolMap[5], newV, newV, 1, 0, 1f);
		if(toPlay.equals("teleport"))spool.play(soundPoolMap[6], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword1"))spool.play(soundPoolMap[7], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword2"))spool.play(soundPoolMap[8], newV, newV, 1, 0, 1f);
		if(toPlay.equals("swordmiss"))spool.play(soundPoolMap[9], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowhit"))spool.play(soundPoolMap[10], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowrelease"))spool.play(soundPoolMap[11], newV, newV, 1, 0, 1f);
		if(toPlay.equals("pageflip"))spool.play(soundPoolMap[12], newV, newV, 1, 0, 1f);
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
    	gameCurrency -= buy(toBuy, gameCurrency, true);
    	saveGame();
    }
    protected void buyReal(String toBuy)
    {
    	realCurrency -= buy(toBuy, realCurrency, true);
    	saveGame();
    }
    protected boolean canBuyGame(String toBuy)
    {
    	return !(buy(toBuy, gameCurrency, false)==0);
    }
    protected boolean canBuyReal(String toBuy)
    {
    	return !(buy(toBuy, realCurrency, false)==0);
    }
	protected int buy(String toBuy, int currency, boolean buying)
    {
    	int ID = getItemID(toBuy);
    	double cost = 0;
    	boolean afforded = false;
    	switch (ID)
    	{
    	case 1:
    		cost = Math.pow(wApollo, 2.5)*6/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wApollo ++;
    			afforded = true;
    		}
    		break;
    	case 2:
    		cost = Math.pow(wPoseidon, 2.5)*6/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wPoseidon ++;
    			afforded = true;
    		}
    		break;
    	case 3:
    		cost = Math.pow(wZues, 2.5)*6/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wZues ++;
    			afforded = true;
    		}
    		break;
    	case 4:
    		cost = Math.pow(wHades, 2.5)*6/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wHades ++;
    			afforded = true;
    		}
    		break;
    	case 5:
    		cost = Math.pow(wHephaestus, 2.5)*10/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wHephaestus ++;
    			afforded = true;
    		}
    		break;
    	case 6:
    		cost = Math.pow(wAres, 2.5)*10/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wAres ++;
    			afforded = true;
    		}
    		break;
    	case 7:
    		cost = Math.pow(wAthena, 2.5)*10/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wAthena ++;
    			afforded = true;
    		}
    		break;
    	case 8:
    		cost = Math.pow(wHermes, 2.5)*10/15.8;
    		if(currency>=cost)
    		{
    			if(buying)wHermes ++;
    			afforded = true;
    		}
    		break;
    	case 9:
    		cost = 200;
    		if(currency>=cost)
    		{
    			if(buying)pHeal ++;
    			afforded = true;
    		}
    		break;
    	case 10:
    		cost = 200;
    		if(currency>=cost)
    		{
    			if(buying)pCool ++;
    			afforded = true;
    		}
    		break;
		case 11:
			cost = 200;
    		if(currency>=cost)
    		{
    			if(buying)pWater ++;
    			afforded = true;
    		}
			break;
		case 12:
			cost = 200;
    		if(currency>=cost)
    		{
    			if(buying)pEarth ++;
    			afforded = true;
    		}
			break;
		case 13:
			cost = 200;
    		if(currency>=cost)
    		{
    			if(buying)pAir ++;
    			afforded = true;
    		}
			break;
		case 14:
			cost = 200;
    		if(currency>=cost)
    		{
    			if(buying)pFire ++;
    			afforded = true;
    		}
			break;
    	}
    	if(!afforded)
    	{
    		cost = 0;
    		//playEffect(R.raw.nomoney);
    	}
    	return (int)cost;
    }
    protected int getItemID(String toBuy)
    {
    	int ID = 0;
    	if(toBuy.equals("Worship Apollo"))
    	{
    		ID = 1;
    	} else if(toBuy.equals("Worship Posiedon"))
    	{
    		ID = 2;
    	} else if(toBuy.equals("Worship Zues"))
    	{
    		ID = 3;
    	} else if(toBuy.equals("Worship Hades"))
    	{
    		ID = 4;
    	} else if(toBuy.equals("Worship Hephaestus"))
    	{
    		ID = 5;
    	} else if(toBuy.equals("Worship Ares"))
    	{
    		ID = 6;
    	} else if(toBuy.equals("Worship Athena"))
    	{
    		ID = 7;
    	} else if(toBuy.equals("Worship Hermes"))
    	{
    		ID = 8;
    	} else if(toBuy.equals("Ambrosia"))
    	{
    		ID = 9;
    	} else if(toBuy.equals("Cooldown"))
    	{
    		ID = 10;
    	} else if(toBuy.equals("Posiedon's Shell"))
    	{
    		ID = 11;
    	} else if(toBuy.equals("Hades' Helm"))
    	{
    		ID = 12;
    	} else if(toBuy.equals("Zues's Armor"))
    	{
    		ID = 13;
    	} else if(toBuy.equals("Apollo's Flame"))
    	{
    		ID = 14;
    	}
    	return ID;
    }
    protected String[] getItemDescribe(String toBuy)
    {
    	String[] describe = new String[2];
    	if(toBuy.equals("Worship Apollo"))
    	{
    		describe[0] = "Damage modifier increased while";
    		describe[1] = "fighting under Apollo";
    	} else if(toBuy.equals("Worship Posiedon"))
    	{
    		describe[0] = "Attack cooldown speed increased";
    		describe[1] = "while fighting under Posiedon";
    	} else if(toBuy.equals("Worship Zues"))
    	{
    		describe[0] = "Movement speed and roll cooldown";
    		describe[1] = "increased while fighting under Zues";
    	} else if(toBuy.equals("Worship Hades"))
    	{
    		describe[0] = "Damage reduction increased while";
    		describe[1] = "fighting under Hades";
    	} else if(toBuy.equals("Worship Hephaestus"))
    	{
    		describe[0] = "Increases health during battles";
    		describe[1] = "";
    	} else if(toBuy.equals("Worship Ares"))
    	{
    		describe[0] = "Increases damage dealt during";
    		describe[1] = "battles";
    	} else if(toBuy.equals("Worship Athena"))
    	{
    		describe[0] = "Decreases cooldown time for all";
    		describe[1] = "attacks or spells during battles";
    	} else if(toBuy.equals("Worship Hermes"))
    	{
    		describe[0] = "Increases movement speed and";
    		describe[1] = "decreases roll cooldown time";
    	} else if(toBuy.equals("Ambrosia"))
    	{
    		describe[0] = "Heals half of players max Hp, ";
    		describe[1] = "stopping at full";
    	} else if(toBuy.equals("Cooldown"))
    	{
    		describe[0] = "Teleport, burst and roll";
    		describe[1] = "cooldowns set to full";
    	} else if(toBuy.equals("Posiedon's Shell"))
    	{
    		describe[0] = "Fight under Posiedon for a short";
    		describe[1] = "length of time, reducing cooldowns";
    	} else if(toBuy.equals("Hades' Helm"))
    	{
    		describe[0] = "Fight under Hades for a short";
    		describe[1] = "length of time, increasing armor";
    	} else if(toBuy.equals("Zues's Armor"))
    	{
    		describe[0] = "Fight under Zues for a short length";
    		describe[1] = "of time, increasing movement speed";
    	} else if(toBuy.equals("Apollo's Flame"))
    	{
    		describe[0] = "Fight under Apollo for a short";
    		describe[1] = "length of time, increasing damage";
    	}
    	return describe;
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
    protected String correctDigits(String end, int digits)
	{
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
    protected void saveGame()
	{
		setSaveData();
		write();
		readSaveData();
	}
    @Override
    public void onStart() {
        super.onStart();
    }
    public void setSaveData()
    {
    	savedData[1] = 1;
        savedData[2] = 0;
        savedData[3] = 1;
        if(stickOnRight) savedData[1] = 0;
        if(shootTapScreen) savedData[2] = 1;
        if(shootTapDirectional) savedData[3] = 0;
        savedData[4] = levelBeaten;
        savedData[5] = (byte)(wApollo);
        savedData[6] = (byte)(wPoseidon);
        savedData[7] = (byte)(wZues);
        savedData[8] = (byte)(wHades);
        savedData[9] = (byte)(wHephaestus);
        savedData[10] = (byte)(wAres);
        savedData[11] = (byte)(wAthena);
        savedData[12] = (byte)(wHermes);
        savedData[13] = pHeal;
        savedData[14] = pCool;
        savedData[15] = pWater;
        savedData[16] = pEarth;
        savedData[17] = pAir;
        savedData[18] = pFire;
        if(control!=null)
        {
        	savedData[23] = (byte)control.playerType;
        } else
        {
        	savedData[23] = playerType;
        }
        savedData[24] = (byte)((int)volumeMusic);
        savedData[25] = (byte)((int)volumeEffect);
        String temp = correctDigits(Integer.toBinaryString(gameCurrency), 14);
        String tempLow = temp.substring(7);
        String tempHigh = temp.substring(0, 7);
        byte low = (byte)Integer.parseInt(tempLow, 2);
        byte high = (byte)Integer.parseInt(tempHigh, 2);
        savedData[19] = high;
        savedData[20] = low;
        temp = correctDigits(Integer.toBinaryString(realCurrency), 14);
        tempLow = correctDigits(temp.substring(7), 8);
        tempHigh = correctDigits(temp.substring(0, 7), 8);
        low = (byte)Integer.parseInt(tempLow, 2);
        high = (byte)Integer.parseInt(tempHigh, 2);
        savedData[21] = (byte)high;
        savedData[22] = (byte)low;
    }
    public void readSaveData()
    {
    	stickOnRight = savedData[1] == 0;
    	shootTapScreen = savedData[2] == 1;
    	shootTapDirectional = savedData[3] == 0;
    	levelBeaten = savedData[4];
    	wApollo = savedData[5];
    	wPoseidon = savedData[6];
    	wZues = savedData[7];
    	wHades = savedData[8];
    	wHephaestus = savedData[9];
    	wAres = savedData[10];
    	wAthena = savedData[11];
    	wHermes = savedData[12];
    	pHeal = savedData[13];
    	pCool = savedData[14];
    	pWater = savedData[15];
    	pEarth = savedData[16];
    	pAir = savedData[17];
    	pFire = savedData[18];
    	playerType = savedData[23];
    	gameCurrency = savedData[20]+(128*savedData[19]);
    	realCurrency = savedData[22]+(128*savedData[21]);
    	volumeMusic = savedData[24];
        volumeEffect = savedData[25];
    }
   @Override
    public void onResume() {
        super.onResume();
        read();
        if(savedData[0] == 1)
    	{
	        readSaveData();
    	}
        startMusic();
        control.imageLibrary.loadAllImages();
        gameRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        setSaveData();
    	write();
        stopMusic();
        control.imageLibrary.recycleImages();
        gameRunning = false;
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
