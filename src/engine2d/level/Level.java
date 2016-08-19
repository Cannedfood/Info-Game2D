package engine2d.level;

import engine2d.Debug;
import engine2d.Game;
import engine2d.GameMath;
import engine2d.Renderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * A level, managing entities and tiles as well as their physics and rendering.
 */
public class Level extends GameMath {

    public final static TileResult TILE_RESULT = new TileResult();
    public final static EntityResult ENTITY_RESULT = new EntityResult();
    public final static PositionResult POSITION_RESULT = new PositionResult();

    private final HashMap<String, Entity> mByName = new HashMap<>();
    private final ArrayList<Entity> mEntities = new ArrayList<>();

    private final int mWidth, mHeight;
    private final Tile[] mTiles;

    /**
     * Creates a level of size w*h
     *
     * @param w The width of the level
     * @param h The height of the level
     */
    public Level(int w, int h) {
        mWidth = w;
        mHeight = h;
        mTiles = new Tile[w * h];
    }

    /**
     * Renders the tiles and entities touching the camera frustum.
     *
     * @param r The rendering backend.
     */
    public void onDraw(Renderer r) {
        r.getCamera().updateCache(0, 0);

        final int minx = max(0, floor_int(r.getCamera().cache_x_min));
        final int miny = max(0, floor_int(r.getCamera().cache_y_min));
        final int maxx = min(mWidth - 1, floor_int(r.getCamera().cache_x_max));
        final int maxy = min(mHeight - 1, floor_int(r.getCamera().cache_y_max));

        for(int y = miny; y <= maxy; y++) {
            int precalc_y = y * mWidth;
            for(int x = minx; x <= maxx; x++) {
                Tile t = mTiles[precalc_y + x];
                if(t != null)
                    t.onDraw(r, x, y);
            }
        }

        synchronized(mEntities) {
            for(int i = 0; i < mEntities.size(); ++i) {
                Entity e = mEntities.get(i);
                if(e.touches(r.getCamera()))
                    e.onDraw(r);
            }
        }
    }

    private void updateEntity(Entity e, float dt) {
        e.motion_y -= e.weight * 9.81f * dt;

        e.onUpdate(dt);

        e.x += e.motion_x * dt;
        e.y += e.motion_y * dt;

        e.onPostUpdate(dt);
        e.updateCache(e.x, e.y);
    }

    private void resolveEntityCollision(Entity e1, Entity e2) {
        float xp = e2.cache_x_min - e1.cache_x_max;
        float xn = e2.cache_x_max - e1.cache_x_min;
        float yp = e2.cache_y_min - e1.cache_y_max;
        float yn = e2.cache_y_max - e1.cache_y_min;

        float xd = absmin(xp, xn);
        float yd = absmin(yp, yn);
        xd *= 1.1f;
        yd *= 1.1f;

        float k = .5f; // TODO: respect mass
        if(abs(xd) < abs(yd)) {
            e1.x += xd * k;
            e2.x -= xd * (1 - k);
            e1.onResolve(e2, xd * k, 0);
            e2.onResolve(e1, -xd * (1 - k), 0);

            float e1_motion_x = e1.motion_x;
            e1.motion_x = e2.motion_x * .9f;
            e2.motion_x = e1_motion_x * .9f;
        }
        else {
            e1.y += yd * k;
            e2.y -= yd * (1 - k);
            e1.onResolve(e2, 0, yd * k);
            e2.onResolve(e1, 0, -yd * (1 - k));

            float e1_motion_y = e1.motion_y;
            e1.motion_y = e2.motion_y * .9f;
            e2.motion_y = e1_motion_y * .9f;
        }

        e1.updateCache(e1.x, e1.y);
        e2.updateCache(e2.x, e2.y);
    }

    private void resolveLevelCollisions() {
        for(int n = 0; n < mEntities.size(); ++n) {
            Entity e = mEntities.get(n);
            for(int i = 0; i < 4; i++) { // TODO: optimize level collision
                final int minx = max(0, floor_int(e.cache_x_min));
                final int miny = max(0, floor_int(e.cache_y_min));
                final int maxx = min(mWidth, ceil_int(e.cache_x_max));
                final int maxy = min(mHeight, ceil_int(e.cache_y_max));

                Tile first_collision = null;
                int cx = 0, cy = 0;

OUTER_LOOP:     for(int y = miny; y < maxy; ++y) {
                    int precalc_y = y * mWidth;
                    for(int x = minx; x < maxx; ++x) {
                        Tile t = mTiles[precalc_y + x];
                        if(t != null && t.hasFlag(Tile.SOLID)) {
                            first_collision = t;
                            cx = x;
                            cy = y;
                            break OUTER_LOOP;
                        }
                    }
                }

                if(first_collision == null)
                    break;
                
                first_collision.onResolve(e, cx, cy);

                e.updateCache(e.x, e.y);
            }

            // Making sure the entity doesn't fall out of the level.
            if(e.cache_x_min < 0) {
                e.motion_x = 0;
                e.x -= e.cache_x_min;
                e.onResolve(null, -e.cache_x_min, 0);
            }
            else if(e.cache_x_max > mWidth) {
                e.motion_x = 0;
                e.x += mWidth - e.cache_x_max;
                e.onResolve(null, mWidth - e.cache_x_max, 0);
            }

            if(e.cache_y_min < 0) {
                e.motion_y = 0;
                e.y -= e.cache_y_min;
                e.onResolve(null, 0, -e.cache_y_min);
            }
            else if(e.cache_y_max > mHeight) {
                e.motion_y = 0;
                e.y += mHeight - e.cache_y_max;
                e.onResolve(null, 0, mHeight - e.cache_y_max);
            }
        }
    }

    /**
     * Is called every game tick and updates all entities and updatable tiles.
     * The updating includes the resolving of collisions.
     *
     * @param dt The time difference since the last tick.
     */
    public void onUpdate(float dt) {
        // TODO: Make level collisions continuous

        synchronized(mEntities) {

            // Update all entities
            for(int i = 0; i < mEntities.size(); ++i) {
                Entity e = mEntities.get(i);
                if(e.isDead()) {
                    synchronized(mEntities) {
                        mEntities.remove(i);
                    }
                    --i;
                }
                else
                    updateEntity(e, dt);
            }

            // Resolving collisions between entities
            for(int i = 0; i < mEntities.size(); i++) {
                Entity e1 = mEntities.get(i);

                for(int j = i + 1; j < mEntities.size(); j++) {
                    Entity e2 = mEntities.get(j);

                    if(e1.touches(e2)
                            && (e1.getCollisionMask() & e2.getCollisionMask()) != 0) {
                        boolean should_resolve; // Determine wether the collision has to be resolved

                        // Don't resolve if one of the entities interrupts the collision or is a ghost
                        should_resolve = e1.onCollide(e2);
                        should_resolve = e2.onCollide(e1) && should_resolve;

                        if(should_resolve)
                            resolveEntityCollision(e1, e2);
                    }
                }
            }

            resolveLevelCollisions();
        }
    }

    /**
     * Adds an entity. Is synchronized to the entity list, which can interfere
     * with rendering on very high frame rates.
     *
     * @param e The entity to add
     */
    public void add(Entity e) {
        synchronized(mEntities) {
            mEntities.add(e);
        }

        e.updateCache(e.x, e.y);
    }

    /* Tile related methods */
    /**
     * Gets a tile below a specific coordinate. If you don't know if it will be
     * out of bounds use probeTile(x,y) instead.
     *
     * @param x The x coordinate of the Tile
     * @param y The y coordinate of the Tile
     * @return The tiles at { x, y } or null.
     */
    public Tile getTile(float x, float y) {
        return getTile((int) x, (int) y);
    }

    /**
     * Gets a tile at a specific coordinate. If you don't know if it will be out
     * of bounds use probeTile(x,y) instead.
     *
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

    /**
     * Gets a tile at a specific coordinate, or null if it is out of bounds.
     */
    public Tile sampleTile(float x, float y) {
        return sampleTile(floor_int(x), floor_int(y));
    }

    /**
     * Gets a tile at a specific coordinate, or null if it is out of bounds.
     */
    public Tile sampleTile(int x, int y) {
        if(x < 0 || x >= mWidth)
            return null;
        if(y < 0 || y >= mHeight)
            return null;
        return getTile(x, y);
    }

    /**
     * Traces the ray from { xbeg, ybeg } to { xdst, ydst }, ignoring entities
     *
     * @return the first solid tile it hits, or null if nothings in the way of
     * the ray.
     */
    public boolean traceTile(TileResult result, float xbeg, float ybeg, float xdst, float ydst) {
        if(Debug.DRAW_TRACING) Game.getBackend().drawLine(0xFFFF0000, xbeg, ybeg, xdst, ydst);
        float dx = xdst - xbeg;
        float dy = ydst - ybeg;

        if(abs(dx) > abs(dy)) {
            float step = dy / dx;
            int beg = (int) xbeg;
            int end = (int) xdst;
            int xstep = xbeg < xdst ? 1 : -1;
            step *= xstep;

            while(beg != end) {
                Tile t = sampleTile(beg, ybeg);
                if(Debug.DRAW_TRACING) Game.getBackend().drawRect(0xFFFF0000, beg, floor_int(ybeg), 1, 1);
                if(t == null || t.hasFlag(Tile.SOLID)) {
                    result.tile = t;
                    result.x = beg;
                    result.y = floor_int(ybeg);
                    if(Debug.DRAW_TRACING) Game.getBackend().drawRect(0xFFFFFF00, result.x, result.y, 1, 1);
                    return true;
                }
                
                beg += xstep;
                ybeg += step;
            }

            return false;
        }
        else {
            float step = dx / dy;
            int beg = (int) ybeg;
            int end = (int) ydst;
            int ystep = ybeg < ydst ? 1 : -1;
            step *= ystep;

            while(beg != end) {
                Tile t = sampleTile(xbeg, beg);
                if(Debug.DRAW_TRACING) Game.getBackend().drawRect(0xFFFF0000, floor_int(xbeg), beg, 1, 1);
                if(t == null || t.hasFlag(Tile.SOLID)) {
                    result.tile = t;
                    result.x = floor_int(xbeg);
                    result.y = beg;
                    if(Debug.DRAW_TRACING) Game.getBackend().drawRect(0xFFFFFF00, result.x, result.y, 1, 1);
                    return true;
                }
                
                beg += ystep;
                xbeg += step;
            }

            return false;
        }
    }

    /**
     * Traces the ray from { xbeg, ybeg } to { xdst, ydst }, ignoring entities
     *
     * @return the first entity it hits, or null if nothings in the way of the
     * ray.
     */
    public boolean traceEntity(EntityResult result, float xbeg, float ybeg, float xdst, float ydst) {
        // TODO
        return false;
    }

    /**
     * Traces the ray from { xbeg, ybeg } to { xdst, ydst }, ignoring entities
     *
     * @return the first occluding entity it hits, or null if nothings in the
     * way of the ray.
     */
    public boolean traceOccluder(EntityResult result, float xbeg, float ybeg, float xdst, float ydst) {
        // TODO
        return false;
    }

    /**
     * Traces the ray from { xbeg, ybeg } to { xdst, ydst }
     *
     * @return the first solid tile it hits, the
     */
    public Object traceRay(float xbeg, float ybeg, float xdst, float ydst) {
        boolean t = traceTile(TILE_RESULT, xbeg, ybeg, xdst, ydst);
        boolean e = traceOccluder(ENTITY_RESULT, xbeg, ybeg, xdst, ydst);

        return null;
    }

    /**
     * Traces the ray from { xbeg, ybeg } to { xdst, ydst }
     *
     * @return the first solid tile or entity it hits, independend of the
     * occluder flag.
     */
    public Object traceAny(float xbeg, float ybeg, float xdst, float ydst) {
        boolean t = traceTile(TILE_RESULT, xbeg, ybeg, xdst, ydst);
        boolean e = traceEntity(ENTITY_RESULT, xbeg, ybeg, xdst, ydst);

        float dt = t ? length(TILE_RESULT.x - xbeg, TILE_RESULT.y - ybeg) : INFINITY;
        float de = e ? ENTITY_RESULT.entity.getSurfaceDistance(xbeg, ybeg) : INFINITY;

        if(!(e | t))
            return null;
        else
            return de < dt ? ENTITY_RESULT : TILE_RESULT;
    }

    public boolean canSee(Entity e, Hitbox b) {

        return false;
    }

    /**
     * Gets a tile below a specific coordinate.
     *
     * @param x The x coordinate of the Tile
     * @param y The y coordinate of the Tile
     * @param t
     */
    public void setTile(float x, float y, Tile t) {
        setTile((int) x, (int) y, t);
    }

    /**
     * Gets a tile at a specific coordinate.
     *
     * @param dx The x coordinate of the Tile
     * @param dy The y coordinate of the Tile
     * @param t The tile to be set to
     */
    public void setTile(int dx, int dy, Tile t) {
        mTiles[dx + dy * mWidth] = t;

        //emitTileUpdate(dx, dy);
    }

    /**
     * Fills a rectangle with tiles of a certain type.
     */
    public void setTiles(float x, float y, float w, float h, Tile t) {
        setTiles((int) x, (int) y, (int) w, (int) h, t);
    }

    /**
     * Fills a rectangle with tiles of a certain type.
     */
    public void setTiles(int x, int y, int w, int h, Tile t) {
        w = min(x + w, mWidth);
        h = min(y + h, mHeight);
        x = max(0, x);
        y = max(0, y);

        for(; y < h; ++y) {
            int ypre = y * mWidth;
            for(; x < w; ++x)
                mTiles[ypre + x] = t;
        }
    }

    public void emitTileUpdate(int x, int y) {
        // TODO: optimize this
        for(int xx = x - 1; xx <= x + 1; xx++)
            for(int yy = y - 1; yy <= y + 1; yy++)
                if(xx != x || yy != y) {
                    Tile t = sampleTile(xx, yy);
                    if(t != null)
                        t.onTileUpdate(xx, yy);
                }
    }

    public void updateAllTiles() {
        for(int y = 0; y < mHeight; y++) {
            int ypre = y * mWidth;
            //System.out.println("Update row " + y);
            for(int x = 0; x < mWidth; x++) {
                Tile t = mTiles[ypre + x];
                if(t != null)
                    t.onTileUpdate(x, y);
            }
        }
    }

    /**
     * @param spread
     * @param speed
     * @param angle_min
     * @param angle_max
     * @param sample
     */
    public void explode(EntityFactory factory, float x, float y, int n, float min_speed, float max_speed, float min_angle, float max_angle) {
        
        for(int i = 0; i < n; i++) {
            float angle = rnd_lerp(min_angle, max_angle);
            float speed = rnd_lerp(min_speed, max_speed);
            
            float mx = cos(angle) * speed;
            float my = sin(angle) * speed;
            
            add(factory.create().setPosition(x, y).setMotion(mx, my));
        }
    }
    
    public final boolean onRightWall(Entity e, float minimize, float dist) {
        int x   = clamp(floor_int(e.cache_x_max + dist), 0, mWidth - 1);
        int ymn = clamp(floor_int(e.cache_y_min + minimize), 0, mHeight - 1);
        int ymx = clamp(floor_int(e.cache_y_max - minimize), 0, mHeight - 1);
        for(int y = ymn; y <= ymx; y++) {
            Tile t = mTiles[y * mWidth + x];
            if(t != null && t.hasFlag(Tile.SOLID))
                return true;
        }
        return false;
    }
    
    public final boolean onLeftWall(Entity e, float minimize, float dist) { 
        int x   = clamp(floor_int(e.cache_x_min - dist), 0, mWidth - 1);
        int ymn = clamp(floor_int(e.cache_y_min + minimize), 0, mHeight - 1);
        int ymx = clamp(floor_int(e.cache_y_max - minimize), 0, mHeight - 1);
        for(int y = ymn; y <= ymx; y++) {
            Tile t = mTiles[y * mWidth + x];
            if(t != null && t.hasFlag(Tile.SOLID))
                return true;
        }
        return false;
    }
    
    public final boolean onGround(Entity e, float minimize, float dist) {
        int y   = clamp(floor_int(e.cache_y_min - dist), 0, mHeight - 1);
        int xmn = clamp(floor_int(e.cache_x_min + minimize), 0, mWidth - 1);
        int xmx = clamp(floor_int(e.cache_x_max - minimize), 0, mWidth - 1);
        int ypre = y * mWidth;
        for(int x = xmn; x <= xmx; x++) {
            Tile t = mTiles[ypre + x];
            if(t != null && t.hasFlag(Tile.SOLID))
                return true;
        }
        return false;
    }
    
    public final boolean onCeiling(Entity e, float minimize, float dist) {
        int y   = clamp(floor_int(e.cache_y_max + dist), 0, mHeight - 1);
        int xmn = clamp(floor_int(e.cache_x_min + minimize), 0, mWidth - 1);
        int xmx = clamp(floor_int(e.cache_x_max - minimize), 0, mWidth - 1);
        int ypre = y * mWidth;
        for(int x = xmn; x <= xmx; x++) {
            Tile t = mTiles[ypre + x];
            if(t != null && t.hasFlag(Tile.SOLID))
                return true;
        }
        return false;
    }

    final public void setName(Entity e, String name) { mByName.put(name, e); }
    final public void removeName(String name) { mByName.remove(name); }
    final public Entity get(String name) { return mByName.get(name); }

    /* Getters and setters */
    public int getWidth() { return mWidth; }

    public int getHeight() { return mHeight; }

    public ArrayList<Entity> getEntities() { return mEntities; }
    public static final TileResult   getTileResult()   { return TILE_RESULT; }
    public static final EntityResult getEntityResult() { return ENTITY_RESULT; }
}
