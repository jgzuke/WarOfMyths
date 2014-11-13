/**
 * Main enemy ai, cooldowns stats etc
 * @param reactionTimeRating how many frames the enemy takes to react to certain scenarios
 * @param playerAreaProtected whether or not the player is in a mostly enclosed area
 * @param enemyAreaProtected whether or not the main enemy is in a mostly enclosed area
 */
package com.magegame;

public final class Enemy_Mage extends Enemy
{
	private double projectileVelocity;
	private int reactTimer = 0;
	private int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	private int reactionTimeRating;
	private int minimumShootTime = 5;
	private String reaction;
	private double abilityTimer_burst = 0;
	private double abilityTimer_Proj_Tracker = 0;
	private boolean rolledSideways;
	private boolean enemyAreaProtected = true;
	private int rollPathChooseCounter;
	private double rollPathChooseRotationStore;
	private double areaProtectedRads;
	private int areaProtectedRotation;
	private int areaProtectedCount;
	/**
	 * sets weight, worth, position, image, and health
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 */
	public Enemy_Mage(Controller creator, int setX, int setY)
	{
		super(creator);
		visualImage = control.imageLibrary.mage_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = setX;
		y = setY;
		lastPlayerX = x;
		lastPlayerY = y;
		hp = 1900;
		baseHp();
		setHpMax(hp);
		worth = 6;
		weight = 0.8;
	}
	/**
	 * Replenishes stats, counts down timers, and checks los etc
	 */
	@
	Override
	protected void frameCall()
	{
		minimumShootTime--;
		visualImage = control.imageLibrary.mage_Image[currentFrame];
		rollTimer--;
		reactionTimeRating = control.getDifficultyLevel();
		reactTimer--;
		if(abilityTimer_burst < 500)
		{
			abilityTimer_burst += control.getDifficultyLevelMultiplier();
		}
		if(abilityTimer_Proj_Tracker < 90)
		{
			abilityTimer_Proj_Tracker += 3*Math.pow(control.getDifficultyLevelMultiplier(), 0.5);
		}
		if(currentFrame == 30)
		{
			currentFrame = 0;
			playing = false;
		}
		if(getCheckLOSTimer() < 1)
		{
			enemyAreaProtected = checkAreaProtected(x, y);
		}
		if(rollTimer < 1)
		{
			if(getRunTimer()>0)
			{
				x += Math.cos(rotation/r2d) * speedCur;
				y += Math.sin(rotation/r2d) * speedCur;
			} else
			{
				currentFrame = 0;
				playing = false;
				if(control.getRandomInt(10) == 0)
				{
					runRandom();
				}
			}
		}
		if(rollTimer < 1 && reactTimer < 1)
		{
			if(getCheckLOSTimer() < 1)
			{
				checkLOS();
				if(LOS)
				{
					lastPlayerX = control.player.x;
					lastPlayerY = control.player.y;
					checkedPlayerLast = false;
				}
				resetCheckLOSTimer();
			}
			checkDanger();
			if(getPathedToHitLength() > 0)
			{
				if(LOS)
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
				x += xMoveRoll;
				y += yMoveRoll;
				if(rollTimer == 1)
				{
					playing = false;
					currentFrame = 0;
					rads = Math.atan2((control.player.y - y), (control.player.x - x));
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
	/**
	 * Starts counting timer until the specific reaction is performed
	 */
	protected void setReactTimer()
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
	/**
	 * Converts reactions from Strings to integers so they can be switched through (strings were used to make code more understandable)
	 * @return Returns reaction as an integer
	 */
	protected int convertReactionToInt(String reaction)
	{
		int react = 0;
		if(reaction.equalsIgnoreCase("Power Ball")) react = 1;
		if(reaction.equalsIgnoreCase("Roll")) react = 2;
		if(reaction.equalsIgnoreCase("Roll Away")) react = 3;
		if(reaction.equalsIgnoreCase("Roll Towards")) react = 4;
		if(reaction.equalsIgnoreCase("Roll Sideways")) react = 5;
		if(reaction.equalsIgnoreCase("Run Away")) react = 6;
		if(reaction.equalsIgnoreCase("Power Burst")) react = 7;
		return react;
	}
	@ Override
	protected void frameReactionsDangerLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 80)
		{
			rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
			rotation = rads * r2d;
			if(enemyAreaProtected == true)
			{
				distanceFound = checkDistance(x, y, control.player.x, control.player.y);
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
	}
	@Override
	protected void frameReactionsDangerNoLOS()
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
	}
	@ Override
	protected void frameReactionsNoDangerLOS()
	{
		double playerDistance = checkDistance(x, y, control.player.x, control.player.y);
		if(playerDistance < 40)
		{
			if(abilityTimer_burst > 400)
			{
				burst();
			}
			else
			{
				setReactTimer();
				reaction = "Roll Away";
			}
		} else
		{
			if(playerDistance < 90)
			{
				if(abilityTimer_burst > 400)
				{
					setReactTimer();
					reaction = "Roll Towards";
				} else if(abilityTimer_Proj_Tracker > 30)
				{
					releaseProj_Tracker();
				}
			} else 
			{
				if(abilityTimer_Proj_Tracker > 30)
				{
					releaseProj_Tracker();
				}
			}
		}
	}
	@Override
	protected void frameReactionsNoDangerNoLOS()
	{		
			/*currentFrame = 0;
			playing = false;
			if(control.getRandomInt(10) == 0)
			{
				runRandom();
			}*/
	}
	/**
	 * Checks whether the point given is in a mostly enclosed area
	 * @return returns whether point is protected
	 */
	protected boolean checkAreaProtected(double X, double Y)
	{
		boolean areaProtected = false;
		areaProtectedRotation = 0;
		areaProtectedCount = 0;
		while(areaProtectedRotation < 360)
		{
			areaProtectedRads = areaProtectedRotation / r2d;
			if(control.checkObstructionsAll(X, Y, areaProtectedRads, 50))
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
	/**
	 * Rolls away from player
	 */
	protected void rollAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		if(!control.checkObstructionsAll(x, y, areaProtectedRads, 42))
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
				if(!control.checkObstructionsAll(x, y, areaProtectedRads, 42))
				{
					roll();
					rollPathChooseCounter = 180;
				}
				else
				{
					rotation = rollPathChooseRotationStore - rollPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(x, y, areaProtectedRads, 42))
					{
						roll();
						rollPathChooseCounter = 180;
					}
				}
			}
		}
	}
	/**
	 * Rolls towards player
	 */
	protected void rollTowards()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		roll();
	}
	/**
	 * Rolls as close to perpendicular as possible to the player
	 */
	protected void rollSideways()
	{
		rolledSideways = true;
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		rads = (rotation + 90) / r2d;
		if(control.checkObstructionsAll(x, y, areaProtectedRads, 42))
		{
			rads = (rotation - 90) / r2d;
			if(control.checkObstructionsAll(x, y, areaProtectedRads, 42))
			{
				rolledSideways = false;
			}
			else
			{
				rotation -= 90;
				rads = rotation / r2d;
				roll();
			}
		} else
		{
			rads = (rotation - 90) / r2d;
			if(control.checkObstructionsAll(x, y, areaProtectedRads, 42))
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
	/**
	 * Rolls in the current direction to enemy is rotated
	 */
	protected void roll()
	{
		rollTimer = 11;
		playing = true;
		currentFrame = 21;
		xMoveRoll = Math.cos(rads) * 8;
		yMoveRoll = Math.sin(rads) * 8;
	}
	/**
	 * Enemy mage burst attack, same as players, creates AOE's at certain times after casting
	 */
	private void burst()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		for(int i = 0; i<6; i++)
		{
			control.createProj_TrackerEnemyAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), 130, true);
		}
		control.createProj_TrackerEnemyBurst(x, y, 0);
		abilityTimer_burst = 0;
		control.activity.playEffect("burst");
		control.activity.playEffect("burst");
		control.activity.playEffect("burst");
		control.activity.playEffect("electric");
	}
	/**
	 * Releases Proj_Tracker towards player
	 */
	protected void releaseProj_Tracker()
	{
		if(abilityTimer_Proj_Tracker > 30&&minimumShootTime<1)
		{
			projectileVelocity = 2+(control.getDifficultyLevelMultiplier()*5);
			double timeToHit = (checkDistance(x, y, control.player.x, control.player.y))/projectileVelocity;
			timeToHit *= (control.getRandomDouble()*0.7)+0.4;
			double newPX;
			double newPY;
				newPX = control.player.x+(pXVelocity*timeToHit);
				newPY = control.player.y+(pYVelocity*timeToHit);
			double xDif = newPX-x;
			double yDif = newPY-y;
			rads = Math.atan2(yDif, xDif);
			rads += 0.2*(0.5-control.getRandomDouble())/Math.pow(control.getDifficultyLevelMultiplier(), 2);
			control.createProj_TrackerEnemy(rads*r2d, Math.cos(rads) * projectileVelocity, Math.sin(rads) * projectileVelocity, 130, x, y);
			abilityTimer_Proj_Tracker -= 30;
			control.activity.playEffect("shoot");
			minimumShootTime = 2;
		}
	}
	
	/**
	 * Acts on stored reaction
	 */
	protected void react()
	{
		switch(convertReactionToInt(reaction))
		{
		case 1:
			releaseProj_Tracker();
			break;
		case 2:
			roll();
			break;
		case 3:
			rollAway();
			break;
		case 4:
			rollTowards();
			break;
		case 5:
			rollSideways();
			if(rolledSideways == false)
			{
				rollTowards();
			}
			break;
		case 6:
			runAway();
			run();
			break;
		case 7:
			burst();
			break;
		}
	}
	/**
	 * Stuns enemy
	 */
	protected void stun()
	{
        rotation =rads * r2d + 180;
        rollTimer = 11;
		playing = true;
		xMoveRoll = Math.cos(rads) * speedCur * 0.7;
		yMoveRoll = Math.sin(rads) * speedCur * 0.7;
        currentFrame = 0;                                 
        reactTimer = 0;
	}
	/**
	 * returns reaction timer
	 * @return reactTimer
	 */
	protected int getReactTimer() {
		return reactTimer;
	}
	/**
	 * returns xMoveRoll
	 * @return xMoveRoll
	 */
	protected double getXMoveRoll() {
		return xMoveRoll;
	}
	/**
	 * returns yMoveRoll
	 * @return yMoveRoll
	 */
	protected double getYMoveRoll() {
		return yMoveRoll;
	}
	/**
	 * sets rolltimer
	 * @param rollTimer
	 */
	protected void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	@Override
	protected void stun(int time) {
		
	}
	@ Override
	protected int getRollTimer()
	{
		return rollTimer;
	}
	@Override
	protected int getType()
	{
		return 6;
	}
}