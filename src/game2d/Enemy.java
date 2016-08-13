package game2d;

import engine2d.level.Entity;
import engine2d.level.Mob;

public class Enemy extends Mob {
    private static final float SCAN_EVERY = 0.02f;
       
    
    private Entity mTarget;
    private float  mTargetDistance;
    private float  mJumpHeight;
    
    private float  mTargetTimer;
    private float  mTargetSeenTimer;
    
    public Enemy(float x, float y, float xm, float ym) {
        super(x, y, xm, ym);
    }
    
    public Enemy(float x, float y) {
        super(x, y);
    }
    
    @Override
    public void onUpdate(float dt) {
        super.onUpdate(dt);
        
        if(mTarget == null) { 
            if(mTargetTimer >= SCAN_EVERY) {
                mTargetTimer -= SCAN_EVERY;
            
                for(Entity e : getLevel().getEntities()) {
                    if(e instanceof Player) {
                        float dx = e.x - x;
                        float dy = e.y - y;
                        float len1 = length(dx, dy);
                        if(len1 > mTargetDistance)
                            continue;
                        
                        float dot = motion_x * dx + motion_y * dy;
                        
                        float len2 = length(motion_x, motion_y);
                        
                        float see_cos = dot / (len1 * len2);
                        
                        if(see_cos < cos(degrees(120))) continue;
                        
                        if(getLevel().canSee(this, e))
                            mTarget = e;
                    }
                }
            }
        }
        else {
            
        }
    }
}
