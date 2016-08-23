/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.enemy;

import engine2d.Debug;
import engine2d.level.Entity;
import engine2d.level.Level;
import engine2d.level.entity.Mob;
import game2d.Player;

/**
 *
 * @author benno
 */
public class MrMelee extends Mob {
    private static final float VIEW_DISTANCE = 30f;
    private static final float TIME_BETWEEN_SEARCHES = .5f;

    private Mob   mTarget;
    private float mAttackTimer = 0;
    private float mSearchTimer = 0;
    private float mLastSeenTimer = 0;
    private float mLastSeenX  = 0;
    private float mLastSeenY  = 0;

    public MrMelee() {
        setHitbox(2, 2);
        
        mMaxHealth = 100f;
        mHealth = mMaxHealth;

        weight = 4.f;
        mLeftSprite = loadSprite("sprite/mr-melee-l.png");
        mRightSprite = loadSprite("sprite/mr-melee-r.png");
        
        mSearchTimer = rndf(TIME_BETWEEN_SEARCHES);
    }

    @Override
    public boolean onCollide(Entity e) {
        if(mAttackTimer <= 0 && e == mTarget) {
            mTarget.onDamage(this, this, "Melee", 30f);
            mAttackTimer = .2f;
            if(mTarget.isDead())
                mTarget = null;
            return false;
        }

        return true;
    }
    
    public boolean canSee(Entity e) {
        return length2(e.x - x, e.y - y) <= pow2(VIEW_DISTANCE) &&
               !getLevel().traceTile(Level.getTileResult(), x, y, e.x, e.y);
    }

    @Override
    public void onUpdate(float dt) {
        mAttackTimer -= dt;
        mSearchTimer -= dt;

        if(mTarget == null) {
            if(mSearchTimer <= 0) {
                mSearchTimer = .5f;

                for(Entity e : getLevel().getEntities()) {
                    if(e instanceof Player) {
                        if(canSee(e)) {
                            mTarget = (Mob) e;
                            mLastSeenX = e.x;
                            mLastSeenY = e.y;
                            break;
                        }
                    }
                }
            }
        }
        else {
            if(canSee(mTarget)) {
                mLastSeenX = mTarget.x;
                mLastSeenY = mTarget.y;
                mLastSeenTimer = 0;
            }
            else
                mLastSeenTimer += dt;
            
            if(Debug.DRAW_AI_TARGET)
                getBackend().drawRect(0xFFFF00FF, mLastSeenX + mTarget.offset_x, mLastSeenY + mTarget.offset_y, mTarget.width, mTarget.height);
            
            float dx = mLastSeenX - x;
            float dy = mLastSeenY - y;

            motion_x = signof(dx, 5f);

            if(getLevel().onGround(this, .1f, .2f)) {
                if(dy > .8f)
                    motion_y = 20;
                else
                    motion_y = 5;
            }
            
            if(mLastSeenTimer > 30)
                mTarget = null;
        }

        super.onUpdate(dt);
    }
}
