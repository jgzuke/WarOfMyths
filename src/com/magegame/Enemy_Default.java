/**
 * AI and variables for rogues
 */
package com.magegame;
public final class Enemy_Default extends Enemy
{
	/**
	 * Sets health, worth, and image
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 */
	public Enemy_Default(Controller creator, double setX, double setY, int setHP, int setWorth)
	{
		super(creator, setX, setY);
		visualImage = control.imageLibrary.rogue_Image[53]; //TODO change to arrayList position
		setImageDimensions();
		baseHp(setHP);
		worth = setWorth;
	}
	@Override
	protected void frameCall()
	{
		if(action.equals("Stun"))
		{
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
			if(checkDistance(x, y, control.player.x,  control.player.y) < 30) //player close enough to attack
			{
				action = "Melee";
				currentFrame = 49;
			}
		} else if(action.equals("Shoot"))
		{
			if(currentFrame<27) //geting weapon ready+aiming
			{
				aimAheadOfPlayer();
			} else if(currentFrame==36) // 
			{
				shootLaser();
			} else if(currentFrame==45) action = "Nothing"; // attack done
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
		visualImage = control.imageLibrary.rogue_Image[currentFrame];
		super.frameCall();
	}
	protected void frameReactionsDangerLOS()
	{
			distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
			if(distanceFound < 100 && distanceFound > 60)
			{
				if(control.getRandomInt(3) == 0)
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
	}
	protected void frameReactionsDangerNoLOS()
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
	protected void frameReactionsNoDangerLOS()
	{
			rads = Math.atan2(( control.player.y - y), (control.player.x - x));
			rotation = rads * r2d;
			distanceFound = checkDistance(x, y, control.player.x,  control.player.y);
			if(distanceFound < 30)
			{
				attacking = true;
				playing = true;
				currentFrame = 21;
			} else
			{
				runTowardPlayer();
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