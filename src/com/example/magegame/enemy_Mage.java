/*
 * Main enemy ai, cooldowns stats etc
 * @param reactionTimeRating how many frames the enemy takes to react to certain scenarios
 * @param playerAreaProtected whether or not the player is in a mostly enclosed area
 * @param enemyAreaProtected whether or not the main enemy is in a mostly enclosed area
 */
package com.example.magegame;

import android.util.Log;

public final class Enemy_Mage extends Enemy
{
	private int reactTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	private int mp = 1750;
	private double sp = 0;
	private int mpMax = 3500;
	private double spMax = 1;
	private int rollTimer = 0;
	private int reactionTimeRating;
	private String reaction;
	private double abilityTimer_roll = 400;
	private double abilityTimer_teleport = 350;
	private double abilityTimer_burst = 500;
	private double abilityTimer_powerBall = 90;
	private boolean rolledSideways;
	private boolean playerAreaProtected = true;
	private boolean enemyAreaProtected = true;
	private int rollPathChooseCounter;
	private double rollPathChooseRotationStore;
	private double areaProtectedRads;
	private int areaProtectedRotation;
	private int areaProtectedCount;
	private double teleportAwayChooseDistance;
	private double teleportAwayChooseX;
	private double teleportAwayChooseY;
	public Enemy_Mage(Controller creator)
	{
		super();
		mainController = creator;
		humanType = mainController.getEnemyType();
		visualImage = mainController.imageLibrary.mage_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = 110;
		y = 160;
		speedCur = 4.5;
	}
	/*
	 * Replenishes stats, counts down timers, and checks los etc
	 * @see com.example.magegame.Enemy#frameCall()
	 */
	@
	Override
	public void frameCall()
	{
		Log.e("game", "enemy" + Integer.toString(humanType));
		sp += 0.001;
		if(humanType==2)
		{
			speedCur = 3.5*(1+sp);
		}
		reactionTimeRating = mainController.getDifficultyLevel();
		reactTimer--;
		if(abilityTimer_roll < 400)
		{
			if(humanType==2)
			{
				abilityTimer_roll += mainController.getDifficultyLevelMultiplier()*(1+sp);
			} else
			{
				abilityTimer_roll += mainController.getDifficultyLevelMultiplier();
			}
		}
		if(abilityTimer_teleport < 350)
		{
			if(humanType==2)
			{
				abilityTimer_teleport += mainController.getDifficultyLevelMultiplier()*(1+sp);
			} else
			{
				abilityTimer_teleport += mainController.getDifficultyLevelMultiplier();
			}
		}
		if(abilityTimer_burst < 500)
		{
			if(humanType==2)
			{
				abilityTimer_burst += mainController.getDifficultyLevelMultiplier()*(1+sp);
			} else
			{
				abilityTimer_burst += mainController.getDifficultyLevelMultiplier();
			}
		}
		if(abilityTimer_powerBall < 90)
		{
			if(humanType==2)
			{
				abilityTimer_powerBall += 3*mainController.getDifficultyLevelMultiplier()*(1+sp);
			} else
			{
				abilityTimer_powerBall += 3*mainController.getDifficultyLevelMultiplier();
			}
		}
		rollTimer--;
		if(humanType==1)
		{
			mp += 5 * mainController.getDifficultyLevelMultiplier()*(1+(2*sp));
		} else
		{
			mp += 5 * mainController.getDifficultyLevelMultiplier();
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
		if(getCheckLOSTimer() < 1)
		{
			enemyAreaProtected = checkAreaProtected(x, y);
			playerAreaProtected = checkAreaProtected(mainController.player.x, mainController.player.y);
		}
		if(rollTimer < 1 && getRunTimer() < 1 && reactTimer < 1)
		{
			if(getCheckLOSTimer() < 1)
			{
				checkLOS();
				resetCheckLOSTimer();
			}
			checkDanger();
			if(getPathedToHitLength() > 0)
			{
				if(LOS == true)
				{
					frameReactionsDangerLOS();
				}
				else
				{
					frameReactionsDangerNoLOS();
				}
			}
			else
			{
				if(LOS == true)
				{
					frameReactionsNoDangerLOS();
				}
				else
				{
					frameReactionsNoDangerNoLOS();
				}
			}
		}
		else
		{
			if(reactTimer < 1)
			{
				if(getRunTimer() < 1)
				{
						x += xMoveRoll;
						y += yMoveRoll;
				}
				else
				{
						x += Math.cos(rads) * speedCur;
						y += Math.sin(rads) * speedCur;
				}
				if(rollTimer == 1 || getRunTimer() == 1)
				{
					playing = false;
					currentFrame = 0;
					rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
					rotation = rads * r2d;
				}
			}
			else
			{
				if(reactTimer == 1)
				{
					react();
				}
			}
		}
		super.frameCall();
	}
	/*
	 * Starts counting timer until the specific reaction is performed
	 */
	public void setReactTimer()
	{
		if(Math.random() > (reactionTimeRating / 10))
		{
			reactTimer = Math.round(reactionTimeRating / 2) + 2;
		}
		else
		{
			reactTimer = reactionTimeRating + 10;
		}
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
	/*
	 * Converts reactions from Strings to integers so they can be switched through (strings were used to make code more understandable)
	 * @return Returns reaction as an integer
	 */
	public int convertReactionToInt(String reaction)
	{
		int react = 0;
		if(reaction.equalsIgnoreCase("Power Ball")) react = 1;
		if(reaction.equalsIgnoreCase("Teleport Away")) react = 2;
		if(reaction.equalsIgnoreCase("Teleport Towards")) react = 3;
		if(reaction.equalsIgnoreCase("Roll")) react = 4;
		if(reaction.equalsIgnoreCase("Roll Away")) react = 5;
		if(reaction.equalsIgnoreCase("Roll Towards")) react = 6;
		if(reaction.equalsIgnoreCase("Roll Sideways")) react = 7;
		if(reaction.equalsIgnoreCase("Run Away")) react = 8;
		if(reaction.equalsIgnoreCase("Power Burst")) react = 9;
		return react;
	}@
	Override
	public void frameReactionsDangerLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 80)
		{
			rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
			rotation = rads * r2d;
			if(enemyAreaProtected == true)
			{
				distanceFound = checkDistance(x, y, mainController.player.x, mainController.player.y);
				if(distanceFound < 30)
				{
					frameReactionsNoDangerLOS();
				}
				else
				{
					if(distanceFound < 80)
					{
						setReactTimer();
						reaction = "Roll Towards";
					}
					else
					{
						setReactTimer();
						reaction = "Roll Sideways";
					}
				}
			}
			else
			{
				setReactTimer();
				reaction = "Roll Sideways";
			}
		}
		else
		{
			frameReactionsNoDangerLOS();
		}
	}@
	Override
	public void frameReactionsDangerNoLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 80)
		{
			rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
			rotation = rads * r2d;
			setReactTimer();
			reaction = "Roll Sideways";
		}
		else
		{
			frameReactionsNoDangerNoLOS();
		}
	}@
	Override
	public void frameReactionsNoDangerLOS()
	{
		distanceFound = checkDistance(x, y, mainController.player.x, mainController.player.y);
		if(distanceFound < 30)
		{
			if(playerAreaProtected == true)
			{
				if(mp > 2510 && abilityTimer_powerBall >= 400)
				{
					setReactTimer();
					reaction = "Power Burst";
				}
				else
				{
					if(mp < 2300)
					{
						if(mp > 650 && abilityTimer_teleport >= 250)
						{
							setReactTimer();
							reaction = "Teleport Away";
						}
						else
						{
							setReactTimer();
							reaction = "Roll Away";
						}
					}
				}
			}
			else
			{
				setReactTimer();
				reaction = "Roll Away";
			}
		}
		else
		{
			distanceFound = checkDistance(x, y, mainController.player.x, mainController.player.y);
			if(distanceFound < 80)
			{
				setReactTimer();
				reaction = "Run Away";
			}
			else
			{
				if(mp > 400 && abilityTimer_powerBall >= 50)
				{
					setReactTimer();
					reaction = "Power Ball";
				}
			}
		}
	}@
	Override
	public void frameReactionsNoDangerNoLOS()
	{
		if(playerAreaProtected == true)
		{
			if(mp > 3150 && abilityTimer_teleport >= 250 && abilityTimer_burst >= 400)
			{
				setReactTimer();
				reaction = "Teleport Towards";
			}
		}
	}
	/*
	 * Checks whether the point given is in a mostly enclosed area
	 * @return returns whether point is protected
	 */
	public boolean checkAreaProtected(double X, double Y)
	{
		boolean areaProtected = false;
		areaProtectedRotation = 0;
		areaProtectedCount = 0;
		while(areaProtectedRotation < 360)
		{
			areaProtectedRads = areaProtectedRotation / r2d;
			if(checkObstructions(X, Y, Math.cos(areaProtectedRads) * 20, Math.sin(areaProtectedRads) * 20, 50, 20))
			{
				areaProtectedCount++;
			}
			areaProtectedRotation += 45;
			if(areaProtectedCount > 3)
			{
				areaProtectedRotation = 370;
				areaProtected = true;
			}
		}
		return areaProtected;
	}
	/*
	 * Rolls away from player
	 */
	public void rollAway()
	{
		rads = Math.atan2(-(mainController.player.y - y), -(mainController.player.x - x));
		rotation = rads * r2d;
		if(!checkObstructions(x, y, Math.cos(rads) * 12, Math.sin(rads) * 12, 42, 12))
		{
			roll();
		}
		else
		{
			rollPathChooseCounter = 0;
			rollPathChooseRotationStore = rotation;
			while(rollPathChooseCounter < 180)
			{
				rollPathChooseCounter += 10;
				rotation = rollPathChooseRotationStore + rollPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 12, Math.sin(rads) * 12, 42, 12))
				{
					roll();
					rollPathChooseCounter = 180;
				}
				else
				{
					rotation = rollPathChooseRotationStore - rollPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 12, Math.sin(rads) * 12, 42, 12))
					{
						roll();
						rollPathChooseCounter = 180;
					}
				}
			}
		}
	}
	/*
	 * Rolls towards player
	 */
	public void rollTowards()
	{
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		roll();
	}
	/*
	 * Rolls as close to perpendicular as possible to the player
	 */
	public void rollSideways()
	{
		rolledSideways = true;
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		rads = (rotation + 90) / r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 12, Math.sin(rads) * 12, 42, 12))
		{
			rads = (rotation - 90) / r2d;
			if(checkObstructions(x, y, Math.cos(rads) * 12, Math.sin(rads) * 12, 42, 12))
			{
				rolledSideways = false;
			}
			else
			{
				rotation -= 90;
				rads = rotation / r2d;
				roll();
			}
		}
		else
		{
			rads = (rotation - 90) / r2d;
			if(checkObstructions(x, y, Math.cos(rads) * 12, Math.sin(rads) * 12, 42, 12))
			{
				rotation += 90;
				rads = rotation / r2d;
				roll();
			}
			else
			{
				if(Math.random() > 0.5)
				{
					rotation += 90;
					roll();
				}
				else
				{
					rotation -= 90;
					roll();
				}
			}
		}
	}
	/*
	 * Rolls in the current direction to enemy is rotated
	 */
	public void roll()
	{
		rollTimer = 11;
		playing = true;
		currentFrame = 48;
		xMoveRoll = Math.cos(rads) * speedCur * 2.2;
		yMoveRoll = Math.sin(rads) * speedCur * 2.2;
	}
	public void teleport(int newX, int newY)
	{
		mainController.teleportStart(x, y);
		x = newX;
		y = newY;
		mainController.teleportFinish(x, y);
		
			mp -= 600;
	}
	/* 
	 * Teleports to players position
	 */
	public void teleportTowards()
	{
		rotation = Math.random() * 360;
		rads = rotation / r2d;
		if(!mainController.player.isTeleporting())
		{
			teleport((int)(mainController.player.x + Math.cos(rads) * 10), (int)(mainController.player.y + Math.sin(rads) * 10));
		}
		setReactTimer();
		reaction = "Power Burst";
		abilityTimer_teleport -= 250;
	}
	/* 
	 * Teleports to farthest defined teleport spot in Controller
	 */
	public void teleportAway()
	{
		distanceFound = checkDistance(x, y, mainController.getTeleportSpots(0, 0), mainController.getTeleportSpots(1, 0));
		teleportAwayChooseDistance = distanceFound;
		teleportAwayChooseX = mainController.getTeleportSpots(0, 0);
		teleportAwayChooseY = mainController.getTeleportSpots(1, 0);
		distanceFound = checkDistance(x, y, mainController.getTeleportSpots(0, 1), mainController.getTeleportSpots(1, 1));
		if(distanceFound > teleportAwayChooseDistance)
		{
			teleportAwayChooseDistance = distanceFound;
			teleportAwayChooseX = mainController.getTeleportSpots(0, 1);
			teleportAwayChooseY = mainController.getTeleportSpots(1, 1);
		}
		distanceFound = checkDistance(x, y, mainController.getTeleportSpots(0, 2), mainController.getTeleportSpots(1, 2));
		if(distanceFound > teleportAwayChooseDistance)
		{
			teleportAwayChooseDistance = distanceFound;
			teleportAwayChooseX = mainController.getTeleportSpots(0, 2);
			teleportAwayChooseY = mainController.getTeleportSpots(1, 2);
		}
		distanceFound = checkDistance(x, y, mainController.getTeleportSpots(0, 3), mainController.getTeleportSpots(1, 3));
		if(distanceFound > teleportAwayChooseDistance)
		{
			teleportAwayChooseDistance = distanceFound;
			teleportAwayChooseX = mainController.getTeleportSpots(0, 3);
			teleportAwayChooseY = mainController.getTeleportSpots(1, 3);
		}
		teleport((int) teleportAwayChooseX, (int) teleportAwayChooseY);
		abilityTimer_teleport -= 250;
	}
	/*
	 * Releases stored powerBall towards player
	 */
	public void releasePowerBall()
	{
		
			mp -= 300;
		playing = false;
		currentFrame = 0;
		if(mainController.player.isTeleporting())
		{
			rads = Math.atan2((mainController.player.getXSave() - y), (mainController.player.getYSave() - x));
		}
		else
		{
			rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		}
		rotation = rads * r2d;
		mainController.createPowerBallEnemy(rotation, Math.cos(rads) * 10, Math.sin(rads) * 10, 170, x, y);
		abilityTimer_powerBall -= 50;
	}
	public void powerBurst()
	{
		mp -= 2500;
		mainController.createPowerBallEnemy(0, 10, 0, 130, x, y);
		mainController.createPowerBallEnemy(45, 7, 7, 130, x, y);
		mainController.createPowerBallEnemy(90, 0, 10, 130, x, y);
		mainController.createPowerBallEnemy(135, -7, 7, 130, x, y);
		mainController.createPowerBallEnemy(180, -10, 0, 130, x, y);
		mainController.createPowerBallEnemy(225, -7, -7, 130, x, y);
		mainController.createPowerBallEnemy(270, 0, -10, 130, x, y);
		mainController.createPowerBallEnemy(315, 7, -7, 130, x, y);
		abilityTimer_burst -= 400;
	}
	/*
	 * Acts out stored reaction
	 */
	public void react()
	{
		switch(convertReactionToInt(reaction))
		{
		case 1:
			releasePowerBall();
			break;
		case 2:
			teleportAway();
			break;
		case 3:
			teleportTowards();
			break;
		case 4:
			roll();
			break;
		case 5:
			rollAway();
			break;
		case 6:
			rollTowards();
			break;
		case 7:
			rollSideways();
			if(rolledSideways == false)
			{
				if(mp > 650)
				{
					teleportAway();
				}
				else
				{
					rollTowards();
				}
			}
			break;
		case 8:
			runAway();
			run();
			break;
		case 9:
			powerBurst();
			break;
		}
	}
	public void stun()
	{
        rotation =rads * r2d + 180;
        roll();                                    
        currentFrame = 1;
        xMoveRoll /= 3;
        yMoveRoll /= 3;                                    
        reactTimer = 0;
	}
	public int getReactTimer() {
		return reactTimer;
	}
	public double getXMoveRoll() {
		return xMoveRoll;
	}
	public double getYMoveRoll() {
		return yMoveRoll;
	}
	public int getMp() {
		return mp;
	}
	public double getSp() {
		return sp;
	}
	public int getMpMax() {
		return mpMax;
	}
	public double getSpMax() {
		return spMax;
	}
	public int getRollTimer() {
		return rollTimer;
	}
	public void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	public void lowerSp(double lowered) {
		sp -= lowered;
	}
	public void setMp(int mp) {
		this.mp = mp;
	}
	public void setSp(int sp) {
		this.sp = sp;
	}
	public void setMpMax(int mpMax) {
		this.mpMax = mpMax;
	}
	public void setSpMax(int spMax) {
		this.spMax = spMax;
	}
}