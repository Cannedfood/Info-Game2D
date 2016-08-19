package game2d;

import engine2d.Input;
import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;
import engine2d.level.Mob;

public class Player extends Mob {
    private static final float WALL_JMP_COOLDOWN = 1;
    
    private static final float WALK_SPEED  = 20;
    private static final float FLY_CONTROL = 0.3f;
    private static final float FLY_MAX     = 6f;
    
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
        mMaxHealth = 50f;
        mHealth = mMaxHealth;
        
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
    public void onUpdate(float dt) {
        mWallJumpCooldown -= dt;
        
        mOnGround = getLevel().onGround(this, 0.2f, 0.7f);
        
        float dy = mInput.getValue("move.y");
        float dx = mInput.getValue("move.x");
        
        if(mOnGround) {
            motion_x = dx * WALK_SPEED;
        }
        else if(mWallJumpCooldown <= 0) {
            if(abs(motion_x) <= FLY_MAX)
                motion_x = clamp(motion_x + dx * FLY_CONTROL, -FLY_MAX, FLY_MAX);
            else if(motion_x * dx < 0)
                motion_x += dx * 0.1f;
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
                motion_x = -20;
            else
                motion_x = 20;
        }
        
        if(!mOnGround && mOnWall == 0)
            selectJumpingSprites();
        else
            selectStandingSprites();
        
        if(mInput.poll("debug1"))
            System.out.println("Player: x=" + floor_int(x) + " y=" + floor_int(y));
        
        if(mInput.poll("debug3"))
            setPosition(652, 89);
        
        if(mInput.is("pointer.down")) {
            float mx = mInput.getValue("pointer.x") - x;
            float my = mInput.getValue("pointer.y") - y;
            
            for(int i = 0; i < 4; ++i) {
                float heightr = rndf();
                Entity e = new PlayerProjectile(
                        this,
                        getCachePositionX(), lerp(cache_y_min, cache_y_max, heightr), 
                        .1f,
                        rndf(1, 5),
                        lerp_argb(0xFFFF0000, 0xFF0000FF, heightr)
                    );
                e.weight = 0.5f;
                e.motion_x = mx + motion_x;
                e.motion_y = my + motion_y;
                getLevel().add(e);
            }
            
            motion_x -= mx * 0.1f;
            motion_y -= my * 0.1f;
        }
        
        super.onUpdate(dt);
    }
    
    @Override
    public int onDamage(Entity source, Entity cause, String msg, float damage) {
        int re = super.onDamage(source, cause, msg, damage);
        
        System.out.println("Took " + damage + " damage: " + msg);
        if(re == KILLED)
            System.out.println("You died.");
        
        return re;
    }
    
    @Override
    public void onPostUpdate(float f) {
        mOnWall = 0;
    }
    
    @Override
    public void onResolve(Entity e, float x, float y) {
        if(x < 0) mOnWall = 1;
        else if(x > 0) mOnWall = -1;
        super.onResolve(e, x, y);
    }
    
    @Override
    public void onDraw(Renderer r) {
        if(mCurrentSprite != null)
                r.drawSprite(mCurrentSprite, cache_x_min, cache_y_min + (mOnGround ? cos(cache_x_min * 3f) * .05f * height : 0f), width, height);
        
        float bar_width = (mHealth / mMaxHealth) * width;
        float bar_height = height * .1f;
        r.drawRect(0xFF991111, cache_x_min, cache_y_max + bar_height, bar_width, bar_height);
        r.drawRect(0xFF441111, cache_x_min + bar_width, cache_y_max + bar_height, width - bar_width, bar_height);
    }
}
