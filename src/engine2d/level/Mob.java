package engine2d.level;

import static engine2d.GameMath.TWO_PI;
import static engine2d.GameMath.cos;
import static engine2d.GameMath.sin;
import engine2d.Renderer;
import engine2d.Sprite;
import static engine2d.level.Entity.getLevel;
import game2d.BloodParticle;
import java.util.Random;

public class Mob extends Entity implements Damagable {
    protected float mMaxHealth = 1.0f;
    protected float mHealth    = 0.1f;
    protected float mRegen     = 0.0f;
    
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
        
        float bar_width = (mHealth / mMaxHealth) * width;
        float bar_height = height * .1f;
        r.drawRect(0xFF991111, cache_x_min, cache_y_max + bar_height, bar_width, bar_height);
        r.drawRect(0xFF441111, cache_x_min + bar_width, cache_y_max + bar_height, width - bar_width, bar_height);
    }
    
    @Override
    public int onDamage(Entity source, Entity cause, String kind, float damage) {
        if(isDead()) return MISSED;
        
        if(pow(rndf(), damage) < .5f) {
            float mx = signof(cause.motion_x, sqrt(abs(cause.motion_x)) * .5f);
            float my = signof(cause.motion_y, sqrt(abs(cause.motion_y)) * .5f);
        
            Entity p = new BloodParticle(
                    cause.x, cause.y, 
                    mx, my, 
                    rnd_lerp(.1f, .3f), 
                    1f);
        
            p.offset_x = -p.width * .5f;
            p.offset_y = -p.height * .5f;

            //p.setSpeed(1);
        
            p.setCollisionMask(MASK_DEFAULT);
        
            getLevel().add(p);
        }
        
        mHealth = clamp(mHealth - damage, 0, mMaxHealth);
        if(mHealth <= 0) {
            kill();
            return KILLED;
        }
        
        return DAMAGED;
    }
    
    @Override
    public boolean onKill() {
        for(int i = 0; i < 30; i++) {
            final float min_size = 0.05f;
            final float max_size = 0.4f;
            final float speed = 20f;
                
            float angle = rndf(TWO_PI);
            
            float ex = rndf(cache_x_min, width);
            float ey = rndf(cache_y_min, height);
            
            float size = rnd_lerp(min_size, max_size);
               
            float cos = cos(angle) * speed;
            float sin = sin(angle) * speed;
                
            Entity p = new BloodParticle(ex, ey, cos, sin, size, .5f);
            p.setCollisionMask(Entity.MASK_DEFAULT);
            p.weight = size * size * 100f;
            getLevel().add(p);
        }
        
        return true;
    }
}
