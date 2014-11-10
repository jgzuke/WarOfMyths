/**
 * All sprites that have an image to be drawn at their position
 * @param visualImage Image to be drawn at objects location
 */
package com.magegame;
import android.graphics.Bitmap;
abstract public class DrawnSprite extends Sprite
{
	protected int imageWidth, imageHeight;
	protected Bitmap visualImage = null;
	/**
	 * Calculates images current dimensions to draw centered
	 */
	protected void setImageDimensions()
	{
		if(visualImage != null)
		{
			imageWidth = visualImage.getWidth();
			imageHeight = visualImage.getHeight();
		}
	}
	/**
	 * returns image width
	 * @return image width
	 */
	protected int getImageWidth() {
		return imageWidth;
	}
	/**
	 * returns current image
	 * @return current image
	 */
	protected Bitmap getVisualImage() {
		return visualImage;
	}
}