package game2d.level;

import game2d.Backend;
import game2d.Game;
import game2d.Renderer;
import game2d.Sprite;

public class Entity extends Hitbox {
    public static final int 
            FLAG_UNIMPORTANT = 0x1; //!< Allows the level to remove this entity for performance reasons (mostly for particles)
    
    public static final int
            MASK_GHOST        = 0x0,
            MASK_DEFAULT      = 0x1,
            MASK_PARTICLE     = 0x2;
    
    /** Mass / Influence of gravity */
    public float weight = 0;
    /** x and y position in the world */
    public float x = 0, y = 0;
    /** Motion along the x and y axis */
    public float motion_x = 0, motion_y = 0;
    
    /** Whether this entity should be removed */
    private boolean mAlive = true;
    /** Various flags */
    private int     mFlags = 0;
    /** */
    private int     mCollisionMask = MASK_DEFAULT;
    
    public Entity(float x, float y) {
        super(1, 1);//!< Make default hitbox 1x1
        this.x = x;
        this.y = y;
    }
    
    public Entity(float x, float y, float xm, float ym) {
        this(x, y);
        motion_x = xm;
        motion_y = ym;
    }
    
    /*******************************************
     * Methods you'd probably want to override *
     *******************************************/
    
    /** Is called every logic tick
     * @param dt The time since the last update in seconds
     */
    public void onUpdate(float dt) {
        motion_y -= weight * 9.81f * dt;
        
        x += motion_x * dt;
        y += motion_y * dt;
    }
    
    public void onDraw(Renderer b) {
        b.drawRect(0xFFFF00FF, this);
    }
    
    /** Is called every time this entity collides with another entity.
     * @param other The other entity
     * @return Interrupts the collision if false is returned.
     */
    public boolean onCollide(Entity other) {
        return true;
    }
    
    
    /***********************
     * Getters and setters *
     ***********************/
    public static Level getLevel() { return Game.getLevel(); }
    
    public boolean isDead() { return !mAlive; }
    public void    kill()   { mAlive = false; }
    
    
    public final void    addFlags(int flags) { mFlags |= flags; }
    public final void    setFlags(int flags) { mFlags = flags; }
    public final boolean hasFlags(int flags) { return (mFlags & flags) == flags; }
    public final boolean hasFlag (int flag)  { return (mFlags & flag) != 0; }
    public final int     getFlags()          { return mFlags; }
    
    public final void setCollisionMask(int mask) { mCollisionMask = mask; }
    public final int  getCollisionMask()         { return mCollisionMask; }
    
    /** Returns the magnitude of the motion vector. */
    public float getSpeed() {
        return (float)Math.sqrt(motion_x * motion_x + motion_y * motion_y);
    }
    
    /** Sets the speed while maintaining the direction.
     * @param target_speed the desired magnitude of the motion vector
     */
    public void setSpeed(float target_speed) {
        float scale = target_speed / getSpeed();
        motion_x *= scale;
        motion_y *= scale;
    }
    
    public void setMotion(float mx, float my) {
        motion_x = mx;
        motion_y = my;
    }
    
    public void accelerate(float ax, float ay) {
        motion_x += ax;
        motion_y += ay;
    }
    
    /***********************
     * Various shortcuts *
     ***********************/
    
    final static Backend getBackend() { return Game.getBackend(); }
    final static Sprite loadSprite(String file) { return Game.getBackend().loadSprite(file); }
}
