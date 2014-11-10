/*
 * Object behavior and ai for enemies with sheilds
 */
package com.example.magegame;
public final class Enemy_Rogue extends Enemy_Muggle
{
	private boolean discovered = false;
	private int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	private boolean charged = false;
	public Enemy_Rogue(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		rogue = true;
		currentFrame = 49;
		visualImage = mainController.imageLibrary.rogue_Image[53];
		setImageDimensions();
		hp = (int)(5000 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}
	@Override
	protected void frameCall()
	{
		visualImage = mainController.imageLibrary.rogue_Image[currentFrame];
		if(currentFrame == 44)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		if(currentFrame == 64)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		if(currentFrame == 49)
		{
			playing = false;
		}
		super.frameCall();
	}
	@	Override
	protected void getHit(int damage)
	{
		if(currentFrame > 50&& currentFrame<53)
		{
			 damage *= 6;
			 currentFrame = 0;
			 discovered = true;
		}
		super.getHit(damage);
	}
	protected void roll()
	{
		rollTimer = 11;
		playing = true;
		currentFrame = 54;
		xMoveRoll = Math.cos(rads) * 8;
		yMoveRoll = Math.sin(rads) * 8;
	}
	protected boolean rollSideways()
	{
		boolean rolledSideways = true;
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		rads = (rotation + 90) / r2d;
		double areaProtectedRads;
		if(mainController.checkObstructionsAll(x, y, rads, 42))
		{
			rads = (rotation - 90) / r2d;
			if(mainController.checkObstructionsAll(x, y, rads, 42))
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
			if(mainController.checkObstructionsAll(x, y, rads, 42))
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
		return rolledSideways;
	}
	protected void rollTowards()
	{
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		roll();
	}
	@
	Override
	protected void frameReactionsDangerLOS()
	{
		if(discovered)
		{
			distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
			if(distanceFound < 100 && distanceFound > 60)
			{
				if(mainController.getRandomInt(3) == 0)
				{
					rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
					if(!rollSideways())
					{
						rollTowards();
					}
				}
			}
			else
			{
				frameReactionsNoDangerLOS();
			}
		} else
		{
			rads = Math.atan2((mainController.getPlayerY() - y), (mainController.getPlayerX() - x));
			rotation = rads * r2d;
			distanceFound = checkDistance(x, y, mainController.getPlayerX(), mainController.getPlayerY());
			if(distanceFound < 30)
			{
				attacking = true;
				playing = true;
				currentFrame = 21;
				discovered = true;
				charged = true;
			}
		}
	}
@	Override
	protected void frameReactionsDangerNoLOS()
	{
		if(discovered)
		{
			distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
			if(distanceFound < 100)
			{
				rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
				if(!rollSideways())
				{
					rollTowards();
				}
			}
			else
			{
				frameReactionsNoDangerNoLOS();
			}
		}
	}@
	Override
	protected void frameReactionsNoDangerLOS()
	{
		if(discovered)
		{
			rads = Math.atan2((mainController.getPlayerY() - y), (mainController.getPlayerX() - x));
			rotation = rads * r2d;
			distanceFound = checkDistance(x, y, mainController.getPlayerX(), mainController.getPlayerY());
			if(distanceFound < 30)
			{
				attacking = true;
				playing = true;
				currentFrame = 21;
			}
			else
			{
				rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
				rotation = rads * r2d;
				runToward(lastPlayerX, lastPlayerY);
			}
		} else
		{
			rads = Math.atan2((mainController.getPlayerY() - y), (mainController.getPlayerX() - x));
			rotation = rads * r2d;
			distanceFound = checkDistance(x, y, mainController.getPlayerX(), mainController.getPlayerY());
			if(distanceFound < 30)
			{
				attacking = true;
				playing = true;
				currentFrame = 21;
				discovered = true;
				charged = true;
			}
		}
	}
@	Override
	protected void frameReactionsNoDangerNoLOS()
	{
		if(discovered)
		{
			currentFrame = 0;
			playing = false;
			if(mainController.getRandomInt(5) == 0)
			{
				if(mainController.getRandomInt(6) == 0)
				{
					playing = true;
					currentFrame = 46;
					discovered = false;
				} else
				{
					runRandom();
				}
			}
		}
	}@
	Override
	protected void attacking()
	{
		if(currentFrame == 28)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
			if(distanceFound < 25)
			{
				if(charged)
				{
					mainController.player.getHit((int)(2000*mainController.getDifficultyLevelMultiplier()));
				} else
				{
					mainController.player.getHit((int)(300*mainController.getDifficultyLevelMultiplier()));
				}
				charged = false;
				mainController.activity.playEffect("sword1");
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 40)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 25, y + Math.sin(rads) * 25, mainController.player.x, mainController.player.y);
			if(distanceFound < 25)
			{
				mainController.player.getHit((int)(450*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("sword2");
				if(mainController.getRandomInt(3) == 0)
				{
					mainController.player.rads = Math.atan2(mainController.player.y-y, mainController.player.x-x);
					mainController.player.stun();
				}
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
	}
}