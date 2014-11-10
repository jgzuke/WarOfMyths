/*
 * Handles cooldowns and stats for player and executes spells
 */
package com.example.magegame;

public final class Player extends Human
{
	protected double touchY;
	private int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	private int mp = 1750;
	private int sp = 0;
	private int mpMax = 3500;
	private int spMax = 3500;
	private int abilityTimer_roll = 400;
	private int abilityTimer_teleport = 350;
	private int abilityTimer_burst = 500;
	private int abilityTimer_powerBall = 90;
	private double xSave = 0;
	private double ySave = 0;
	protected boolean teleporting = false;
	protected boolean chargingSp = false;
	protected double touchX;
	protected boolean touching;
	public Player(Controller creator)
	{
		mainController = creator;
		humanType = mainController.getPlayerType();
		visualImage = mainController.imageLibrary.player_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = 370;
		y = 160;
		thisPlayer = true;
	}
	/*
	 * Counts timers and executes movement and predefined behaviors
	 * @see com.example.magegame.human#frameCall()
	 */
	@
	Override
	public void frameCall()
	{
		if(abilityTimer_roll < 400)
		{
			abilityTimer_roll++;
		}
		if(abilityTimer_teleport < 350)
		{
			abilityTimer_teleport++;
		}
		if(abilityTimer_burst < 500)
		{
			abilityTimer_burst++;
		}
		if(abilityTimer_powerBall < 90)
		{
			abilityTimer_powerBall++;
		}
			if(createSpecialGraphicGainCounter == true)
			{
				mainController.spGraphicPlayer.setGaining(true);
				createSpecialGraphicGainCounter = false;
			}
		rollTimer--;
		mp += 5;
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
		if(chargingSp)
		{
			createSpecialGraphicGainCounter = true;
			playing = false;
			currentFrame = 0;
			sp += 5;
		}
		if(rollTimer < 1)
		{
			rads = Math.atan2(touchY, touchX);
			rotation = rads * r2d;
			if(!deleted && !chargingSp)
			{
				if(chargingSp)
				{
					sp += 5;
				}
				if(teleporting)
				{
					if(humanType == 1 && sp > 30)
					{
						mp -= 10;
						sp -= 30;
					}
					else
					{
						mp -= 30;
					}
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
					if(!touching || (Math.abs(touchX) < 0.07 && Math.abs(touchY) < 0.07))
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
			if(humanType == 2 && sp > 45)
			{
				x += xMoveRoll * 1.5;
				y += yMoveRoll * 1.5;
				sp -= 45;
			}
			else
			{
				x += xMoveRoll;
				y += yMoveRoll;
			}
		}
		visualImage = mainController.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	public void movement()
	{
		rads = Math.atan2(touchY-y, touchX-x);
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
					if(humanType == 1 && sp > 30)
					{
						mp -= 280;
						sp -= 30;
					}
					else
					{
						mp -= 300;
					}
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
				xMoveRoll = Math.cos(rads) * speedCur * 2;
				yMoveRoll = Math.sin(rads) * speedCur * 2;
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
						if(humanType == 2 && sp > 30)
						{
							mp -= 580;
							sp -= 30;
						}
						else
						{
							mp -= 600;
						}
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
					mainController.createPowerBallPlayer(0, 10, 0, 170, x, y);
					mainController.createPowerBallPlayer(45, 7, 7, 170, x, y);
					mainController.createPowerBallPlayer(90, 0, 10, 170, x, y);
					mainController.createPowerBallPlayer(135, -7, 7, 170, x, y);
					mainController.createPowerBallPlayer(180, -10, 0, 170, x, y);
					mainController.createPowerBallPlayer(225, -7, -7, 170, x, y);
					mainController.createPowerBallPlayer(270, 0, -10, 170, x, y);
					mainController.createPowerBallPlayer(315, 7, -7, 170, x, y);
					abilityTimer_burst -= 300;
					if(humanType == 2 && sp > 30)
					{
						mp -= 2480;
						sp -= 30;
					}
					else
					{
						mp -= 2500;
					}
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
	public int getAbilityTimer_roll() {
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
	public int getAbilityTimer_teleport() {
		return abilityTimer_teleport;
	}
	public int getAbilityTimer_burst() {
		return abilityTimer_burst;
	}
	public int getAbilityTimer_powerBall() {
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
	public int getSp() {
		return sp;
	}
	public int getSpMax() {
		return spMax;
	}
	public void lowerSp(int lowered) {
		sp -= lowered;
	}
	
}