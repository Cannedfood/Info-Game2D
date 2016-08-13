package engine2d.level;

import engine2d.Renderer;
import engine2d.Sprite;
import game2d.BloodParticle;

public class Mob extends Entity implements Damagable {
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
        
        // r.debugDraw(this);
    }
    
    @Override
    public void onDamage(Entity caused_by, String kind, float damage) {
        mHealth -= damage;
        if(mHealth <= 0)
            kill();
        
        float mx = signof(caused_by.motion_x, sqrt(abs(caused_by.motion_x)) * .5f);
        float my = signof(caused_by.motion_y, sqrt(abs(caused_by.motion_y)) * .5f);
        
        //if(caused_by instanceof Particle)
        Entity p = new BloodParticle(
                caused_by.x, caused_by.y, 
                mx, my, 
                lerp(.1f, .3f, getRandom().nextFloat()), 
                1f);
        
        p.offset_x = -p.width * .5f;
        p.offset_y = -p.height * .5f;

        //p.setSpeed(1);
        
        p.setCollisionMask(MASK_DEFAULT);
        
        getLevel().add(p);
    }
}
