/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.game.towerdefense;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;


/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class TileView extends SurfaceView {

    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */
	public static float mTileSize;

    protected static int mXTileCount = 320;
    protected static int mYTileCount = 480;
    
    protected static int mWidth = 0;
    protected static int mHeight = 0;

    private static int mXOffset;
    private static int mYOffset;


    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    private int[][] mTileGrid;

    private final Paint mPaint = new Paint();

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        //mTileSize = a.getInt(R.styleable.TileView_tileSize, 12);
        a.recycle();
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        //mTileSize = a.getInt(R.styleable.TileView_tileSize, 12);
        a.recycle();
    }
    
    /**
     * Set the size of a tile in order to maintain a constant map size
     */
    public void setTileSize()
    {
    	int width = this.getWidth();
    	int height = this.getHeight();
    	mTileSize = this.tileSize(width, height);
    }

    
    
    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */
    
    public void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	mTileGrid = new int[mXTileCount][mYTileCount];
    	
    	mWidth = w;
    	mHeight = h;
        
        mTileSize = tileSize(w,h);
        
        mXOffset = (int) ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = (int) ((h - (mTileSize * mYTileCount)) / 2);
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap((int)mTileSize, (int)mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, (int)mTileSize, (int)mTileSize);
        tile.draw(canvas);
        
        mTileArray[key] = bitmap;
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     * 
     * @param tileindex
     * @param x
     * @param y
     */
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[x][y] = tileindex;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mXTileCount; x += 1) {
            for (int y = 0; y < mYTileCount; y += 1) {
                if (mTileGrid[x][y] > 0) {
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y]], 
                    		mXOffset + x * mTileSize,
                    		mYOffset + y * mTileSize,
                    		mPaint);
                }
            }
        }

    }
    
    private float tileSize(int screenWidth, int screenHeight)
    {
    	float XTileSize = (float) screenWidth  / (float) mXTileCount;
        float YTileSize = (float) screenHeight / (float) mYTileCount;
        
        return (XTileSize < YTileSize) ? XTileSize : YTileSize;
    }

}
