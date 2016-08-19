package engine2d;

import engine2d.level.Level;
import engine2d.level.LevelLoader;
import java.util.Random;
import java.util.logging.Logger;

/**
 * The class running everything
 */
public class Game extends GameMath implements Runnable {
    private static Level  LEVEL;
    private static Backend BACKEND;
    
    /** Sets the backend (statically)
     * @param backend The new back end */
    public static void    setBackend(Backend backend) { BACKEND = backend; }
    
    /** Returns the current backend.
     * @return Returns the current backend
     */
    public static Backend getBackend() { return BACKEND; }
    
    /** Returns the current level (statically).
     * @return  The current level. */
    public static final Level getLevel() { return LEVEL; }
    
    private final Thread mLogicThread; //!< The thread running game logic
    private boolean mRunning = false;  //!< Whether the game
    
    public Game() {
        if(LEVEL == null)
            LEVEL = new Level(64, 64);
        mLogicThread = new Thread(this);
    }
    
    protected static void loadLevel(LevelLoader ll, Sprite data) {
        LEVEL = ll.loadLevel(data);
        LEVEL.updateAllTiles();
    }
    
    /** Starts the logic thread / the game. 
     * @throws java.lang.IllegalStateException when the game is already running.
     */
    public void start() throws IllegalStateException {
        if(mRunning)
            throw new IllegalStateException("Tried to start the game twice");
        
        mRunning = true;
        mLogicThread.start();
    }
    
    /** Stops the game thread, keeping the game state intact.
     * @throws java.lang.IllegalStateException when the game isn't running.
     */
    public void stop() throws IllegalStateException {
        if(!mRunning)
            throw new IllegalStateException("Tried to stop a game that was not running");
        
        mRunning = false;
        try {
            mLogicThread.join(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    @Override
    /** Entry point for the Logic thread. */
    public void run() {
        while(mRunning) {
            long beg = System.currentTimeMillis();
            onUpdate(.01f);
            long end = System.currentTimeMillis();
            try { Thread.sleep(max(0, 10 - end + beg)); } 
            catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /** Renders the whole level.
     */
    public void onRender() {
        LEVEL.onDraw(getBackend());
    }
    
    /** Is called by the logic thread on every logic tick. */
    protected void onUpdate(float dt) {
        getBackend().preUpdate(this, 0.01f);
        LEVEL.onUpdate(0.01f);
        getBackend().postUpdate(this, 0.01f);
    }
}
