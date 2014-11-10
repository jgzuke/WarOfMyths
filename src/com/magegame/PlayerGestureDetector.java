package com.magegame;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PlayerGestureDetector implements OnTouchListener {
	private Player player;
	private Controller control;
	private double screenDimensionMultiplier;
	private int screenMinX;
	private int screenMinY;
	private int trackingId;
	private int actionMask;
	private double xTouch;
	private double yTouch;
	protected int buttonShiftX;
	private boolean lastScreenWorship=false;
	private boolean startDragMusic = false;
	private boolean startDragEffect = false;
	private int touchingShootID = 0;
	public PlayerGestureDetector(Controller mainSet)
	{
		control = mainSet;
		screenDimensionMultiplier = mainSet.screenDimensionMultiplier;
		screenMinX = mainSet.screenMinX;
		screenMinY = mainSet.screenMinY;
		getSide();
	}
	protected void getSide()
	{
		if(control.activity.stickOnRight)
		{
			buttonShiftX = 0;
		} else
		{
			buttonShiftX = 390;
		}
	}
	protected void setPlayer(Player playerSet)
	{
		player = playerSet;
	}
	@Override
    public boolean onTouch(View v, MotionEvent e) {
			actionMask = e.getActionMasked();
		        switch (actionMask){
		        case MotionEvent.ACTION_DOWN:
		        	clickDown(e.getX(), e.getY(), e.getPointerId(0), true);
		        break;
		        case MotionEvent.ACTION_MOVE:
		            if(player.touching)
		            {
		            	player.touchX = visualX(e.getX(trackingId))-(427-buttonShiftX);
		            	player.touchY = visualY(e.getY(trackingId))-267;
		            	if(Math.abs(player.touchX)<10)
		            	{
		            		player.touchX=0;
		            	}
		            	if(Math.abs(player.touchY)<10)
		            	{
		            		player.touchY=0;
		            	}
		            }
		            if(player.touchingShoot)
		            {
		            	if(control.activity.shootTapDirectional)
		            	{
		            		player.touchShootY = visualY(e.getY(touchingShootID))-267;
		            		player.touchShootX = visualX(e.getX(touchingShootID))-(53+(buttonShiftX*0.95897));
		            	}
		            }
		            if(control.gamePaused&&control.currentPause.equals("options"))
		            {
		            	if(startDragMusic)
		            	{
		            		double rawVolume = visualX(e.getX(trackingId))-264;
		        			if(rawVolume<0) rawVolume = 0;
		            		if(rawVolume>127) rawVolume = 127;
		            		double volume = Math.pow(rawVolume, 3)/16129;
		            		if(volume<0) volume = 0;
		            		if(volume>127) volume = 127;
		            		control.activity.volumeMusic = volume;
		            		control.invalidate();
		            	}
		            	if(startDragEffect)
		            	{
		            		double rawVolume = visualX(e.getX(trackingId))-264;
		        			if(rawVolume<0) rawVolume = 0;
		            		if(rawVolume>127) rawVolume = 127;
		            		double volume = Math.pow(rawVolume, 3)/16129;
		            		if(volume<0) volume = 0;
		            		if(volume>127) volume = 127;
		            		control.activity.volumeEffect = volume;
		            		control.invalidate();
		            	}
		            }
		        break;
		        case MotionEvent.ACTION_UP:
		        	player.touching = false;
		        	player.touchingShoot = false;
		        	if(startDragMusic||startDragEffect)
		        	{
		        		control.activity.playEffect("pageflip");
		        	}
		        	startDragMusic = false;
		        	startDragEffect = false;
		        break;
		        case MotionEvent.ACTION_POINTER_UP:
		        	if(e.getPointerId(e.getActionIndex()) == trackingId)
		        	{
		        		player.touching = false;
		        		if(startDragMusic||startDragEffect)
			        	{
			        		control.activity.playEffect("pageflip");
			        	}
		        		startDragMusic = false;
			        	startDragEffect = false;
		        	}
		        	if(e.getPointerId(e.getActionIndex()) == touchingShootID)
		        	{
		        		player.touchingShoot = false;
		        	}
		        break;
		        case MotionEvent.ACTION_POINTER_DOWN:
		        	clickDown(e.getX(e.getActionIndex()), e.getY(e.getActionIndex()), e.getPointerId(e.getActionIndex()), false);
		        break;
		        }
		        return true;
    }
	protected void clickDown(float x, float y, int ID, boolean firstPointer)
	{
		if(control.gamePaused)
		{
			if(control.currentPause.equals("paused"))
			{
				clickDownPaused(x, y);
			} else if(control.currentPause.equals("options"))
			{
				clickDownOptions(x, y, ID);
			} else if(control.currentPause.equals("startfight"))
			{
				clickDownStartFight(x, y);
			} else if(control.currentPause.equals("buy"))
			{
				clickDownBuy(x, y);
			} else if(control.currentPause.equals("won"))
			{
				clickDownWon(x, y);
			} else if(control.currentPause.equals("lost"))
			{
				clickDownLost(x, y);
			} else if(control.currentPause.equals("chooseGod"))
			{
				clickDownChooseGod(x, y);
			} else if(control.currentPause.equals("worship"))
			{
				clickDownWorship(x, y);
			} else if(control.currentPause.equals("blessing"))
			{
				clickDownBlessing(x, y);
			} else if(control.currentPause.equals("buycash"))
			{
				clickDownBuyCash(x, y);
			} else if(control.currentPause.equals("buyitemcash"))
			{
				clickDownBuyItemCash(x, y);
			} else if(control.currentPause.equals("buypremium"))
			{
				clickDownBuyPremium(x, y);
			}
		} else
		{
			if(control.levelNum == 10)
			{
				if(!clickDownNotPausedMenu(x, y))
				{
					clickDownNotPaused(x, y, ID, firstPointer);
				}
			} else
			{
				clickDownNotPaused(x, y, ID, firstPointer);
			}
		}
	}
	protected boolean pressedBack(float x, float y)
	{
		boolean pressed = false;
		if(control.pointOnSquare(x, y, 430, 0, 480, 50)&&control.activity.stickOnRight)
        {
			pressed = true;
        } else if(control.pointOnSquare(x, y, 0, 0, 50, 50)&&!control.activity.stickOnRight)
        {
        	pressed = true;
        }
		return pressed;
	}
	protected void clickDownBuy(float x, float y)
	{
		if(control.pointOnSquare(x, y, 66, 231, 214, 276)&&control.activity.canBuyGame(control.buyingItem))
		{
			control.activity.buyGame(control.buyingItem);
			if(lastScreenWorship)
			{
				control.currentPause="worship";
			} else
			{
				control.currentPause="blessing";
			}
			control.invalidate();
		} else if(control.pointOnSquare(x, y, 109, 21, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			if(lastScreenWorship)
			{
				control.currentPause="worship";
			} else
			{
				control.currentPause="blessing";
			}
			control.invalidate();
        }
	}
	protected void clickDownBuyCash(float x, float y)
	{
		if(control.pointOnSquare(x, y, 20, 110, 130, 290))
		{
			control.activity.buyRealCurrency100();
		} else if(control.pointOnSquare(x, y, 130, 110, 240, 290))
		{
			control.activity.buyRealCurrency100();
		} else if(control.pointOnSquare(x, y, 240, 110, 350, 290))
		{
			control.activity.buyRealCurrency100();
		} else if(control.pointOnSquare(x, y, 350, 110, 460, 290))
		{
			control.activity.buyRealCurrency100();
		} else if(pressedBack(x, y))
        {
			if(lastScreenWorship)
			{
				control.currentPause="worship";
			} else
			{
				control.currentPause="blessing";
			}
			control.invalidate();
        }
	}
	protected void clickDownWon(float x, float y)
	{
		if(control.pointOnSquare(x, y, 66, 231, 214, 276))
		{
			control.startingLevel =(int)(control.levelNum/10)-1;
			control.gamePaused = true;
			control.currentPause = "startfight";
			control.invalidate();
		} else if(control.pointOnSquare(x, y, 266, 231, 414, 276))
		{
			control.activity.startMenu();
		} else if(pressedBack(x, y))
        {
			control.activity.startMenu();
        }
	}
	protected void clickDownLost(float x, float y)
	{
		if(control.pointOnSquare(x, y, 66, 231, 214, 276))
		{
			control.startingLevel = (int)(control.levelNum/10)-2;
			control.gamePaused = true;
			control.currentPause = "startfight";
			control.invalidate();
		} else if(control.pointOnSquare(x, y, 266, 231, 414, 276))
		{
			control.activity.startMenu();
		} else if(pressedBack(x, y))
        {
			control.activity.startMenu();
        }
	}
	protected void clickDownWorship(float x, float y)
	{
		lastScreenWorship = true;
		boolean hit = true;
		if(control.pointOnCircle(x, y, 138, 99, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Apollo";
		} else if(control.pointOnCircle(x, y, 299, 99, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Posiedon";
		} else if(control.pointOnCircle(x, y, 430, 99, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Zues";
		} else if(control.pointOnCircle(x, y, 135, 174, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Hades";
		} else if(control.pointOnCircle(x, y, 279, 174, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Ares";
		} else if(control.pointOnCircle(x, y, 445, 174, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Athena";
		} else if(control.pointOnCircle(x, y, 142, 249, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Hermes";
		} else if(control.pointOnCircle(x, y, 312, 249, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Hephaestus";
		} else if(control.pointOnCircle(x, y, 430, 249, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Worship Hera";
		} else if(control.pointOnSquare(x, y, 30, 119, 150, 154))
		{
			control.activity.buyGame("Worship Apollo");
		} else if(control.pointOnSquare(x, y, 180, 119, 300, 154))
		{
			control.activity.buyGame("Worship Posiedon");
		} else if(control.pointOnSquare(x, y, 330, 119, 450, 154))
		{
			control.activity.buyGame("Worship Zues");
		} else if(control.pointOnSquare(x, y, 30, 194, 150, 229))
		{
			control.activity.buyGame("Worship Hades");
		} else if(control.pointOnSquare(x, y, 180, 194, 300, 229))
		{
			control.activity.buyGame("Worship Ares");
		} else if(control.pointOnSquare(x, y, 330, 194, 450, 229))
		{
			control.activity.buyGame("Worship Athena");
		} else if(control.pointOnSquare(x, y, 30, 268, 150, 303))
		{
			control.activity.buyGame("Worship Hermes");
		} else if(control.pointOnSquare(x, y, 180, 268, 300, 303))
		{
			control.activity.buyGame("Worship Hephaestus");
		} else if(control.pointOnSquare(x, y, 330, 268, 450, 303))
		{
			control.activity.buyGame("Worship Hera");
		} else if(control.pointOnSquare(x, y, 109, 21, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.gamePaused = false;
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	protected void clickDownBuyItemCash(float x, float y)
	{
		if(control.pointOnSquare(x, y, 66, 231, 214, 276)&&control.activity.canBuyReal(control.buyingItem))
		{
			control.activity.buyReal(control.buyingItem);
			control.currentPause="buypremium";
			control.invalidate();
		} else if(control.pointOnSquare(x, y, 109, 21, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
	    {
			control.currentPause="buypremium";
			control.invalidate();
	    }
	}
	protected void clickDownBuyPremium(float x, float y)
	{
		boolean hit = true;
		if(control.pointOnCircle(x, y, 138, 99, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Apollo";
		} else if(control.pointOnCircle(x, y, 299, 99, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Posiedon";
		} else if(control.pointOnCircle(x, y, 430, 99, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Zues";
		} else if(control.pointOnCircle(x, y, 135, 174, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Hades";
		} else if(control.pointOnCircle(x, y, 279, 174, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Ares";
		} else if(control.pointOnCircle(x, y, 445, 174, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Athena";
		} else if(control.pointOnCircle(x, y, 142, 249, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Hermes";
		} else if(control.pointOnCircle(x, y, 312, 249, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Hephaestus";
		} else if(control.pointOnCircle(x, y, 430, 249, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Worship Hera";
		} else if(control.pointOnSquare(x, y, 30, 119, 150, 154))
		{
			control.activity.buyReal("Worship Apollo");
		} else if(control.pointOnSquare(x, y, 180, 119, 300, 154))
		{
			control.activity.buyReal("Worship Posiedon");
		} else if(control.pointOnSquare(x, y, 330, 119, 450, 154))
		{
			control.activity.buyReal("Worship Zues");
		} else if(control.pointOnSquare(x, y, 30, 194, 150, 229))
		{
			control.activity.buyReal("Worship Hades");
		} else if(control.pointOnSquare(x, y, 180, 194, 300, 229))
		{
			control.activity.buyReal("Worship Ares");
		} else if(control.pointOnSquare(x, y, 330, 194, 450, 229))
		{
			control.activity.buyReal("Worship Athena");
		} else if(control.pointOnSquare(x, y, 30, 268, 150, 303))
		{
			control.activity.buyReal("Worship Hermes");
		} else if(control.pointOnSquare(x, y, 180, 268, 300, 303))
		{
			control.activity.buyReal("Worship Hephaestus");
		} else if(control.pointOnSquare(x, y, 330, 268, 450, 303))
		{
			control.activity.buyReal("Worship Hera");
		} else if(control.pointOnSquare(x, y, 109, 21, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.gamePaused = false;
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	protected void clickDownBlessing(float x, float y)
	{
		lastScreenWorship = false;
		boolean hit = true;
		if(control.pointOnCircle(x, y, 201, 100, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Ambrosia";
		} else if(control.pointOnCircle(x, y, 410, 100, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Cooldown";
		} else if(control.pointOnCircle(x, y, 223, 175, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Apollo's Flame";
		} else if(control.pointOnCircle(x, y, 439, 175, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Hades' Helm";
		} else if(control.pointOnCircle(x, y, 207, 250, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Zues's Armor";
		} else if(control.pointOnCircle(x, y, 440, 250, 20))
		{
			control.currentPause = "buy";
			control.buyingItem = "Posiedon's Shell";
		} else if(control.pointOnSquare(x, y, 60, 119, 210, 154))
		{
			control.activity.buyGame("Ambrosia");
		} else if(control.pointOnSquare(x, y, 270, 119, 420, 154))
		{
			control.activity.buyGame("Cooldown");
		} else if(control.pointOnSquare(x, y, 60, 194, 210, 229))
		{
			control.activity.buyGame("Apollo's Flame");
		} else if(control.pointOnSquare(x, y, 270, 194, 420, 229))
		{
			control.activity.buyGame("Hades' Helm");
		} else if(control.pointOnSquare(x, y, 60, 268, 210, 303))
		{
			control.activity.buyGame("Zues's Armor");
		} else if(control.pointOnSquare(x, y, 270, 268, 420, 303))
		{
			control.activity.buyGame("Posiedon's Shell");
		} else if(control.pointOnSquare(x, y, 109, 21, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.gamePaused = false;
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	protected void clickDownStartFight(float x, float y)
	{
		if(pressedBack(x, y))
	    {
			if(control.levelNum == 10)
			{
				control.gamePaused = false;
			} else
			{
				control.activity.startMenu();
			}
	    }
		if(control.pointOnSquare(x, y, 180, 171, 300, 219))
		{
			control.activity.startFight(control.startingLevel+2);
			control.invalidate();
		}
		if(control.startingLevel != 0)
		{
			if(control.pointOnSquare(x, y, 30, 104, 130, 144))
			{			
				control.changeDifficulty(10);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 130, 29, 230, 69))
			{
				control.changeDifficulty(6);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 250, 29, 350, 69))
			{
				control.changeDifficulty(3);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 350, 104, 450, 144))
			{
				control.changeDifficulty(0);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 16, 234, 116, 304))
			{
				control.drainHp = !control.drainHp;
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 132, 234, 232, 304))
			{
				control.lowerHp = !control.lowerHp;
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 248, 234, 348, 304))
			{
				control.limitSpells = !control.limitSpells;
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 364, 234, 464, 304))
			{
				control.enemyRegen = !control.enemyRegen;
				control.invalidate();
			}
		}
	}
	protected void clickDownChooseGod(float x, float y)
	{
		if(pressedBack(x, y))
	    {
			control.gamePaused = false;
	    }
			if(control.pointOnSquare(x, y, 16, 192, 116, 292))
			{
				control.changePlayerType(2);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 132, 192, 232, 292))
			{
				control.changePlayerType(1);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 248, 192, 348, 292))
			{
				control.changePlayerType(3);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 364, 192, 464, 292))
			{
				control.changePlayerType(0);
				control.invalidate();
			}
	}
	protected void clickDownOptions(float x, float y, int ID)
	{
		if(control.pointOnSquare(x, y, 264, 90, 392, 116))
		{
			trackingId = ID;
			startDragMusic = true;
			double rawVolume = visualX(x)-264;
			if(rawVolume<0) rawVolume = 0;
    		if(rawVolume>127) rawVolume = 127;
    		double volume = Math.pow(rawVolume, 2)/127;
    		if(volume<0) volume = 0;
    		if(volume>127) volume = 127;
    		control.activity.volumeMusic = volume;
    		control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 264, 117, 392, 143))
		{
			trackingId = ID;
			startDragEffect = true;
			double rawVolume = visualX(x)-264;
			if(rawVolume<0) rawVolume = 0;
    		if(rawVolume>127) rawVolume = 127;
    		double volume = Math.pow(rawVolume, 2)/127;
    		if(volume<0) volume = 0;
    		if(volume>127) volume = 127;
    		control.activity.volumeEffect = volume;
    		control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 249, 174, 269, 194))
		{
			control.activity.shootTapScreen = false;
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 417, 174, 437, 194))
		{
			control.activity.shootTapScreen = true;
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 236, 200, 256, 220))
		{
			control.activity.shootTapDirectional = true;
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 450, 200, 470, 220))
		{
			control.activity.shootTapDirectional = false;
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 227, 147, 247, 167))
		{
			control.activity.stickOnRight = true;
			control.activity.playEffect("pageflip");
			control.changePlayOptions();
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 338, 147, 358, 167))
		{
			control.activity.stickOnRight = false;
			control.activity.playEffect("pageflip");
			control.changePlayOptions();
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 168, 228, 188, 248))
		{
			control.activity.holdShoot = true;
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 259, 228, 279, 248))
		{
			control.activity.holdShoot = false;
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(pressedBack(x, y))
	    {
	    	control.currentPause = "paused";
			control.invalidate();
	    }
	}
	protected void clickDownPaused(float x, float y)
	{
		boolean clicked = true;
		if(pressedBack(x, y))
	    {
	    	control.gamePaused = false;
	    } else if(control.pointOnSquare(x, y, 262, 95, 414, 157))
        {
        	control.activity.startMenu();
        } else if(control.pointOnSquare(x, y, 245, 181, 414, 243))
        {
        	control.currentPause = "options";
			control.invalidate();
        } else if(control.pointOnCircle(x, y, 60, 60, 35) && control.activity.pHeal>0)
        {
        	player.getPowerUp(1);
        	control.activity.pHeal--;
        } else if(control.pointOnCircle(x, y, 160, 60, 35) && control.activity.pCool>0)
        {
        	player.getPowerUp(2);
        	control.activity.pCool--;
        } else if(control.pointOnCircle(x, y, 60, 160, 35) && control.activity.pWater>0)
        {
        	if(control.playerType!=1)
        	{
        		player.getPowerUp(3);
        		control.activity.pWater--;
        	} else
        	{
        		control.alreadyWorshipping();
        	}
        } else if(control.pointOnCircle(x, y, 160, 160, 35) && control.activity.pEarth>0)
        {
        	if(control.playerType!=3)
        	{
	        	player.getPowerUp(4);
	        	control.activity.pEarth--;
        	} else
        	{
        		control.alreadyWorshipping();
        	}
        } else if(control.pointOnCircle(x, y, 60, 260, 35) && control.activity.pAir>0)
        {
        	if(control.playerType!=2)
        	{
	        	player.getPowerUp(5);
	        	control.activity.pAir--;
        	} else
        	{
        		control.alreadyWorshipping();
        	}
        } else if(control.pointOnCircle(x, y, 160, 260, 35) && control.activity.pFire>0)
        {
        	if(control.playerType!=0)
        	{
	        	player.getPowerUp(6);
	        	control.activity.pFire--;
        	} else
        	{
        		control.alreadyWorshipping();
        	}
        } else
        {
        	clicked = false;
        }
		if(clicked)
		{
			control.invalidate();
		}
	}
	protected boolean clickDownNotPausedMenu(float setX, float setY)
	{		
		boolean hitButton = false;
		if(control.pointOnScreen(setX, setY))
		{
			double x = screenX(setX);
			double y = screenY(setY);
			hitButton = true;
			if(getDistance(x, y, 56, 189)<15)
	        {
	        	control.gamePaused = true;
				control.currentPause = "worship";
				control.invalidate();
	        } else if(getDistance(x, y, 58, 242)<15)
	        {
	        	control.gamePaused = true;
				control.currentPause = "blessing";
				control.invalidate();
	        } else if(getDistance(x, y, 103, 273)<15)
	        {
	        	control.gamePaused = true;
				control.currentPause = "chooseGod";
				control.invalidate();
	        } else
	        {
	        	hitButton = false;
	        }
		}
		if(control.pointOnSquare(setX, setY, 400, 12, 470, 210)&&!control.activity.stickOnRight)
        {
			hitButton=true;
        }
		if(control.pointOnSquare(setX, setY, 10, 12, 70, 210)&&control.activity.stickOnRight)
        {
			hitButton=true;
        }
		return hitButton;
	}
	protected boolean clickDownNotPausedTutorial(float setX, float setY)
	{
		boolean hitButton = false;
		if(control.pointOnScreen(setX, setY))
		{
			double x = visualX(setX);
			double y = visualY(setY);
			if(control.pointOnSquare(setX, setY, 200, 280, 280, 320))
	        {
				hitButton = true;
				control.activity.playEffect("pageflip");
				control.currentTutorial ++;
				if(control.currentTutorial == 2)
				{
					control.enemies[0] = new Enemy_Target(control, 145, 150, 0, false);
					control.enemies[1] = new Enemy_Target(control, 355, 150, 180, false);
					control.enemies[2] = new Enemy_Target(control, 250, 25, 90, false);
					control.enemies[3] = new Enemy_Target(control, 250, 275, -90, false);
				}
				if(control.currentTutorial == 3)
				{
					control.enemies[0] = new Enemy_Target(control, 52, 150, 0, false);
					control.enemies[1] = new Enemy_Target(control, 415, 25, 90, true);
					control.enemies[2] = new Enemy_Target(control, 435, 275, -90, true);
					control.enemies[3] = null;
				}
				if(control.currentTutorial == 4)
				{
					control.enemies[0] = null;
					control.enemies[1] = null;
					control.enemies[2] = null;
					control.enemies[3] = null;
				}
				if(control.currentTutorial >9)
				{
					if(control.activity.levelBeaten == 0)
		        	{
						control.activity.levelBeaten++;
		        	}
					control.activity.startFight(3);
				} else
				{
					control.imageLibrary.directionsTutorial = control.imageLibrary.loadImage("menu_tutorial000"+Integer.toString(control.currentTutorial), 217, 235);
				}
				control.invalidate();
	        }
		}
		return hitButton;
	}
	protected void clickDownNotPaused(float x, float y, int ID, boolean firstPointer)
	{
		if(control.pointOnSquare(x, y, 430, 0, 480, 50)&&control.activity.stickOnRight)
        {
        	control.gamePaused = true;
        	control.currentPause = "paused";
        	control.invalidate();
        } else if(control.pointOnSquare(x, y, 0, 0, 50, 50)&&!control.activity.stickOnRight)
        {
        	control.gamePaused = true;
        	control.currentPause = "paused";
        	control.invalidate();
        } else if(control.pointOnSquare(x, y, buttonShiftX+12, 82, buttonShiftX+82, 152))
        {
        	//player.teleport(visualX(x), visualY(y));
        } else if(control.pointOnSquare(x, y, buttonShiftX+12, 12, buttonShiftX+82, 82))
        {
        	player.burst();
        }else if(control.pointOnSquare(x, y, buttonShiftX+12, 152, buttonShiftX+82, 222))
        {
        	player.roll();
        }/* else if(control.pointOnSquare(x, y, buttonShiftX+12, 33, buttonShiftX+82, 103))
        {
        	player.burst();
        }else if(control.pointOnSquare(x, y, buttonShiftX+12, 118, buttonShiftX+82, 188))
        {
        	player.roll();
        }*/ else if(control.pointOnCircle(x, y, 53+(buttonShiftX*0.95897), 267, 60) && !control.activity.shootTapScreen)
        {
        	if(control.activity.holdShoot)
        	{
        		player.touchingShoot = true;
        		touchingShootID = ID;
        		player.touchShootY = visualY(y)-267;
        		player.touchShootX = visualX(x)-(53+(buttonShiftX*0.95897));
        	} else
        	{
        		if(control.activity.shootTapDirectional)
	        	{
	        		double temp1 = player.rads;
	            	player.rads = Math.atan2(visualY(y)-267, visualX(x)-(53+(buttonShiftX*0.95897)));
	        		player.releasePowerBall();
	        		control.shootStick.rotation=player.rads*180/Math.PI;
	        		player.rads = temp1;
	        	} else
	        	{
	        		player.releasePowerBall();
	        		control.shootStick.rotation=player.rads*180/Math.PI;
	        	}
        	}
        } else if(control.pointOnScreen(x, y))
        {
        	if(player.teleporting)
        	{
            	player.teleport(screenX(x), screenY(y));
        	} else if(control.activity.shootTapScreen)
        	{
        		if(control.activity.holdShoot)
            	{
        			player.touchingShoot = true;
        			touchingShootID = ID;
        			player.touchShootY = visualY(y)-267;
            		player.touchShootX = visualX(x)-(53+(buttonShiftX*0.95897));
            	} else
            	{
            		if(control.activity.shootTapDirectional)
	            	{
	        			double temp1 = player.rads;
	        			player.rads = Math.atan2(screenY(y)-player.y, screenX(x)-player.x);
	            		player.releasePowerBall();
	            		player.rads = temp1;
	            	} else
	            	{
	            		player.releasePowerBall();
	            	}
            	}
        	}
        } else if(control.pointOnCircle(x, y, 427-(buttonShiftX*0.95897), 267, 60))
        {
        	player.touching = true;
        	player.touchX = visualX(x)-(427-(buttonShiftX*0.95897));
        	player.touchY = visualY(y)-267;
        	trackingId = ID;
        }
	}
	protected double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(visualX(x1)-visualX(x2), 2) + Math.pow(visualY(y1)-visualY(y2), 2));
	}
	protected double screenX(double x)
	{
		return (visualX(x)-90)-control.xShiftLevel;
	}
	protected double screenY(double y)
	{
		return (visualX(y)+10)-control.yShiftLevel;
	}
	protected double visualX(double x)
	{
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	protected double visualY(double y)
	{
		return ((y-screenMinY)/screenDimensionMultiplier);
	}
}