package game2d;

import game2d.level.Hitbox;

/**  An abstract platform dependent backend.
 * Implements the renderer and some other methods a backend must provide.
 */
public abstract class Backend implements Renderer {
    private final Input mInput = new Input();
    private final Hitbox mCamera = new Hitbox(0, 0, 64, 64);
    
    /** Constructor of the backend.
     * The camera is initialized to 64x64 at position { 0, 0 }
     */
    public Backend() {
        mCamera.updateCache(0, 0);
    }
    
    /** Load a sprite.
     * This method caches used Sprites, so loading multiple times is fairly fast.
     * @param path The path to a .png image.
     * @return Returns the loaded sprite or null if the loading fails.
     */
    public abstract Sprite loadSprite(String path);
    
    /** Is called as the first thing in  @see Game.onUpdate().
     * @param g The game that is updated
     * @param dt The time difference for which will be updated.
     */
    public abstract void preUpdate(Game g, float dt);
    /** Is called as the last statement in @see Game.onUpdate(). 
     * @param g The game that is updated
     * @param dt The time difference for which was updated for.
     */
    public abstract void postUpdate(Game g, float dt);
    
    @Override
    public void setCamera(Hitbox h) {
        mCamera.setHitbox(h);
        mCamera.updateCache(0, 0);
    }
    
    @Override
    public final Hitbox getCamera() { return mCamera; }
    
    /** @return The Input of the backend. */
    public final Input getInput() { return mInput; }

    @Override
    public abstract void drawSprite(Sprite sprite, float x, float y, float w, float h);

    @Override
    public abstract void drawSprite(Sprite sprite, Hitbox h);

    @Override
    public abstract void drawRect(int color, float x, float y, float w, float h);

    @Override
    public abstract void drawRect(int color, Hitbox h);
}
