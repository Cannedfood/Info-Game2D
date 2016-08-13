package engine2d.level.particle;

import engine2d.Renderer;
import engine2d.level.Entity;
import engine2d.level.Mob;

public class DamagingParticle extends FluidParticle {
    float damage;
    
    public DamagingParticle(float x, float y, float xm, float ym, int color, float size, float weight, float damage) {
        super(x, y, xm, ym, size, weight);
        this.damage = damage;
    }
    
    @Override
    public boolean onCollide(Entity other) {
        if(other instanceof Mob) {
            ((Mob) other).onDamage(this, "explosion scrapnel", damage);
            kill();
        }
        
        return false;
    }
    
    @Override
    public void onResolve(Entity e, float x, float y) {
        kill();
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(0xFF006600, this);
    }
}
