/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
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
		super(X, Y, 25, 25, 0, creator.imageLibrary.structure_Spawn);
		control = creator;
		hp = 6000;
		baseHp();
		hpMax = hp;
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
			control.spriteController.makeEnemy(1, (int)x, (int)y);
			control.spriteController.createProj_TrackerEnemyAOE(x, y, 140, false);
			control.activity.playEffect("burst");
		}
	}
}