package game2d.level.particle;

import game2d.level.Entity;
import game2d.level.Mob;

public class DamagingParticle extends FluidParticle {
    float damage;
    
    public DamagingParticle(float x, float y, float xm, float ym, int color, float size, float weight, float damage) {
        super(x, y, xm, ym, color, size, weight);
        this.damage = damage;
    }
    
    @Override
    public boolean onCollide(Entity other) {
        if(other instanceof Mob)
            ((Mob) other).onDamage(damage, this);
        
        kill();
        
        return true;
    }
}
