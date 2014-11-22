/**
 * behavior for all projectiles
 */
package com.magegame;
abstract public class Proj_Tracker extends DrawnSprite
{
	protected boolean hitBack;
	protected double xForward;
	protected double yForward;
	protected int power;
	protected double xDif = 0;
	protected double yDif = 0;
	protected double realX;
	protected double realY;
	/**
	 * moves ball forward and decreases power
	 */
	@ Override
	protected void frameCall()
	{
		realX += xForward;
		realY += yForward;
		x = (int) realX;
		y = (int) realY;
		power--;
		if(power < 5)
		{
			deleted = true;
		}
	}
}