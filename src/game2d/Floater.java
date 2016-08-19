/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.level.Entity;
import engine2d.level.Hitbox;

/**
 *
 * @author benno
 */
public class Floater extends Entity {
    int color = 0xFFFF00FF;
    float lifetime = 0;
    
    public Floater(Hitbox clone_from, float lifetime_) {
        super(0, 0);
        lifetime = lifetime_;
        
        setHitbox(clone_from);
        x = clone_from.getCachePositionX();
        y = clone_from.getCachePositionY();
        weight = 0;
        setCollisionMask(MASK_GHOST);
    }
    
    public Floater(float x_, float y_, float size, float lifetime_) {
        super(0, 0);
        lifetime = lifetime_;
        
        setHitbox(size, size);
        x = x_;
        y = y_;
        weight = 0;
        setCollisionMask(MASK_GHOST);
    }
    
    Floater setColor(int c) {
        color = c;
        return this;
    }
    
    @Override
    public void onUpdate(float dt) {
        lifetime -= dt;
        if(lifetime < 0)
            kill();
    }
    
    @Override
    public void onResolve(Entity e, float dx, float dy) {
        //kill();
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(color, this);
    }
}
