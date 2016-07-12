package game2d.level;

public class Projectile extends Entity {
    public Projectile(float x, float y) {
        super(x, y);
    }
    
    public Projectile(float x, float y, float xm, float ym) {
        super(x, y, xm, ym);
    }
    
    @Override
    public boolean onCollide(Entity other) {
        kill();
        return false;
    }
}
