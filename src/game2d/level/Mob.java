package game2d.level;

import game2d.Renderer;
import game2d.Sprite;

public class Mob extends Entity {
    protected float mMaxHealth = 1.0f;
    protected float mHealth    = 0.1f;
    protected float mRegen     = 0.05f;
    
    protected Sprite mLeftSprite;
    protected Sprite mRightSprite;
    protected Sprite mCurrentSprite;
    
    public Mob(float x, float y) {
        super(x, y);
    }
    
    public Mob(float x, float y, float xm, float ym) {
        super(x, y, xm, ym);
    }
    
    public void updateSprite() {
        if(this.motion_x > 0)
            mCurrentSprite = mRightSprite;
        else if(this.motion_x < 0)
            mCurrentSprite = mLeftSprite;
        else if(mCurrentSprite == null)
            mCurrentSprite = mRightSprite;
    }
    
    @Override
    public void onUpdate(float dt) {
        if(mHealth <= 0)
            kill();
        else if(mHealth < mMaxHealth)
            mHealth += mRegen * dt;
        
        super.onUpdate(dt);
        updateSprite();
    }
    
    @Override
    public void onDraw(Renderer r) {
        if(mCurrentSprite != null)
            r.drawSprite(mCurrentSprite, cache_x_min, cache_y_min, width, height);
        else
            super.onDraw(r);
        r.debugDraw(this);
    }
}
