package com.example.magegame;
abstract public class enemy_Muggle extends Enemy
{
	public boolean attacking = false;
        public double lastPlayerX;
        public double lastPlayerY;
        public boolean checkedPlayerLast = false;
	public enemy_Muggle(Controller creator, double setX, double setY)
	{
                mainController = creator;
		humanType = mainController.EnemyType;
		visualImage = mainController.game.imageLibrary.pikeman_Image[0];
		setImageDimensions();
		width = 30;
		height = 30;
		x = setX;
		y = setY;
                rotation = 0;
		danger[0] = levelX;
		danger[1] = levelY;
		danger[2] = levelXForward;
		danger[3] = levelYForward;
                lastPlayerX = x;
                lastPlayerY = y;       
                HpMax = 4000;
                Hp = HpMax;
                speedCur = 2.5 + mainController.randomGenerator.nextDouble();
        }@
	Override
	public void frameCall()
	{
		if(!attacking && runTimer < 1)
		{
			if(checkLOSTimer < 1)
			{
				checkLOS();
                                if(LOS)
                                {
                                    lastPlayerX = mainController.player.x;
                                    lastPlayerY = mainController.player.y;
                                    checkedPlayerLast = false;
                                }
				checkLOSTimer = 10;
			}
			checkDanger();
			if(pathedToHitLength > 0)
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
			
				if(runTimer < 1)
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
        public void runToward(double towardsX, double towardsY)
	{
		rads = Math.atan2(towardsY - y, towardsX - x);
		rotation = rads * r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 16, 4))
		{
			runPathChooseCounter = 0;
			runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 16, 4))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 16, 4))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
                runTimer = 4;
		playing = true;
                if(currentFrame > 48)
                {
                    currentFrame = 0;
                }
	}
        public void runRandom()
	{
                boolean canMove = false;
                rotation = mainController.randomGenerator.nextInt(360);
                rads = rotation / r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 100, 4))
		{
			runPathChooseCounter = 0;
			runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 100, 4))
				{
					runPathChooseCounter = 180;
                                        canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 100, 4))
					{
						runPathChooseCounter = 180;
                                                canMove = true;
					}
				}
			}
		}
                if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
		{
			runPathChooseCounter = 0;
			runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
                if(canMove)
                {
                    runTimer = 25;
                } else
                {
                    runTimer = 10;
                }
		playing = true;
                if(currentFrame > 48)
                {
                    currentFrame = 0;
                }
	}
	abstract public void attacking();
}