package game2d;

import engine2d.level.Entity;
import engine2d.level.Mob;

public class Enemy extends Mob {
    private Entity mTarget;
    private float  mTargetDistance;
    private float  mJumpHeight;
    
    public Enemy(float x, float y, float xm, float ym) {
        super(x, y, xm, ym);
    }
    
    public Enemy(float x, float y) {
        super(x, y);
    }
    
    @Override
    public void onUpdate(float dt) {
        super.onUpdate(dt);
    }
}
