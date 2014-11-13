/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.magegame;

abstract public class Structure extends DrawnSprite
{
	protected int hp;
	protected int hpMax;
	protected int timer = 0;
	protected int width;
	protected int height;
	protected int worth;
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public Structure()
	{
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@ Override
	protected void frameCall()
	{
		timer++;
		hp+=5;
		if(hp>hpMax)
		{
			hp=hpMax;
		}
	}
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		if(!deleted)
		{
			if(control.player.powerUpTimer>0 && control.player.powerID == 4)
			{
				damage *= 1.5*Math.pow((double)control.activity.wApollo/10, 0.7);
			}
			control.player.abilityTimer_burst += damage*control.activity.bReplentish/30;
			control.player.abilityTimer_roll += damage*control.activity.bReplentish/50;
			control.player.abilityTimerTransformed_pound += damage*control.activity.bReplentish/50;
			control.player.abilityTimer_Proj_Tracker += damage*control.activity.bReplentish/100;
			control.player.sp += damage*0.00003;
			hp -= damage;
			if(hp < 1)
			{
				hp = 0;
				deleted = true;
				control.player.sp += 0.30;
				control.createProj_TrackerEnemyAOE(x, y, 180, false);
				control.activity.playEffect("burst");
				control.createPowerUp(x, y);
				for(int i = 0; i < worth; i ++)
				{
					double rads = control.getRandomDouble()*6.28;
					if(worth-i>20)
					{
						control.createCoin20(x+Math.cos(rads)*12, y+Math.sin(rads)*12);
						i+=19;
					} else if(worth-i>5)
					{
						control.createCoin5(x+Math.cos(rads)*12, y+Math.sin(rads)*12);
						i+=4;
					} else
					{
						control.createCoin1(x+Math.cos(rads)*12, y+Math.sin(rads)*12);
					}
				}
			}
		}
	}
	protected void baseHp() {
		hp *= Math.pow(control.getDifficultyLevelMultiplier(), ((double)hp/10000));
	}
}