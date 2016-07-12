/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.game;

import game2d.Renderer;
import game2d.level.Entity;
import game2d.level.Mob;
import game2d.level.SimpleParticle;
import java.util.Random;

/**
 *
 * @author benno
 */
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
    public void kill() {
        if(!isDead()) {
            Random r = new Random();
            
            final float speed = 100f;
            final float size = 0.1f;
            final float weight = 1f;
            
            final float distance = 3f;
            
            for(float b = 0; b < Math.PI * 2; b += (float)Math.PI / 500f) {
                float rf = r.nextFloat();
                float inv_rf = 1 - rf;
                
                float sin = (float)Math.sin(b);
                float cos = (float)Math.cos(b);
                float d = distance * rf;
                
                int r1 = 0xFF;
                int r2 = 0x80;
                int g1 = 0xAA;
                int g2 = 0x80;
                
                int c = 0xFF000000 | 
                        (int)(r1 * rf + inv_rf * r2) << 16 | 
                        (int)(g1 * rf + inv_rf * g2) << 8;
                
                Entity e = new SimpleParticle(
                        x + cos * d, y + sin * d, //< position
                        cos * speed, sin * speed, //< motion
                        c,               //< color
                        size,
                        weight
                );
                /*e.weight = weight;
                e.width = size;
                e.height = size;
                e.addFlags(FLAG_UNIMPORTANT);
                e.setCollisionMask(MASK_PARTICLE);*/
                
                getLevel().add(e);
            }
        }
        
        super.kill();
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
