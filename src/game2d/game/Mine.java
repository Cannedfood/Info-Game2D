package game2d.game;

import game2d.Renderer;
import game2d.level.Entity;
import game2d.level.Mob;
import game2d.level.particle.DamagingParticle;
import java.util.Random;

public class Mine extends Entity {
    
    public Mine(float x, float y) {
        super(x, y);
        weight = 1.f;
    }
    
    public Mine(float x, float y, float xm, float ym) {
        super(x, y, xm, ym);
        weight = 1.f;
    }
    
    @Override
    public boolean onKill() {
        final Random r = getRandom();
        
        final float speed = 100f;
        final float size = 0.1f;
        final float mass = 1f;
        final float damage = 0.05f;
            
        final float distance = 3f;
            
        for(float b = 0; b < PI * 2; b += PI / 500) {
            float rf = r.nextFloat();
            float inv_rf = 1 - rf;
                
            float sin = sin(b);
            float cos = cos(b);
            float d = distance * rf;
                
            int r1 = 0xFF;
            int r2 = 0x80;
            int g1 = 0xAA;
            int g2 = 0x80;
                
            int c = 0xFF000000 | 
                    (int)(r1 * rf + inv_rf * r2) << 16 | 
                    (int)(g1 * rf + inv_rf * g2) << 8;
                
            Entity e = new DamagingParticle(
                    x + cos * d, y + sin * d, //< position
                    cos * speed, sin * speed, //< motion
                    c,               //< color
                    size,
                    mass,
                    damage
            );
                
            e.setCollisionMask(Entity.MASK_PARTICLE | Entity.MASK_DEFAULT);
                
            getLevel().add(e);
        }
        
        return true;
    }
    
    @Override
    public boolean onCollide(Entity other) {
        if(other instanceof Mob)
            kill();
        
        return true;
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(0xFFBB2222, cache_x_min, cache_y_min, width, height);
    }
}
