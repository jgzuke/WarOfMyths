package com.example.magegame;

import android.view.Display;

public final class Player extends human
{
	public Display mDisplay;
	public double mSensorX;
	public double mSensorY;
	public int rollTimer = 0;
	public double xMoveRoll;
	public double yMoveRoll;
	public int Mp = 1750;
	public int Sp = 0;
	public int MpMax = 3500;
	public int SpMax = 3500;
	public int abilityTimer_roll = 400;
	public int abilityTimer_teleport = 350;
	public int abilityTimer_burst = 500;
	public int abilityTimer_powerBall = 90;
	public double XSave = 0;
	public double YSave = 0;
	public boolean teleporting = false;
	public boolean[] keysPressed = new boolean[9];
	public Player(Controller creator)
	{
		mainController = creator;
		humanType = mainController.PlayerType;
		visualImage = mainController.game.imageLibrary.player_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = 370;
		y = 160;
	}@
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
		if(isPlayer)
		{
			if(createSpecialGraphicGainCounter == true)
			{
				mainController.spGraphicPlayer.gaining = true;
				createSpecialGraphicGainCounter = false;
			}
		}
		else
		{
			if(createSpecialGraphicGainCounter == true)
			{
				mainController.spGraphicEnemy.gaining = true;
				createSpecialGraphicGainCounter = false;
			}
		}
		rollTimer--;
		Mp += 5;
		if(currentFrame == 58)
		{
			currentFrame = 0;
			playing = false;
		}
		if(Mp > MpMax)
		{
			Mp = MpMax;
		}
		if(Sp > SpMax)
		{
			Sp = SpMax;
		}
		super.frameCall();
		if(keysPressed[0])
		{
			createSpecialGraphicGainCounter = true;
			playing = false;
			currentFrame = 0;
			Sp += 5;
		}
		if(rollTimer < 1)
		{
			rads = Math.atan2(mSensorY, mSensorX);
			rotation = rads * r2d;
			if(!deleted && !keysPressed[0])
			{
				if(keysPressed[0])
				{
					Sp += 5;
				}
				if(teleporting)
				{
					if(humanType == 1 && Sp > 30)
					{
						Mp -= 10;
						Sp -= 30;
					}
					else
					{
						Mp -= 30;
					}
					if(Mp < 0)
					{
						Mp = 0;
						x = XSave;
						y = YSave;
						teleporting = false;
						mainController.teleportFinish(x, y);
					}
				}
				else
				{
					if(Math.abs(mSensorX) < 0.07 && Math.abs(mSensorY) < 0.07)
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
			if(humanType == 2 && Sp > 45)
			{
				x += xMoveRoll * 1.5;
				y += yMoveRoll * 1.5;
				Sp -= 45;
			}
			else
			{
				x += xMoveRoll;
				y += yMoveRoll;
			}
		}
		visualImage = mainController.game.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	public void movement()
	{
		playing = false;
		if(keysPressed[4])
		{
			rotation += 90;
			rads = rotation / r2d;
			playing = true;
		}
		else
		if(keysPressed[5])
		{
			rotation -= 90;
			rads = rotation / r2d;
			playing = true;
		}
		else
		if(keysPressed[6])
		{
			playing = true;
		}
		else
		if(keysPressed[7])
		{
			rotation += 180;
			rads = rotation / r2d;
			playing = true;
		}
		if(playing)
		{
			x += Math.cos(rads) * speedCur;
			y += Math.sin(rads) * speedCur;
		}
		else
		{
			currentFrame = 0;
		}
	}
	public void releasePowerBall()
	{
		if(teleporting == false && rollTimer < 0)
		{
			if(Mp > 300)
			{
				if(humanType == 1 && Sp > 30)
				{
					Mp -= 280;
					Sp -= 30;
				}
				else
				{
					Mp -= 300;
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
	}
	public void roll()
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
	}
	public void teleport(double X, double Y)
	{
		if(rollTimer < 0)
		{
			if(teleporting == false)
			{
				if(Mp > 700)
				{
					XSave = x;
					YSave = y;
					teleporting = true;
					if(humanType == 2 && Sp > 30)
					{
						Mp -= 580;
						Sp -= 30;
					}
					else
					{
						Mp -= 600;
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
	}
	public void burst()
	{
		if(teleporting == false && rollTimer < 0)
		{
			if(Mp > 2500)
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
				if(humanType == 2 && Sp > 30)
				{
					Mp -= 2480;
					Sp -= 30;
				}
				else
				{
					Mp -= 2500;
				}
			}
			else
			{
				mainController.notEnoughMana();
			}
		}
	}
}