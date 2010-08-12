package com.game.towerdefense;

import java.util.ArrayList;
import java.util.Iterator;

import com.game.towerdefense.creeps.Creep;
import com.game.towerdefense.creeps.GenericCreep;
import com.game.towerdefense.towers.GenericTower;
import com.game.towerdefense.towers.Tower;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * View that draws, takes keystrokes, etc. for a simple LunarLander game.
 * 
 * Has a mode which RUNNING, PAUSED, etc. Has a x, y, dx, dy, ... capturing the
 * current ship physics. All x/y etc. are measured with (0,0) at the lower left.
 * updatePhysics() advances the physics based on realtime. draw() renders the
 * ship, and does an invalidate() to prompt another draw() as soon as possible
 * by the system.
 */
class TowerDefenseView extends TileView implements SurfaceHolder.Callback {

	class TowerDefenseThread extends Thread {
		/*
		 * Difficulty setting constants
		 */
		public static final int DIFFICULTY_EASY = 0;
		public static final int DIFFICULTY_HARD = 1;
		public static final int DIFFICULTY_MEDIUM = 2;

		/*
		 * State-tracking constants
		 */
		public static final int STATE_LOSE = 1;
		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
		public static final int STATE_WIN = 5;
		
		/*
		 * Different towers
		 */
		public static final int GENERIC_TOWER = 1;
		public static final int HEAVY_TOWER = 2;
		public static final int SNIPER_TOWER = 3;

		/*
		 * Goal condition constants
		 */
		public static final int TOTAL_LIVES = 20; // The number of creeps which
		// can slips through

		/*
		 * UI constants (i.e. the speed & fuel bars)
		 */

		public static final int CREEP_HEALTH_BAR = 24; // width of the bar(s)
		public static final int CREEP_HEALTH_BAR_HEIGHT = 3; // height of the
		// bar(s)
		public static final int CREEP_HEALTH_BAR_HOVER_HEIGHT = 10;

		private static final String KEY_DIFFICULTY = "mDifficulty";
		private static final String KEY_DX = "mDX";

		private static final String KEY_DY = "mDY";
		private static final String KEY_FUEL = "mFuel";
		private static final String KEY_GOAL_ANGLE = "mGoalAngle";
		private static final String KEY_GOAL_SPEED = "mGoalSpeed";
		private static final String KEY_GOAL_WIDTH = "mGoalWidth";

		private static final String KEY_GOAL_X = "mGoalX";
		private static final String KEY_HEADING = "mHeading";
		private static final String KEY_LANDER_HEIGHT = "mLanderHeight";
		private static final String KEY_LANDER_WIDTH = "mLanderWidth";
		private static final String KEY_WINS = "mWinsInARow";

		private static final String KEY_X = "mX";
		private static final String KEY_Y = "mY";

		/*
		 * Member (state) fields
		 */
		/** The drawable to use as the background of the animation canvas */
		private Bitmap mBackgroundImage;

		/**
		 * Current height of the surface/canvas.
		 * 
		 * @see #setSurfaceSize
		 */
		private int mCanvasHeight = 1;

		/**
		 * Current width of the surface/canvas.
		 * 
		 * @see #setSurfaceSize
		 */
		private int mCanvasWidth = 1;

		/**
		 * Current difficulty -- amount of fuel, allowed angle, etc. Default is
		 * MEDIUM.
		 */
		private int mDifficulty;
		
		/**
		 * Currently selected tower
		 */
		private int mSelectedTower;
		
		/**
		 * Top bar height
		 */
		private int mBarHeight;

		/** What to draw creeps running through the level */
		private Drawable mCreepImage;

		/** What to draw the towers */
		private Drawable mTowerImage;
		private Drawable mTower2Image;
		private Drawable mTower3Image;

		/** What to draw the road */
		private Drawable mPathImage;

		/**
		 * mPendingCreepList: a list of Creeps which is currently pending
		 * mCreepList: a list of Creeps which is currently moving around
		 * mTowerList: the location of the defense towers.
		 */
		private ArrayList<Creep> mPendingCreepList = new ArrayList<Creep>();
		private ArrayList<Creep> mCreepList = new ArrayList<Creep>();
		private ArrayList<Tower> mTowerList = new ArrayList<Tower>();
		private Route route = null;
		private Wave wave = new Wave();
		private TileMap mTileMap;
		private TowerManager mTowerManager;
		
		/**
		 * The rectangles for the tower buttons
		 */
		private Rect mRect1;
		private Rect mRect2;
		private Rect mRect3;

		/** The amount of lives the player has */
		private int mLivesLeft = TOTAL_LIVES;

		/** Used to figure out elapsed time between frames */
		private long mLastTime;

		/** Used to figure out elapsed time between frames */
		private long mTimeMainLoop;
		private double mFPS = 0;
		private double mMinFPS;
		private double mMaxFPS;

		private int mIteration;

		/** Used to figure out elapsed time since last creep */
		private long mLastCreepTime;

		/** Message handler used by thread to interact with TextView */
		private Handler mHandler;

		/** Paint to draw the lines on screen. */
		private Paint mLinePaint;
		private Paint mHealthLeftColor;
		private Paint mHealthLostColor;
		private Paint mTextColor;

		/** "Bad" speed-too-high variant of the line color. */
		private Paint mLinePaintBad;

		/** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
		private int mMode;

		/** Indicate whether the surface has been created & is ready to draw */
		private boolean mRun = false;

		/** Scratch rect object. */
		private RectF mScratchRect;

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;

		/**
		 * How long should our application sleep between the iterations
		 */
		public long mMoveDelay = 20;

		/**
		 * Lives
		 */
		private int mLives = TOTAL_LIVES;
		
		/**
		 * Lives
		 */
		private Bank mMoney = new Bank(220);

		/**
		 * Create a simple handler that we can use to cause animation to happen.
		 * We set ourselves as a target and we can use the sleep() function to
		 * cause an update/invalidate to occur at a later date.
		 */
		private RefreshHandler mRedrawHandler = new RefreshHandler();

		class RefreshHandler extends Handler {

			@Override
			public void handleMessage(Message msg) {
				TowerDefenseThread.this.run();
				// TowerDefenseThread.this.invalidate();
			}

			public void sleep(long delayMillis) {
				this.removeMessages(0);
				sendMessageDelayed(obtainMessage(0), delayMillis);
			}
		};

		public TowerDefenseThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			// get handles to some important objects
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;

			Resources res = context.getResources();
			// cache handles to our key sprites & other drawables
			mCreepImage = context.getResources().getDrawable(R.drawable.creeps);
			mTowerImage = context.getResources().getDrawable(R.drawable.tower1);
			mTower2Image = context.getResources().getDrawable(R.drawable.tower2);
			mTower3Image = context.getResources().getDrawable(R.drawable.tower3);
			
			
			mPathImage = context.getResources().getDrawable(
					R.drawable.greenstar);

			// load background image as a Bitmap instead of a Drawable b/c
			// we don't need to transform it and it's faster to draw this way
			mBackgroundImage = BitmapFactory.decodeResource(res,
					R.drawable.background);

			// Use the regular lander image as the model size for all sprites
			// mLanderWidth = mLanderImage.getIntrinsicWidth();
			// mLanderHeight = mLanderImage.getIntrinsicHeight();

			// Initialize paints for speedometer
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(139, 69, 19, 0);
			mLinePaint.setStrokeWidth(40);

			mHealthLeftColor = new Paint();
			mHealthLeftColor.setAntiAlias(true);
			mHealthLeftColor.setARGB(255, 255, 0, 0);

			mHealthLostColor = new Paint();
			mHealthLostColor.setAntiAlias(true);
			mHealthLostColor.setARGB(255, 0, 0, 0);

			mTextColor = new Paint();
			mTextColor.setAntiAlias(true);
			mTextColor.setARGB(255, 255, 255, 255);

			mLinePaintBad = new Paint();
			mLinePaintBad.setAntiAlias(true);
			mLinePaintBad.setARGB(139, 69, 19, 0);

			mScratchRect = new RectF(0, 0, 0, 0);

			mDifficulty = DIFFICULTY_MEDIUM;

			doStart();
		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		public void doStart() {
			synchronized (mSurfaceHolder) {

				TowerDefenseView mTowerDefenseView = (TowerDefenseView) findViewById(R.id.tower_defense);

				route = RouteGenerator.GenericRoute(mTowerDefenseView);
				mTileMap = new TileMap(mTowerDefenseView);
				mTowerManager = new TowerManager(mTileMap);

				//mTowerManager.createGenericTower(150, 150, mTowerImage, mMoney);
				//mTowerManager.createGenericTower(225, 225, mTowerImage, mMoney);

				for (int i = 0; i < 20; i++) {
					GenericCreep creep = new GenericCreep(10, 100, 10, route);
					wave.addCreeps(creep, mCreepImage);
				}

			}
		}

		/**
		 * Pauses the physics update & animation.
		 */
		public void pause() {
			synchronized (mSurfaceHolder) {
				if (mMode == STATE_RUNNING)
					setState(STATE_PAUSE);
			}
		}

		/**
		 * Restores game state from the indicated Bundle. Typically called when
		 * the Activity is being restored after having been previously
		 * destroyed.
		 * 
		 * @param savedState
		 *            Bundle containing the game state
		 */
		public synchronized void restoreState(Bundle savedState) {
			synchronized (mSurfaceHolder) {
				setState(STATE_PAUSE);
				// TODO restore game. See LunarView.java
			}
		}

		@Override
		public void run() {
			mIteration++;

			if (mTimeMainLoop != 0) {
				double timeDiff = System.currentTimeMillis() - mTimeMainLoop;
				double timeDiffSeconds = (double) (timeDiff / 1000.0);
				mFPS = Math.round((1.0 / timeDiffSeconds));

				if (mFPS > mMaxFPS)
					mMaxFPS = mFPS;

				if (mFPS < mMinFPS)
					mMinFPS = mFPS;
			}
			mTimeMainLoop = System.currentTimeMillis();

			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					if (mMode == STATE_RUNNING)
						updateGameState();
					if (c != null) { // prevent crash on exit
						doDraw(c);
					}
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}

			// mRedrawHandler.sleep(mMoveDelay);
			mRedrawHandler.sleep(1);
		}

		/**
		 * Dump game state to the provided Bundle. Typically called when the
		 * Activity is being suspended.
		 * 
		 * @return Bundle with this view's state
		 */
		public Bundle saveState(Bundle map) {
			synchronized (mSurfaceHolder) {
				if (map != null) {
					// TODO save the state of the game
				}
			}
			return map;
		}

		/**
		 * Sets the current difficulty.
		 * 
		 * @param difficulty
		 */
		public void setDifficulty(int difficulty) {
			synchronized (mSurfaceHolder) {
				mDifficulty = difficulty;
			}
		}

		/**
		 * Used to signal the thread whether it should be running or not.
		 * Passing true allows the thread to run; passing false will shut it
		 * down if it's already running. Calling start() after this was most
		 * recently called with false will result in an immediate shutdown.
		 * 
		 * @param b
		 *            true to run, false to shut down
		 */
		public void setRunning(boolean b) {
			mRun = b;
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @see #setState(int, CharSequence)
		 * @param mode
		 *            one of the STATE_* constants
		 */
		public void setState(int mode) {
			synchronized (mSurfaceHolder) {
				setState(mode, null);
			}
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @param mode
		 *            one of the STATE_* constants
		 * @param message
		 *            string to add to screen or null
		 */
		public void setState(int mode, CharSequence message) {
			/*
			 * This method optionally can cause a text message to be displayed
			 * to the user when the mode changes. Since the View that actually
			 * renders that text is part of the main View hierarchy and not
			 * owned by this thread, we can't touch the state of that View.
			 * Instead we use a Message + Handler to relay commands to the main
			 * thread, which updates the user-text View.
			 */
			synchronized (mSurfaceHolder) {
				mMode = mode;

				if (mMode == STATE_RUNNING) {
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", "");
					b.putInt("viz", View.INVISIBLE);
					msg.setData(b);
					mHandler.sendMessage(msg);
				} else {
					Resources res = mContext.getResources();
					CharSequence str = "";
					if (mMode == STATE_READY)
						str = res.getText(R.string.mode_ready);
					else if (mMode == STATE_PAUSE)
						str = res.getText(R.string.mode_pause);
					else if (mMode == STATE_LOSE)
						str = res.getText(R.string.mode_lose);
					else if (mMode == STATE_WIN)
						str = res.getString(R.string.mode_win_prefix) + " "
								+ res.getString(R.string.mode_win_suffix);

					if (message != null) {
						str = message + "\n" + str;
					}

					if (mMode == STATE_LOSE)
						mLivesLeft = TOTAL_LIVES;

					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", str.toString());
					b.putInt("viz", View.VISIBLE);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;

				// don't forget to resize the background image
				mBackgroundImage = mBackgroundImage.createScaledBitmap(
						mBackgroundImage, width, height, true);
			}
		}

		/**
		 * Resumes from a pause.
		 */
		public void unpause() {
			// Move the real time clock up to now
			synchronized (mSurfaceHolder) {
				mLastTime = System.currentTimeMillis() + 100;
			}
			setState(STATE_RUNNING);
		}

		/**
		 * Handles a key-down event.
		 * 
		 * @param keyCode
		 *            the key that was pressed
		 * @param msg
		 *            the original event object
		 * @return true
		 */
		boolean doKeyDown(int keyCode, KeyEvent msg) {
			synchronized (mSurfaceHolder) {
				// TODO Do I need keys?
				return false;
			}
		}

		/**
		 * Handles a key-up event.
		 * 
		 * @param keyCode
		 *            the key that was pressed
		 * @param msg
		 *            the original event object
		 * @return true if the key was handled and consumed, or else false
		 */
		boolean doKeyUp(int keyCode, KeyEvent msg) {
			boolean handled = false;

			synchronized (mSurfaceHolder) {
				// TODO Do I need keys?
			}

			return handled;
		}

		/**
		 * Draws the ship, fuel/speed bars, and background to the provided
		 * Canvas.
		 */
		private void doDraw(Canvas canvas) {

			TowerDefenseView mTowerDefenseView = (TowerDefenseView) findViewById(R.id.tower_defense);
			mTowerDefenseView.setTileSize();

			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);
			
			//MenuBox.DrawInterfaceBox(mTowerDefenseView, canvas, mLives, mMoney);
			drawMenuBox(mTowerDefenseView, canvas);

			// draw text
//			canvas.drawText("Escaped creeps: " + mLives, 200, 20, mTextColor);
//			canvas.drawText("FPS: " + mFPS, 200, 40, mTextColor);
//			canvas.drawText("min FPS: " + mMinFPS, 200, 60, mTextColor);
//			canvas.drawText("max FPS: " + mMaxFPS, 200, 80, mTextColor);
//			canvas.drawText("Bank: " + mMoney.getAmount(), 200, 100, mTextColor);

			// Draw path
			Tile startPoint = null;
			Tile endPoint = null;

			Tile globalStartPoint = route.getStart();
			Tile globalEndPoint = route.getEnd();

			ArrayList<Tile> routeCheckPoints = route.getCheckPoints();

			// Draw Route
			for (Tile c : routeCheckPoints) {
				startPoint = c;
				if (endPoint != null) {
					// Draw the route
					int startX = startPoint.getPixel().x;
					int startY = startPoint.getPixel().y;

					canvas.drawLine(startPoint.getPixel().x, startPoint
							.getPixel().y, endPoint.getPixel().x, endPoint
							.getPixel().y, mLinePaint);
				} else {
					canvas.drawLine(globalStartPoint.getPixel().x,
							globalStartPoint.getPixel().y, startPoint
									.getPixel().x, startPoint.getPixel().y,
							mLinePaint);
				}
				endPoint = c;
			}

			canvas.drawLine(endPoint.getPixel().x, endPoint.getPixel().y,
					globalEndPoint.getPixel().x, globalEndPoint.getPixel().y,
					mLinePaint);

			for (Creep creep : mCreepList) {
				Tile pos = creep.getPosition();
				Drawable creepImg = creep.getImage();

				int leftBound = creep.getLeftBound();
				int upperBound = creep.getUpperBound();
				int rightBound = creep.getRightBound();
				int lowerBound = creep.getLowerBound();

				creepImg.setBounds(leftBound, upperBound, rightBound,
						lowerBound); // l,
				// t
				// ,
				// r
				// ,b
				creepImg.draw(canvas);

				// Draw health bar
				int healthLeft = (int) (CREEP_HEALTH_BAR * creep
						.getHealthPercentage()) / 100;

				int healthBarBottom = CREEP_HEALTH_BAR_HOVER_HEIGHT
						- CREEP_HEALTH_BAR_HEIGHT;

				// red health bar

				int barUpperBound = upperBound - CREEP_HEALTH_BAR_HOVER_HEIGHT;
				int barLowerBound = upperBound - healthBarBottom;
				mScratchRect.set(leftBound, barUpperBound, leftBound
						+ healthLeft, barLowerBound); // l t r b
				canvas.drawRect(mScratchRect, mHealthLeftColor);

				// black damage bar
				mScratchRect.set(leftBound + healthLeft, barUpperBound,
						leftBound + CREEP_HEALTH_BAR, barLowerBound); // l t r b
				canvas.drawRect(mScratchRect, mHealthLostColor);
			}

			// Draw towers
			for (Tower tower : mTowerManager.towers()) {
				Drawable creepImg = tower.getImage();

				int leftBound = tower.getLeftBound();
				int upperBound = tower.getUpperBound();
				int rightBound = tower.getRightBound();
				int lowerBound = tower.getLowerBound();

				creepImg.setBounds(leftBound, upperBound, rightBound,
						lowerBound); // l,
				// t
				// ,
				// r
				// ,b
				creepImg.draw(canvas);
			}

			canvas.save();
			canvas.restore();

			// TODO see LunarViewer
		}
		
		private void drawMenuBox(TowerDefenseView view, Canvas canvas) {
			int width = view.getWidth();
			int height = view.getHeight();
			mBarHeight = (int) (140.0/mTileSize);
			int imageSize = 20;
			
			Paint backgroundColor = new Paint();
			backgroundColor.setAntiAlias(true);
			backgroundColor.setARGB(255, 0, 0, 0);
			
			Paint foregroundColor = new Paint();
			foregroundColor.setAntiAlias(true);
			foregroundColor.setARGB(255, 255, 255, 255);
			
			canvas.drawRect(0, 0, width, mBarHeight, backgroundColor);
			
			mRect1 = new Rect(width/8-imageSize, 50-imageSize, width/8+imageSize, 50+imageSize); 
			mRect2 = new Rect(3*width/8-imageSize, 50-imageSize, 3*width/8+imageSize, 50+imageSize); 
			mRect3 = new Rect(5*width/8-imageSize, 50-imageSize, 5*width/8+imageSize, 50+imageSize); 
			
			drawTower(canvas, mTowerImage, foregroundColor, mRect1);
			drawTower(canvas, mTower2Image, foregroundColor, mRect2);
			drawTower(canvas, mTower3Image, foregroundColor, mRect3);
			
			canvas.drawText("Escaped creeps: " + mLives, 6*width/8, 40, foregroundColor);
			canvas.drawText("Bank: " + mMoney.getAmount(), 6*width/8, 60, foregroundColor);
		}
		
		private void drawTower(Canvas canvas, Drawable image, Paint color, Rect r) {
			int imageSize = 20;
			
			//image.setBounds(x-imageSize, y-imageSize, x+imageSize, y+imageSize);
			image.setBounds(r);
			image.draw(canvas);
			canvas.drawText("Price: " + 10, r.centerX()-10, r.centerY()+imageSize+5, color);
		}

		/**
		 * Figures the lander state (x, y, fuel, ...) based on the passage of
		 * realtime. Does not invalidate(). Called at the start of draw().
		 * Detects the end-of-game and sets the UI to the next state.
		 */
		private void updateGameState() {

			long now = System.currentTimeMillis();

			// Do nothing if mLastTime is in the future.
			// This allows the game-start to delay the start of the physics
			// by 100ms or whatever.
			if (mLastTime > now)
				return;

			// Release new creatures at a certain time interval
			if (wave.shouldRelease()) {
				mCreepList.add(wave.release());
			}

			for (Iterator<Creep> i = mCreepList.iterator(); i.hasNext();) {
				Creep creep = i.next();

				boolean escaped = creep.isLastPos();
				if (escaped || creep.getHealth() <= 0) {
					if (escaped) {
						mLives -= 1;
					} else {
						mMoney.increaseMoney(creep.getValue());
					}
					i.remove();
				} else {
					creep.move();
				}
			}

			// shoot
			for (Tower tower : mTowerManager.towers()) {
				tower.shoot(mCreepList);
			}

			double elapsed = (now - mLastTime) / 1000.0;

			mLastTime = now;
		}

		public boolean onTouchEvent(MotionEvent event) {
			int action = event.getAction();
			
			float x = event.getX();
			float y = event.getY();
			
			// define button rectangles
			if (mRect1.contains((int)x, (int)y)) {
				mSelectedTower = GENERIC_TOWER;
				return true;
			}
			
			if (mRect2.contains((int)x, (int)y)) {
				mSelectedTower = HEAVY_TOWER;
				return true;
			}
			
			if (mRect3.contains((int)x, (int)y)) {
				mSelectedTower = SNIPER_TOWER;
				return true;
			}

			if (action == MotionEvent.ACTION_UP && y > mBarHeight) {

				Tile towerPlace = getTileTouch(x, y);
				
				switch (mSelectedTower) {
					case GENERIC_TOWER:  
						mTowerManager.createGenericTower(towerPlace, mTowerImage, mMoney);
						break;
						
					case HEAVY_TOWER:  
						mTowerManager.createHeavyTower(towerPlace, mTower2Image, mMoney);
						break;
						
					case SNIPER_TOWER:  
						mTowerManager.createSniperTower(towerPlace, mTower3Image, mMoney);
						break;
				}

				return true;
			}

			return true;

		}

		/**
		 * Returns the state of the game
		 * 
		 * @return
		 */
		public int getMode() {
			return mMode;
		}

		private Tile getTileTouch(float touchX, float touchY) {
			int x = (int) (touchX / mTileSize);
			int y = (int) (touchY / mTileSize);

			return mTileMap.getTile(x, y);
		}

	}

	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context mContext;

	/** Pointer to the text view to display "Paused.." etc. */
	private TextView mStatusText;

	/** The thread that actually draws the animation */
	private TowerDefenseThread thread;

	public TowerDefenseView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new TowerDefenseThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				mStatusText.setVisibility(m.getData().getInt("viz"));
				mStatusText.setText(m.getData().getString("text"));
			}
		});

		setFocusable(true); // make sure we get key events
	}

	/**
	 * Fetches the animation thread corresponding to this TowerDefenseView.
	 * 
	 * @return the animation thread
	 */
	public TowerDefenseThread getThread() {
		return thread;
	}

	/**
	 * Standard override to get touch events.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return thread.onTouchEvent(event);
	}

	/**
	 * Standard override to get key-press events.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		return thread.doKeyDown(keyCode, msg);
	}

	/**
	 * Standard override for key-up. We actually care about these, so we can
	 * turn off the engine or stop rotating.
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent msg) {
		return thread.doKeyUp(keyCode, msg);
	}

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (!hasWindowFocus)
			thread.pause();
	}

	/**
	 * Installs a pointer to the text view used for messages.
	 */
	public void setTextView(TextView textView) {
		mStatusText = textView;
	}

	/* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		thread.setRunning(true);
		thread.start();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

}