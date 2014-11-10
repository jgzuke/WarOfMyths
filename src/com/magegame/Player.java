/**
 * Handles cooldowns and stats for player and executes spells
 */
package com.magegame;

public final class Player extends Human
{
	protected int transformedTimer = 30000;
	protected int transformed = 0;
	protected double touchY;
	private double damageMultiplier = 1;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 1;
	protected double spMod = 1;
	protected double spChangeForType;
	protected double abilityTimer_roll = 0;
	protected double abilityTimer_burst = 0;
	protected double abilityTimer_powerBall = 0;
	protected double abilityTimerTransformed_pound = 0;
	protected double abilityTimerTransformed_hit = 0;
	private double xSave = 0;
	private double ySave = 0;
	private boolean usedDionysusWine = false;
	protected int projectileSpeed = 13;
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
		weight = 1;
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
		abilityTimer_powerBall = 0;
		usedDionysusWine = false;
		projectileSpeed = 10;
		touching = false;
		x = 370;
		y = 160;
		hp = (int)(4890 * Math.pow((double)control.activity.wHephaestus/10, 0.9))+2000;
		if(control.lowerHp)
		{
			hp = (int)(hp/8);
		}
		setHpMax(hp);
		deleted = false;
		playing = false;
		createSpecialGraphicGainCounter = false;
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
			control.imageLibrary.recycleArray(10, control.imageLibrary.trans);
		}
		if(control.drainHp)
		{
			hpAccurate += (double)hp/1000;
			super.getHit((int)hpAccurate);
			hpAccurate -= (int)hpAccurate;
			if(deleted) control.restartGame();
		}
		minimumShootTime--;
		powerUpTimer--;
		if(usedDionysusWine)
		{
			super.getHit(100);
		}
		sp -= 0.0001;
		spMod = 1+(sp*spChangeForType);
		speedCur = 4.7*Math.pow((double)control.activity.wHermes/10, 0.4);
		speedCur *= 1.2;
		if(humanType==2)
		{
			speedCur *= Math.pow(spMod, 0.5);
		}
		if(powerUpTimer>0 && powerID == 3)
		{
			speedCur += 0.7*(double)control.activity.wZues/10;
		}
		if(sp > 1.5)
		{
			sp = 1.5;
		}
		if(sp < 0.5)
		{
			sp = 0.5;
		}
		if(transformed == 1||transformed == 2)
		{
			hp += 7;
			speedCur *= 1.2;	
			double cooldown;
			cooldown = (double)control.activity.wAthena/10;
			abilityTimerTransformed_pound += cooldown;
			abilityTimerTransformed_hit += cooldown;
			if(abilityTimerTransformed_pound >= 120)
			{
				abilityTimerTransformed_pound = 120;
			}
			if(abilityTimerTransformed_hit >= 20)
			{
				abilityTimerTransformed_hit = 20;
			}
			super.frameCall();
			if(currentFrame<21)
			{
				rads = Math.atan2(touchY, touchX);
				rotation = rads * r2d;
				if(!deleted)
				{
					if(!touching || (Math.abs(touchX) < 5 && Math.abs(touchY) < 5))
					{
						playing = false;
						currentFrame = 0;
					} else
					{
						movement();
					}
				}
			} else
			{
				double distanceFound;
				if(currentFrame == 28)
				{
					for(int i = 0; i < control.enemies.length; i++)
					{
						if(control.enemies[i] != null)
						{
							distanceFound = checkDistance(x + Math.cos(rotation/r2d) * 35, y + Math.sin(rotation/r2d) * 35, control.enemies[i].x, control.enemies[i].y);
							if(distanceFound < 50)
							{
								control.enemies[i].getHit((int)(400*spMod)+400);
								control.activity.playEffect("sword2");
								control.activity.playPlayerEffect();
							}
						}
					}
					control.activity.playEffect("swordmiss");
				}
				if(currentFrame == 53)
				{
					double newX = x + Math.cos(rotation/r2d) * 30;
					double newY = y + Math.sin(rotation/r2d) * 30;
					if(transformed == 2)
					{
						newX = x + Math.cos(rotation/r2d) * 50;
						newY = y + Math.sin(rotation/r2d) * 50;
					}
					for(int i = 0; i<6; i++)
					{	
						control.createPowerBallPlayerAOE(newX-20+control.getRandomInt(40), newY-20+control.getRandomInt(40), 130, true);
					}
					control.createPowerBallPlayerBurst(newX, newY, 0);
					control.activity.playEffect("burst");
					control.activity.playEffect("burst");
					control.activity.playEffect("burst");
					control.activity.playPlayerEffect();
					control.activity.playEffect("swordmiss");
				}
				if(currentFrame == 37)
				{
					currentFrame = 0;
					playing = false;
				}
				if(currentFrame == 57)
				{
					currentFrame = 0;
					playing = false;
				}
			}
		} else
		{
			double cooldown;
			cooldown = (double)control.activity.wAthena*(double)control.activity.wHermes/100;
			if(humanType==1)
			{
				cooldown *= spMod;
			}
			abilityTimer_roll += cooldown*1.4;
			if(abilityTimer_roll >= 120)
			{
				abilityTimer_roll = 120;
			}
			cooldown = (double)control.activity.wAthena/10;
			if(humanType==1)
			{
				cooldown *= Math.pow(spMod, 0.7);
			}
			if(powerUpTimer>0 && powerID == 1)
			{
				cooldown *= 1.5*(double)control.activity.wPoseidon/10;
			}
			abilityTimer_burst += cooldown*1.4;
			abilityTimer_powerBall += cooldown*5;
			if(abilityTimer_burst >= 500)
			{
				abilityTimer_burst = 500;
			}
			if(abilityTimer_powerBall >= 91+(control.activity.bReserve*20))
			{
				abilityTimer_powerBall = 91+(control.activity.bReserve*20);
			}
			if(control.limitSpells)
			{
				abilityTimer_burst = 0;
				abilityTimer_roll = 0;
			}
			rollTimer--;
			if(currentFrame == 30)
			{
				currentFrame = 0;
				playing = false;
			}
			super.frameCall();
			if(rollTimer < 1)
			{
				if(touchingShoot)
	            {
	            	if(abilityTimer_powerBall > 30&&minimumShootTime<1)
	            	{
		            	if(control.activity.shootTapDirectional)
			        	{
			        		double temp1 = rads;
			            	rads = Math.atan2(touchShootY, touchShootX);
			        		releasePowerBall();
			        		control.shootStick.rotation=rads*180/Math.PI;
			        		rads = temp1;
			        	} else
			        	{
			        		releasePowerBall();
			        		control.shootStick.rotation=rads*180/Math.PI;
			        	}
		            	minimumShootTime = 2;
	            	}
	            }
				rads = Math.atan2(touchY, touchX);
				rotation = rads * r2d;
				if(!deleted)
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
			else
			{			
					x += xMoveRoll;
					y += yMoveRoll;
					if(rollTimer == 2)
					{
						weight = 1;
					}
			}
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
	protected void releasePowerBall()
	{
		if(abilityTimer_powerBall > 30)
		{
			if(rollTimer < 0)
			{
					control.createPowerBallPlayer(rads*r2d, projectileSpeed, 130, x, y);
					abilityTimer_powerBall -= 30;
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
			control.startWarning("Cool Down");
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
			control.startWarning("Cool Down");
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
			control.startWarning("Cool Down");
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
				control.createPowerBallPlayerAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), 130, true);
			}
			control.createPowerBallPlayerBurst(x, y, 0);
			abilityTimer_burst -= 300;
			control.activity.playEffect("burst");
			control.activity.playEffect("burst");
			control.activity.playEffect("burst");
			control.activity.playPlayerEffect();
			control.playerBursted = 0;
		} else
		{
			control.startWarning("Cool Down");
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
		        control.startWarning("Stunned!");
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
		if(powerUpTimer>0 && powerID == 2)
		{
			damage *= (0.7/control.activity.wHades*10);
		}
			damage *= damageMultiplier;
			if(humanType == 3)
			{
				damage /= spMod;
			}
			super.getHit(damage);
			sp -= sp*damage/1500;
			if(deleted) control.restartGame();
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
			abilityTimer_powerBall = 90;
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
			control.activity.gameCurrency += 1;
			break;
		case 9:
			control.activity.gameCurrency += 5;
			break;
		case 10:
			control.activity.gameCurrency += 20;
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
	 * returns roll timer
	 * @return roll timer
	 */
	protected double getAbilityTimer_roll() {
		return abilityTimer_roll;
	}
	/**
	 * returns last x
	 * @return last x
	 */
	protected double getXSave() {
		return xSave;
	}
	/**
	 * returns last y
	 * @return last y
	 */
	protected double getYSave() {
		return ySave;
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
	/**
	 * returns burst timer
	 * @return burst timer
	 */
	protected double getAbilityTimer_burst() {
		return abilityTimer_burst;
	}
	/**
	 * returns power ball timer
	 * @return power ball timer
	 */
	protected double getAbilityTimer_powerBall() {
		return abilityTimer_powerBall;
	}
	/**
	 * returns roll timer
	 * @return roll timer
	 */
	protected int getRollTimer() {
		return rollTimer;
	}
	/**
	 * sets rolling timer
	 * @param rollTimer time to set
	 */
	protected void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	/**
	 * sets roll cooldown timer
	 * @param abilityTimer_roll time to set
	 */
	protected void setAbilityTimer_roll(int abilityTimer_roll) {
		this.abilityTimer_roll = abilityTimer_roll;
	}
	/**
	 * sets power ball timer
	 * @param abilityTimer_powerBall time to set
	 */
	protected void setAbilityTimer_burst(int abilityTimer_burst) {
		this.abilityTimer_burst = abilityTimer_burst;
	}
	/**
	 * sets burst timer
	 * @param abilityTimer_powerBall time to set
	 */
	protected void setAbilityTimer_powerBall(int abilityTimer_powerBall) {
		this.abilityTimer_powerBall = abilityTimer_powerBall;
	}
	/**
	 * returns special
	 * @return special
	 */
	protected double getSp() {
		return sp;
	}
}