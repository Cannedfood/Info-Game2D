package game2d.level;

import game2d.Renderer;
import java.util.ArrayList;
import java.util.HashMap;

/** A level, managing entities and tiles as well as their physics and rendering. */
public class Level {
    private final HashMap<String, Entity> mByName = new HashMap<>();
    private final ArrayList<Entity> mEntities = new ArrayList<>();
    
    private final int mWidth, mHeight;
    private final Tile[] mTiles;
    
    /** Creates a level of size w*h
     * @param w The width of the level
     * @param h The height of the level
     */
    public Level(int w, int h) {
        mWidth = w;
        mHeight = h;
        mTiles = new Tile[w * h];
    }
    
    /** Renders the tiles and entities touching the camera frustum.
     * @param r The rendering backend.
     */
    public void onDraw(Renderer r) {
        synchronized(mEntities) {
            int minx = Math.max(0, (int)Math.floor(r.getCamera().offset_x));
            int miny = Math.max(0, (int)Math.floor(r.getCamera().offset_y));
            int maxx = Math.min(mWidth, (int)Math.ceil(r.getCamera().offset_x + r.getCamera().width));
            int maxy = Math.min(mHeight, (int)Math.ceil(r.getCamera().offset_y + r.getCamera().height));
        
            for(int y = miny; y < maxy; y++) {
                int precalc_y = y * mWidth;
                for(int x = minx; x < maxx; x++) {
                    Tile t = mTiles[precalc_y + x];
                    if(t != null) t.onDraw(r, x, y);
                }
            }
        
            for(int i = 0; i < mEntities.size(); ++i) {
                Entity e = mEntities.get(i);
                if(r.getCamera().touches(e))
                    e.onDraw(r);
            }
        }
    }
    
    /** Is called every game tick and updates all entities and updatable tiles.
     * The updating includes the resolving of collisions.
     * @param dt The time difference since the last tick.
     */
    public void onUpdate(float dt) {
        // Update all entities
        for(int i = 0; i < mEntities.size(); ++i) {
            Entity e = mEntities.get(i);
            if(e.isDead()) {
                synchronized(mEntities) {
                    mEntities.remove(i);
                }
                --i;
            }
            else {
                e.onUpdate(dt);
                e.updateCache(e.x, e.y);
            }
        }
            
        // Resolving collisions between entities
        for(int i = 0; i < mEntities.size(); i++) {
            Entity e1 = mEntities.get(i);
            
            for(int j = i + 1; j < mEntities.size(); j++) {
                Entity e2 = mEntities.get(j);
                
                if(e1.touches(e2) &&
                   (e1.getCollisionMask() & e2.getCollisionMask()) != 0)
                {
                    boolean should_resolve; // Determine wether the collision has to be resolved
                    
                    // Don't resolve if one of the entities interrupts the collision or is a ghost
                    should_resolve = e1.onCollide(e2);
                    should_resolve = should_resolve & e2.onCollide(e1);
                    
                    if(should_resolve) {
                        // Resolve collision
                        
                        float xp = e2.cache_x_min - e1.cache_x_max;
                        float xn = e2.cache_x_max - e1.cache_x_min;
                        float yp = e2.cache_y_min - e1.cache_y_max;
                        float yn = e2.cache_y_max - e1.cache_y_min;
                        
                        float xd = Math.abs(xp) < Math.abs(xn) ? xp : xn;
                        float yd = Math.abs(yp) < Math.abs(yn) ? yp : yn;
                        xd *= 1.1f;
                        yd *= 1.1f;
                        
                        float k = .5f; // TODO: respect mass
                        if(Math.abs(xd) < Math.abs(yd)) {
                            e1.x += xd * k;
                            e2.x -= xd * (1 - k);
                            
                            float e1_motion_x = e1.motion_x;
                            e1.motion_x = e2.motion_x * .9f;
                            e2.motion_x = e1_motion_x * .9f;
                        }
                        else {
                            e1.y += yd * k;
                            e2.y -= yd * (1 - k);
                            
                            float e1_motion_y = e1.motion_y;
                            e1.motion_y = e2.motion_y * .9f;
                            e2.motion_y = e1_motion_y * .9f;
                        }
                    
                        e1.updateCache(e1.x, e1.y);
                        e2.updateCache(e2.x, e2.y);
                    }
                }
            }
        }
        
        // Resolving collisions with level
        for(Entity e : mEntities) {
            for(int i = 0; i < 4; i++) { // TODO: optimize level collision
                int minx = Math.max(0, (int)Math.floor(e.cache_x_min));
                int miny = Math.max(0, (int)Math.floor(e.cache_y_min));
                int maxx = Math.min(mWidth, (int)Math.ceil(e.cache_x_max));
                int maxy = Math.min(mHeight, (int)Math.ceil(e.cache_y_max));
                
                Tile first_collision = null;
                int cx = 0, cy = 0;
                
                OUTER_LOOP:
                for(int y = maxy - 1; y >= miny; --y) {
                    int precalc_y = y * mWidth;
                    for(int x = minx; x < maxx; ++x) {
                        Tile t = mTiles[precalc_y + x];
                        if(t != null && t.hasFlag(Tile.FLAG_SOLID) && t.onTouch(e, x, y)) {
                            first_collision = t;
                            cx = x;
                            cy = y;
                            break OUTER_LOOP;
                        }
                    }
                }
                
                if(first_collision == null) break;
                
                
                        float xp = cx - e.cache_x_max;
                        float xn = cx + 1 - e.cache_x_min;
                        float yp = cy - e.cache_y_max;
                        float yn = cy + 1 - e.cache_y_min;
                        
                        float xd = Math.abs(xp) < Math.abs(xn) ? xp : xn;
                        float yd = Math.abs(yp) < Math.abs(yn) ? yp : yn;
                        
                        if(Math.abs(xd) < Math.abs(yd)) {
                            e.x += xd;
                            
                            e.motion_x = 0;
                        }
                        else {
                            e.y += yd;
                            e.motion_y = 0;
                        }
                
                
                e.updateCache(e.x, e.y);
            }
            
            // Making sure the entity doesn't fall out of the level.
            if(e.cache_x_min < 0) {
                e.x -= e.cache_x_min;
                e.motion_x = 0;
            }
            else if(e.cache_x_max > mWidth) {
                e.x -= e.cache_x_max - mWidth;
                e.motion_x = 0;
            }
            
            if(e.cache_y_min < 0) {
                e.y -= e.cache_y_min;
                e.motion_y = 0;
            }
            else if(e.cache_y_max > mHeight) {
                e.y -= e.cache_y_max - mHeight;
                e.motion_y = 0;
            }
        }
    }
    
    /** Adds an entity.
     * Is synchronized to the entity list, which can interfere with rendering on very high frame rates.
     * @param e The entity to add
     */
    public void add(Entity e) {
        synchronized(mEntities) {
            mEntities.add(e);
        }
        
        e.updateCache(e.x, e.y);
    }
    
    /* Tile related methods */
    
    /** Gets a tile below a specific coordinate.
     * @param x The x coordinate of the Tile
     * @param y The y coordinate of the Tile
     * @return The tiles at { x, y } or null.
     */
    public Tile getTile(float x, float y) { return getTile((int)x, (int)y); }
    /** Gets a tile at a specific coordinate.
     * @param x The x coordinate of the Tile
     * @param y The y coordinate of the Tile
     * @return The tiles at { x, y } or null.
     */
    public Tile getTile(int x, int y) {
        int indx = x + y * mWidth;
        if(indx < 0 || indx > mTiles.length)
            return null;
        return mTiles[indx];
    }
    
    /** Gets a tile below a specific coordinate.
     * @param x The x coordinate of the Tile
     * @param y The y coordinate of the Tile
     * @param t
     */
    public void setTile(float x, float y, Tile t) { setTile((int)x, (int)y, t); }
    /** Gets a tile at a specific coordinate.
     * @param x The x coordinate of the Tile
     * @param y The y coordinate of the Tile
     * @param t The tile to be set to
     */
    public void setTile(int x, int y, Tile t) {
        int indx = x + y * mWidth;
        /*if(indx < 0 || indx > mTiles.length)
            return;*/
        mTiles[indx] = t;
    }
    
    /** Fills a rectangle with tiles of a certain type.
     */
    public void setTiles(float x, float y, float w, float h, Tile t) {
        setTiles((int)x, (int)y,(int)w, (int)h, t);
    }
    
    /** Fills a rectangle with tiles of a certain type.
     */
    public void setTiles(int x, int y, int w, int h, Tile t) {
        w = Math.min(x + w, mWidth);
        h = Math.min(y + h, mHeight);
        x = Math.max(0, x);
        y = Math.max(0, y);
        
        for(; y < h; ++y) {
            int ypre = y * mWidth;
            for(; x < w; ++x) {
                mTiles[ypre + x] = t;
            }
        }
    }
    
    /**
     * @param <CEntity>
     * @param spread
     * @param speed
     * @param angle_min
     * @param angle_max
     * @param sample
     */
    public <CEntity> void explode(float spread, float speed, float angle_min, float angle_max, Entity sample) {
        // TODO
    }
    
    public void setName(Entity e, String name) {
        // TODO
    }
    
    public void removeName(Entity e) {
        // TODO
    }
    
    public void removeName(String name) {
        // TODO
    }
    
    final Entity get(String name) { return mByName.get(name); }
    
    /* Getters and setters */
    
    public int getWidth()  { return mWidth; }
    public int getHeight() { return mHeight; }
    public ArrayList<Entity> getEntities() { return mEntities; }
}
