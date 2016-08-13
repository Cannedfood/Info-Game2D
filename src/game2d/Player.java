package game2d;

import engine2d.Input;
import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;
import engine2d.level.Mob;
import java.util.Random;

public class Player extends Mob {
    private static final float WALL_JMP_COOLDOWN = 1;
    
    Sprite mSpriteJumpL;
    Sprite mSpriteJumpR;
    Sprite mSpriteStandL;
    Sprite mSpriteStandR;
    
    private final Input mInput;
    
    private boolean mOnGround = false;
    private int     mOnWall   = 0;
    private float   mWallJumpCooldown = 0;
    
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
    
    private void init() {
        setHitbox(2, 2);
        weight = 4.f;
        
        mSpriteStandL = loadSprite("sprite/pika-stand-l.png");
        mSpriteStandR = loadSprite("sprite/pika-stand-r.png");
        mSpriteJumpL  = loadSprite("sprite/pika-jmp-l.png");
        mSpriteJumpR  = loadSprite("sprite/pika-jmp-r.png");
        
        selectStandingSprites();
    }
    
    private void selectStandingSprites() {
        mCurrentSprite = (mCurrentSprite == mSpriteJumpL) ? mSpriteStandL : mSpriteStandR;
        mLeftSprite = mSpriteStandL;
        mRightSprite = mSpriteStandR;
    }
    
    private void selectJumpingSprites() {
        mCurrentSprite = (mCurrentSprite == mSpriteStandL) ? mSpriteJumpL : mSpriteJumpR;
        mLeftSprite = mSpriteJumpL;
        mRightSprite = mSpriteJumpR;
    }
    
    @Override
    public boolean onKill() {
        Random r = new Random();
            
        for(int i = 0; i < width * height * 20; i++) {
            final float min_size = 0.05f;
            final float max_size = 0.4f;
            final float speed = 10f;
                
            float angle = r.nextFloat() * TWO_PI;
            
            float ex = cache_x_min + r.nextFloat() * width;
            float ey = cache_y_min + r.nextFloat() * height;
               
            float rand_size = r.nextFloat();
               
            float size = min_size * (1 - rand_size) + max_size * rand_size;
               
            float sin = sin(angle) * speed;
            float cos = cos(angle) * speed;
                
            Entity p = new BloodParticle(ex, ey, sin, cos, size, .5f);
            p.setCollisionMask(Entity.MASK_DEFAULT);
            p.weight = size * size * 100f;
            getLevel().add(p);
        }
        
        return true;
    }
    
    @Override
    public void onUpdate(float dt) {
        mWallJumpCooldown -= dt;
        
        if(mInput.poll("pointer.down")) {
            /*float mx = mInput.getValue("pointer.x") - x;
            float my = mInput.getValue("pointer.y") - y;
            accelerate(mx * .1f, my * .1f);*/
            
            x = mInput.getValue("pointer.x");
            y = mInput.getValue("pointer.y");
            motion_x = motion_y = 0;
        }
        
        float dy = mInput.getValue("move.y");
        float dx = mInput.getValue("move.x");
        
        if(mWallJumpCooldown <= 0 && dx * motion_x < 0 || abs(dx) > abs(motion_x)) {
            if(mOnGround)
                motion_x = dx * 10;
            else
                motion_x += dx;
        }
        
        if(mOnWall != 0)
            motion_y = clamp(motion_y, -.5f, .5f);
        
        if(mOnGround && dy > 0)
            motion_y = dy * 10 * weight;
        else if(!mOnGround && mOnWall != 0 && dy != 0) {
            mWallJumpCooldown = WALL_JMP_COOLDOWN;
            if(dy > 0)
                motion_y = dy * 8 * weight;
            else
                motion_y = weight != 0 ? dy * 8 / weight : 0;
            
            if(mOnWall > 0)
                motion_x = 20;
            else
                motion_x = -20;
        }
        
        if(!mOnGround && mOnWall == 0)
            selectJumpingSprites();
        else
            selectStandingSprites();
        
        super.onUpdate(dt);
    }
    
    @Override
    public void onPostUpdate(float dt) {
        mOnGround = false;
        mOnWall = 0;
    }
    
    @Override
    public void onResolve(Entity e, float dx, float dy) {
        super.onResolve(e, dx, dy);
        
        if(e == null) // If colliding with the level
        {
            mOnGround = dy > 0;
            mOnWall = dx == 0 ? 0 : (dx < 0 ? -1 : 1);
        }
    }
    
    @Override
    public void onDraw(Renderer r) {
        if(mCurrentSprite != null)
                r.drawSprite(mCurrentSprite, cache_x_min, cache_y_min + (mOnGround ? cos(cache_x_min * 3f) * .05f * height : 0f), width, height);
    }
}
