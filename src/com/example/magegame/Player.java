/*
 * Handles cooldowns and stats for player and executes spells
 */
package com.example.magegame;

public final class Player extends Human
{
	protected double touchY;
	private double damageMultiplier = 1;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 0;
	protected double spMod = 1;
	protected double spChangeForType;
	private double abilityTimer_roll = 0;
	private double abilityTimer_teleport = 0;
	private double abilityTimer_burst = 0;
	private double abilityTimer_powerBall = 0;
	private double xSave = 0;
	private double ySave = 0;
	protected boolean teleporting = false;
	private boolean usedDionysusWine = false;
	protected int projectileSpeed = 10;
	protected double touchX;
	protected boolean touching;
	protected int powerUpTimer = 0;
	protected int powerID = 0;
	public Player(Controller creator)
	{
		mainController = creator;
		resetVariables();
		visualImage = mainController.imageLibrary.player_Image[0];
		/*if(mainController.activity.useHestiasBlessing>0)
		{
			mainController.activity.useHestiasBlessing --;
			damageMultiplier /= 2;
		}
		if(mainController.activity.useArtemisArrow>0)
		{
			mainController.activity.useArtemisArrow --;
			projectileSpeed = 17;
		}*/
	}
	public void resetVariables()
	{
		damageMultiplier = 1;
		rollTimer = 0;
		sp = 0.5;
		abilityTimer_roll = 60;
		abilityTimer_teleport = 175;
		abilityTimer_burst = 250;
		abilityTimer_powerBall = 0;
		teleporting = false;
		usedDionysusWine = false;
		projectileSpeed = 10;
		touching = false;
		x = 370;
		y = 160;
		hp = (int)(700 * mainController.activity.wHephaestus);
		if(mainController.lowerHp)
		{
			hp = (int)(hp/8);
		}
		setHpMax(hp);
		deleted = false;
		playing = false;
		createSpecialGraphicGainCounter = false;
		powerUpTimer=0;
	}
	/*
	 * Counts timers and executes movement and predefined behaviors
	 * @see com.example.magegame.human#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		if(mainController.drainHp)
		{
			super.getHit(5);
			if(deleted)
			{
				mainController.activity.startMenu(false);
			}
		}
		powerUpTimer--;
		if(usedDionysusWine)
		{
			super.getHit(100);
		}
		sp -= 0.0001;
		spMod = 1+(sp*spChangeForType);
		if(humanType==2)
		{
			speedCur = 4*Math.pow(spMod, 0.5)*Math.pow(mainController.activity.wHermes/10, 0.5);
		} else
		{
			speedCur = 4*Math.pow(mainController.activity.wHermes/10, 0.5);
		}
		if(powerUpTimer>0 && powerID == 3)
		{
			speedCur += 0.7*mainController.activity.wZues/10;
		}
		double cooldown;
		cooldown = mainController.activity.wAthena*mainController.activity.wHermes/100;
		if(humanType==2)
		{
			cooldown *= spMod;
		}
		if(abilityTimer_roll < 120)
		{
			abilityTimer_roll += cooldown;
		}
		if(abilityTimer_teleport < 350)
		{			
			abilityTimer_teleport += cooldown;
		}
		cooldown = mainController.activity.wAthena/10;
			if(humanType==1)
			{
				cooldown *= spMod;
			}
			if(powerUpTimer>0 && powerID == 1)
			{
				cooldown *= 1.5*mainController.activity.wPoseidon/10;
			}
		if(abilityTimer_burst < 500)
		{
			abilityTimer_burst += cooldown;
		}
		if(abilityTimer_powerBall < 40)
		{
			abilityTimer_powerBall += cooldown*3;
		}
		if(mainController.limitSpells)
		{
			abilityTimer_teleport = 0;
			abilityTimer_burst = 0;
			abilityTimer_roll = 0;
		}
		rollTimer--;
		if(currentFrame == 30)
		{
			currentFrame = 0;
			playing = false;
		}
		if(sp > 1)
		{
			sp = 1;
		}
		if(sp < 0)
		{
			sp = 0;
		}
		super.frameCall();
		if(rollTimer < 1)
		{
			rads = Math.atan2(touchY, touchX);
			rotation = rads * r2d;
			if(!deleted)
			{
				if(teleporting)
				{
					hp -= 10;
					if(hp < 0)
					{
						getHit(1000);
					}
				}
				else
				{
					if(!touching || (Math.abs(touchX) < 5 && Math.abs(touchY) < 5))
					{
						playing = false;
						currentFrame = 0;
					}
					else
					{
						movement();
					}
				}
			}
		}
		else
		{			
				x += xMoveRoll;
				y += yMoveRoll;
		}
		visualImage = mainController.imageLibrary.player_Image[currentFrame];
		setImageDimensions();
	}
	protected void movement()
	{
		playing = true;
		rads = Math.atan2(touchY, touchX);
		rotation = rads * r2d;
		x += Math.cos(rads) * speedCur;
		y += Math.sin(rads) * speedCur;
	}
	protected void releasePowerBall()
	{
		if(abilityTimer_powerBall > 30)
		{
			if(teleporting == false && rollTimer < 0)
			{
					playing = false;
					currentFrame = 0;
					mainController.createPowerBallPlayer(rads*r2d, Math.cos(rads) * projectileSpeed, Math.sin(rads) * projectileSpeed, 130, x, y);
					abilityTimer_powerBall -= 30;
					mainController.activity.playEffect("shoot");
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void roll()
	{
		if(abilityTimer_roll > 50)
		{
			if(teleporting == false && rollTimer < 0)
			{
				rollTimer = 11;
				playing = true;
				currentFrame = 21;
				xMoveRoll = Math.cos(rads) * 8;
				yMoveRoll = Math.sin(rads) * 8;
				abilityTimer_roll -= 50;
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void teleport(double X, double Y)
	{
		if(abilityTimer_teleport > 250)
		{
			if(rollTimer < 0)
			{
				//mainController.teleportStart(x, y);
				x += Math.cos(rads)*50;
				y += Math.sin(rads)*50;
				//mainController.teleportFinish(x, y);
				abilityTimer_teleport -= 250;
				mainController.createTeleport(x - Math.cos(rads)*25, y - Math.sin(rads)*25, rotation);
				mainController.activity.playEffect("teleport");
				/*if(teleporting == false)
				{
						xSave = x;
						ySave = y;
						teleporting = true;
						mainController.teleportStart(x, y);
						x = 999999999;
				} else
				{
					x = X;
					y = Y;
					teleporting = false;
					mainController.teleportFinish(x, y);
					abilityTimer_teleport -= 250;
				}*/
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void burst()
	{
		if(abilityTimer_burst > 400)
		{
			if(teleporting == false && rollTimer < 0)
			{
					mainController.createPowerBallPlayerBurst(x, y, 130);
					abilityTimer_burst -= 400;
					mainController.activity.playEffect("burst");
			}
		} else
		{
			mainController.coolDown();
		}
	}
	protected void stun()
	{
		rotation = rads * r2d + 180;
        roll();
        currentFrame = 0;
        xMoveRoll /= 3;
        yMoveRoll /= 3;
        abilityTimer_roll += 20;
	}
	@Override
	protected void getHit(int damage)
	{
		damage *= 0.7;
		if(powerUpTimer>0 && powerID == 2)
		{
			damage *= (0.7/mainController.activity.wHephaestus/10);
		}
			damage *= damageMultiplier;
			if(humanType == 3)
			{
				damage /= spMod;
			}
			super.getHit(damage);
			sp -= sp*damage/1500;
			if(deleted)
			{
				/*if(mainController.activity.useAmbrosia>0)
				{
					mainController.activity.useAmbrosia --;
					hp = getHpMax()/2;
					deleted = false;
				} else if(mainController.activity.useDionysusWine>0)
				{
					mainController.activity.useDionysusWine --;
					hp = getHpMax();
					usedDionysusWine = true;
					damageMultiplier = 0;
					deleted = false;
				} else
				{*/
					mainController.activity.startMenu(false);
				//}
			}
	}
	protected void getPowerUp(int PowerID)
	{
		switch(PowerID)
		{
		case 1:
			hp += getHpMax()/2;
			if(hp>getHpMax())hp=getHpMax();
			break;
		case 2:
			abilityTimer_roll = 120;
			abilityTimer_teleport = 350;
			abilityTimer_burst = 500;
			break;
		case 3:
			powerUpTimer=300;
			powerID=1;
			break;
		case 4:
			powerUpTimer=300;
			powerID=2;
			break;
		case 5:
			powerUpTimer=300;
			powerID=3;
			break;
		case 6:
			powerUpTimer=300;
			powerID=4;
			break;
		}
	}
	protected double getAbilityTimer_roll() {
		return abilityTimer_roll;
	}
	protected double getXSave() {
		return xSave;
	}
	protected double getYSave() {
		return ySave;
	}
	protected boolean isTeleporting() {
		return teleporting;
	}
	protected double getAbilityTimer_teleport() {
		return abilityTimer_teleport;
	}
	protected double getAbilityTimer_burst() {
		return abilityTimer_burst;
	}
	protected double getAbilityTimer_powerBall() {
		return abilityTimer_powerBall;
	}
	protected int getRollTimer() {
		return rollTimer;
	}
	protected void setRollTimer(int rollTimer) {
		this.rollTimer = rollTimer;
	}
	protected void setAbilityTimer_roll(int abilityTimer_roll) {
		this.abilityTimer_roll = abilityTimer_roll;
	}
	protected void setAbilityTimer_teleport(int abilityTimer_teleport) {
		this.abilityTimer_teleport = abilityTimer_teleport;
	}
	protected void setAbilityTimer_burst(int abilityTimer_burst) {
		this.abilityTimer_burst = abilityTimer_burst;
	}
	protected void setAbilityTimer_powerBall(int abilityTimer_powerBall) {
		this.abilityTimer_powerBall = abilityTimer_powerBall;
	}
	protected double getSp() {
		return sp;
	}
}