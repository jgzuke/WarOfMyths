/**
 * AI and variables for rogues
 */
package com.magegame;
public final class Enemy_Default extends Enemy
{
	public Enemy_Default(Controller creator, double X, double Y, int HP, int Worth,
		boolean gun, boolean sheild, boolean hide, boolean sword, boolean Sick, int type)
	{
		super(creator, X, Y, HP, Worth, gun, sheild, hide, sword, Sick, type);
	}
	@Override
	protected void frameCall()
	{
		if(action.equals("Stun"))
		{
			currentFrame=93;
			stunTimer--;
			if(stunTimer==0) action = "Nothing";		//stun over, go have fun
		} else if(action.equals("Melee"))
		{
			currentFrame++;
			if(currentFrame==53)
			{
				meleeAttack(300);
			} else if(currentFrame==63)
			{
				meleeAttack(420);
			}
			if(currentFrame==71) action = "Nothing";	//attack over
		} else if(action.equals("Sheild"))
		{
			currentFrame++;
			if(currentFrame==81) action = "Nothing";	//block done
		} else if(action.equals("Roll"))
		{
			x += xMove;
			y += yMove;
			currentFrame++;
			if(currentFrame==93) action = "Nothing";	//roll done
		} else if(action.equals("Hide"))
		{
			currentFrame = 94;
			if(checkDistance(x, y, control.player.x,  control.player.y) < 30) //player close enough to attack
			{
				action = "Melee";
				currentFrame = 49;
			}
		} else if(action.equals("Shoot"))
		{
			currentFrame++;
			if(currentFrame<27) //geting weapon ready+aiming
			{
				aimAheadOfPlayer();
			} else if(currentFrame==36) // shoots
			{
				shootLaser();
				checkLOS();
				if(LOS&&hp>600) currentFrame=25; // shoots again
			} else if(currentFrame==45) action = "Nothing";   // attack done
		} else if(action.equals("Move"))
		{
			currentFrame++;
			if(currentFrame == 19) currentFrame = 0; // restart walking motion
			x += xMove;
			y += yMove;
			runTimer--;
			if(runTimer==0) action = "Nothing"; // stroll done
		} else
		{
			currentFrame=0;
			checkLOSTimer--;
			if(checkLOSTimer < 1) checkLOS();
			checkDanger();
			if(pathedToHitLength > 0)
			{
				if(LOS)
				{
					frameReactionsDangerLOS();
				} else
				{
					frameReactionsDangerNoLOS();
				}
			} else
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
		visualImage = control.imageLibrary.enemy_Image[currentFrame];
		super.frameCall();
	}
	protected void frameReactionsDangerLOS()
	{
		frameReactionsNoDangerLOS();
	}
	protected void frameReactionsDangerNoLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 100)
		{
			if(hasSheild)
			{
				action = "Sheild";
				currentFrame=72;
			} else
			{
				rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
				roll();
			}
		} else
		{
			frameReactionsNoDangerNoLOS();
		}
	}
	protected void frameReactionsNoDangerLOS()
	{
		rads = Math.atan2(( control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, control.player.x,  control.player.y);
		if(distanceFound < 30)
		{
			if(hp<800)
			{
				rollAway();
			} else
			{
				if(hasSword)
				{
					action = "Melee";
					currentFrame = 46;
				} else
				{
					rollSideways();
				}
			}
		} else if(distanceFound < 200)
		{
			if(hp<800)
			{
				runAway();
			} else
			{
				if(hasGun)
				{
					action = "Shoot";
					currentFrame = 21;
				} else
				{
					runTowardPlayer();
				}
			}
		} else
		{
			if(hp<800)
			{
				runAway();//TODO change to run to safe
			} else
			{
				runTowardPlayer();
			}
		}
	}
	protected void frameReactionsNoDangerNoLOS()
	{
		if(control.getRandomInt(20) == 0)
		{
			runRandom();
		}
	}
}