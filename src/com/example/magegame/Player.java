/*
 * Handles cooldowns and stats for player and executes spells
 */
package com.example.magegame;

import android.util.Log;

public final class Player extends Human
{
	protected double touchY;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	private int mp = 1750;
	private double sp = 0;
	private int mpMax = 3500;
	private double spMax = 1;
	private double abilityTimer_roll = 400;
	private double abilityTimer_teleport = 350;
	private double abilityTimer_burst = 500;
	private double abilityTimer_powerBall = 90;
	private double xSave = 0;
	private double ySave = 0;
	protected boolean teleporting = false;
	protected double touchX;
	protected boolean touching;
	public Player(Controller creator)
	{
		mainController = creator;
		visualImage = mainController.imageLibrary.player_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = 370;
		y = 160;
		thisPlayer = true;
		speedCur = 4.5;
	}
	/*
	 * Counts timers and executes movement and predefined behaviors
	 * @see com.example.magegame.human#frameCall()
	 */
	@
	Override
	public void frameCall()
	{
		sp += 0.001;
		if(humanType==2)
		{
			speedCur = 3.5*(1+sp);
		}
		if(abilityTimer_roll < 400)
		{
			if(humanType==2)
			{
				abilityTimer_roll += 1+sp;
			} else
			{
				abilityTimer_roll ++;
			}
		}
		if(abilityTimer_teleport < 350)
		{
			if(humanType==2)
			{
				abilityTimer_teleport += 1+sp;
			} else
			{
				abilityTimer_teleport ++;
			}
		}
		if(abilityTimer_burst < 500)
		{
			if(humanType==2)
			{
				abilityTimer_burst += 1+sp;
			} else
			{
				abilityTimer_burst ++;
			}
		}
		if(abilityTimer_powerBall < 90)
		{
			if(humanType==2)
			{
				abilityTimer_powerBall += 3*(1+sp);
			} else
			{
				abilityTimer_powerBall += 3;
			}
		}
		rollTimer--;
		if(humanType==1)
		{
			mp += 5 *(1+(2*sp));
		} else
		{
			mp += 5;
		}
		if(currentFrame == 58)
		{
			currentFrame = 0;
			playing = false;
		}
		if(mp > mpMax)
		{
			mp = mpMax;
		}
		if(sp > spMax)
		{
			sp = spMax;
		}
		super.frameCall();
		if(rollTimer < 1)
		{
			rads = Math.atan2(touchY, touchX);
			rotation = rads * r2d;
			if(!deleted)
			{
				if(teleporting)
				{
					mp -= 30;
					if(mp < 0)
					{
						mp = 0;
						x = xSave;
						y = ySave;
						teleporting = false;
						mainController.teleportFinish(x, y);
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
		}
		visualImage = mainController.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	public void movement()
	{
		rads = Math.atan2(touchY, touchX);
		rotation = rads * r2d;
		x += Math.cos(rads) * speedCur;
		y += Math.sin(rads) * speedCur;
	}
	public void releasePowerBall()
	{
		if(abilityTimer_powerBall > 50)
		{
			if(teleporting == false && rollTimer < 0)
			{
				if(mp > 300)
				{					
						mp -= 300;
					playing = false;
					currentFrame = 0;
					mainController.createPowerBallPlayer(rotation, Math.cos(rads) * 10, Math.sin(rads) * 10, 170, x, y);
					abilityTimer_powerBall -= 50;
				}
				else
				{
					mainController.notEnoughMana();
				}
			}
		} else
		{
			mainController.coolDown();
		}
	}
	public void roll()
	{
		if(abilityTimer_roll > 100)
		{
			if(teleporting == false && rollTimer < 0)
			{
				rollTimer = 11;
				playing = true;
				currentFrame = 48;
				xMoveRoll = Math.cos(rads) * speedCur * 2.2;
				yMoveRoll = Math.sin(rads) * speedCur * 2.2;
				abilityTimer_roll -= 100;
			}
		} else
		{
			mainController.coolDown();
		}
	}
	public void teleport(double X, double Y)
	{
		if(abilityTimer_teleport > 150)
		{
			if(rollTimer < 0)
			{
				if(teleporting == false)
				{
					if(mp > 700)
					{
						xSave = x;
						ySave = y;
						teleporting = true;
							mp -= 600;
						mainController.teleportStart(x, y);
						x = 999999999;
					}
					else
					{
						mainController.notEnoughMana();
					}
				}
				else
				{
					x = X;
					y = Y;
					teleporting = false;
					mainController.teleportFinish(x, y);
					abilityTimer_teleport -= 150;
				}
			}
		} else
		{
			mainController.coolDown();
		}
	}
	public void burst()
	{
		if(abilityTimer_burst > 300)
		{
			if(teleporting == false && rollTimer < 0)
			{
				if(mp > 2500)
				{
					mainController.createPowerBallPlayer(0, 10, 0, 130, x, y);
					mainController.createPowerBallPlayer(45, 7, 7, 130, x, y);
					mainController.createPowerBallPlayer(90, 0, 10, 130, x, y);
					mainController.createPowerBallPlayer(135, -7, 7, 130, x, y);
					mainController.createPowerBallPlayer(180, -10, 0, 130, x, y);
					mainController.createPowerBallPlayer(225, -7, -7, 130, x, y);
					mainController.createPowerBallPlayer(270, 0, -10, 130, x, y);
					mainController.createPowerBallPlayer(315, 7, -7, 130, x, y);
					abilityTimer_burst -= 300;
						mp -= 2500;
				}
				else
				{
					mainController.notEnoughMana();
				}
			}
		} else
		{
			mainController.coolDown();
		}
	}
	public void stun()
	{
		rotation = rads * r2d + 180;
        roll();
        currentFrame = 1;
        xMoveRoll /= 3;
        yMoveRoll /= 3;
        abilityTimer_roll += 100;
	}
	@Override
	public void getHit(int damage)
	{
		if(humanType == 3)
		{
			damage /= (1+sp);
		}
		super.getHit(damage);
		if(deleted)
		{
			mainController.activity.startMenu();
		}
	}
	public double getAbilityTimer_roll() {
		return abilityTimer_roll;
	}
	public double getXSave() {
		return xSave;
	}
	public double getYSave() {
		return ySave;
	}
	public boolean isTeleporting() {
		return teleporting;
	}
	public double getAbilityTimer_teleport() {
		return abilityTimer_teleport;
	}
	public double getAbilityTimer_burst() {
		return abilityTimer_burst;
	}
	public double getAbilityTimer_powerBall() {
		return abilityTimer_powerBall;
	}
	public int getRollTimer() {
		return rollTimer;
	}
	public void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	public void setAbilityTimer_roll(int abilityTimer_roll) {
		this.abilityTimer_roll = abilityTimer_roll;
	}
	public void setAbilityTimer_teleport(int abilityTimer_teleport) {
		this.abilityTimer_teleport = abilityTimer_teleport;
	}
	public void setAbilityTimer_burst(int abilityTimer_burst) {
		this.abilityTimer_burst = abilityTimer_burst;
	}
	public void setAbilityTimer_powerBall(int abilityTimer_powerBall) {
		this.abilityTimer_powerBall = abilityTimer_powerBall;
	}
	public int getMp() {
		return mp;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public int getMpMax() {
		return mpMax;
	}
	public void setMpMax(int mpMax) {
		this.mpMax = mpMax;
	}
	public double getSp() {
		return sp;
	}
	public double getSpMax() {
		return spMax;
	}
	public void lowerSp(double lowered) {
		sp -= lowered;
	}
	
}