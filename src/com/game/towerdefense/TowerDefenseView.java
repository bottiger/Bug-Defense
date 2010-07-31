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
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
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
class TowerDefenseView extends SurfaceView implements SurfaceHolder.Callback {

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
		 * Goal condition constants
		 */
		public static final int TOTAL_LIVES = 20; // The number of creeps which
		// can slips through

		/*
		 * UI constants (i.e. the speed & fuel bars)
		 */
		public static final int TILE_SIZE = 10;

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

		/** What to draw creeps running through the level */
		private Drawable mCreepImage;

		/** What to draw the towers */
		private Drawable mTowerImage;

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
		public long mMoveDelay = 250;
		
		/**
		 * Lives
		 */
		private int mLives = TOTAL_LIVES;

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
			mTowerImage = context.getResources().getDrawable(R.drawable.tank);
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
				// First set the game for Medium difficulty
				ArrayList<Coordinate> checkPoints = new ArrayList<Coordinate>();
				checkPoints.add(new Coordinate(100, 40));
				checkPoints.add(new Coordinate(100, 350));
				checkPoints.add(new Coordinate(180, 350));
				checkPoints.add(new Coordinate(180, 200));
				checkPoints.add(new Coordinate(300, 200));

				Coordinate creepStart = new Coordinate(0, 40);
				Coordinate creepEnd = new Coordinate(300, 400);

				route = new Route(creepStart, creepEnd, checkPoints);

				GenericCreep testMonster = new GenericCreep(10, 100, 10, route);
				GenericCreep testMonster2 = new GenericCreep(10, 100, 10, route);
				GenericCreep testMonster3 = new GenericCreep(10, 100, 10, route);

				testMonster.setImage(mCreepImage);
				testMonster2.setImage(mCreepImage);
				testMonster3.setImage(mCreepImage);
				
				mPendingCreepList.add(testMonster);
				mPendingCreepList.add(testMonster2);
				mPendingCreepList.add(testMonster3);

				Coordinate[] tc = { new Coordinate(12, 12),
						new Coordinate(12, 13), new Coordinate(13, 12),
						new Coordinate(13, 13) };

				Tower testTower = new GenericTower(new Coordinate(150, 150));
				Tower testTower2 = new GenericTower(new Coordinate(225, 225));
				
				testTower.setImage(mTowerImage);
				testTower2.setImage(mTowerImage);
				mTowerList.add(testTower);
				mTowerList.add(testTower2);
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

		// @Override
		// public void run() {
		// while (mRun) {
		// Canvas c = null;
		// try {
		// c = mSurfaceHolder.lockCanvas(null);
		// synchronized (mSurfaceHolder) {
		// if (mMode == STATE_RUNNING)
		// updateGameState();
		// doDraw(c);
		// }
		// } finally {
		// // do this in a finally so that if an exception is thrown
		// // during the above, we don't leave the Surface in an
		// // inconsistent state
		// if (c != null) {
		// mSurfaceHolder.unlockCanvasAndPost(c);
		// }
		// }
		// }
		// }

		@Override
		public void run() {
			mIteration++;
			
			if (mTimeMainLoop != 0) {
				double timeDiff = System.currentTimeMillis()- mTimeMainLoop;
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
					doDraw(c);
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
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);
			
			// draw text
			canvas.drawText("Escaped creeps: " + mLives, 200, 20, mTextColor);
			canvas.drawText("FPS: " + mFPS, 200, 40, mTextColor);
			canvas.drawText("min FPS: " + mMinFPS, 200, 60, mTextColor);
			canvas.drawText("max FPS: " + mMaxFPS, 200, 80, mTextColor);

			// Draw path
			Coordinate startPoint = null;
			Coordinate endPoint = null;

			Coordinate globalStartPoint = route.getStart();
			Coordinate globalEndPoint = route.getEnd();

			ArrayList<Coordinate> t = route.getCheckPoints();

			for (Coordinate c : t) {
				startPoint = c;
				if (endPoint != null) {
					// Draw the route
					canvas.drawLine(startPoint.x, startPoint.y, endPoint.x,
							endPoint.y, mLinePaint);
				} else {
					canvas.drawLine(globalStartPoint.x, globalStartPoint.y,
							startPoint.x, startPoint.y, mLinePaint);
				}
				endPoint = c;
			}

			canvas.drawLine(endPoint.x, endPoint.y, globalEndPoint.x,
					globalEndPoint.y, mLinePaint);

			// Draw creeos
			// mCreepImage.setBounds(40, 40, 40 + 24, 40 + 24);
			// mCreepImage.draw(canvas);

			for (Creep creep : mCreepList) {
				Coordinate pos = creep.getPosition();
				Drawable creepImg = creep.getImage();

				creepImg.setBounds(pos.x -12, pos.y -12, pos.x +12, pos.y +12); // l,
																			// t
																			// ,
																			// r
																			// ,b
				creepImg.draw(canvas);

				// creep.setHealth(66);

				// Draw health bar
				int healthLeft = (int) (CREEP_HEALTH_BAR * creep
						.getHealthPercentage()) / 100;

				int healthBarBottom = CREEP_HEALTH_BAR_HOVER_HEIGHT
						- CREEP_HEALTH_BAR_HEIGHT;

				// red health bar
				mScratchRect.set(pos.x -12, pos.y - CREEP_HEALTH_BAR_HOVER_HEIGHT -12,
						pos.x + healthLeft -12, pos.y - healthBarBottom -12); // l t r b
				canvas.drawRect(mScratchRect, mHealthLeftColor);

				// black damage bar
				mScratchRect.set(pos.x + healthLeft -12, pos.y
						- CREEP_HEALTH_BAR_HOVER_HEIGHT -12, pos.x
						+ CREEP_HEALTH_BAR -12, pos.y - healthBarBottom -12); // l t r b
				canvas.drawRect(mScratchRect, mHealthLostColor);
			}

			// Draw towers
			for (Tower tower : mTowerList) {
				Coordinate pos = tower.getPosition();
				Drawable creepImg = tower.getImage();

				creepImg.setBounds(pos.x, pos.y, pos.x + 24, pos.y + 24); // l,
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

			if (mLastCreepTime + 1000 < now) {
				if (!mPendingCreepList.isEmpty()) {
					mCreepList.add(mPendingCreepList.get(0));
					mPendingCreepList.remove(0);
					mLastCreepTime = now;
				}
			}

			for (Iterator<Creep> i = mCreepList.iterator(); i.hasNext(); ) {
			    Creep creep = i.next();

			    boolean escaped = creep.isLastPos();
			    if (escaped || creep.getHealth() <= 0) {
			    	if (escaped) {
			    		mLives -= 1;
			    	}
			        i.remove();
			    } else {
			    	creep.move();
			    }
			}

			/*
			for (Creep creep : mCreepList) {
				// remove creatures
				if (creep.isLastPos() || creep.getHealth() <= 0) {
					mCreepList.remove(creep);
				} else {
					// move creatures
					creep.move();
				}
				//creep.move();
			}
			*/

			// shoot
			for (Tower tower : mTowerList) {
				tower.shoot(mCreepList);
			}

			double elapsed = (now - mLastTime) / 1000.0;

			mLastTime = now;
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