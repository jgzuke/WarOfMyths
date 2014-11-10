//@author (classes and interfaces only, required)
//@version (classes and interfaces only, required. See footnote 1)
//@param (methods and constructors only)
//@return (methods only)
//@exception (@throws is a synonym added in Javadoc 1.2)
//@see
//@since
//@serial (or @serialField or @serialData)
//@deprecated (see How and When To Deprecate APIs)
/* Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
 * @param DifficultyLevel Chosen difficulty setting which dictates enemy reaction time and DifficultyLevelMultiplier
 * @param DifficultyLevelMultiplier Function of DifficultyLevel which changes enemy health, mana, speed
 * @param EnemyType Mage type of enemy
 * @param PlayerType Mage type of player
 * @param LevelNum Level chosen to fight on
 * @param player Player object that has health etc and generates movement handler
 * @param enemy Enemy object with health etc and ai
 * @param enemies Array of all enemies currently on screen excluding main mage enemy
 * @param powerBalls Array of all enemy or player powerBalls
 * @param powerBallAOEs Array of all enemy or player powerBall explosions
 * @param spGraphicEnemy Handles the changing of main enemy's sp
 * @param spGraphicPlayer Handles the changing of player's sp
 * @param obstaclesRectanglesX1 Array of all walls left x value
 * @param obstaclesRectanglesX2 Array of all walls right x value
 * @param obstaclesRectanglesY1 Array of all walls top y value
 * @param obstaclesRectanglesY2 Array of all walls bottom x value
 * @param obstaclesCirclesX Array of all pillars middle x value
 * @param obstaclesCirclesY Array of all pillars middle y value
 * @param obstaclesCirclesRadius Array of all pillars radius
 * @param currentCircle Current index of obstaclesCirclesX to write to
 * @param currentRectangle Current index of obstaclesRectanglesX1 to write to
 * @param teleportSpots Array of levels four teleport spots x and y for enemy mage
 * @param game Game object holding imageLibrary
 * @param context Main activity context for returns
 * @param aoeRect Rectangle to draw sized bitmaps
 * @param mHandler Timer for frameCaller
 * @param handleMovement Handles players movement attacks etc
 * @param screenMinX Start of game on screen horizontally
 * @param screenMinY Start of game on screen vertically
 * @param screenDimensionMultiplier
 * @param frameCaller Calls objects and controllers frameCalls
 */
package com.example.magegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
abstract public class AllViews extends View
{
	protected boolean gameRunning = false;
	protected boolean switchView = false;
	protected int screenMinX;
	protected int screenMinY;
	protected double screenDimensionMultiplier;
	protected Paint paint = new Paint();
	protected Matrix rotateImages = new Matrix();
	protected int percentLoaded = 0;
	protected StartActivity activity;
	public AllViews(Context startSet)
	{
		super(startSet);
	}
	/*
	 * Replaces canvas.drawRect(int, int, int, int, Paint) and auto scales
	 */
	public void drawRect(int x, int y, int x2, int y2, Canvas g)
	{
		g.drawRect(x, y, x2, y2, paint);
	}
	/*
	 * Replaces canvas.drawCircle(int, int, int paint) and auto scales
	 */
	public void drawCircle(int x, int y, int radius, Canvas g)
	{
		g.drawCircle(x, y, radius, paint);
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, int, int, paint) and auto scales
	 */
	public void drawBitmap(Bitmap picture, int x, int y, Canvas g)
	{
		g.drawBitmap(picture, x, y, paint);
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	public void drawBitmapRotated(DrawnSprite sprite, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-sprite.getVisualImage().getWidth() / 2, -sprite.getVisualImage().getHeight() / 2);
		rotateImages.postRotate((float) sprite.getRotation());
		rotateImages.postTranslate((float) sprite.getX(), (float) sprite.getY());
		g.drawBitmap(sprite.getVisualImage(), rotateImages, paint);
		sprite = null;
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	public void drawBitmapRect(Bitmap picture, Rect rectangle, Canvas g)
	{
		g.drawBitmap(picture, null, rectangle, paint);
	}
	/*
	 * Replaces canvas.drawText(String, int, int, Paint) and auto scales
	 */
	public void drawText(String text, int x, int y, Canvas g)
	{
		// TODO
		g.drawText(text, x, y, paint);
	}	
}