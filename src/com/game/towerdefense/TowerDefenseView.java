package com.game.towerdefense;

import java.util.ArrayList;
import java.util.Iterator;

import com.game.towerdefense.creeps.*;
import com.game.towerdefense.towers.*;
import com.game.towerdefense.ui.PlaceableTower;
import com.game.towerdefense.ui.UIController;
import com.game.towerdefense.ui.UpgradeDialog;

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
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.GestureDetector.*;
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
public class TowerDefenseView extends TileView implements
		SurfaceHolder.Callback, OnGestureListener {

	class TowerDefenseThread extends Thread {

		DisplayMetrics metrics = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(metrics);

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
		public static final int INITIAL_MONEY = 50;

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
		 * Currently selected tower to build
		 */
		private int mBuildTower;

		/**
		 * Currently selected tower to view
		 */
		private Tower mSelectedTower;

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
		private ArrayList<Shot> mShotList = new ArrayList<Shot>();
		private Route route = null;
		private WaveManager mWaveManager;
		private TileMap mTileMap = new TileMap();
		private TowerManager mTowerManager;

		/**
		 * The rectangles for the tower buttons
		 */
		private Rect mRect1;
		private Rect mRect2;
		private Rect mRect3;

		/**
		 * The height and width of the screen.
		 */
		private int mWidth;
		private int mHeight;

		/**
		 * 
		 */
		private int pathWidth;

		/**
		 * 
		 */
		private PlaceableTower mPlaceableTower;

		/** Used to figure out elapsed time between frames */
		private long mLastTime;

		/** Used to figure out elapsed time between frames */
		private long mTimeMainLoop;
		private long mTimeLastRecord = System.currentTimeMillis();;
		private double mFPS = 0;
		private double mFrames = 0;

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
		public final float mFPSGoal = 30;
		public long mMoveDelay;

		/**
		 * Game State
		 */
		private GameState mGameState = new GameState();

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

			mMoveDelay = (int) (1000.0 / mFPSGoal);

			Resources res = context.getResources();
			// cache handles to our key sprites & other drawables
			mCreepImage = context.getResources().getDrawable(R.drawable.creeps);
			mTowerImage = context.getResources().getDrawable(R.drawable.tower1);
			mTower2Image = context.getResources()
					.getDrawable(R.drawable.tower2);
			mTower3Image = context.getResources()
					.getDrawable(R.drawable.tower3);

			mPathImage = context.getResources().getDrawable(
					R.drawable.greenstar);

			// load background image as a Bitmap instead of a Drawable b/c
			// we don't need to transform it and it's faster to draw this way
			mBackgroundImage = BitmapFactory.decodeResource(res,
					R.drawable.background);

			mScratchRect = new RectF(0, 0, 0, 0);

			// Initialize paints for the game
			mHealthLeftColor = Color.healthLeftColor();
			mHealthLostColor = Color.black();
			mTextColor = Color.white();

			mDifficulty = DIFFICULTY_MEDIUM;

			doStart();
		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		public void doStart() {
			synchronized (mSurfaceHolder) {
				TowerDefenseView mTowerDefenseView = (TowerDefenseView) findViewById(R.id.tower_defense);

				mPendingCreepList = new ArrayList<Creep>();
				mCreepList = new ArrayList<Creep>();
				mTowerList = new ArrayList<Tower>();
				mShotList = new ArrayList<Shot>();
				TileMap.clearTiles();

				route = GameGenerator.genericRoute();
				mTowerManager = new TowerManager(mTileSize);
				mWaveManager = GameGenerator.defaultWaves(mTowerDefenseView);

				GameState.setBank(new Bank(INITIAL_MONEY));
				GameState.setLives(TOTAL_LIVES);

				setState(STATE_RUNNING);
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
		// public synchronized void restoreState(Bundle savedState) {
		// synchronized (mSurfaceHolder) {
		// setState(STATE_PAUSE);
		// // TODO restore game. See LunarView.java
		// }
		// }

		@Override
		public void run() {
			mIteration++;
			mFrames++;

			if (mTimeMainLoop != 0 && mTimeMainLoop > (mTimeLastRecord + 1000)) {
				long currentTime = System.currentTimeMillis();

				double timeDiff = mTimeMainLoop - mTimeLastRecord;
				double timeDiffSeconds = (double) (timeDiff / 1000.0);
				mFPS = Math.round((mFrames / timeDiffSeconds));

				mFrames = 0;
				mTimeLastRecord = currentTime;
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

			mRedrawHandler.sleep(mMoveDelay
					- (System.currentTimeMillis() - mTimeMainLoop));
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
					GameState.getBank();
					map.putInt("bank", Bank.getAmount()); // mMoney.getAmount()
					map.putInt("lives", GameState.getLives()); // mLives
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
						GameState.setLives(TOTAL_LIVES); // mLivesLeft =
					// TOTAL_LIVES;

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

			mWidth = mTowerDefenseView.getWidth();
			mHeight = mTowerDefenseView.getHeight();

			mBarHeight = mTowerDefenseView.getPercentOfHeight(12);

			Drawable[] icons = { mTowerImage, mTower2Image, mTower3Image };
			UIController.drawMenuBox(mTowerDefenseView, canvas, icons);
			UIController.drawTopBox(mTowerDefenseView, canvas);

			// Draw path
			Tile startPoint = null;
			Tile endPoint = null;

			Tile globalStartPoint = route.getStart();
			Tile globalEndPoint = route.getEnd();

			ArrayList<Tile> routeCheckPoints = route.getCheckPoints();

			mLinePaint = Color.pathColor(route.getWidth()); // getPercentOfHeight(5)
			// Draw Route
			for (Tile c : routeCheckPoints) {
				startPoint = c;
				int x1, y1, x2, y2;
				if (endPoint != null) {
					x1 = startPoint.getPixel().x;
					y1 = startPoint.getPixel().y;
					x2 = endPoint.getPixel().x;
					y2 = endPoint.getPixel().y;
				} else {
					x1 = globalStartPoint.getPixel().x;
					y1 = globalStartPoint.getPixel().y;
					x2 = startPoint.getPixel().x;
					y2 = startPoint.getPixel().y;
				}
				// Draw the route
				canvas.drawLine(x1, y1, x2, y2, mLinePaint); // l, t, r, b
				endPoint = c;
			}

			canvas.drawLine(endPoint.getPixel().x, endPoint.getPixel().y,
					globalEndPoint.getPixel().x, globalEndPoint.getPixel().y,
					mLinePaint);

			// Draw towers
			for (Tower tower : mTowerManager.towers()) {
				Drawable towerImg = tower.getImage();

				int leftBound = tower.getLeftBound();
				int upperBound = tower.getUpperBound();
				int rightBound = tower.getRightBound();
				int lowerBound = tower.getLowerBound();

				towerImg.setBounds(leftBound, upperBound, rightBound,
						lowerBound);
				towerImg.draw(canvas);
				if (tower == mSelectedTower) {
					UIController.drawTowerRange(canvas, tower);
				}
			}

			canvas.save();

			// draw shots
			for (Shot shot : mShotList) {
				float x = shot.getPosition().x;
				float y = shot.getPosition().y;
				canvas.drawCircle(x, y, 2, Color.black());
			}

			// Draw creeps
			for (Creep creep : mCreepList) {
				Drawable creepImg = creep.getImage();

				int leftBound = creep.getLeftBound();
				int upperBound = creep.getUpperBound();
				int rightBound = creep.getRightBound();
				int lowerBound = creep.getLowerBound();

				creepImg.setBounds(leftBound, upperBound, rightBound,
						lowerBound); // l, t, r, b
				creepImg.draw(canvas);

				// Draw health bar
				int healthLeft = (int) (CREEP_HEALTH_BAR * creep
						.getHealthPercentage()) / 100;
				healthLeft = (healthLeft < 0) ? 0 : healthLeft;

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

			if (mPlaceableTower != null)
				mPlaceableTower.draw(canvas);

			canvas.restore();

			// TODO see LunarViewer
		}

		/**
		 * Figures the lander state (x, y, fuel, ...) based on the passage of
		 * realtime. Does not invalidate(). Called at the start of draw().
		 * Detects the end-of-game and sets the UI to the next state.
		 */
		private void updateGameState() {

			long now = System.currentTimeMillis();

			Wave wave = mWaveManager.getCurrentWave();
			route.setWidth(getPercentOfHeight(5)); // width

			if (GameState.getLives() <= 0) {
				// StatReporter.sendEmail(getContext(),
				// mWaveManager.getWaveNumber());
				// mLives = -1;
				this.setState(STATE_LOSE);
			}

			if (wave == null)
				return;

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
						GameState.decreaseLives();
					} else {
						GameState.getBank();
						Bank.increaseMoney(creep.getValue());
					}
					i.remove();
				} else {
					creep.move();
				}
			}

			// shoot
			for (Tower tower : mTowerManager.towers()) {
				Shot s = tower.shoot(mCreepList);
				if (s != null)
					mShotList.add(s);
			}

			// move shots
			Iterator<Shot> itr = mShotList.iterator();
			while (itr.hasNext()) {
				Shot shot = itr.next();
				shot.move();
				if (shot.isHit()) {
					itr.remove();
				}
			}

			double elapsed = (now - mLastTime) / 1000.0;

			mLastTime = now;
		}

		public boolean onTouchEvent(MotionEvent event) {
			int action = event.getAction();

			int towerOffset = mBarHeight; // hack. this should not be
			// "mBarHeight"

			float x = event.getX();
			float y = event.getY();

			if (action == MotionEvent.ACTION_DOWN) {
				if (y > mHeight - mBarHeight) {

					// define button rectangles
					if (UIController.getTowerButton(0).contains((int) x,
							(int) y)) {
						mBuildTower = GENERIC_TOWER;
						return true;
					}

					if (UIController.getTowerButton(1).contains((int) x,
							(int) y)) {
						mBuildTower = HEAVY_TOWER;
						return true;
					}

					if (UIController.getTowerButton(2).contains((int) x,
							(int) y)) {
						mBuildTower = SNIPER_TOWER;
						return true;
					}
				} else {
					mBuildTower = 0;
					mSelectedTower = null;
				}
			}

			if (y < mHeight - mBarHeight) {

				y = y - towerOffset;
				Tile towerPlace = getTileTouch(x, y);

				if (action == MotionEvent.ACTION_MOVE) {

					Tower t;

					switch (mBuildTower) {
					case GENERIC_TOWER:
						t = new GenericTower(towerPlace);
						mPlaceableTower = new PlaceableTower(towerPlace, t
								.getSize(), mTowerImage, t.getRange());
						break;

					case HEAVY_TOWER:
						t = new HeavyTower(towerPlace);
						mPlaceableTower = new PlaceableTower(towerPlace, t
								.getSize(), mTower2Image, t.getRange());
						break;

					case SNIPER_TOWER:
						t = new SniperTower(towerPlace);
						mPlaceableTower = new PlaceableTower(towerPlace, t
								.getSize(), mTower3Image, t.getRange());
						break;
					}

					return true;

				}

				if (action == MotionEvent.ACTION_UP) {

					mPlaceableTower = null;

					switch (mBuildTower) {
					case GENERIC_TOWER:
						mTowerManager.createGenericTower(towerPlace,
								mTowerImage, GameState.getBank());
						break;

					case HEAVY_TOWER:
						mTowerManager.createHeavyTower(towerPlace,
								mTower2Image, GameState.getBank());
						break;

					case SNIPER_TOWER:
						mTowerManager.createSniperTower(towerPlace,
								mTower3Image, GameState.getBank());
						break;
					}
					
					mBuildTower = 0;

					return true;
				}

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

			return mTileMap.getTile(x, y, mTileSize);
		}

		public void onLongPress(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			// if (Pixel.toTile(x, y).hasTower()) {
			// if (Pixel.toTile(x, y).hasTower()) {
			// Tower tower = Pixel.toTile(x, y).getTower();
			// tower.upgrade();
			Vibrator v = (Vibrator) mContext
					.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(300);
			// }
			// }

		}

	}

	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context mContext;

	/** Pointer to the text view to display "Paused.." etc. */
	private TextView mStatusText;

	/** The thread that actually draws the animation */
	private TowerDefenseThread thread;

	/** */
	private GestureDetector gestureScanner;

	public TowerDefenseView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// TODO nothing

		gestureScanner = new GestureDetector(this);

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
	 * @return X percent of the height
	 */
	public static int getPercentOfHeight(int percent) {
		return (int) (TileView.mHeight * ((double) percent / 100));
	}

	/**
	 * @return X percent of the width
	 */
	public static int getPercentOfWidth(int percent) {
		return (int) (TileView.mWidth * ((double) percent / 100));
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
		if (gestureScanner.onTouchEvent(event))
			return true;
		else
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
		try {
			thread.setRunning(true);
			thread.start();
		} catch (IllegalThreadStateException e) {
			return;
		}
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

	public boolean onDown(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		if (Pixel.toTile(x, y).hasTower()) {
			thread.mSelectedTower = Pixel.toTile(x, y).getTower();
			return true;
		} else {
			thread.mSelectedTower = null;
		}
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		if (Pixel.toTile(x, y).hasTower()) {
			if (Pixel.toTile(x, y).getTower().upgrade()) {
				UpgradeDialog.open(mContext);
				//Vibrator v = (Vibrator) mContext
				//		.getSystemService(Context.VIBRATOR_SERVICE);
				//v.vibrate(300);
			}
		}

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}