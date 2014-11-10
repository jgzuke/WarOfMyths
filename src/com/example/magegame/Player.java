/*
 * Handles cooldowns and stats for player and executes spells
 */
package com.example.magegame;

public final class Player extends Human
{
	protected double touchY;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 0;
	protected double spMod = 1;
	protected double spChangeForType;
	private double abilityTimer_roll = 0;
	private double abilityTimer_teleport = 0;
	private double abilityTimer_burst = 0;
	private double abilityTimer_powerBall = 0;
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
		hp = (int)(7000 * mainController.activity.worshipHephaestus);
		setHpMax(hp);
	}
	/*
	 * Counts timers and executes movement and predefined behaviors
	 * @see com.example.magegame.human#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		mainController.player.sp += 0.0001;
		spMod = 1+(sp*spChangeForType);
		if(humanType==2)
		{
			speedCur = 4*Math.pow(spMod, 0.5)*Math.pow(mainController.activity.worshipHermes, 0.5);
		} else
		{
			speedCur = 4*Math.pow(mainController.activity.worshipHermes, 0.5);
		}
		if(abilityTimer_roll < 120)
		{
			if(humanType==2)
			{
				abilityTimer_roll += spMod*mainController.activity.worshipAthena;
			} else
			{
				abilityTimer_roll += mainController.activity.worshipAthena;
			}
		}
		if(abilityTimer_teleport < 350)
		{
			if(humanType==2)
			{
				abilityTimer_teleport += spMod*mainController.activity.worshipAthena;
			} else
			{
				abilityTimer_teleport += mainController.activity.worshipAthena;
			}
		}
		if(abilityTimer_burst < 500)
		{
			if(humanType==1)
			{
				abilityTimer_burst += spMod*mainController.activity.worshipAthena;
			} else
			{
				abilityTimer_burst += mainController.activity.worshipAthena;
			}
		}
		if(abilityTimer_powerBall < 40)
		{
			if(humanType==1)
			{
				abilityTimer_powerBall += 3*spMod*mainController.activity.worshipAthena;
			} else
			{
				abilityTimer_powerBall += 3*mainController.activity.worshipAthena;
			}
		}
		rollTimer--;
		if(currentFrame == 58)
		{
			currentFrame = 0;
			playing = false;
		}
		if(sp > 1)
		{
			sp = 1;
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
		}
		visualImage = mainController.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	protected void movement()
	{
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
					playing = false;
					currentFrame = 0;
					mainController.createPowerBallPlayer(rotation, Math.cos(rads) * 10, Math.sin(rads) * 10, 130, x, y);
					abilityTimer_powerBall -= 30;
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void roll()
	{
		if(abilityTimer_roll > 50)
		{
			if(teleporting == false && rollTimer < 0)
			{
				rollTimer = 11;
				playing = true;
				currentFrame = 48;
				xMoveRoll = Math.cos(rads) * speedCur * 2.2;
				yMoveRoll = Math.sin(rads) * speedCur * 2.2;
				abilityTimer_roll -= 50;
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void teleport(double X, double Y)
	{
		if(abilityTimer_teleport > 250)
		{
			if(rollTimer < 0)
			{
				if(teleporting == false)
				{
						xSave = x;
						ySave = y;
						teleporting = true;
						mainController.teleportStart(x, y);
						x = 999999999;
				}
				else
				{
					x = X;
					y = Y;
					teleporting = false;
					mainController.teleportFinish(x, y);
					abilityTimer_teleport -= 250;
				}
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void burst()
	{
		if(abilityTimer_burst > 400)
		{
			if(teleporting == false && rollTimer < 0)
			{
					mainController.createPowerBallPlayer(0, 10, 0, 130, x, y);
					mainController.createPowerBallPlayer(45, 7, 7, 130, x, y);
					mainController.createPowerBallPlayer(90, 0, 10, 130, x, y);
					mainController.createPowerBallPlayer(135, -7, 7, 130, x, y);
					mainController.createPowerBallPlayer(180, -10, 0, 130, x, y);
					mainController.createPowerBallPlayer(225, -7, -7, 130, x, y);
					mainController.createPowerBallPlayer(270, 0, -10, 130, x, y);
					mainController.createPowerBallPlayer(315, 7, -7, 130, x, y);
					abilityTimer_burst -= 400;
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void stun()
	{
		rotation = rads * r2d + 180;
        roll();
        currentFrame = 1;
        xMoveRoll /= 3;
        yMoveRoll /= 3;
        abilityTimer_roll += 20;
	}
	@Override
	protected void getHit(int damage)
	{
		if(humanType == 3)
		{
			damage /= spMod;
		}
		super.getHit(damage);
		sp -= sp*damage/2000;
		if(deleted)
		{
			mainController.activity.startMenu();
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