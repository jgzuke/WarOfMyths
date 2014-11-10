/*
 * Holds imageLibrary to access graphics
 */
package com.example.magegame;

import android.content.Context;
public class Game
{
	ImageLibrary imageLibrary;
	public Game(Context gameSet)
    {
    	imageLibrary = new ImageLibrary(gameSet);	    	
    }    
}
