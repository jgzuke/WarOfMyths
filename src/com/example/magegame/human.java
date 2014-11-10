/*
 * Enemies and player, regains health, provides variables and universal getHit method
 */
package com.example.magegame;
abstract public class Human extends DrawnSprite
{
	protected int hp;
	private int hpMax;
	protected double r2d = 180 / Math.PI;
	protected double rads;
	protected double speedCur;
	protected boolean hitBack;
	protected boolean playing = false;
	protected boolean createSpecialGraphicGainCounter = false;
	protected boolean thisPlayer = false;
	protected int humanType;
	/*
	 * Regains health
	 * @see com.example.magegame.Sprite#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		if(currentFrame == 20)
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
	protected int getHp() {
		return hp;
	}
	protected void getHit(int damage)
	{
		hp -= damage*2;
		if(hp < 1)
		{
			hp = 0;
			deleted = true;
		}
	}
	protected int getHpMax() {
		return hpMax;
	}
	protected void setSpeedCur(double speedCur) {
		this.speedCur = speedCur;
	}
	protected void setHpMax(int hpMax) {
		this.hpMax = hpMax;
	}
	protected void setHp(int hp) {
		this.hp = hp;
	}
	protected boolean isThisPlayer() {
		return thisPlayer;
	}
}