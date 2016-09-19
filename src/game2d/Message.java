/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.level.Entity;

/**
 *
 * @author benno
 */
public class Message extends Entity {
    private String mMsg;
    private float mLifetime;
    
    public Message(String s, float lifetime) {
        mMsg = s;
        mLifetime = lifetime;
        setMotion(0, 0);
        setCollisionMask(MASK_GHOST);
        setHitbox(0, 0, 100, 100);
    }
    
    @Override
    public void onUpdate(float dt) {
        mLifetime -= dt;
        if(mLifetime < 0)
            kill();
    }
    
    @Override
    public void onResolve(Entity e, float x, float y) {
        this.x -= x;
        this.y -= y;
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawString(0xFF888888, mMsg, x, y - .1f);
        r.drawString(0xFFFFFFFF, mMsg, x + .1f, y);
   }
}
