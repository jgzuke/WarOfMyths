/*
 * All sprites that have an image to be drawn at their position
 * @ param visualImage Image to be drawn at objects location
 */
package com.example.magegame;
import android.graphics.Bitmap;
abstract public class DrawnSprite extends Sprite
{
	protected int imageWidth, imageHeight;
	protected Bitmap visualImage = null;
	/*
	 * Calculates images current dimensions to draw centered
	 */
	public void setImageDimensions()
	{
		imageWidth = visualImage.getWidth();
		imageHeight = visualImage.getHeight();
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public Bitmap getVisualImage() {
		return visualImage;
	}
}