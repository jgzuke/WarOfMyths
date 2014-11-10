/*
 * Enemies and player, regains health, provides variables and universal getHit method
 */
package com.example.magegame;
abstract public class Human extends DrawnSprite
{
	protected int hp = 7000;
	private int hpMax = 7000;
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
	public void frameCall()
	{
		hp ++;
		if(currentFrame == 47)
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
	public int getHp() {
		return hp;
	}
	public void getHit(int damage)
	{
		hp -= damage;
		if(hp < 1)
		{
			hp = 0;
			deleted = true;
		}
	}
	public int getHpMax() {
		return hpMax;
	}
	public void setSpeedCur(double speedCur) {
		this.speedCur = speedCur;
	}
	public void setHpMax(int hpMax) {
		this.hpMax = hpMax;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public boolean isThisPlayer() {
		return thisPlayer;
	}
}