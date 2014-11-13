/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.magegame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.magegame.R;
import com.magegame.util.IabHelper;
import com.magegame.util.IabResult;
import com.magegame.util.Inventory;
import com.magegame.util.Purchase;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
public class StartActivity extends Activity
{
	protected Controller control;
	protected double screenDimensionMultiplier;
	protected int screenMinX;
	protected int screenMinY;
	protected int gameCurrency = 2000;
	protected int realCurrency = 2000;
	protected byte uApollo = 7;
	protected byte uPoseidon = 7;
	protected byte uZues = 7;
	protected byte uHades = 7;
	protected byte uHephaestus = 7;
	protected byte uAres = 7;
	protected byte uAthena = 7;
	protected byte uHermes = 7;
	protected byte uHera = 7;
	protected double wApollo = 7;
	protected double wPoseidon = 7;
	protected double wZues = 7;
	protected double wHades = 7;
	protected double wHephaestus = 7;
	protected double wAres = 7;
	protected double wAthena = 7;
	protected double wHermes = 7;
	protected double wHera = 7;
	protected byte pGolem = 1;
	protected byte pHammer = 1;
	protected byte pHeal = 1;
	protected byte pCool = 1;
	protected byte pWater = 1;
	protected byte pEarth = 1;
	protected byte pAir = 1;
	protected byte pFire = 1;
	protected byte bReserve = 0;
	protected byte bExcess = 0;
	protected byte bReplentish = 0;
	protected byte bTracking = 0;
	protected boolean shootTapScreen = false;
	protected boolean shootTapDirectional = true;
	protected boolean holdShoot = true;
	protected boolean ownSkin1 = false;
	protected boolean ownSkin2 = false;
	protected boolean ownSkin3 = false;
	protected boolean ownSkin4 = false;
	protected boolean ownSkin5 = false;
	protected boolean ownSkin6 = false;
	protected boolean ownSkin7 = false;
	protected boolean highGraphics = false;
	protected byte currentSkin = 0;
	protected byte levelBeaten = 18;
	protected boolean gameRunning = true;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 50;
	protected byte[] savedData = new byte[savePoints];
	protected MediaPlayer backMusic;
	private SoundPool spool;
	private int[] soundPoolMap = new int[15];
	protected AudioManager audioManager;
	protected double volumeMusic = 127;
	protected double volumeEffect = 100;
	private String TAG = "game";
	private String SKU_100 = "platinum_100";
	private String SKU_250 = "platinum_250";
	private String SKU_1000 = "platinum_1000";
	private String SKU_5000 = "platinum_5000";
	private int RC_REQUEST = 10001;
	IabHelper mHelper;@
	Override
	/**
	 * sets screena and window variables and reads in data
	 * creates control object and sets up IAB
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setWindowAndAudio();
		setScreenDimensions();
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
		startMusic();
		control = new Controller(this, this);
		setContentView(control);
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
	/**
	 * sets screen variables as well as audio settings
	 */
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
		soundPoolMap[13] = spool.load(this, R.raw.money_1, 1);
		soundPoolMap[14] = spool.load(this, R.raw.money_2, 1);
	}
	/**
	 * plays a random money effect
	 */
	protected void playMoney()
	{
		Log.e("game", "money");
		float newV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		newV = (float)(newV * volumeEffect / 127);
		if(Math.random()>0.5)
		{
			spool.play(soundPoolMap[13], newV, newV, 1, 0, 1f);
		} else
		{
			spool.play(soundPoolMap[14], newV, newV, 1, 0, 1f);
		}
		//TODO
	}
	/**
	 * starts the menu screen by turning all negative effects to player off
	 */
	protected void startMenu()
	{
		control.drainHp = false;
		control.lowerHp = false;
		control.limitSpells = false;
		control.enemyRegen = false;
		control.changeDifficulty(10);
		control.startFighting(10);
		control.gamePaused = false;
	}
	/**
	 * player loses a fight, start screen
	 */
	protected void loseFight()
	{
		control.gamePaused = true;
		control.currentPause = "lost";
		control.invalidate();
	}
	/**
	 * player wins a fight, increases level, starts next level
	 */
	protected void winFight()
	{
		control.startWarning("Won Round ("+Integer.toString((int)control.moneyMade)+"g)");
		if(control.levelNum < 180)
		{
			if((int)(control.levelNum / 10) - 2 == levelBeaten)
			{
				levelBeaten++;
				realCurrency += control.moneyMultiplier*3;
				control.startWarning("Victory ("+Integer.toString((int)control.moneyMade)+"g/"+Integer.toString((int)control.moneyMultiplier*3)+"p)");
			}
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
		} else
		{
			startMenu();
		}
	}
	/**
	 * starts a level from menu
	 * @param levelSet level to start
	 */
	protected void startFight(int levelSet)
	{
		control.startFighting(levelSet * 10);
		control.gamePaused = false;
		if(levelSet == 2)
		{
			control.player.getPowerUp(2);
		}
	}
	/**
	 * resets volume
	 */
	protected void resetVolume()
	{
		float systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float)(systemVolume * volumeMusic / 127);
		backMusic.setVolume(systemVolume, systemVolume);
	}
	/**
	 * sets dimensions of screen
	 */
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
	/**
	 * starts background music
	 */
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
	/**
	 * plays effect based on sent integer
	 * @param toPlay id of effect to play
	 */
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
		if(toPlay.equals("powerup")) spool.play(soundPoolMap[6], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword1")) spool.play(soundPoolMap[7], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword2")) spool.play(soundPoolMap[8], newV, newV, 1, 0, 1f);
		if(toPlay.equals("swordmiss")) spool.play(soundPoolMap[9], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowhit")) spool.play(soundPoolMap[10], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowrelease")) spool.play(soundPoolMap[11], newV, newV, 1, 0, 1f);
		if(toPlay.equals("pageflip")) spool.play(soundPoolMap[12], newV, newV, 1, 0, 1f);
	}
	/**
	 * stops background music and releases it
	 */
	protected void stopMusic()
	{
		if(backMusic != null)
		{
			backMusic.stop();
			backMusic.release();
			backMusic = null;
		}
	}
	/**
	 * buys an item with in game money
	 * @param toBuy item to buy
	 */
	protected void buyGame(String toBuy)
	{
		int cost = buy(toBuy, gameCurrency, true);
		if(cost != 0)
		{
			playMoney();
			gameCurrency -= cost;
			saveGame();
		} else
		{
			control.startWarningImediate("Not enough Money");
		}
	}
	/**
	 * buys an item with real money
	 * @param toBuy item to buy
	 */
	protected void buyReal(String toBuy)
	{
		int cost = buy(toBuy, realCurrency, true);
		if(cost != 0)
		{
			playMoney();
			realCurrency -= cost;
			saveGame();
		} else
		{
			control.startWarningImediate("Not enough Money");
		}
	}
	/**
	 * checks whether an item is affordable
	 * @param toBuy item to check
	 * @return whether you can buy it
	 */
	protected boolean canBuyGame(String toBuy)
	{
		return !(buy(toBuy, gameCurrency, false) == 0);
	}
	/**
	 * checks whether an item is affordable
	 * @param toBuy item to check
	 * @return whether you can buy it
	 */
	protected boolean canBuyReal(String toBuy)
	{
		return !(buy(toBuy, realCurrency, false) == 0);
	}
	/**
	 * buys or checks price of an item
	 * @param toBuy item to buy or check
	 * @param currency how much currency you have
	 * @param buying whether you are buying or checking price
	 * @return price of item, returns 0 if unaffordable 
	 */
	protected int buy(String toBuy, int currency, boolean buying)
	{
		int ID = getItemID(toBuy);
		int cost = 0;
		boolean afforded = false;
		double power = 3.4;
		switch(ID)
		{
		case 1:
			cost = (int)(Math.pow(wApollo, power)/2.49);
			if(currency >= cost)
			{
				if(buying) wApollo++;
				afforded = true;
			}
			break;
		case 2:
			cost = (int)(Math.pow(wPoseidon, power)/2.49);
			if(currency >= cost)
			{
				if(buying) wPoseidon++;
				afforded = true;
			}
			break;
		case 3:
			cost = (int)(Math.pow(wZues, power)/2.49);
			if(currency >= cost)
			{
				if(buying) wZues++;
				afforded = true;
			}
			break;
		case 4:
			cost = (int)(Math.pow(wHades, power)/2.49);
			if(currency >= cost)
			{
				if(buying) wHades++;
				afforded = true;
			}
			break;
		case 5:
			cost = (int)(Math.pow(wHephaestus, power)/1.8675);
			if(currency >= cost)
			{
				if(buying) wHephaestus++;
				afforded = true;
			}
			break;
		case 6:
			cost = (int)(Math.pow(wAres, power)/1.8675);
			if(currency >= cost)
			{
				if(buying) wAres++;
				afforded = true;
			}
			break;
		case 7:
			cost = (int)(Math.pow(wAthena, power)/1.8675);
			if(currency >= cost)
			{
				if(buying) wAthena++;
				afforded = true;
			}
			break;
		case 8:
			cost = (int)(Math.pow(wHermes, power)/1.8675);
			if(currency >= cost)
			{
				if(buying) wHermes++;
				afforded = true;
			}
			break;
		case 15:
			cost = (int)(Math.pow(wHera, power)/1.8675);
			if(currency >= cost)
			{
				if(buying) wHera++;
				afforded = true;
			}
			break;
		case 9:
			cost = 200;
			if(currency >= cost&&pHeal<10)
			{
				if(buying) pHeal++;
				afforded = true;
			}
			break;
		case 10:
			cost = 200;
			if(currency >= cost&&pCool<10)
			{
				if(buying) pCool++;
				afforded = true;
			}
			break;
		case 11:
			cost = 200;
			if(currency >= cost&&pWater<10)
			{
				if(buying) pWater++;
				afforded = true;
			}
			break;
		case 12:
			cost = 200;
			if(currency >= cost&&pEarth<10)
			{
				if(buying) pEarth++;
				afforded = true;
			}
			break;
		case 13:
			cost = 200;
			if(currency >= cost&&pAir<10)
			{
				if(buying) pAir++;
				afforded = true;
			}
			break;
		case 14:
			cost = 200;
			if(currency >= cost&&pFire<10)
			{
				if(buying) pFire++;
				afforded = true;
			}
			break;
		case 16:
			cost = 100;
			if(currency >= cost)
			{
				if(!ownSkin1) 
				{
					if(buying)
					{
						ownSkin1 = true;
					}
					afforded = true;
				}
			}
			break;
		case 17:
			cost = 100;
			if(currency >= cost)
			{
				if(!ownSkin2) 
				{
					if(buying)
					{
						ownSkin2 = true;
					}
					afforded = true;
				}
			}
			break;
		case 18:
			cost = 200;
			if(currency >= cost)
			{
				if(!ownSkin3) 
				{
					if(buying)
					{
						ownSkin3 = true;
					}
					afforded = true;
				}
			}
			break;
		case 19:
			cost = 200;
			if(currency >= cost)
			{
				if(!ownSkin4) 
				{
					if(buying)
					{
						ownSkin4 = true;
					}
					afforded = true;
				}
			}
			break;
		case 20:
			cost = 500;
			if(currency >= cost)
			{
				if(!ownSkin5) 
				{
					if(buying)
					{
						ownSkin5 = true;
					}
					afforded = true;
				}
			}
			break;
		case 21:
			cost = 700;
			if(currency >= cost)
			{
				if(!ownSkin6) 
				{
					if(buying)
					{
						ownSkin6 = true;
					}
					afforded = true;
				}
			}
			break;
		case 22:
			cost = 1000;
			if(currency >= cost)
			{
				if(!ownSkin7) 
				{
					if(buying)
					{
						ownSkin7 = true;
					}
					afforded = true;
				}
			}
			break;
		case 23:
			cost = 100;
			if(currency >= cost)
			{
				if(buying) gameCurrency += 1000;
				afforded = true;
			}
			break;
		case 24:
			cost = 600;
			if(currency >= cost)
			{
				if(buying) gameCurrency += 8000;
				afforded = true;
			}
			break;
		case 25:
			cost = 2500;
			if(currency >= cost)
			{
				if(buying) gameCurrency += 40000;
				afforded = true;
			}
			break;
		case 26:
			cost = 25;
			if(currency >= cost&&pHammer<5)
			{
				if(buying) pHammer++;
				afforded = true;
			}
			break;
		case 27:
			cost = 25;
			if(currency >= cost&&pGolem<5)
			{
				if(buying) pGolem++;
				afforded = true;
			}
			break;
		case 28:
			cost = 30*(int)(Math.pow(bReserve, 2)+1);
			if(currency >= cost)
			{
				if(buying) bReserve++;
				afforded = true;
			}
			break;
		case 29:
			cost = 30*(int)(Math.pow(bExcess, 2)+1);
			if(currency >= cost)
			{
				if(buying) bExcess++;
				afforded = true;
			}
			break;
		case 30:
			cost = 30*(int)(Math.pow(bReplentish, 2)+1);
			if(currency >= cost)
			{
				if(buying) bReplentish++;
				afforded = true;
			}
			break;
		case 31:
			cost = 30*(int)(Math.pow(bTracking, 2)+1);
			if(currency >= cost)
			{
				if(buying) bTracking++;
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
	/**
	 * returns items ID
	 * @param toBuy item to get id for
	 * @return id of item
	 */
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
		} else if(toBuy.equals("skin1"))
		{
			ID = 16;
		} else if(toBuy.equals("skin2"))
		{
			ID = 17;
		} else if(toBuy.equals("skin3"))
		{
			ID = 18;
		} else if(toBuy.equals("skin4"))
		{
			ID = 19;
		} else if(toBuy.equals("skin5"))
		{
			ID = 20;
		} else if(toBuy.equals("skin6"))
		{
			ID = 21;
		} else if(toBuy.equals("skin7"))
		{
			ID = 22;
		} else if(toBuy.equals("1000g"))
		{
			ID = 23;
		} else if(toBuy.equals("8000g"))
		{
			ID = 24;
		} else if(toBuy.equals("40000g"))
		{
			ID = 25;
		} else if(toBuy.equals("Iron Golem"))
		{
			ID = 26;
		} else if(toBuy.equals("Gold Golem"))
		{
			ID = 27;
		} else if(toBuy.equals("Reserve"))
		{
			ID = 28;
		} else if(toBuy.equals("Excess"))
		{
			ID = 29;
		} else if(toBuy.equals("Replentish"))
		{
			ID = 30;
		} else if(toBuy.equals("Trailing"))
		{
			ID = 31;
		}
		return ID;
	}
	/**
	 * returns item description
	 * @param toBuy item to get description for
	 * @return description
	 */
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
			describe[0] = "Heals 2000 of players Hp, ";
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
		else if(toBuy.equals("1000g"))
		{
			describe[0] = "One thousand gold to spend";
			describe[1] = "on upgrades etc.";
		}
		else if(toBuy.equals("8000g"))
		{
			describe[0] = "Eight thousand gold to";
			describe[1] = "spend on upgrades etc.";
		}
		else if(toBuy.equals("40000g"))
		{
			describe[0] = "Forty thousand gold";
			describe[1] = "to spendon upgrades etc.";
		}
		else if(toBuy.equals("Iron Golem"))
		{
			describe[0] = "Temporarily transforms player";
			describe[1] = "into a large iron golem";
		}
		else if(toBuy.equals("Gold Golem"))
		{
			describe[0] = "Temporarily transforms player";
			describe[1] = "into a large golden golem";
		}
		else if(toBuy.equals("Reserve"))
		{
			describe[0] = "Increases the maximum number of";
			describe[1] = "shots the player can store";
		}
		else if(toBuy.equals("Excess"))
		{
			describe[0] = "Increases profit from completing";
			describe[1] = "levels or killing enemies";
		}
		else if(toBuy.equals("Replentish"))
		{
			describe[0] = "Damaging enemies lowers cooldowns";
			describe[1] = "for spells etc.";
		}
		else if(toBuy.equals("Trailing"))
		{
			describe[0] = "Players shots trail enemies better,";
			describe[1] = "making them more likely to hit";
		}
		return describe;
	}
	@ Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	/**
	 * adds 0's to the start of a string until it reaches a certain number of digits
	 * @param end starting string
	 * @param digits number or desired digits
	 * @return complete full length string
	 */
	protected String correctDigits(String end, int digits)
	{
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
	/**
	 * saves all required data 
	 */
	protected void saveGame()
	{
		setSaveData();
		write();
		readSaveData();
	}
	@ Override
	public void onStart()
	{
		super.onStart();
	}
	/**
	 * set data to write it to save file
	 */
	public void setSaveData()
	{
		savedData[1] = 1;
		savedData[2] = 0;
		savedData[3] = 1;
		savedData[29] = 0;
		if(shootTapScreen) savedData[2] = 1;
		if(shootTapDirectional) savedData[3] = 0;
		if(holdShoot) savedData[29] = 1;
		savedData[4] = levelBeaten;
		savedData[5] = uApollo;
		savedData[6] = uPoseidon;
		savedData[7] = uZues;
		savedData[8] = uHades;
		savedData[9] = uHephaestus;
		savedData[10] = uAres;
		savedData[11] = uAthena;
		savedData[12] = uHermes;
		savedData[13] = pHeal;
		savedData[14] = pCool;
		savedData[15] = pWater;
		savedData[16] = pEarth;
		savedData[17] = pAir;
		savedData[18] = pFire;
		savedData[38] = pGolem;
		savedData[39] = pHammer;
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
		savedData[30] = 0;
		savedData[31] = 0;
		savedData[32] = 0;
		savedData[33] = 0;
		savedData[34] = 0;
		savedData[35] = 0;
		savedData[36] = 0;		
		if(ownSkin1) savedData[30] = 1;
		if(ownSkin2) savedData[31] = 1;
		if(ownSkin3) savedData[32] = 1;
		if(ownSkin4) savedData[33] = 1;
		if(ownSkin5) savedData[34] = 1;
		if(ownSkin6) savedData[35] = 1;
		if(ownSkin7) savedData[36] = 1;
		savedData[37] = currentSkin;
		savedData[40] = bReserve;
		savedData[41] = bExcess;
		savedData[42] = bReplentish;
		savedData[43] = bTracking;
		savedData[44] = 1;
		if(highGraphics) savedData[44] = 0;
	}
	/**
	 * read data once it has been put into savedData array
	 */
	public void readSaveData()
	{
		shootTapScreen = savedData[2] == 1;
		shootTapDirectional = savedData[3] == 0;
		holdShoot = savedData[29] == 1;
		levelBeaten = savedData[4];
		uApollo = savedData[5];
		uPoseidon = savedData[6];
		uZues = savedData[7];
		uHades = savedData[8];
		uHephaestus = savedData[9];
		uAres = savedData[10];
		uAthena = savedData[11];
		uHermes = savedData[12];
		pHeal = savedData[13];
		pCool = savedData[14];
		pWater = savedData[15];
		pEarth = savedData[16];
		pAir = savedData[17];
		pFire = savedData[18];
		pGolem = savedData[38];
		pHammer = savedData[39];
		gameCurrency = savedData[20] + (128 * savedData[19]) + (16384*savedData[27]);
		realCurrency = savedData[22] + (128 * savedData[21]) + (16384*savedData[28]);
		volumeMusic = savedData[24];
		volumeEffect = savedData[25];
		wHera = savedData[26];
		ownSkin1 = savedData[30] == 1;
		ownSkin2 = savedData[31] == 1;
		ownSkin3 = savedData[32] == 1;
		ownSkin4 = savedData[33] == 1;
		ownSkin5 = savedData[34] == 1;
		ownSkin6 = savedData[35] == 1;
		ownSkin7 = savedData[36] == 1;
		currentSkin = savedData[37];
		bReserve = savedData[40];
		bExcess = savedData[41];
		bReplentish = savedData[42];
		bTracking = savedData[43];
		savedData[44] = 1;
		highGraphics = savedData[44] == 0;
	}
	/**
	 * reads saved data
	 * starts music, loads images
	 */
	@ Override
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
	}
	/**
	 * stops music, stops timer, saves data
	 */
	@ Override
	public void onPause()
	{
		super.onPause();
		setSaveData();
		write();
		stopMusic();
		control.imageLibrary.recycleImages();
		gameRunning = false;
	}
	@ Override
	public void onStop()
	{
		super.onStop();
	}
	/**
	 * reads data from file and sets variables accordingly
	 */
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
	/**
	 * saves data to file
	 */
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
	/**
	 * opens the save file to be read from
	 */
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
	/**
	 * opens the save file to be written to
	 */
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
	/**
	 * closes the save file from reading
	 */
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
	/**
	 * closes the save file from writing
	 */
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