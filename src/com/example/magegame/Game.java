package com.example.magegame;

import android.content.Context;
public class Game
{
	public imageLibrary imageLibrary;
    public Game(Context gameSet, double screenDimensionMultiplierSet)
    {
    	imageLibrary = new imageLibrary(gameSet, screenDimensionMultiplierSet);	    	
    }    
}
