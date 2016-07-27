package game2d;

import game2d.level.Level;
import java.util.Random;
import java.util.logging.Logger;

/**
 * The class running everything
 */
public class Game implements Runnable {
    private static Random mLogicRandom = new Random();
    private static Level  LEVEL;
    private static Backend BACKEND;
    
    public static Random getRandom() { return mLogicRandom; }
    
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
            onUpdate(.01f);
            try { Thread.sleep(10); } 
            catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }
    
    /** Renders the whole level.
     * Will not call Backend.flush()!
     */
    public void onRender() {
        LEVEL.onDraw(getBackend());
        getBackend().flush();
    }
    
    /** Is called by the logic thread on every logic tick. */
    protected void onUpdate(float dt) {
        getBackend().preUpdate(this, 0.01f);
        LEVEL.onUpdate(0.01f);
        getBackend().postUpdate(this, 0.01f);
    }
}
