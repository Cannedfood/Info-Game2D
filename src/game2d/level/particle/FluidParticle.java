package game2d.level.particle;

import game2d.level.Entity;
import game2d.level.Particle;

public class FluidParticle extends Particle {
    public FluidParticle(float x, float y, float xm, float ym, float size, float weight) {
        super(x, y, xm, ym);
        setCollisionMask(MASK_PARTICLE);
        addFlags(FLAG_UNIMPORTANT);
        setHitbox(size, size);
        this.weight = weight;
    }
    
    @Override
    public void onUpdate(float dt) {
        float scale = 1 - pow2(dt) * 0.1f;
        motion_x *= scale;
        motion_y *= scale;
    }
    
    @Override
    public boolean onCollide(Entity e) {
        if(e instanceof FluidParticle) {
            if(getMiddleY() - e.getMiddleY() < 0)
                motion_y += .1f;
            else
                motion_y -= .1f;
            
            if(getMiddleX() - e.getMiddleX() < 0)
                motion_y += .1f;
            else
                motion_y -= .1f;
            
            return false;
        }
        
        return true;
    }
}
