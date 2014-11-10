/*
 * Handles cooldowns and stats for player and executes spells
 */
package com.magegame;

import android.util.Log;

public final class Player extends Human
{
	protected double touchY;
	private double damageMultiplier = 1;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 0;
	protected double spMod = 1;
	protected double spChangeForType;
	private double abilityTimer_roll = 0;
	private double abilityTimer_teleport = 0;
	private double abilityTimer_burst = 0;
	protected double abilityTimer_powerBall = 0;
	private double xSave = 0;
	private double ySave = 0;
	protected boolean teleporting = false;
	private boolean usedDionysusWine = false;
	protected int projectileSpeed = 13;
	protected double touchX;
	protected boolean touching;
	protected boolean touchingShoot;
	protected double touchShootX;
	protected double touchShootY;
	protected int powerUpTimer = 0;
	protected int powerID = 0;
	private int minimumShootTime = 5;
	private double hpAccurate;
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
	public void resetVariables()
	{
		damageMultiplier = 1;
		rollTimer = 0;
		sp = 0.5;
		abilityTimer_roll = 60;
		abilityTimer_teleport = 175;
		abilityTimer_burst = 250;
		abilityTimer_powerBall = 0;
		teleporting = false;
		usedDionysusWine = false;
		projectileSpeed = 10;
		touching = false;
		x = 370;
		y = 160;
		hp = (int)(7000 * Math.pow((double)control.activity.wHephaestus/10, 0.9));
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
	/*
	 * Counts timers and executes movement and predefined behaviors
	 * @see com.example.magegame.human#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
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
		spMod = 1+(sp*spChangeForType);
		speedCur = 4.7*Math.pow((double)control.activity.wHermes/10, 0.4);
		if(humanType==2)
		{
			speedCur *= Math.pow(spMod, 0.5);
		}
		if(powerUpTimer>0 && powerID == 3)
		{
			speedCur += 0.7*(double)control.activity.wZues/10;
		}
		double cooldown;
		cooldown = (double)control.activity.wAthena*(double)control.activity.wHermes/100;
		if(humanType==1)
		{
			cooldown *= spMod;
		}
		if(abilityTimer_roll < 120)
		{
			abilityTimer_roll += cooldown;
		}
		if(abilityTimer_teleport < 350)
		{			
			abilityTimer_teleport += cooldown;
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
		if(abilityTimer_burst < 500)
		{
			abilityTimer_burst += cooldown;
		}
		if(abilityTimer_powerBall < 90)
		{
			abilityTimer_powerBall += cooldown*3;
		}
		if(control.limitSpells)
		{
			abilityTimer_teleport = 0;
			abilityTimer_burst = 0;
			abilityTimer_roll = 0;
		}
		rollTimer--;
		if(currentFrame == 30)
		{
			currentFrame = 0;
			playing = false;
		}
		if(sp > 1)
		{
			sp = 1;
		}
		if(sp < 0)
		{
			sp = 0;
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
				if(teleporting)
				{
					hp -= 10;
					if(hp < 0)
					{
						getHit(1000);
					}
				}
				else
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
		visualImage = control.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	protected void movement()
	{
		playing = true;
		rads = Math.atan2(touchY, touchX);
		rotation = rads * r2d;
		x += Math.cos(rads) * speedCur;
		y += Math.sin(rads) * speedCur;
	}
	protected void releasePowerBall()
	{
		if(abilityTimer_powerBall > 30)
		{
			if(teleporting == false && rollTimer < 0)
			{
					control.createPowerBallPlayer(rads*r2d, Math.cos(rads) * projectileSpeed, Math.sin(rads) * projectileSpeed, 130, x, y);
					abilityTimer_powerBall -= 30;
					control.activity.playEffect("shoot");
			}
		} else
		{
			//control.coolDown();
		}
	}
	protected void roll()
	{
		if(abilityTimer_roll > 50)
		{
			if(teleporting == false && rollTimer < 0)
			{
				double speed;
				if(humanType==2)
				{
					speed = 4*Math.pow(spMod, 0.5)*Math.pow((double)control.activity.wHermes/10, 0.5);
				} else
				{
					speed = 4*Math.pow((double)control.activity.wHermes/10, 0.5);
				}
				rollTimer = 11;
				playing = true;
				currentFrame = 21;
				xMoveRoll = Math.cos(rads) * Math.pow(speed, 0.5)*4;
				yMoveRoll = Math.sin(rads) * Math.pow(speed, 0.5)*4;
				abilityTimer_roll -= 50;
				weight = 10;
			}
		} else
		{
			control.coolDown();
		}
	}
	protected void teleport(double X, double Y)
	{
		if(abilityTimer_teleport > 250)
		{
			if(rollTimer < 0)
			{
				//control.teleportStart(x, y);
				double newX = x + Math.cos(rads)*50;
				double newY = y + Math.sin(rads)*50;
				double centreX = newX;
				double centreY = newY;
				if(!control.checkHitBack(newX, newY))
				{
					x = newX;
					y = newY;
				} else if(!control.checkHitBack(newX+20, newY))
				{
					x = newX+20;
					y = newY;
				} else if(!control.checkHitBack(newX, newY+20))
				{
					x = newX;
					y = newY+20;
				} else if(!control.checkHitBack(newX-20, newY))
				{
					x = newX-20;
					y = newY;
				} else if(!control.checkHitBack(newX, newY-20))
				{
					x = newX;
					y = newY-20;
				} else
				{
					abilityTimer_teleport+=250;
				}
				//control.teleportFinish(x, y);
				abilityTimer_teleport -= 250;
				control.createTeleport(x - Math.cos(rads)*25, y - Math.sin(rads)*25, rotation);
				control.activity.playEffect("teleport");
				/*if(teleporting == false)
				{
						xSave = x;
						ySave = y;
						teleporting = true;
						control.teleportStart(x, y);
						x = 999999999;
				} else
				{
					x = X;
					y = Y;
					teleporting = false;
					control.teleportFinish(x, y);
					abilityTimer_teleport -= 250;
				}*/
			}
		} else
		{
			control.coolDown();
		}
	}
	protected void burst()
	{
		if(abilityTimer_burst > 400)
		{
			if(teleporting == false && rollTimer < 0)
			{
				for(int i = 0; i<6; i++)
				{
					control.createPowerBallPlayerAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), 130);
				}
				control.createPowerBallPlayerBurst(x, y, 0);
				abilityTimer_burst -= 400;
				control.activity.playEffect("burst");
				control.activity.playEffect("burst");
				control.activity.playEffect("burst");
				if(control.playerType==0)
				{
					control.activity.playEffect("burn");
				} else if(control.playerType==1)
				{
					control.activity.playEffect("water");
				} else if(control.playerType==2)
				{
					control.activity.playEffect("electric");
				} else
				{
					control.activity.playEffect("earth");
				}
			}
		} else
		{
			control.coolDown();
		}
	}
	protected void stun()
	{
		rotation = rads * r2d + 180;
        roll();
        currentFrame = 0;
        xMoveRoll /= 3;
        yMoveRoll /= 3;
        abilityTimer_roll += 20;
	}
	@Override
	protected void getHit(int damage)
	{
		damage *= 0.7;
		if(powerUpTimer>0 && powerID == 2)
		{
			damage *= (0.7/control.activity.wHephaestus*10);
		}
			damage *= damageMultiplier;
			if(humanType == 3)
			{
				damage /= spMod;
			}
			super.getHit(damage);
			sp -= sp*damage/1500;
			if(deleted)
			{
				/*if(control.activity.useAmbrosia>0)
				{
					control.activity.useAmbrosia --;
					hp = getHpMax()/2;
					deleted = false;
				} else if(control.activity.useDionysusWine>0)
				{
					control.activity.useDionysusWine --;
					hp = getHpMax();
					usedDionysusWine = true;
					damageMultiplier = 0;
					deleted = false;
				} else
				{*/
					control.activity.loseFight();
				//}
			}
	}
	protected void getPowerUp(int PowerID)
	{
		switch(PowerID)
		{
		case 1:
			hp += getHpMax()/2;
			if(hp>getHpMax())hp=getHpMax();
			break;
		case 2:
			abilityTimer_roll = 120;
			abilityTimer_teleport = 350;
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
		}
	}
	protected double getAbilityTimer_roll() {
		return abilityTimer_roll;
	}
	protected double getXSave() {
		return xSave;
	}
	protected double getYSave() {
		return ySave;
	}
	protected boolean isTeleporting() {
		return teleporting;
	}
	protected double getAbilityTimer_teleport() {
		return abilityTimer_teleport;
	}
	protected double getAbilityTimer_burst() {
		return abilityTimer_burst;
	}
	protected double getAbilityTimer_powerBall() {
		return abilityTimer_powerBall;
	}
	protected int getRollTimer() {
		return rollTimer;
	}
	protected void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	protected void setAbilityTimer_roll(int abilityTimer_roll) {
		this.abilityTimer_roll = abilityTimer_roll;
	}
	protected void setAbilityTimer_teleport(int abilityTimer_teleport) {
		this.abilityTimer_teleport = abilityTimer_teleport;
	}
	protected void setAbilityTimer_burst(int abilityTimer_burst) {
		this.abilityTimer_burst = abilityTimer_burst;
	}
	protected void setAbilityTimer_powerBall(int abilityTimer_powerBall) {
		this.abilityTimer_powerBall = abilityTimer_powerBall;
	}
	protected double getSp() {
		return sp;
	}
}