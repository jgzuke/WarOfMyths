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
			            		player.touchShootY = visualY(e.getY(e.findPointerIndex(touchingShootID)))-267;
			            		player.touchShootX = visualX(e.getX(e.findPointerIndex(touchingShootID)))-(53+(buttonShiftX*0.95897));
		            }
		        break;
		        case MotionEvent.ACTION_UP:
		        	player.touching = false;
		        	player.touchingShoot = false;
		        break;
		        case MotionEvent.ACTION_POINTER_UP:
		        	if(e.getPointerId(e.getActionIndex()) == trackingId)
		        	{
		        		player.touching = false;
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
	/**
	 * all taps are handled here
	 * @param x x position of click
	 * @param y y position of click
	 * @param ID id of click pointer
	 * @param firstPointer whether this pointer is the only one on screen
	 */
	protected void clickDown(float x, float y, int ID, boolean firstPointer)
	{
			clickDownNotPaused(x, y, ID, firstPointer);
			if(!clickDownNotPaused(x, y, ID, firstPointer))
			{
					clickDownNotPausedNormal(x, y, ID, firstPointer);
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
        	control.activity.pauseGame();
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
		if(control.pointOnSquare(x, y, buttonShiftX+12, 41, buttonShiftX+82, 111))
        {
        	player.burst();
        }else if(control.pointOnSquare(x, y, buttonShiftX+12, 145, buttonShiftX+82, 215))
        {
        	player.roll();
        } else if(control.pointOnCircle(x, y, 53+(buttonShiftX*0.95897), 267, 65))
        {
        		player.touchingShoot = true;
        		touchingShootID = ID;
        		player.touchShootY = visualY(y)-267;
        		player.touchShootX = visualX(x)-(53+(buttonShiftX*0.95897));
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