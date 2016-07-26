package game2d.game;

import game2d.level.Entity;
import game2d.level.Mob;

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
