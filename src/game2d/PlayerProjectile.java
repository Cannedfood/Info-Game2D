/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.level.Damagable;
import engine2d.level.Entity;
import engine2d.level.Mob;

/**
 *
 * @author benno
 */
public class PlayerProjectile extends Entity {
    private final Player mPlayer;
    private int          mColor;
    private float        mDamage;
    private float        mLifetime;
    private boolean      mDisabled;
    
    public PlayerProjectile(Player p, float x, float y, float damage, float lifetime, int color) {
        super(x, y);
        setHitbox(.1f, .1f);
        weight = .1f;
        mPlayer = p;
        mDamage = damage;
        mLifetime = lifetime;
        mColor = color;
    }
    
    @Override
    public void onUpdate(float dt) {
        mLifetime -= dt;
        if(mLifetime <= 0) kill();
    }
    
    @Override
    public boolean onCollide(Entity e) {
        if(e instanceof Player)
            return false;
        else if(e instanceof Mob) {
            if(!mDisabled)  {
                int res = ((Mob)e).onDamage(mPlayer, this, "Magical dust", mDamage);
                if(res == Damagable.KILLED)
                    mPlayer.onDamage(this, this, "Health buff from killing entity", -10);
            }
            
            kill();
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onResolve(Entity e, float dx, float dy) {
        if(!(e instanceof PlayerProjectile)) {
            mDisabled = true;
            mColor = 0xFF000000;
        }
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(mColor, this);
    }
}
