/**
 * All enemies but main mage, defines some variables and starts ai reactions
 * @param lastPlayerX Last X coordinate the player was seen at
 * @param lastPlayerY Last Y coordinate the player was seen at
 */
package com.magegame;
abstract public class Enemy_Muggle extends Enemy
{
	protected boolean attacking = false;
	/**
	 * Sets position and control object
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 */
	public Enemy_Muggle(Controller creator, double setX, double setY)
	{
		super(creator);
		width = 30;
		height = 30;
		x = setX;
		y = setY;
		rotation = 0;
		lastPlayerX = x;
		lastPlayerY = y;
	}
	/**
	 * Calls correct ai method, sets correct los and in danger states
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
	@Override
	protected void getHit(double damage)
	{
		damage /= control.getDifficultyLevelMultiplier();
		super.getHit(damage);
	}
	/**
	 * How object acts during an attack animation
	 */
	abstract protected void attacking();
	@Override
	protected void stun(int time)
	{
		
	}
	@ Override
	protected int getRollTimer()
	{
		return 0;
	}
}