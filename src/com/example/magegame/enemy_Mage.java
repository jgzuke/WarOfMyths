/*
 * Main enemy ai, cooldowns stats etc
 * @param reactionTimeRating how many frames the enemy takes to react to certain scenarios
 * @param playerAreaProtected whether or not the player is in a mostly enclosed area
 * @param enemyAreaProtected whether or not the main enemy is in a mostly enclosed area
 */
package com.example.magegame;

public final class Enemy_Mage extends Enemy
{
	private int reactTimer = 0;
	private int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	private int reactionTimeRating;
	private String reaction;
	private double abilityTimer_burst = 0;
	private double abilityTimer_powerBall = 0;
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
	public Enemy_Mage(Controller creator, int setX, int setY)
	{
		super();
		mainController = creator;
		visualImage = mainController.imageLibrary.mage_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = setX;
		y = setY;
		lastPlayerX = x;
		lastPlayerY = y;
		speedCur = 4.5;
		hp = (int)(3000 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}
	/*
	 * Replenishes stats, counts down timers, and checks los etc
	 * @see com.example.magegame.Enemy#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		rollTimer--;
		reactionTimeRating = mainController.getDifficultyLevel();
		reactTimer--;
		if(abilityTimer_burst < 500)
		{
			abilityTimer_burst += mainController.getDifficultyLevelMultiplier();
		}
		if(abilityTimer_powerBall < 40)
		{
			abilityTimer_powerBall += 3*Math.pow(mainController.getDifficultyLevelMultiplier(), 0.5);
		}
		if(currentFrame == 58)
		{
			currentFrame = 0;
			playing = false;
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
				if(LOS)
				{
					lastPlayerX = mainController.player.x;
					lastPlayerY = mainController.player.y;
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
	/*
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
	}@
	Override
	protected void frameReactionsDangerLOS()
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
	}@
	Override
	protected void frameReactionsNoDangerLOS()
	{
		double playerDistance = checkDistance(x, y, mainController.player.x, mainController.player.y);
		if(playerDistance < 30)
		{
			if(playerAreaProtected == true && abilityTimer_burst > 400)
			{
				setReactTimer();
				reaction = "Power Burst";
			}
			else
			{
				setReactTimer();
				reaction = "Roll Away";
			}
		} else
		{
			if(playerDistance < 80)
			{
				if(abilityTimer_burst > 400)
				{
					setReactTimer();
					reaction = "Roll Towards";
				} else if(abilityTimer_powerBall > 30)
				{
					releasePowerBall();
				} else
				{
					setReactTimer();
					reaction = "Roll Away";
				}
			} else 
			{
				if(abilityTimer_powerBall > 30)
				{
					releasePowerBall();
				}
			}
		}
	}
	@Override
	protected void frameReactionsNoDangerNoLOS()
	{		
			currentFrame = 0;
			playing = false;
			if(mainController.getRandomInt(20) == 0)
			{
				runRandom();
			}
	}
	/*
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
	protected void rollAway()
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
	protected void rollTowards()
	{
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		roll();
	}
	/*
	 * Rolls as close to perpendicular as possible to the player
	 */
	protected void rollSideways()
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
	protected void roll()
	{
		rollTimer = 11;
		playing = true;
		currentFrame = 48;
		xMoveRoll = Math.cos(rads) * speedCur * 2.2;
		yMoveRoll = Math.sin(rads) * speedCur * 2.2;
	}
	
	/*
	 * Releases stored powerBall towards player
	 */
	protected void releasePowerBall()
	{
		if(abilityTimer_powerBall > 30)
		{
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
			mainController.createPowerBallEnemy(rotation, Math.cos(rads) * 10, Math.sin(rads) * 10, 130, x, y);
			abilityTimer_powerBall -= 30;
		}
	}
	protected void powerBurst()
	{
		if(abilityTimer_burst > 400)
		{
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
	}
	/*
	 * Acts out stored reaction
	 */
	protected void react()
	{
		switch(convertReactionToInt(reaction))
		{
		case 1:
			releasePowerBall();
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
			powerBurst();
			break;
		}
	}
	protected void stun()
	{
        rotation =rads * r2d + 180;
        rollTimer = 11;
		playing = true;
		xMoveRoll = Math.cos(rads) * speedCur * 0.7;
		yMoveRoll = Math.sin(rads) * speedCur * 0.7;
        currentFrame = 1;                                 
        reactTimer = 0;
	}
	protected int getReactTimer() {
		return reactTimer;
	}
	protected double getXMoveRoll() {
		return xMoveRoll;
	}
	protected double getYMoveRoll() {
		return yMoveRoll;
	}
	protected int getRollTimer() {
		return rollTimer;
	}
	protected void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	@Override
	protected void stun(int time) {
		
	}
}