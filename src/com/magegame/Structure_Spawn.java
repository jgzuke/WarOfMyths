/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds powerBalls headed towards object and their coordinates velocity etc
 */
package com.magegame;

public class Structure_Spawn extends Structure
{
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public Structure_Spawn(Controller creator, double X, double Y)
	{
		control = creator;
		x = X;
		y = Y;
		hp = 6000;
		baseHp();
		hpMax = hp;
		width = 25;
		height = 25;
		worth = 17;
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	protected void frameCall()
	{
		super.frameCall();
		if(timer == 100)
		{
			timer = 0;
			int index = control.lowestPositionEmpty(control.enemies);
			control.enemies[index] = new Enemy_Shield(control, (int)x, (int)y);
			control.enemies[index].sick = true;
			control.enemies[index].hpMax /= 2.5;
			control.createPowerBallEnemyAOE(x, y, 140, false);
			control.activity.playEffect("burst");
		}
	}
}