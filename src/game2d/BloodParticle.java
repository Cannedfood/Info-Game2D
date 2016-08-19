/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;
import engine2d.level.Mob;
import engine2d.level.particle.FluidParticle;

/**
 *
 * @author benno
 */
public class BloodParticle extends FluidParticle {
    private final float COOLDOWN = 0.1f;
    
    private final Sprite mPuddleSprite;
    private final Sprite mDropSprite;
    
    private boolean mTmpOnGround;
    private boolean mOnGround;
    
    private float mTouchCooldown = 0;
    
    private float mLifetime = rndf(6, 4);
    
    private float default_size;
    
    public BloodParticle(float x, float y, float xm, float ym, float size, float weight) {
        super(x, y, xm, ym, size, weight);
        
        default_size = size;
        friction_mulitplier = 0f;
        
        mPuddleSprite = loadSprite("sprite/blob.png");
        mDropSprite   = loadSprite("sprite/drop.png");
        mOnGround = false;
    }
    
    @Override
    public void onDraw(Renderer r) {
        if(mOnGround)
            r.drawSprite(mPuddleSprite, this);
        else
            r.drawSprite(mDropSprite, this);
    }
    
    @Override
    public void onPostUpdate(float dt) {
        mLifetime -= dt;
        if(mLifetime <= 0) kill();
        
        mTouchCooldown -= dt;
        
        mOnGround = mTmpOnGround;
        
        if(!mOnGround) {
            width = default_size;
            height = default_size;
        }
        
        mTmpOnGround = false;
    }
    
    @Override
    public boolean onCollide(Entity e) {
        if(e instanceof Mob) {
            if(mTouchCooldown < 0) {
                mTouchCooldown = COOLDOWN;
                y += .3f * -e.motion_y;
                motion_y += 10f * -e.motion_y;
                motion_x += rndf() - .5f;
            }
            else
                mTouchCooldown = COOLDOWN;
            return false;
        }
        return !(e instanceof FluidParticle);
    }
    
    @Override
    public void onResolve(Entity e, float dx, float dy) {
        if(dy > 0) {
            mOnGround = true;
            mTmpOnGround = true;
            height = .25f;
            width = default_size * 5f;
        }
    }
}
