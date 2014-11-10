package com.example.magegame;
public final class enemy_Mage extends Enemy
{        
	private int reactionTimeRating;        
	public int reactTimer = 0;        
	private String reaction;
	public int rollTimer = 0;
	public double xMoveRoll;
	public double yMoveRoll;
	public int Mp = 1750;
	public int Sp = 0;
	public int MpMax = 3500;
	public int SpMax = 3500;
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
	public enemy_Mage(Controller creator)
	{
		mainController = creator;
		humanType = mainController.EnemyType;
		visualImage = mainController.game.imageLibrary.mage_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = 110;
		y = 160;
		danger[0] = levelX;
		danger[1] = levelY;
		danger[2] = levelXForward;
		danger[3] = levelYForward;
	}@
	Override
	public void frameCall()
	{
                reactionTimeRating = mainController.DifficultyLevel;
		reactTimer--;                
		if(abilityTimer_roll < 400)
		{
			abilityTimer_roll += mainController.DifficultyLevelMultiplier;
		}
		if(abilityTimer_teleport < 350)
		{
			abilityTimer_teleport += mainController.DifficultyLevelMultiplier;
		}
		if(abilityTimer_burst < 500)
		{
			abilityTimer_burst += mainController.DifficultyLevelMultiplier;
		}
		if(abilityTimer_powerBall < 90)
		{
			abilityTimer_powerBall += mainController.DifficultyLevelMultiplier;
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
		Mp += 5 * mainController.DifficultyLevelMultiplier;
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
		if(checkLOSTimer < 1)
		{
			enemyAreaProtected = checkAreaProtected(x, y);
			playerAreaProtected = checkAreaProtected(mainController.player.x, mainController.player.y);
		}
		if(rollTimer < 1 && runTimer < 1 && reactTimer < 1)
		{
			if(checkLOSTimer < 1)
			{
				checkLOS();
				checkLOSTimer = 10;
			}
			checkDanger();
			if(pathedToHitLength > 0)
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
				if(runTimer < 1)
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
				else
				{
					if(humanType == 2 && Sp > 30)
					{
						x += Math.cos(rads) * (speedCur * 1.5);
						y += Math.sin(rads) * (speedCur * 1.5);
						Sp -= 30;
					}
					else
					{
						x += Math.cos(rads) * speedCur;
						y += Math.sin(rads) * speedCur;
					}
				}
				if(rollTimer == 1 || runTimer == 1)
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
				if(Mp > 2510 && abilityTimer_powerBall >= 400)
				{
					setReactTimer();
					reaction = "Power Burst";
				}
				else
				{
					if(Mp < 2300)
					{
						if(Mp > 650 && abilityTimer_teleport >= 250)
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
				if(Mp > 400 && abilityTimer_powerBall >= 50)
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
			if(Mp > 3150 && abilityTimer_teleport >= 250 && abilityTimer_burst >= 400)
			{
				setReactTimer();
				reaction = "Teleport Towards";
			}
			Sp += 5 * mainController.DifficultyLevelMultiplier;
			createSpecialGraphicGainCounter = true;
		}
		else
		{
			Sp += 5 * mainController.DifficultyLevelMultiplier;
			createSpecialGraphicGainCounter = true;
		}
	}
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
	public void rollTowards()
	{
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		roll();
	}
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
	public void roll()
	{
		rollTimer = 11;
		playing = true;
		currentFrame = 48;
		xMoveRoll = Math.cos(rads) * speedCur * 2;
		yMoveRoll = Math.sin(rads) * speedCur * 2;
	}
	public void teleport(int newX, int newY)
	{
		mainController.teleportStart(x, y);
		x = newX;
		y = newY;
		mainController.teleportFinish(x, y);
		if(humanType == 1 && Sp > 30)
		{
			Mp -= 580;
			Sp -= 30;
		}
		else
		{
			Mp -= 600;
		}
	}
	public void teleportTowards()
	{
		rotation = Math.random() * 360;
		rads = rotation / r2d;
		if(mainController.player.teleporting == false)
		{
			teleport((int)(mainController.player.x + Math.cos(rads) * 10), (int)(mainController.player.y + Math.sin(rads) * 10));
		}
		setReactTimer();
		reaction = "Power Burst";
		abilityTimer_teleport -= 250;
	}
	public void teleportAway()
	{
		distanceFound = checkDistance(x, y, mainController.teleportSpots[0][0], mainController.teleportSpots[1][0]);
		teleportAwayChooseDistance = distanceFound;
		teleportAwayChooseX = mainController.teleportSpots[0][0];
		teleportAwayChooseY = mainController.teleportSpots[1][0];
		distanceFound = checkDistance(x, y, mainController.teleportSpots[0][1], mainController.teleportSpots[1][1]);
		if(distanceFound > teleportAwayChooseDistance)
		{
			teleportAwayChooseDistance = distanceFound;
			teleportAwayChooseX = mainController.teleportSpots[0][1];
			teleportAwayChooseY = mainController.teleportSpots[1][1];
		}
		distanceFound = checkDistance(x, y, mainController.teleportSpots[0][2], mainController.teleportSpots[1][2]);
		if(distanceFound > teleportAwayChooseDistance)
		{
			teleportAwayChooseDistance = distanceFound;
			teleportAwayChooseX = mainController.teleportSpots[0][2];
			teleportAwayChooseY = mainController.teleportSpots[1][2];
		}
		distanceFound = checkDistance(x, y, mainController.teleportSpots[0][3], mainController.teleportSpots[1][3]);
		if(distanceFound > teleportAwayChooseDistance)
		{
			teleportAwayChooseDistance = distanceFound;
			teleportAwayChooseX = mainController.teleportSpots[0][3];
			teleportAwayChooseY = mainController.teleportSpots[1][3];
		}
		teleport((int) teleportAwayChooseX, (int) teleportAwayChooseY);
		abilityTimer_teleport -= 250;
	}
	public void releasePowerBall()
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
		if(mainController.player.teleporting == true)
		{
			rads = Math.atan2((mainController.player.XSave - y), (mainController.player.YSave - x));
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
		if(humanType == 1 && Sp > 30)
		{
			Mp -= 2480;
			Sp -= 30;
		}
		else
		{
			Mp -= 2500;
		}
		mainController.createPowerBallEnemy(0, 10, 0, 170, x, y);
		mainController.createPowerBallEnemy(45, 7, 7, 170, x, y);
		mainController.createPowerBallEnemy(90, 0, 10, 170, x, y);
		mainController.createPowerBallEnemy(135, -7, 7, 170, x, y);
		mainController.createPowerBallEnemy(180, -10, 0, 170, x, y);
		mainController.createPowerBallEnemy(225, -7, -7, 170, x, y);
		mainController.createPowerBallEnemy(270, 0, -10, 170, x, y);
		mainController.createPowerBallEnemy(315, 7, -7, 170, x, y);
		abilityTimer_burst -= 400;
	}
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
				if(Mp > 650)
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
}