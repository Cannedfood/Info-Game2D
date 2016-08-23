package engine2d.level.particle;

import engine2d.GameMath;
import engine2d.Renderer;
import engine2d.level.Entity;
import engine2d.level.entity.Mob;
import game2d.Floater;

public class DamagingParticle extends FluidParticle {
    private Entity mOwner;
    private float mDamage;
    
    public DamagingParticle(Entity owner, float x, float y, float xm, float ym, int color, float size, float weight, float damage) {
        super(x, y, xm, ym, size, weight);
        mOwner = owner;
        mDamage = damage;
    }
    
    @Override
    public boolean onCollide(Entity other) {
        if(other instanceof Mob) {
            ((Mob) other).onDamage(mOwner, this, "explosion shrapnel", mDamage);
            kill();
        }
        
        return false;
    }
    
    @Override
    public void onResolve(Entity e, float x, float y) {
        kill();
    }
    
    @Override
    public boolean onKill() {
        getLevel().add(new Floater(this, rndf(1, 10)));
        return true;
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(0xFF006600, this);
    }
}
