package game2d.level;

import game2d.Input;
import java.util.Random;

public class Player extends Mob {
    private Input mInput;
    
    public Player(float x, float y, Input input) {
        super(x, y);
        mInput = input;
        init();
    }
    
    public Player(float x, float y, float xm, float ym, Input input) {
        super(x, y, xm, ym);
        mInput = input;
        init();
    }
    
    private final void init() {
        setHitbox(4, 4);
        weight = 1.f;
        mLeftSprite  = loadSprite("sprite/pikapi-l.png");
        mRightSprite = loadSprite("sprite/pikapi-r.png");
    }
    
    @Override
    public boolean onKill() {
        Random r = new Random();
            
        for(int i = 0; i < width * height * 20; i++) {
            final float min_size = 0.05f;
            final float max_size = 0.4f;
            final float speed = 10f;
                
            float angle = (float) (r.nextFloat() * Math.PI * 2);
                
            float ex = cache_x_min + r.nextFloat() * width;
            float ey = cache_y_min + r.nextFloat() * height;
               
            float rand_size = r.nextFloat();
               
            float size = min_size * (1 - rand_size) + max_size * rand_size;
               
            float sin = (float) Math.sin(angle) * speed;
            float cos = (float) Math.cos(angle) * speed;
                
            Entity p = new SimpleParticle(ex, ey, sin, cos, 0xFF880000, size, size);
            p.setCollisionMask(Entity.MASK_GHOST);
            p.weight = size * size * 100f;
            getLevel().add(p);
        }
        
        return true;
    }
    
    @Override
    public void onUpdate(float dt) {
        if(mInput.is("pointer.down")) {
            float mx = mInput.getValue("pointer.x") - x;
            float my = mInput.getValue("pointer.y") - y;
            accelerate(mx * .1f, my * .1f);
        }
        
        final float speed = 5f;
        float x = mInput.getValue("move.x");
        float y = mInput.getValue("move.y");
        
        float mul = speed / (float) Math.sqrt(x * x + y * y);
        x *= mul;
        y *= mul;
        
        if(x * motion_x < 0 || Math.abs(x) > Math.abs(motion_x))
            motion_x = x;
        
        if(y * motion_y < 0 || Math.abs(y) > Math.abs(motion_y))
            motion_y = y;
        
        super.onUpdate(dt);
    }
}
