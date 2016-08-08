package game2d.level.particle;

import game2d.Renderer;
import game2d.level.Entity;
import game2d.level.Mob;

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
        
        return true;
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(0xFF006600, this);
    }
}
