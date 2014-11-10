/*
 * All enemies but main mage, defines some variables and starts ai reactions
 * @param lastPlayerX Last X coordinate the player was seen at
 * @param lastPlayerY Last Y coordinate the player was seen at
 */
package com.example.magegame;
abstract public class Enemy_Muggle extends Enemy
{
	protected boolean attacking = false;
	public Enemy_Muggle(Controller creator, double setX, double setY)
	{
		super();
		mainController = creator;
		width = 30;
		height = 30;
		x = setX;
		y = setY;
		rotation = 0;
		speedCur = 1 + (mainController.getDifficultyLevelMultiplier()*2);
		lastPlayerX = x;
		lastPlayerY = y;
	}
	/*
	 * Calls correct ai method, sets correct los and in danger states
	 * @see com.example.magegame.Enemy#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		if(!attacking && getRunTimer() < 1)
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
			if(getRunTimer() < 1)
			{
				attacking();
			}
			else
			{
				x += Math.cos(rads) * speedCur;
				y += Math.sin(rads) * speedCur;
			}
		}
		super.frameCall();
	}
	
	/*
	 * How object acts during an attack
	 */
	abstract protected void attacking();
	@Override
	protected void stun(int time)
	{
		
	}
}