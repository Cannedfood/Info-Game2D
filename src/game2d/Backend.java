package game2d;

import game2d.level.Hitbox;

/**  An abstract platform dependent backend.
 * Implements the renderer and some other methods a backend must provide.
 */
public abstract class Backend implements Renderer {
    private Input mInput = new Input();
    private Hitbox mCamera = new Hitbox(0, 0, 64, 64);
    
    public Backend() {
        mCamera.updateCache(0, 0);
    }
    
    public abstract Sprite loadSprite(String file);
    
    public abstract void preUpdate(Game g, float dt);
    public abstract void postUpdate(Game g, float dt);
    
    @Override
    public void setCamera(Hitbox h) {
        mCamera.setHitbox(h);
        mCamera.updateCache(0, 0);
    }
    
    @Override
    public final Hitbox getCamera() { return mCamera; }
    
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
