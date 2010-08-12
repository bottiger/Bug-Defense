package com.game.towerdefense;

import com.game.towerdefense.TowerDefenseView.TowerDefenseThread;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

/**
 * This is a simple TowerDefense activity that houses a single TowerView. It
 * demonstrates...
 * <ul>
 * <li>animating by calling invalidate() from draw()
 * <li>loading and drawing resources
 * <li>handling onPause() in an animation
 * </ul>
 */
public class TowerDefense extends Activity {

	private static final int MENU_EASY = 1;
	private static final int MENU_HARD = 2;
	private static final int MENU_MEDIUM = 3;

	private static final int MENU_PAUSE = 4;
	private static final int MENU_RESUME = 5;
	private static final int MENU_START = 6;
	private static final int MENU_STOP = 7;

	/** A handle to the thread that's actually running the animation. */
	private TowerDefenseThread mTowerDefenseThread;

	/** A handle to the View in which the game is running. */
	private TowerDefenseView mTowerDefenseView;
	
	/** A handle to the TileMap in which contains all the Tiles. */
	private TileMap mTileMap;

	/**
	 * Invoked during init to give the Activity a chance to set up its Menu.
	 * 
	 * @param menu
	 *            the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_START, 0, R.string.menu_start);
		menu.add(0, MENU_STOP, 0, R.string.menu_stop);
		menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
		menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
		menu.add(0, MENU_EASY, 0, R.string.menu_easy);
		menu.add(0, MENU_MEDIUM, 0, R.string.menu_medium);
		menu.add(0, MENU_HARD, 0, R.string.menu_hard);

		return true;
	}

	/**
	 * Invoked when the user selects an item from the Menu.
	 * 
	 * @param item
	 *            the Menu entry which was selected
	 * @return true if the Menu item was legit (and we consumed it), false
	 *         otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_START:
			mTowerDefenseThread.doStart();
			return true;
		case MENU_STOP:
			mTowerDefenseThread.setState(TowerDefenseThread.STATE_LOSE,
					getText(R.string.message_stopped));
			return true;
		case MENU_PAUSE:
			mTowerDefenseThread.pause();
			return true;
		case MENU_RESUME:
			mTowerDefenseThread.unpause();
			return true;
		case MENU_EASY:
			mTowerDefenseThread
					.setDifficulty(TowerDefenseThread.DIFFICULTY_EASY);
			return true;
		case MENU_MEDIUM:
			mTowerDefenseThread
					.setDifficulty(TowerDefenseThread.DIFFICULTY_MEDIUM);
			return true;
		case MENU_HARD:
			mTowerDefenseThread
					.setDifficulty(TowerDefenseThread.DIFFICULTY_HARD);
			return true;
		}

		return false;
	}

	/**
	 * Invoked when the Activity is created.
	 * 
	 * @param savedInstanceState
	 *            a Bundle containing state saved from a previous execution, or
	 *            null if this is a new execution
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// turn off the window's title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// tell system to use the layout defined in our XML file
		setContentView(R.layout.tower_defense);

		// get handles to the TowerView from XML, and its LunarThread
		mTowerDefenseView = (TowerDefenseView) findViewById(R.id.tower_defense);
		mTowerDefenseThread = mTowerDefenseView.getThread();
		mTileMap = new TileMap(mTowerDefenseView);

		// give the LunarView a handle to the TextView used for messages
		mTowerDefenseView.setTextView((TextView) findViewById(R.id.text));

		if (savedInstanceState == null) {
			// we were just launched: set up a new game
			// mTowerDefenseThread.setState(TowerDefenseThread.STATE_READY);
			mTowerDefenseThread.setState(TowerDefenseThread.STATE_RUNNING);
			Log.w(this.getClass().getName(), "SIS is null");
		} else {
			// we are being restored: resume a previous game
			mTowerDefenseThread.setState(TowerDefenseThread.STATE_RUNNING);
			mTowerDefenseThread.restoreState(savedInstanceState);
			Log.w(this.getClass().getName(), "SIS is nonnull");
		}
	}

	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mTowerDefenseView.getThread().pause(); // pause game when Activity
												// pauses
	}

	/**
	 * Notification that something is about to happen, to give the Activity a
	 * chance to save state.
	 * 
	 * @param outState
	 *            a Bundle into which this Activity should save its state
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// just have the View's thread save its state into our Bundle
		super.onSaveInstanceState(outState);
		mTowerDefenseThread.saveState(outState);
		Log.w(this.getClass().getName(), "SIS called");
	}
}
