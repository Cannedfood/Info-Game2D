package game2d.level.particle;

import game2d.level.Entity;

public class FluidParticle extends SimpleParticle {
    public FluidParticle(float x, float y, float xm, float ym, int color, float size, float weight) {
        super(x, y, xm, ym, color, size, weight);
    }
    
    @Override
    public boolean onCollide(Entity e) {
        if(e instanceof FluidParticle) {
            motion_y += getMiddleY() - e.getMiddleY();
            motion_x += getMiddleX() - e.getMiddleX();
            
            return false;
        }
        return true;
    }
}
