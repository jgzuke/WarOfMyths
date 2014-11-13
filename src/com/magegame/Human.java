/**
 * Enemies and player, regains health, provides variables and universal getHit method
 */
package com.magegame;
abstract public class Human extends DrawnSprite
{
	protected int hp;
	protected int hpMax;
	protected double r2d = 180 / Math.PI;
	protected double rads;
	protected double speedCur;
	protected boolean hitBack;
	protected boolean playing = false;
	protected boolean createSpecialGraphicGainCounter = false;
	protected boolean thisPlayer = false;
	protected double weight = 2;
	/**
	 * Regains health, ends walk animation, plays animation
	 */
	@
	Override
	protected void frameCall()
	{
		if(currentFrame == 19)
		{
			currentFrame = 0;
		}
		if(playing)
		{
			currentFrame++;
		}
		if(hp > hpMax)
		{
			hp = hpMax;
		}
	}
	/**
	 * returns health
	 * @return health
	 */
	protected int getHp() {
		return hp;
	}
	/**
	 * takes damage
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		hp -= damage*2;
		if(hp < 1)
		{
			hp = 0;
			deleted = true;
		}
	}
	/**
	 * returns max health
	 * @return max health
	 */
	protected int getHpMax() {
		return hpMax;
	}
	/**
	 * sets speed
	 * @param speedCur speed to set
	 */
	protected void setSpeedCur(double speedCur) {
		this.speedCur = speedCur;
	}
	/**
	 * sets max health
	 * @param speedCur max health to set
	 */
	protected void setHpMax(int hpMax) {
		this.hpMax = hpMax;
	}
	/**
	 * sets health
	 * @param speedCur health to set
	 */
	protected void setHp(int hp) {
		this.hp = hp;
	}
	/**
	 * returns whether this object is the player
	 * @return is it the player
	 */
	protected boolean isThisPlayer() {
		return thisPlayer;
	}
}