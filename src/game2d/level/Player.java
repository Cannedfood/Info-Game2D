package game2d.level;

import game2d.Input;

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
        setHitbox(1, 1);
        weight = 1.f;
        mLeftSprite  = loadSprite("/home/benno/Pictures/Ball-left.png");
        mRightSprite = loadSprite("/home/benno/Pictures/Ball-right.png");
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
