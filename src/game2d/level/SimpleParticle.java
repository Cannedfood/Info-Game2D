/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.level;

import game2d.Renderer;

/**
 *
 * @author benno
 */
public class SimpleParticle extends Entity {
    private int mColor;
    
    public SimpleParticle(float x, float y, float xm, float ym, int color, float size, float weight) {
        super(x, y, xm, ym);
        setCollisionMask(MASK_PARTICLE);
        addFlags(FLAG_UNIMPORTANT);
        setHitbox(size, size);
        mColor = color;
        this.weight = weight;
        
    }
    
    @Override
    public boolean onCollide(Entity other) {
        if(other instanceof Mob)
            other.kill();
        return true;
    }
    
    @Override
    public void onUpdate(float dt) {
        super.onUpdate(dt);
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(mColor, this);
    }
}
