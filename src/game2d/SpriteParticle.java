/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;

/**
 *
 * @author benno
 */
public class SpriteParticle extends Entity {
    private float mLifetime;
    private Sprite mSprite;

    public SpriteParticle(float x, float y, float mx, float my, float size, float mass, float lifetime, Sprite s) {
        setPosition(x, y);
        setMotion(mx, my);
        setHitbox(size, size);
        weight = mass;
        mLifetime = lifetime;
        mSprite = s;
    }
    
    @Override
    public void onUpdate(float dt) {
        mLifetime -= dt;
        if(mLifetime <= 0) kill();
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawSprite(mSprite, this);
    }
}
