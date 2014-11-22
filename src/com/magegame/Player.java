/**
 * Handles cooldowns and stats for player and executes spells
 */
package com.magegame;

import android.widget.Toast;

public final class Player extends Human
{
	protected int transformedTimer = 30000;
	protected int transformed = 0;
	protected double touchY;
	private double damageMultiplier = 1;
	protected boolean playing = false;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 1;
	protected double spMod = 1;
	protected double abilityTimer_roll = 0;
	protected double abilityTimer_burst = 0;
	protected double abilityTimer_Proj_Tracker = 0;
	protected double abilityTimerTransformed_pound = 0;
	protected double abilityTimerTransformed_hit = 0;
	private double xSave = 0;
	private double ySave = 0;
	private boolean usedDionysusWine = false;
	protected int projectileSpeed = 40;
	protected double touchX;
	protected boolean touching;
	protected boolean touchingShoot;
	protected double touchShootX;
	protected double touchShootY;
	protected int powerUpTimer = 0;
	protected int powerID = 0;
	private int minimumShootTime = 4;
	private double hpAccurate;
	/**
	 * Sets all variables to start, sets image
	 * @param creator control object
	 */
	public Player(Controller creator)
	{
		control = creator;
		resetVariables();
		visualImage = control.imageLibrary.player_Image[0];
		/*if(control.activity.useHestiasBlessing>0)
		{
			control.activity.useHestiasBlessing --;
			damageMultiplier /= 2;
		}
		if(control.activity.useArtemisArrow>0)
		{
			control.activity.useArtemisArrow --;
			projectileSpeed = 17;
		}*/
	}
	/**
	 * resets all variables to the start of a match or round
	 */
	public void resetVariables()
	{
		if(transformed != 0)
		{
			transformed = 0;
			control.imageLibrary.loadPlayerImage();
		}
		transformedTimer = 30000;
		damageMultiplier = 1;
		rollTimer = 0;
		sp = 1;
		abilityTimer_roll = 120;
		abilityTimer_burst = 250;
		abilityTimer_Proj_Tracker = 0;
		usedDionysusWine = false;
		projectileSpeed = 40;
		touching = false;
		x = 370;
		y = 160;
		hp = (int)(4890 * Math.pow((double)control.activity.buyUpgradeHealth/10, 0.9))+2000;
		if(control.lowerHp)
		{
			hp = (int)(hp/8);
		}
		setHpMax(hp);
		deleted = false;
		playing = false;
		powerUpTimer=0;
	}
	/**
	 * Counts timers and executes movement and predefined behaviors
	 */
	@
	Override
	protected void frameCall()
	{
		if(transformedTimer == 0)
		{
			control.imageLibrary.loadTrans();
			if(transformed == 1)
			{
				control.imageLibrary.player_Image = control.imageLibrary.loadArray1D(58, "human_playergolem", 85, 85);
			} else
			{
				control.imageLibrary.player_Image = control.imageLibrary.loadArray1D(58, "human_playerhammer", 80, 80);
			}
		}
		transformedTimer++;
		if(transformedTimer == 500)
		{
			transformed = 0;
			control.imageLibrary.transattack.recycle();
			control.imageLibrary.loadPlayerImage();
			control.imageLibrary.recycleArray(control.imageLibrary.trans);
		}
		if(control.drainHp)
		{
			hpAccurate += (double)hp/1000;
			super.getHit((int)hpAccurate);
			hpAccurate -= (int)hpAccurate;
			if(deleted)
			{
				control.activity.loseFight();
			}
		}
		minimumShootTime--;
		powerUpTimer--;
		if(usedDionysusWine)
		{
			super.getHit(100);
		}
		sp -= 0.0001;
		spMod = 1;
		speedCur = 4.7*Math.pow((double)control.activity.buyUpgradeSpeed/10, 0.4);
		speedCur *= 1.2;
		if(sp > 1.5)
		{
			sp = 1.5;
		}
		if(sp < 0.5)
		{
			sp = 0.5;
		}
			double cooldown;
			cooldown = (double)control.activity.buyUpgradeCooldown*(double)control.activity.buyUpgradeSpeed/100;
			abilityTimer_roll += cooldown*1.4;
			if(abilityTimer_roll >= 120)
			{
				abilityTimer_roll = 120;
			}
			cooldown = (double)control.activity.buyUpgradeCooldown/10;
			abilityTimer_burst += cooldown*1.4;
			abilityTimer_Proj_Tracker += cooldown*5;
			if(abilityTimer_burst >= 500)
			{
				abilityTimer_burst = 500;
			}
			if(abilityTimer_Proj_Tracker >= 91+(control.activity.buyExtraReserve*20))
			{
				abilityTimer_Proj_Tracker = 91+(control.activity.buyExtraReserve*20);
			}
			if(control.limitSpells)
			{
				abilityTimer_burst = 0;
				abilityTimer_roll = 0;
			}
			rollTimer--;
			if(currentFrame == 30) // roll finished
			{
				currentFrame = 0;
				playing = false;
			}
			if(currentFrame == 19)currentFrame = 0; // restart walking animation
			if(playing) currentFrame++;
			if(currentFrame > 31)currentFrame = 0; // player stopped shooting
			super.frameCall();
			if(rollTimer < 1)
			{
				if(touchingShoot)
	            {
	            	if(abilityTimer_Proj_Tracker > 30&&minimumShootTime<1)
	            	{
			        	double temp1 = rads;
			            rads = Math.atan2(touchShootY, touchShootX);
			            rotation=rads*r2d;
			        	releaseProj_Tracker();
			        	control.shootStick.rotation=rads*180/Math.PI;
			        	rads = temp1;
		            	minimumShootTime = 2;
	            	}
	            }
				rads = Math.atan2(touchY, touchX);
				rotation = rads * r2d;
				if(!deleted)
				{
					if(touchingShoot)
					{
						currentFrame = 31;
						playing = false;
						rads = Math.atan2(touchShootY, touchShootX);
				        rotation=rads*180/Math.PI;
					} else
					{
						if(!touching || (Math.abs(touchX) < 5 && Math.abs(touchY) < 5))
						{
							playing = false;
							currentFrame = 0;
						}
						else
						{
							movement();
						}
					}
				}
			} else
			{			
					x += xMoveRoll;
					y += yMoveRoll;
			}
		visualImage = control.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	/**
	 * moves player at a set speed, direction is based of move stick
	 */
	protected void movement()
	{
		playing = true;
		rads = Math.atan2(touchY, touchX);
		rotation = rads * r2d;
		x += Math.cos(rads) * speedCur;
		y += Math.sin(rads) * speedCur;
	}
	/**
	 * shoots a power ball
	 */
	protected void releaseProj_Tracker()
	{
		if(abilityTimer_Proj_Tracker > 30)
		{
			if(rollTimer < 0)
			{
					control.spriteController.createProj_TrackerPlayer(rads*r2d, projectileSpeed, 130, x, y);
					abilityTimer_Proj_Tracker -= 30;
					control.activity.playEffect("shoot");
			}
		} else
		{
			//control.coolDown();
		}
	}
	/**
	 * rolls forward
	 */
	protected void roll()
	{
		if(abilityTimer_roll > 40)
		{
			rads = Math.atan2(touchY, touchX);
			rotation = rads * r2d;
			rollTimer = 11;
			playing = true;
			currentFrame = 21;
			xMoveRoll = Math.cos(rads) * 8;
			yMoveRoll = Math.sin(rads) * 8;
			abilityTimer_roll -= 40;
		} else
		{
			Toast.makeText(control.context, "Cool Down", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * When player is transformed this pounds the ground or shield to create explosion
	 */
	protected void pound()
	{
		if(abilityTimerTransformed_pound > 100)
		{
			if(currentFrame<21)
			{
				playing = true;
				currentFrame = 39;
				abilityTimerTransformed_pound -= 100;
			}
		} else
		{
			Toast.makeText(control.context, "Cool Down", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * When player is transformed this swings sword or hammer
	 */
	protected void hit()
	{
		if(abilityTimerTransformed_hit > 10)
		{
			if(currentFrame<21)
			{
				playing = true;
				currentFrame = 21;
				abilityTimerTransformed_hit -= 15;
			}
		} else
		{
			Toast.makeText(control.context, "Cool Down", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * players burst attack
	 */
	protected void burst()
	{
		if(abilityTimer_burst > 300)
		{
			for(int i = 0; i<6; i++)
			{	
				control.spriteController.createProj_TrackerPlayerAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), 130, true);
			}
			control.spriteController.createProj_TrackerPlayerBurst(x, y, 0);
			abilityTimer_burst -= 300;
			control.activity.playEffect("burst");
			control.activity.playEffect("burst");
			control.activity.playEffect("burst");
			control.playerBursted = 0;
		} else
		{
			Toast.makeText(control.context, "Cool Down", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * stuns player
	 */
	protected void stun()
	{
		if(transformed == 0)
		{
			if(rollTimer<2)
			{
				rotation = rads * r2d + 180;
		        roll();
		        currentFrame = 0;
		        xMoveRoll /= 3;
		        yMoveRoll /= 3;
		        abilityTimer_roll += 20;
		        Toast.makeText(control.context, "Stunned!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/**
	 * reduces and amplifies damage based on shields etc.
	 */
	@Override
	protected void getHit(double damage)
	{
		control.playerHit = 0;
		if(transformed == 1)
		{
			damage *= 0.08;
		}
		if(transformed == 2)
		{
			damage *= 0.1;
		}
		damage *= 0.7;
			damage *= damageMultiplier;
			super.getHit(damage);
			sp -= sp*damage/1500;
			if(deleted) control.activity.loseFight();
	}
	/**
	 * gives player a benefit, ranging from health to transformation
	 * @param PowerID id of power received
	 */
	protected void getPowerUp(int PowerID)
	{
		if(PowerID<7||PowerID>10) control.activity.playEffect("powerup");
		if(PowerID>6&&PowerID<11) control.activity.playMoney();
		switch(PowerID)
		{
		case 1:
			hp += 2000;
			if(hp>getHpMax())hp=getHpMax();
			break;
		case 2:
			abilityTimer_roll = 120;
			abilityTimer_Proj_Tracker = 90;
			abilityTimer_burst = 500;
			break;
		case 3:
			powerUpTimer=300;
			powerID=1;
			break;
		case 4:
			powerUpTimer=300;
			powerID=2;
			break;
		case 5:
			powerUpTimer=300;
			powerID=3;
			break;
		case 6:
			powerUpTimer=300;
			powerID=4;
			break;
		case 7:
			control.moneyMade += control.moneyMultiplier;
			control.activity.gameCurrency += control.moneyMultiplier;
			break;
		case 8:
			control.hasKey = true;
			break;
		case 9:
			control.moneyMade += 5*control.moneyMultiplier;
			control.activity.gameCurrency += 5*control.moneyMultiplier;
			break;
		case 10:
			control.moneyMade += 20*control.moneyMultiplier;
			control.activity.gameCurrency += 20*control.moneyMultiplier;
			break;
		case 11:
			transformedTimer = 0;
			transformed = 1;
			break;
		case 12:
			transformedTimer = 0;
			transformed = 2;
			break;
		}
	}
	/**
	 * checks distance between two points
	 * @param fromX point one x
	 * @param fromY point one y
	 * @param toX point 2 x
	 * @param toY point 2 y
	 * @return distance between points
	 */
	private double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
}