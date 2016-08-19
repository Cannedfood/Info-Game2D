package engine2d.level.particle;

import engine2d.Renderer;
import engine2d.level.Entity;
import engine2d.level.Mob;

public class SimpleParticle extends Entity {
    private int mColor;
    private float mLifetime;
    
    public SimpleParticle(float x, float y, float xm, float ym, int color, float size, float weight, float lifetime) {
        super(x, y, xm, ym);
        setCollisionMask(MASK_PARTICLE);
        addFlags(FLAG_UNIMPORTANT);
        setHitbox(size, size);
        this.weight = weight;
        mColor = color;
        mLifetime = lifetime;
    }
    
    @Override
    public void onUpdate(float dt) {
        mLifetime -= dt;
        if(mLifetime <= 0) kill();
    }
    
    @Override
    public void onDraw(Renderer r) {
        r.drawRect(mColor, this);
    }
}
