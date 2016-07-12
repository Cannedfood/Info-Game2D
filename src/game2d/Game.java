package game2d;

import game2d.level.Level;
import java.util.logging.Logger;

/**
 * The class running everything
 */
public class Game implements Runnable {
    private static Level  LEVEL;
    private static Backend BACKEND;
    public static void    setBackend(Backend backend) { BACKEND = backend; }
    public static Backend getBackend() { return BACKEND; }
    
    
    private final Thread mLogicThread;
    private boolean mRunning = false;
    
    public Game() {
        if(LEVEL == null)
            LEVEL = new Level(64, 64);
        mLogicThread = new Thread(this);
    }

    public void render() {
        LEVEL.onDraw(getBackend());
        getBackend().flush();
    }
    
    public void start() throws Exception {
        if(mRunning)
            throw new IllegalStateException("Tried to start the game twice");
        
        mRunning = true;
        mLogicThread.start();
    }
    
    public void stop() throws Exception {
        if(!mRunning)
            throw new IllegalStateException("Tried to stop a game that was not running");
        
        mRunning = false;
        mLogicThread.join(2000);
    }

    @Override
    public void run() {
        while(mRunning) {
            getBackend().preUpdate(this, 0.01f);
            
            try { Thread.sleep(10); } 
            catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            
            LEVEL.update(0.01f);
            getBackend().postUpdate(this, 0.01f);
        }
    }
    
    public static final Level getLevel() { return LEVEL; }
}
