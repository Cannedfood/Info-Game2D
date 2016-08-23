package engine2d.level;

import engine2d.Backend;
import engine2d.Game;
import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Hitbox;
import engine2d.level.Level;

public class Entity extends Hitbox {
    public static final int 
            FLAG_UNIMPORTANT = 0x1, //!< Allows the level to remove this entity for performance reasons (mostly for particles)
            FLAG_OCCLUDER = 0x2; //!< This entity will occlude rays
    
    public static final int
            MASK_GHOST        = 0x0,
            MASK_DEFAULT      = 0x1,
            MASK_PARTICLE     = 0x2;
    
    /** Mass / Influence of gravity */
    public float weight = 0;
    /** Multiply speed by this when colliding */
    public float friction_mulitplier = 0.99f;
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

    public Entity() {
        super(1, 1);
    }
    
    /*******************************************
     * Methods you'd probably want to override *
     *******************************************/
    
    /** Is called every logic tick
     * @param dt The time since the last update in seconds
     */
    public void onUpdate(float dt) {}
    
    public void onPostUpdate(float dt) {}
    
    public void onDraw(Renderer b) { b.drawRect(0xFFFF00FF, this); }
    
    /** Returns true if the ray hits this object */
    public boolean onRay(float nx, float ny, float startx, float starty, float endx, float endy) {
        return true;
    }
    
    /** Is called every time this entity collides with another entity.
     * @param other The other entity
     * @return Interrupts the collision if false is returned.
     */
    public boolean onCollide(Entity other) {
        return true;
    }
    
    /** Is called whenever a collision was resolved.
     * @param e Null (if colliding with the level) or the entity this has collided with.
     * @param dx The x amount to resolve
     * @param dy The y amount to resolve
     */
    public void onResolve(Entity e, float dx, float dy) {}
    
    /** Is called just before the entity is killed.
     * @return whether the entity should really be killed.
     */
    protected boolean onKill() {
        return true;
    }
    
    
    /***********************
     * Getters and setters *
     ***********************/
    
    public final boolean isDead() { return !mAlive; }
    public final void kill() {
        if(mAlive && onKill())
            mAlive = false;
    }
    
    public final void    addFlags(int flags) { mFlags |= flags; }
    public final void    setFlags(int flags) { mFlags = flags; }
    public final boolean hasFlags(int flags) { return (mFlags & flags) == flags; }
    public final boolean hasFlag (int flag)  { return (mFlags & flag) != 0; }
    public final int     getFlags()          { return mFlags; }
    
    public final Entity setCollisionMask(int mask) { mCollisionMask = mask; return this; }
    public final int  getCollisionMask()         { return mCollisionMask; }
    
    /** Returns the magnitude of the motion vector. */
    public float getSpeed() {
        return sqrt(motion_x * motion_x + motion_y * motion_y);
    }
    
    /** Sets the speed while maintaining the direction.
     * @param target_speed the desired magnitude of the motion vector
     */
    public final Entity setSpeed(float target_speed) {
        float scale = target_speed / getSpeed();
        motion_x *= scale;
        motion_y *= scale;
        return this;
    }
    
    public final Entity setMotion(float mx, float my) {
        motion_x = mx;
        motion_y = my;
        return this;
    }
    
    public final Entity accelerate(float ax, float ay) {
        motion_x += ax;
        motion_y += ay;
        return this;
    }
    
    public final Entity setPosition(float x_, float y_) {
        x = x_;
        y = y_;
        return this;
    }
    
    public final Entity setWeight(float f) {
        weight = f;
        return this;
    }
    
    public final float getSurfaceDistance(float px, float py) {
        float xx = clamp(px, cache_x_min, cache_x_max);
        float yy = clamp(py, cache_y_min, cache_y_max);
        return length(xx - px, yy - py);
    }
    
    /***********************
     * Various shortcuts *
     ***********************/
    
    public final static Backend getBackend() { return Game.getBackend(); }
    public final static Level getLevel() { return Game.getLevel(); }
    public final static Sprite loadSprite(String file) { return Game.getBackend().loadSprite(file); }
}
