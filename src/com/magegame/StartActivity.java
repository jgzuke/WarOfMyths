package com.magegame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.magegame.R;
import com.magegame.util.IabHelper;
import com.magegame.util.IabResult;
import com.magegame.util.Inventory;
import com.magegame.util.Purchase;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
	protected int gameCurrency = 200;
	protected int realCurrency = 200;
	protected byte wApollo = 8;
	protected byte wPoseidon = 8;
	protected byte wZues = 8;
	protected byte wHades = 8;
	protected byte wHephaestus = 8;
	protected byte wAres = 8;
	protected byte wAthena = 8;
	protected byte wHermes = 8;
	protected byte wHera = 8;
	protected byte pHeal = 1;
	protected byte pCool = 1;
	protected byte pWater = 1;
	protected byte pEarth = 1;
	protected byte pAir = 1;
	protected byte pFire = 1;
	protected boolean stickOnRight = false;
	protected boolean shootTapScreen = false;
	protected boolean shootTapDirectional = true;
	protected boolean holdShoot = true;
	protected byte levelBeaten = 10;
	protected boolean gameRunning = true;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 40;
	protected byte[] savedData = new byte[savePoints];
	protected MediaPlayer backMusic;
	private SoundPool spool;
	private int[] soundPoolMap = new int[13];
	protected AudioManager audioManager;
	protected byte playerType = 0;
	protected double volumeMusic = 127;
	protected double volumeEffect = 127;
	private String TAG = "game";
	private String SKU_100 = "platinum_100";
	private String SKU_250 = "platinum_250";
	private String SKU_1000 = "platinum_1000";
	private String SKU_5000 = "platinum_5000";
	private int RC_REQUEST = 10001;
	IabHelper mHelper;@
	Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		read();
		boolean firstTime = false;
		if(savedData[0] == 0)
		{
			savedData[0] = 1;
			setSaveData();
			write();
			firstTime = true;
		}
		else
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
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGLP+ZTiqZSbr1GJ7dWrRTBeju8IFdqWFNfejT8fAFcnxptJUsrtqpNdkaJJpEIZbT8XkcGcI3kwOhEfepJDjARZ+k6JFsHc3xPqaT2ACyfctAeUfBIHJA1PWxwnsbfxQIg0fv9lbfJaO9E7KphhtqE51jqSKnnO013sGbqi4QoZL1Ov/6f0pOv2TRnpN7XNbr/EGlUa9AKkyxlWmlxhlJowb03Kwh8e0uUs+kJjRzy+aNdGsNZRDwforn2XLZd9du9CTJ9K65K9/sUVgn5Zkj4bVYK8Y1CkMdiPBJAgz/v9Zh6FVJTTCa1LQmwMI6rZfvCWs6LrcmItis+4U0M9cwIDAQAB";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		// enable debug logging (for a production application, you should set this to false).
		mHelper.enableDebugLogging(true);
		// TODO change this when ready to release
		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
		{
			public void onIabSetupFinished(IabResult result)
			{
				Log.d(TAG, "Setup finished.");
				if(!result.isSuccess())
				{
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}
				// Have we been disposed of in the meantime? If so, quit.
				if(mHelper == null) return;
				// IAB is fully set up. Now, let's get an inventory of stuff we own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
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
		control.imageLibrary.directionsTutorial = control.imageLibrary.loadImage("menu_directions", 200, 180);
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
		if((int)(control.levelNum / 10) - 2 == levelBeaten)
		{
			levelBeaten++;
		}
		/*control.gamePaused = true;
		control.currentPause = "won";
		control.invalidate();*/
		/*main.startingLevel =(int)(main.levelNum/10)-1;
		main.gamePaused = true;
		main.currentPause = "startfight";
		main.invalidate();*/
		control.startingLevel =(int)(control.levelNum/10)-1;
		if(control.difficultyLevel == 10)
		{
			control.moneyMultiplier = 5;
		}
		if(control.difficultyLevel == 6)
		{
			control.moneyMultiplier = 7;
		}
		if(control.difficultyLevel == 3)
		{
			control.moneyMultiplier = 12;
		}
		if(control.difficultyLevel == 0)
		{
			control.moneyMultiplier = 20;
		}
		control.moneyMultiplier = (int)((double)control.moneyMultiplier*control.getLevelWinningsMultiplier(control.startingLevel));
		if(control.drainHp)
		{
			control.moneyMultiplier *= 1.4;
		}
		if(control.lowerHp)
		{
			control.moneyMultiplier *= 1.4;
		}
		if(control.limitSpells)
		{
			control.moneyMultiplier *= 1.4;
		}
		if(control.enemyRegen)
		{
			control.moneyMultiplier *= 1.4;
		}
		startFight(control.startingLevel+2);
	}
	protected void startFight(int levelSet)
	{
		control.startFighting(levelSet * 10);
		control.gamePaused = false;
		if(levelSet == 2)
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
		}
		else
		{
			screenWidthstart = dimension2;
			screenHeightstart = dimension1;
		}
		ratio = (double)(screenWidthstart / screenHeightstart);
		if(ratio > 1.5)
		{
			screenMinX = (int)(screenWidthstart - (screenHeightstart * 1.5)) / 2;
			screenMinY = 0;
			screenDimensionMultiplier = ((screenHeightstart * 1.5) / 480);
		}
		else
		{
			screenMinY = (int)(screenHeightstart - (screenWidthstart / 1.5)) / 2;
			screenMinX = 0;
			screenDimensionMultiplier = ((screenWidthstart / 1.5) / 320);
		}
	}
	protected void startMusic()
	{
		stopMusic();
		backMusic = MediaPlayer.create((Context) this, R.raw.busqueda);
		backMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{@
			Override
			public void onPrepared(MediaPlayer backMusic)
			{
				backMusic.start();
			}
		});
		backMusic.setLooping(true);
		float systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float)(systemVolume * volumeMusic / 127);
		backMusic.setVolume(systemVolume, systemVolume);
	}
	protected void playEffect(String toPlay)
	{
		float newV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		newV = (float)(newV * volumeEffect / 127);
		if(toPlay.equals("burn")) spool.play(soundPoolMap[0], newV, newV, 1, 0, 1f);
		if(toPlay.equals("electric")) spool.play(soundPoolMap[1], newV, newV, 1, 0, 1f);
		if(toPlay.equals("water")) spool.play(soundPoolMap[2], newV, newV, 1, 0, 1f);
		if(toPlay.equals("earth")) spool.play(soundPoolMap[3], newV, newV, 1, 0, 1f);
		if(toPlay.equals("burst")) spool.play(soundPoolMap[4], newV, newV, 1, 0, 1f);
		if(toPlay.equals("shoot")) spool.play(soundPoolMap[5], newV, newV, 1, 0, 1f);
		if(toPlay.equals("teleport")) spool.play(soundPoolMap[6], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword1")) spool.play(soundPoolMap[7], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword2")) spool.play(soundPoolMap[8], newV, newV, 1, 0, 1f);
		if(toPlay.equals("swordmiss")) spool.play(soundPoolMap[9], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowhit")) spool.play(soundPoolMap[10], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowrelease")) spool.play(soundPoolMap[11], newV, newV, 1, 0, 1f);
		if(toPlay.equals("pageflip")) spool.play(soundPoolMap[12], newV, newV, 1, 0, 1f);
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
		return !(buy(toBuy, gameCurrency, false) == 0);
	}
	protected boolean canBuyReal(String toBuy)
	{
		return !(buy(toBuy, realCurrency, false) == 0);
	}
	protected int buy(String toBuy, int currency, boolean buying)
	{
		int ID = getItemID(toBuy);
		int cost = 0;
		boolean afforded = false;
		switch(ID)
		{
		case 1:
			cost = (int)(Math.pow(wApollo, 2.8)/3.15);
			if(currency >= cost)
			{
				if(buying) wApollo++;
				control.changePlayerType();
				afforded = true;
			}
			break;
		case 2:
			cost = (int)(Math.pow(wPoseidon, 2.8)/3.15);
			if(currency >= cost)
			{
				if(buying) wPoseidon++;
				control.changePlayerType();
				afforded = true;
			}
			break;
		case 3:
			cost = (int)(Math.pow(wZues, 2.8)/3.15);
			if(currency >= cost)
			{
				if(buying) wZues++;
				control.changePlayerType();
				afforded = true;
			}
			break;
		case 4:
			cost = (int)(Math.pow(wHades, 2.8)/3.15);
			if(currency >= cost)
			{
				if(buying) wHades++;
				control.changePlayerType();
				afforded = true;
			}
			break;
		case 5:
			cost = (int)(Math.pow(wHephaestus, 2.8)/1.123);
			if(currency >= cost)
			{
				if(buying) wHephaestus++;
				afforded = true;
			}
			break;
		case 6:
			cost = (int)(Math.pow(wAres, 2.8)/1.123);
			if(currency >= cost)
			{
				if(buying) wAres++;
				afforded = true;
			}
			break;
		case 7:
			cost = (int)(Math.pow(wAthena, 2.8)/1.123);
			if(currency >= cost)
			{
				if(buying) wAthena++;
				afforded = true;
			}
			break;
		case 8:
			cost = (int)(Math.pow(wHermes, 2.8)/1.123);
			if(currency >= cost)
			{
				if(buying) wHermes++;
				afforded = true;
			}
			break;
		case 15:
			cost = (int)(Math.pow(wHera, 2.8)/1.123);
			if(currency >= cost)
			{
				if(buying) wHera++;
				afforded = true;
			}
			break;
		case 9:
			cost = 200;
			if(currency >= cost)
			{
				if(buying) pHeal++;
				afforded = true;
			}
			break;
		case 10:
			cost = 200;
			if(currency >= cost)
			{
				if(buying) pCool++;
				afforded = true;
			}
			break;
		case 11:
			cost = 200;
			if(currency >= cost)
			{
				if(buying) pWater++;
				afforded = true;
			}
			break;
		case 12:
			cost = 200;
			if(currency >= cost)
			{
				if(buying) pEarth++;
				afforded = true;
			}
			break;
		case 13:
			cost = 200;
			if(currency >= cost)
			{
				if(buying) pAir++;
				afforded = true;
			}
			break;
		case 14:
			cost = 200;
			if(currency >= cost)
			{
				if(buying) pFire++;
				afforded = true;
			}
			break;
		}
		if(!afforded)
		{
			cost = 0;
			//playEffect(R.raw.nomoney);
		}
		return(int) cost;
	}
	protected int getItemID(String toBuy)
	{
		int ID = 0;
		if(toBuy.equals("Worship Apollo"))
		{
			ID = 1;
		}
		else if(toBuy.equals("Worship Posiedon"))
		{
			ID = 2;
		}
		else if(toBuy.equals("Worship Zues"))
		{
			ID = 3;
		}
		else if(toBuy.equals("Worship Hades"))
		{
			ID = 4;
		}
		else if(toBuy.equals("Worship Hephaestus"))
		{
			ID = 5;
		}
		else if(toBuy.equals("Worship Ares"))
		{
			ID = 6;
		}
		else if(toBuy.equals("Worship Athena"))
		{
			ID = 7;
		}
		else if(toBuy.equals("Worship Hermes"))
		{
			ID = 8;
		}
		else if(toBuy.equals("Ambrosia"))
		{
			ID = 9;
		}
		else if(toBuy.equals("Cooldown"))
		{
			ID = 10;
		}
		else if(toBuy.equals("Posiedon's Shell"))
		{
			ID = 11;
		}
		else if(toBuy.equals("Hades' Helm"))
		{
			ID = 12;
		}
		else if(toBuy.equals("Zues's Armor"))
		{
			ID = 13;
		}
		else if(toBuy.equals("Apollo's Flame"))
		{
			ID = 14;
		}
		else if(toBuy.equals("Worship Hera"))
		{
			ID = 15;
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
		}
		else if(toBuy.equals("Worship Posiedon"))
		{
			describe[0] = "Attack cooldown speed increased";
			describe[1] = "while fighting under Posiedon";
		}
		else if(toBuy.equals("Worship Zues"))
		{
			describe[0] = "Movement speed and roll cooldown";
			describe[1] = "increased while fighting under Zues";
		}
		else if(toBuy.equals("Worship Hades"))
		{
			describe[0] = "Damage reduction increased while";
			describe[1] = "fighting under Hades";
		}
		else if(toBuy.equals("Worship Hephaestus"))
		{
			describe[0] = "Increases health during battles";
			describe[1] = "";
		}
		else if(toBuy.equals("Worship Ares"))
		{
			describe[0] = "Increases damage dealt during";
			describe[1] = "battles";
		}
		else if(toBuy.equals("Worship Athena"))
		{
			describe[0] = "Decreases cooldown time for all";
			describe[1] = "attacks or spells during battles";
		}
		else if(toBuy.equals("Worship Hermes"))
		{
			describe[0] = "Increases movement speed and";
			describe[1] = "decreases roll cooldown time";
		}
		else if(toBuy.equals("Ambrosia"))
		{
			describe[0] = "Heals half of players max Hp, ";
			describe[1] = "stopping at full";
		}
		else if(toBuy.equals("Cooldown"))
		{
			describe[0] = "Teleport, burst and roll";
			describe[1] = "cooldowns set to full";
		}
		else if(toBuy.equals("Posiedon's Shell"))
		{
			describe[0] = "Fight under Posiedon for a short";
			describe[1] = "length of time, reducing cooldowns";
		}
		else if(toBuy.equals("Hades' Helm"))
		{
			describe[0] = "Fight under Hades for a short";
			describe[1] = "length of time, increasing armor";
		}
		else if(toBuy.equals("Zues's Armor"))
		{
			describe[0] = "Fight under Zues for a short length";
			describe[1] = "of time, increasing movement speed";
		}
		else if(toBuy.equals("Apollo's Flame"))
		{
			describe[0] = "Fight under Apollo for a short";
			describe[1] = "length of time, increasing damage";
		}
		else if(toBuy.equals("Worship Hera"))
		{
			describe[0] = "Increases rate of drop of";
			describe[1] = "blessings during battles";
		}
		return describe;
	}@
	Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	public void winFight(int difficultyLevel)
	{}
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
	}@
	Override
	public void onStart()
	{
		super.onStart();
	}
	public void setSaveData()
	{
		savedData[1] = 1;
		savedData[2] = 0;
		savedData[3] = 1;
		savedData[29] = 0;
		if(stickOnRight) savedData[1] = 0;
		if(shootTapScreen) savedData[2] = 1;
		if(shootTapDirectional) savedData[3] = 0;
		if(holdShoot) savedData[29] = 1;
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
		if(control != null)
		{
			savedData[23] = (byte) control.playerType;
		}
		else
		{
			savedData[23] = playerType;
		}
		savedData[24] = (byte)((int) volumeMusic);
		savedData[25] = (byte)((int) volumeEffect);
		String temp = correctDigits(Integer.toBinaryString(gameCurrency), 21);
		String tempLow = correctDigits(temp.substring(14), 8);
		String tempHigh = correctDigits(temp.substring(7, 14), 8);
		String tempHuge = correctDigits(temp.substring(0, 7), 8);
		byte low = (byte) Integer.parseInt(tempLow, 2);
		byte high = (byte) Integer.parseInt(tempHigh, 2);
		byte huge = (byte) Integer.parseInt(tempHuge, 2);
		savedData[19] = (byte)high;
		savedData[20] = (byte)low;
		savedData[27] = (byte)huge;
		temp = correctDigits(Integer.toBinaryString(realCurrency), 21);
		tempLow = correctDigits(temp.substring(14), 8);
		tempHigh = correctDigits(temp.substring(7, 14), 8);
		tempHuge = correctDigits(temp.substring(0, 7), 8);
		low = (byte) Integer.parseInt(tempLow, 2);
		high = (byte) Integer.parseInt(tempHigh, 2);
		huge = (byte) Integer.parseInt(tempHuge, 2);
		savedData[21] = (byte)high;
		savedData[22] = (byte)low;
		savedData[28] = (byte)huge;
		savedData[26] = (byte)(wHera);
	}
	public void readSaveData()
	{
		stickOnRight = savedData[1] == 0;
		shootTapScreen = savedData[2] == 1;
		shootTapDirectional = savedData[3] == 0;
		holdShoot = savedData[29] == 1;
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
		gameCurrency = savedData[20] + (128 * savedData[19]) + (16384*savedData[27]);
		realCurrency = savedData[22] + (128 * savedData[21]) + (16384*savedData[28]);
		volumeMusic = savedData[24];
		volumeEffect = savedData[25];
		wHera = savedData[26];
	}@
	Override
	public void onResume()
	{
		super.onResume();
		read();
		if(savedData[0] == 1)
		{
			readSaveData();
		}
		startMusic();
		control.imageLibrary.loadAllImages();
		gameRunning = true;
	}@
	Override
	public void onPause()
	{
		super.onPause();
		setSaveData();
		write();
		stopMusic();
		control.imageLibrary.recycleImages();
		gameRunning = false;
	}@
	Override
	public void onStop()
	{
		super.onStop();
	}
	private void read()
	{
		openRead();
		try
		{
			fileRead.read(savedData, 0, savePoints);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeRead();
	}
	private void write()
	{
		openWrite();
		try
		{
			fileWrite.write(savedData, 0, savePoints);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeWrite();
	}
	private void openRead()
	{
		try
		{
			fileRead = openFileInput("ProjectSaveData");
		}
		catch(FileNotFoundException e)
		{
			openWrite();
			closeWrite();
			openRead();
		}
	}
	private void openWrite()
	{
		try
		{
			fileWrite = openFileOutput("ProjectSaveData", Context.MODE_PRIVATE);
		}
		catch(FileNotFoundException e)
		{
			// TODO Auto-generated catch block
		}
	}
	private void closeRead()
	{
		try
		{
			fileRead.close();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void closeWrite()
	{
		try
		{
			fileWrite.close();
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener()
	{
		public void onQueryInventoryFinished(IabResult result, Inventory inventory)
		{
			Log.d(TAG, "Query inventory finished.");
			// Have we been disposed of in the meantime? If so, quit.
			if(mHelper == null) return;
			// Is it a failure?
			if(result.isFailure())
			{
				complain("Failed to query inventory: " + result);
				return;
			}
			Log.d(TAG, "Query inventory was successful.");
			// Check for gas delivery -- if we own gas, we should fill up the tank immediately
			Purchase c100Purchase = inventory.getPurchase(SKU_100);
			if(c100Purchase != null && verifyDeveloperPayload(c100Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_100), mConsumeFinishedListener);
				return;
			}
			Purchase c250Purchase = inventory.getPurchase(SKU_250);
			if(c250Purchase != null && verifyDeveloperPayload(c250Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_250), mConsumeFinishedListener);
				return;
			}
			Purchase c1000Purchase = inventory.getPurchase(SKU_1000);
			if(c1000Purchase != null && verifyDeveloperPayload(c1000Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_1000), mConsumeFinishedListener);
				return;
			}
			Purchase c5000Purchase = inventory.getPurchase(SKU_5000);
			if(c5000Purchase != null && verifyDeveloperPayload(c5000Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_5000), mConsumeFinishedListener);
				return;
			}
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};
	// User clicked the "Buy Gas" button
	public void buyRealCurrency100()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_100, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	public void buyRealCurrency250()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_250, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	public void buyRealCurrency1000()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_1000, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	public void buyRealCurrency5000()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_5000, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	private String generatePayload()
	{
		return "";
	}
	@ Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if(mHelper == null) return;
		// Pass on the activity result to the helper for handling
		if(!mHelper.handleActivityResult(requestCode, resultCode, data))
		{
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else
		{
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}
	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p)
	{
		//if(p.getDeveloperPayload().equals(""))return true;
		/*
		 * TODO: verify that the developer payload of the purchase is correct. It will be
		 * the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase and
		 * verifying it here might seem like a good approach, but this will fail in the
		 * case where the user purchases an item on one device and then uses your app on
		 * a different device, because on the other device you will not have access to the
		 * random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different between them,
		 *    so that one user's purchase can't be replayed to another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app wasn't the
		 *    one who initiated the purchase flow (so that items purchased by the user on
		 *    one device work on other devices owned by the user).
		 *
		 * Using your own server to store and verify developer payloads across app
		 * installations is recommended.
		 */
		return true;
	}
	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
	{
		public void onIabPurchaseFinished(IabResult result, Purchase purchase)
		{
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
			// if we were disposed of in the meantime, quit.
			if(mHelper == null) return;
			if(result.isFailure())
			{
				complain("Error purchasing: " + result);
				return;
			}
			if(!verifyDeveloperPayload(purchase))
			{
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}
			Log.d(TAG, "Purchase successful.");
			if(purchase.getSku().equals(SKU_100)||purchase.getSku().equals(SKU_250)||purchase.getSku().equals(SKU_1000)||purchase.getSku().equals(SKU_5000))
			{
				// bought 1/4 tank of gas. So consume it.
				Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};
	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
	{
		public void onConsumeFinished(Purchase purchase, IabResult result)
		{
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
			// if we were disposed of in the meantime, quit.
			if(mHelper == null) return;
			// We know this is the "gas" sku because it's the only one we consume,
			// so we don't check which sku was consumed. If you have more than one
			// sku, you probably should check...
			if(result.isSuccess())
			{
				// successfully consumed, so we apply the effects of the item in our
				// game world's logic, which in our case means filling the gas tank a bit
				if(purchase.getSku().equals(SKU_100))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 100;
				}
				if(purchase.getSku().equals(SKU_250))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 250;
				}
				if(purchase.getSku().equals(SKU_1000))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 1000;
				}
				if(purchase.getSku().equals(SKU_5000))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 5000;
				}
			}
			else
			{
				complain("Error while consuming: " + result);
			}
			Log.d(TAG, "End consumption flow.");
		}
	};
	// We're being destroyed. It's important to dispose of the helper here!
	@
	Override
	public void onDestroy()
	{
		super.onDestroy();
		// very important:
		Log.d(TAG, "Destroying helper.");
		if(mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}
	}
	void complain(String message)
	{
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}
	void alert(String message)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}
}