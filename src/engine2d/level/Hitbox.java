package engine2d.level;

import engine2d.GameMath;

public class Hitbox extends GameMath {
    public float offset_x, offset_y;
    public float width, height;
    
    /** The hitbox after the translation of the object */
    public float cache_x_min, cache_x_max;
    public float cache_y_min, cache_y_max;
    
    public Hitbox(float w, float h) {
        setHitbox(w, h);
    }
    
    public Hitbox(float minx, float miny, float maxx, float maxy) {
        setHitbox(minx, miny, maxx, maxy);
    }
    
    public final void setHitbox(float w, float h) {
        setHitbox(-w * .5f, -h * .5f, w, h);
    }
    
    public final void setHitbox(float offx, float offy, float w, float h) {
        offset_x = offx;
        offset_y = offy;
        width    = w;
        height   = h;
    }
    
    public final void setHitbox(Hitbox other) {
        setHitbox(other.offset_x, other.offset_y, other.width, other.height);
    }
    
    public final void updateCache(float x, float y) {
        cache_x_min = x + offset_x;
        cache_x_max = cache_x_min + width;
        cache_y_min = y + offset_y;
        cache_y_max = cache_y_min + height;
    }
    
    public final boolean touches(Hitbox other) {
        return  cache_x_max > other.cache_x_min &&
                cache_y_max > other.cache_y_min &&
                other.cache_x_max > cache_x_min &&
                other.cache_y_max > cache_y_min;
    }
    
    public final float getMiddleX() { return cache_x_min + width * .5f;  }
    public final float getMiddleY() { return cache_y_min + height * .5f; }
    
    public final float getCachePositionX() { return cache_x_min - offset_x; }
    public final float getCachePositionY() { return cache_y_min - offset_y; }
}
