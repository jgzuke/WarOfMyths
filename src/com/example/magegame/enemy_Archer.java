package com.example.magegame;
public final class enemy_Archer extends enemy_Muggle
{
	public enemy_Archer(Controller creator, double setX, double setY)
	{
                super(creator, setX, setY);
		visualImage = mainController.game.imageLibrary.pikeman_Image[0];
		setImageDimensions();
                HpMax = 3500;
                Hp = HpMax;
	}@
	Override
	public void frameCall()
	{       
                if(currentFrame == 159)
                {
                    currentFrame = 0;
                    playing = false;
                    attacking = false;
                }
                visualImage = mainController.game.imageLibrary.pikeman_Image[currentFrame];
		super.frameCall();
	}@
	Override
	public void frameReactionsDangerLOS()
	{
            frameReactionsNoDangerLOS();
        }@
	Override
	public void frameReactionsDangerNoLOS()
	{
            frameReactionsNoDangerNoLOS();
        }@
	Override
	public void frameReactionsNoDangerLOS()
	{
                rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
                distanceFound = checkDistance(x, y, mainController.player.x, mainController.player.y);
		if(distanceFound < 30)
		{
                    runAway();
                } else if(distanceFound < 120)
                {
                    currentFrame = 49;
                    attacking = true;
                    playing = true;
                } else
                {
                    runToward(lastPlayerX, lastPlayerY);
                }
        }@
	Override
	public void frameReactionsNoDangerNoLOS()
	{
                distanceFound = checkDistance(x, y, lastPlayerX, lastPlayerY);
		if(checkedPlayerLast || distanceFound < 10)
		{
                    Hp += 5;
                    currentFrame = 0;
                    playing = false;
                    if(mainController.randomGenerator.nextInt(20) == 0)
                    {
                        runRandom();
                    }
                    checkedPlayerLast = true;
                } else
                {
                    rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
                    rotation = rads * r2d;
                    runToward(lastPlayerX, lastPlayerY);
                }
        }@
	Override
	public void attacking()
	{
                if(currentFrame == 79)
                {
                    distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
                    if(distanceFound < 30)
                    {
                        mainController.player.getHit(700);
                    }
                }
                if(currentFrame == 121)
                {
                    distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
                    if(distanceFound < 30)
                    {
                        mainController.player.getHit(400);
                    }
                }
                if(currentFrame == 131)
                {
                    distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
                    if(distanceFound < 30)
                    {
                        mainController.player.getHit(400);
                    }
                }
	}
}