package engine2d.level.entity;

import engine2d.level.Entity;

public class Projectile extends Entity {
    public Projectile(float x, float y) {
        setPosition(x, y);
    }
    
    public Projectile(float x, float y, float xm, float ym) {
        setPosition(x, y);
        setMotion(xm, ym);
    }
    
    @Override
    public boolean onCollide(Entity other) {
        kill();
        return false;
    }
}
