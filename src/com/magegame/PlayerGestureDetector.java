/**
 * detects gestures and clicks
 */
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
	private String lastScreen;
	private boolean startDragMusic = false;
	private boolean startDragEffect = false;
	private boolean startDragLevels = false;
	private int startDragLevelsYSave = 0;
	private int startDragLevelsSlideYSave = 0;
	private boolean startDragLevelSlider = false;
	protected int chooseLevelSliderY = 35;
	private int touchingShootID = 0;
	/**
	 * sets screen dimensions and checks current option settings
	 * @param mainSet control object
	 */
	public PlayerGestureDetector(Controller mainSet)
	{
		control = mainSet;
		screenDimensionMultiplier = mainSet.screenDimensionMultiplier;
		screenMinX = mainSet.screenMinX;
		screenMinY = mainSet.screenMinY;
		buttonShiftX = 390;
	}
	/**
	 * sets player as controls player object
	 * @param playerSet
	 */
	protected void setPlayer(Player playerSet)
	{
		player = playerSet;
	}
	/**
	 * decides which gesture capture is appropriate to call, drags or changes to position are done here
	 */
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
			            player.touchX = visualX(e.getX(e.findPointerIndex(trackingId)))-(427-buttonShiftX);
			            player.touchY = visualY(e.getY(e.findPointerIndex(trackingId)))-267;
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
		            		if(!control.activity.shootTapScreen)
			            	{
			            		player.touchShootY = visualY(e.getY(e.findPointerIndex(touchingShootID)))-267;
			            		player.touchShootX = visualX(e.getX(e.findPointerIndex(touchingShootID)))-(53+(buttonShiftX*0.95897));
			            	} else
			            	{
			            		player.touchShootY = screenY(e.getY(e.findPointerIndex(touchingShootID)))-player.y;
			            		player.touchShootX = screenX(e.getX(e.findPointerIndex(touchingShootID)))-player.x;
			            	}
		            	}
		            }
		            if(control.gamePaused&&control.currentPause.equals("options"))
		            {
		            	if(startDragMusic)
		            	{
		            		double rawVolume = visualX(e.getX(e.findPointerIndex(trackingId)))-264;
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
		            		double rawVolume = visualX(e.getX(e.findPointerIndex(trackingId)))-264;
		        			if(rawVolume<0) rawVolume = 0;
		            		if(rawVolume>127) rawVolume = 127;
		            		double volume = Math.pow(rawVolume, 3)/16129;
		            		if(volume<0) volume = 0;
		            		if(volume>127) volume = 127;
		            		control.activity.volumeEffect = volume;
		            		control.invalidate();
		            	}
		            }
		            if(control.gamePaused&&control.currentPause.equals("chooseLevel"))
		            {
		            	if(startDragLevels)
		            	{
		            		chooseLevelSliderY = startDragLevelsSlideYSave;
		            		chooseLevelSliderY += startDragLevelsYSave-(int)visualY(e.getY(e.findPointerIndex(trackingId)));
		            		if(chooseLevelSliderY<35) chooseLevelSliderY=35;
		            		if(chooseLevelSliderY>285) chooseLevelSliderY=285;
		            		control.invalidate();
		            	}
		            	if(startDragLevelSlider)
		            	{
		            		chooseLevelSliderY = (int)visualY(e.getY(e.findPointerIndex(trackingId)));
		            		if(chooseLevelSliderY<35) chooseLevelSliderY=35;
		            		if(chooseLevelSliderY>285) chooseLevelSliderY=285;
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
		        	startDragLevels = false;
		        	startDragLevelSlider = false;
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
			        	startDragLevels = false;
			        	startDragLevelSlider = false;
		        	}
		        	if(e.getPointerId(e.getActionIndex()) == touchingShootID)
		        	{
		        		player.touchingShoot = false;
		        	}
		        	if(e.getPointerId(e.getActionIndex())<trackingId)
		        	{
		        		
		        	}
		        break;
		        case MotionEvent.ACTION_POINTER_DOWN:
		        	clickDown(e.getX(e.getActionIndex()), e.getY(e.getActionIndex()), e.getPointerId(e.getActionIndex()), false);
		        break;
		        }
		        return true;
    }
	/**
	 * all taps are handled here
	 * @param x x position of click
	 * @param y y position of click
	 * @param ID id of click pointer
	 * @param firstPointer whether this pointer is the only one on screen
	 */
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
			} else if(control.currentPause.equals("buyall"))
			{
				clickDownBuyAll(x, y);
			} else if(control.currentPause.equals("buy"))
			{
				clickDownBuy(x, y);
			} else if(control.currentPause.equals("won"))
			{
				clickDownWon(x, y);
			} else if(control.currentPause.equals("lost"))
			{
				clickDownLost(x, y);
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
			} else if(control.currentPause.equals("chooseLevel"))
			{
				clickDownChooseLevel(x, y);
			} else if(control.currentPause.equals("buyskins"))
			{
				clickDownBuySkins(x, y);
			}
		} else
		{
			if(control.levelNum == 10)
			{
				if(!clickDownNotPausedMenu(x, y))
				{
					clickDownNotPaused(x, y, ID, firstPointer);
				}
			}
			if(!clickDownNotPaused(x, y, ID, firstPointer))
			{
				if(player.transformed == 0)
				{
					clickDownNotPausedNormal(x, y, ID, firstPointer);
				} else if(player.transformed == 1&&control.levelNum!=10)
				{
					clickDownNotPausedGolem(x, y, ID, firstPointer);
				} else if(player.transformed == 2&&control.levelNum!=10)
				{
					clickDownNotPausedHammer(x, y, ID, firstPointer);
				}
			}
		}
	}
	/**
	 * checks whether the back button was pressed
	 * @param x x value of click
	 * @param y y value of click
	 * @return whether back was clicked
	 */
	protected boolean pressedBack(float x, float y)
	{
		boolean pressed = false;
		if(control.pointOnSquare(x, y, 0, 0, 50, 50))
        {
        	pressed = true;
        	control.activity.playEffect("pageflip");
        }
		return pressed;
	}
	/**
	 * checks clicks when in the buy specific gold item screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBuy(float x, float y)
	{
		if(control.pointOnSquare(x, y, 66, 231, 214, 276)&&control.activity.canBuyGame(control.buyingItem))
		{
			control.activity.buyGame(control.buyingItem);
			control.currentPause=lastScreen;
			control.invalidate();
		} else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.currentPause=lastScreen;
			control.invalidate();
        }
	}
	/**
	 * checks clicks when in the buy specific gold item screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBuyAll(float x, float y)
	{
		lastScreen = "buyall";
		boolean hit = true;
		if(control.pointOnSquare(x, y, 20, 60, 230, 170))
		{
			control.currentPause = "blessing";
		} else if(control.pointOnSquare(x, y, 20, 190, 230, 300))
		{
			control.currentPause = "worship";
		} else if(control.pointOnSquare(x, y, 250, 60, 460, 170))
		{
			control.currentPause = "buypremium";
		} else if(control.pointOnSquare(x, y, 250, 190, 460, 300))
		{
			control.currentPause = "buyskins";
		} else if(pressedBack(x, y))
        {
			control.gamePaused = false;
        } else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	protected void clickDownChooseLevel(float x, float y)
	{
		//TODO
		if(control.pointOnSquare(x, y, 420, 0, 480, 320))
		{
			startDragLevelSlider = true;
		} else if(pressedBack(x, y))
        {
			control.gamePaused = false;
			control.invalidate();
        } else if(control.pointOnSquare(x, y, 20, 20, 420, 300))
		{
        	for(int i = 1; i < 9; i++)
    		{
    			int yVal = (80*i)-60-(int)((double)360/250*(chooseLevelSliderY-35));
    			if(yVal<300&&yVal>-60)
    			{
    				if(control.pointOnSquare(x, y, 80, yVal+40, 160, yVal+75))
    				{
    					if(i==1)
    					{
	    					control.currentPause = "startfight";
	    					control.startingLevel = 0;
	    					control.currentTutorial = 1;
	    					control.invalidate();
    					} else
    					{
    						if(control.activity.levelBeaten >= (2*i)-2)
        					{
        						control.currentPause = "startfight";
        						control.startingLevel = (2*i)-2;
        						control.invalidate();
        					} else
        					{
        						control.startWarningImediate("Level Locked");
        					}
    					}
    				}
    				if(control.pointOnSquare(x, y, 280, yVal+40, 360, yVal+75))
    				{
    					if(control.activity.levelBeaten >= (2*i)-1)
    					{
    						control.gamePaused = true;
    						control.currentPause = "startfight";
    						control.startingLevel = (2*i)-1;
    						control.invalidate();
    						player.x += 15;
    					} else
    					{
    						control.startWarningImediate("Level Locked");
    					}
    				}
    			}
    		}
	        startDragLevels = true;
	        startDragLevelsYSave = (int)visualY(y);
	        startDragLevelsSlideYSave = chooseLevelSliderY;
		}
	}
	/**
	 * checks clicks when in the buy cash screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBuyCash(float x, float y)
	{
		if(control.pointOnSquare(x, y, 20, 110, 130, 290))
		{
			control.activity.buyRealCurrency100();
		} else if(control.pointOnSquare(x, y, 130, 110, 240, 290))
		{
			control.activity.buyRealCurrency250();
		} else if(control.pointOnSquare(x, y, 240, 110, 350, 290))
		{
			control.activity.buyRealCurrency1000();
		} else if(control.pointOnSquare(x, y, 350, 110, 460, 290))
		{
			control.activity.buyRealCurrency5000();
		} else if(pressedBack(x, y))
        {
			control.currentPause=lastScreen;
			control.invalidate();
        }
	}
	/**
	 * checks clicks when in won level screen
	 * @param x x value of click
	 * @param y y value of click
	 */
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
	/**
	 * checks clicks when in the level lost screen
	 * @param x x value of click
	 * @param y y value of click
	 */
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
	/**
	 * checks clicks when in the worhip gods screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownWorship(float x, float y)
	{
		lastScreen = "worship";
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
		} else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.currentPause="buyall";
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	/**
	 * checks clicks when in the buy specific cash item screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBuyItemCash(float x, float y)
	{
		if(control.pointOnSquare(x, y, 66, 231, 214, 276)&&control.activity.canBuyReal(control.buyingItem))
		{
			control.activity.buyReal(control.buyingItem);
			control.currentPause="buypremium";
			control.invalidate();
		} else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
	    {
			control.currentPause="buypremium";
			control.invalidate();
	    }
	}
	/**
	 * checks clicks when in the buy cash items screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBuyPremium(float x, float y)
	{
		lastScreen = "buypremium";
		boolean hit = true;
		if(control.pointOnCircle(x, y, 149, 99, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "1000g";
		} else if(control.pointOnCircle(x, y, 305, 99, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "8000g";
		} else if(control.pointOnCircle(x, y, 458, 99, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "40000g";
		} else if(control.pointOnCircle(x, y, 151, 174, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Iron Golem";
		} else if(control.pointOnCircle(x, y, 310, 174, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Gold Golem";
		} else if(control.pointOnCircle(x, y, 444, 174, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Reserve";
		} else if(control.pointOnCircle(x, y, 139, 249, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Excess";
		} else if(control.pointOnCircle(x, y, 306, 249, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Replentish";
		} else if(control.pointOnCircle(x, y, 448, 249, 20))
		{
			control.currentPause = "buyitemcash";
			control.buyingItem = "Trailing";
		} else if(control.pointOnSquare(x, y, 30, 119, 150, 154))
		{
			control.activity.buyReal("1000g");
		} else if(control.pointOnSquare(x, y, 180, 119, 300, 154))
		{
			control.activity.buyReal("8000g");
		} else if(control.pointOnSquare(x, y, 330, 119, 450, 154))
		{
			control.activity.buyReal("40000g");
		} else if(control.pointOnSquare(x, y, 30, 194, 150, 229))
		{
			control.activity.buyReal("Iron Golem");
		} else if(control.pointOnSquare(x, y, 180, 194, 300, 229))
		{
			control.activity.buyReal("Gold Golem");
		} else if(control.pointOnSquare(x, y, 330, 194, 450, 229))
		{
			control.activity.buyReal("Reserve");
		} else if(control.pointOnSquare(x, y, 30, 268, 150, 303))
		{
			control.activity.buyReal("Excess");
		} else if(control.pointOnSquare(x, y, 180, 268, 300, 303))
		{
			control.activity.buyReal("Replentish");
		} else if(control.pointOnSquare(x, y, 330, 268, 450, 303))
		{
			control.activity.buyReal("Trailing");
		} else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.currentPause="buyall";
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	/**
	 * checks clicks when in the buy skins screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBuySkins(float x, float y)
	{
		lastScreen = "buyskins";
		boolean hit = true;
		if(control.pointOnSquare(x, y, 0, 40, 160, 133))
		{
			control.activity.currentSkin = 0;
			control.imageLibrary.loadPlayerImage();
		} else if(control.pointOnSquare(x, y, 0, 133, 160, 227))
		{
			if(!control.activity.ownSkin1)
			{
				control.activity.buyReal("skin1");
			}
			if(control.activity.ownSkin1)
			{
				control.activity.currentSkin = 1;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 0, 227, 160, 320))
		{
			if(!control.activity.ownSkin2)
			{
				control.activity.buyReal("skin2");
			}
			if(control.activity.ownSkin2)
			{
				control.activity.currentSkin = 2;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 160, 133, 320, 227))
		{
			if(!control.activity.ownSkin3)
			{
				control.activity.buyReal("skin3");
			}
			if(control.activity.ownSkin3)
			{
				control.activity.currentSkin = 3;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 160, 227, 320, 320))
		{
			if(!control.activity.ownSkin4)
			{
				control.activity.buyReal("skin4");
			}
			if(control.activity.ownSkin4)
			{
				control.activity.currentSkin = 4;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 320, 40, 480, 133))
		{
			if(!control.activity.ownSkin5)
			{
				control.activity.buyReal("skin5");
			}
			if(control.activity.ownSkin5)
			{
				control.activity.currentSkin = 5;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 320, 133, 480, 227))
		{
			if(!control.activity.ownSkin6)
			{
				control.activity.buyReal("skin6");
			}
			if(control.activity.ownSkin6)
			{
				control.activity.currentSkin = 6;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 320, 227, 480, 320))
		{
			if(!control.activity.ownSkin7)
			{
				control.activity.buyReal("skin7");
			}
			if(control.activity.ownSkin7)
			{
				control.activity.currentSkin = 7;
				control.imageLibrary.loadPlayerImage();
			}
		} else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.currentPause="buyall";
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	/**
	 * checks clicks when in the buy powerups screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownBlessing(float x, float y)
	{
		lastScreen = "blessing";
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
		} else if(control.pointOnSquare(x, y, 109, 0, 162, 51))
		{
			control.currentPause="buycash";
			control.invalidate();
		} else if(pressedBack(x, y))
        {
			control.currentPause="buyall";
        } else
        {
        	hit = false;
        }
		if(hit)
		{
			control.invalidate();
		}
	}
	/**
	 * checks clicks when in the start fight screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownStartFight(float x, float y)
	{
		if(pressedBack(x, y))
	    {
			if(control.levelNum == 10)
			{
				control.currentPause = "chooseLevel";
				control.invalidate();
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
			if(control.pointOnSquare(x, y, 16, 126, 116, 166))
			{			
				control.changeDifficulty(10);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 16, 176, 116, 216))
			{
				control.changeDifficulty(6);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 364, 126, 464, 166))
			{
				control.changeDifficulty(3);
				control.invalidate();
			} else if(control.pointOnSquare(x, y, 364, 176, 464, 216))
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
	/**
	 * checks clicks when in the options screen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownOptions(float x, float y, int ID)
	{
		if(control.pointOnSquare(x, y, 264, 90, 392, 116))
		{
			trackingId = ID;
			startDragMusic = true;
			double rawVolume = visualX(x)-264;
			if(rawVolume<1) rawVolume = 1;
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
			if(rawVolume<1) rawVolume = 1;
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
		/*if(control.pointOnSquare(x, y, 227, 147, 247, 167))
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
		}*/
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
		if(control.pointOnSquare(x, y, 270, 257, 290, 277))
		{
			control.activity.highGraphics = true;
			control.paint.setFilterBitmap(true);
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(control.pointOnSquare(x, y, 168, 257, 188, 277))
		{
			control.activity.highGraphics = false;
			control.paint.setFilterBitmap(false);
			control.activity.playEffect("pageflip");
			control.invalidate();
		}
		if(pressedBack(x, y))
	    {
	    	control.currentPause = "paused";
			control.invalidate();
	    }
	}
	/**
	 * checks clicks when in the pausescreen
	 * @param x x value of click
	 * @param y y value of click
	 */
	protected void clickDownPaused(float x, float y)
	{
		boolean clicked = true;
		if(pressedBack(x, y))
	    {
	    	control.gamePaused = false;
	    } else if(control.pointOnSquare(x, y, 300, 95, 464, 157))
        {
        	control.activity.startMenu();
        	control.activity.playEffect("pageflip");
        } else if(control.pointOnSquare(x, y, 300, 181, 464, 243))
        {
        	control.currentPause = "options";
        	control.activity.playEffect("pageflip");
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
        		player.getPowerUp(3);
        		control.activity.pWater--;
        } else if(control.pointOnCircle(x, y, 160, 160, 35) && control.activity.pEarth>0)
        {
	        	player.getPowerUp(4);
	        	control.activity.pEarth--;
        } else if(control.pointOnCircle(x, y, 60, 260, 35) && control.activity.pAir>0)
        {
	        	player.getPowerUp(5);
	        	control.activity.pAir--;
        } else if(control.pointOnCircle(x, y, 160, 260, 35) && control.activity.pFire>0)
        {
	        	player.getPowerUp(6);
	        	control.activity.pFire--;
        } else if(control.pointOnCircle(x, y, 250, 110, 35) && control.activity.pGolem>0)
        {
	        player.getPowerUp(11);
	        control.activity.pGolem--;
        } else if(control.pointOnCircle(x, y, 250, 210, 35) && control.activity.pHammer>0)
        {
	        player.getPowerUp(12);
	        control.activity.pHammer--;
        } else
        {
        	clicked = false;
        }
		if(clicked)
		{
			control.invalidate();
		}
	}
	/**
	 * checks clicks when in the menu level
	 * @param setX x value of click
	 * @param setY y value of click
	 * @return whether anything was clicked
	 */
	protected boolean clickDownNotPausedMenu(float setX, float setY)
	{		
		boolean hitButton = false;
		if(control.pointOnScreen(setX, setY))
		{
			double x = screenX(setX);
			double y = screenY(setY);
			hitButton = true;
			if(getDistance(x, y, 58, 242)<15)
	        {
	        	control.gamePaused = true;
				control.currentPause = "buyall";
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
		if(control.pointOnSquare(setX, setY, 390, 0, 480, 320))
        {
			hitButton=true;
        }
		return hitButton;
	}
	/**
	 * checks clicks when not paused
	 * @param x x value of click
	 * @param y y value of click
	 * @param ID id of click
	 * @param firstPointer whether this is the only pointer on screen
	 * @return whether anything was clicked
	 */
	protected boolean clickDownNotPaused(float x, float y, int ID, boolean firstPointer)
	{
		boolean touched = false;
		if(control.pointOnSquare(x, y, 0, 0, 50, 50))
        {
        	control.gamePaused = true;
        	control.currentPause = "paused";
        	control.invalidate();
        	touched = true;
        } else if(control.pointOnCircle(x, y, 427-(buttonShiftX*0.95897), 267, 65))
        {
        	player.touching = true;
        	player.touchX = visualX(x)-(427-(buttonShiftX*0.95897));
        	player.touchY = visualY(y)-267;
        	trackingId = ID;
        	touched = true;
        }
		return touched;
	}
	/**
	 * checks clicks when player is not transformed
	 * @param x x value of click
	 * @param y y value of click
	 * @param ID id of click
	 * @param firstPointer whether this is the only pointer on screen
	 */
	protected void clickDownNotPausedNormal(float x, float y, int ID, boolean firstPointer)
	{
		if(control.pointOnSquare(x, y, buttonShiftX+12, 41, buttonShiftX+82, 111)&&control.levelNum!=10)
        {
        	player.burst();
        }else if(control.pointOnSquare(x, y, buttonShiftX+12, 145, buttonShiftX+82, 215)&&control.levelNum!=10)
        {
        	player.roll();
        } else if(control.pointOnCircle(x, y, 53+(buttonShiftX*0.95897), 267, 65) && !control.activity.shootTapScreen)
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
	        		player.releaseProj_Tracker();
	        		control.shootStick.rotation=player.rads*180/Math.PI;
	        		player.rads = temp1;
	        	} else
	        	{
	        		player.releaseProj_Tracker();
	        		control.shootStick.rotation=player.rads*180/Math.PI;
	        	}
        	}
        } else if(control.pointOnScreen(x, y))
        {
        	if(control.activity.shootTapScreen)
        	{
        		if(control.activity.holdShoot)
            	{
        			player.touchingShoot = true;
        			touchingShootID = ID;
        			player.touchShootY = screenY(y)-player.y;
            		player.touchShootX = screenX(x)-player.x;
            	} else
            	{
            		if(control.activity.shootTapDirectional)
	            	{
	        			double temp1 = player.rads;
	        			player.rads = Math.atan2(screenY(y)-player.y, screenX(x)-player.x);
	            		player.releaseProj_Tracker();
	            		player.rads = temp1;
	            	} else
	            	{
	            		player.releaseProj_Tracker();
	            	}
            	}
        	}
        }
	}
	/**
	 * checks clicks when player is transformed into golem
	 * @param x x value of click
	 * @param y y value of click
	 * @param ID id of click
	 * @param firstPointer whether this is the only pointer on screen
	 */
	protected void clickDownNotPausedGolem(float x, float y, int ID, boolean firstPointer)
	{
		if(control.pointOnSquare(x, y, buttonShiftX+12, 41, buttonShiftX+82, 111))
        {
        	player.pound();
        }else if(control.pointOnSquare(x, y, buttonShiftX+12, 145, buttonShiftX+82, 215))
        {
        	player.hit();
        }
	}
	/**
	 * checks clicks when player is transformed into hammer guy
	 * @param x x value of click
	 * @param y y value of click
	 * @param ID id of click
	 * @param firstPointer whether this is the only pointer on screen
	 */
	protected void clickDownNotPausedHammer(float x, float y, int ID, boolean firstPointer)
	{
		if(control.pointOnSquare(x, y, buttonShiftX+12, 41, buttonShiftX+82, 111))
        {
        	player.pound();
        }else if(control.pointOnSquare(x, y, buttonShiftX+12, 145, buttonShiftX+82, 215))
        {
        	player.hit();
        }
	}
	/**
	 * checks distance between two points
	 * @param x1 first x
	 * @param y1 first y
	 * @param x2 second x
	 * @param y2 second y
	 * @return distance between points
	 */
	protected double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(visualX(x1)-visualX(x2), 2) + Math.pow(visualY(y1)-visualY(y2), 2));
	}
	/**
	 * converts value from click x point to where on the level it would be
	 * @param x x value of click
	 * @return x position of click in level
	 */
	protected double screenX(double x)
	{
		return (visualX(x)-90)-control.xShiftLevel;
	}
	/**
	 * converts value from click y point to where on the level it would be
	 * @param y y value of click
	 * @return y position of click in level
	 */
	protected double screenY(double y)
	{
		return (visualX(y)+10)-control.yShiftLevel;
	}
	/**
	 * converts value from click x point to where on the screen it would be
	 * @param x x value of click
	 * @return x position of click on screen
	 */
	protected double visualX(double x)
	{
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	/**
	 * converts value from click y point to where on the screen it would be
	 * @param y y value of click
	 * @return y position of click on screen
	 */
	protected double visualY(double y)
	{
		return ((y-screenMinY)/screenDimensionMultiplier);
	}
}