package com.example.magegame;
abstract public class human extends drawnSprite
{
	public int Hp = 7000;
	public int HpMax = 7000;
	public double r2d = 180 / Math.PI;
	public double rads;
	public double speedCur = 3;
	public boolean hitBack;
	public boolean playing = false;
	public boolean createSpecialGraphicGainCounter = false;
	public boolean isPlayer = false;
	public int humanType;
	public human()
	{}@
	Override
	public void frameCall()
	{
		Hp ++;
		if(currentFrame == 47)
		{
			currentFrame = 0;
		}
		if(playing)
		{
			currentFrame++;
		}
		if(Hp > HpMax)
		{
			Hp = HpMax;
		}
	}
	public void getHit(int damage)
	{
		Hp -= damage;
		if(Hp < 1)
		{
			Hp = 0;
			deleted = true;
		}
	}
}