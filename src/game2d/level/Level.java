package game2d.level;

import game2d.Renderer;
import java.util.ArrayList;

public class Level {
    private final ArrayList<Entity> mEntities = new ArrayList<>();
    
    private int mWidth, mHeight;
    private Tile[] mTiles;
    
    public Level(int x, int y) {
        mWidth = x;
        mHeight = y;
        mTiles = new Tile[x * y];
    }
    
    public void onDraw(Renderer b) {
        synchronized(mEntities) {
            int minx = Math.max(0, (int)Math.floor(b.getCamera().offset_x));
            int miny = Math.max(0, (int)Math.floor(b.getCamera().offset_y));
            int maxx = Math.min(mWidth, (int)Math.ceil(b.getCamera().offset_x + b.getCamera().width));
            int maxy = Math.min(mHeight, (int)Math.ceil(b.getCamera().offset_y + b.getCamera().height));
        
            for(int y = miny; y < maxy; y++) {
                int precalc_y = y * mWidth;
                for(int x = minx; x < maxx; x++) {
                    Tile t = mTiles[precalc_y + x];
                    if(t != null) t.onDraw(b, x, y);
                }
            }
        
            for(int i = 0; i < mEntities.size(); ++i) {
                Entity e = mEntities.get(i);
                if(b.getCamera().touches(e))
                    e.onDraw(b);
            }
        }
    }
    
    public void update(float dt) {
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
    
    public void add(Entity e) {
        synchronized(mEntities) {
            mEntities.add(e);
        }
        
        e.updateCache(e.x, e.y);
    }
    
    /* Tile related methods */
    
    public Tile getTile(float x, float y) { return getTile((int)x, (int)y); }
    public Tile getTile(int x, int y) {
        int indx = x + y * mWidth;
        if(indx < 0 || indx > mTiles.length)
            return null;
        return mTiles[indx];
    }

    public void setTile(float x, float y, Tile t) { setTile((int)x, (int)y, t); }
    public void setTile(int x, int y, Tile t) {
        int indx = x + y * mWidth;
        /*if(indx < 0 || indx > mTiles.length)
            return;*/
        mTiles[indx] = t;
    }
    
    public void setTiles(float x, float y, float w, float h, Tile t) {
        setTiles((int)x, (int)y,(int)w, (int)h, t);
    }
    
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
    
    /* Getters and setters */
    
    public int getWidth()  { return mWidth; }
    public int getHeight() { return mHeight; }
    public ArrayList<Entity> getEntities() { return mEntities; }
}
