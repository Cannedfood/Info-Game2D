package engine2d.level;

import engine2d.Backend;
import engine2d.Game;
import engine2d.GameMath;
import engine2d.Renderer;
import engine2d.Sprite;
import java.util.Random;

public class Tile extends GameMath {
    public static final int BREAK = 0,
            REMOVE = 1;

    public static final int SOLID = 1; //!< Entities can collide with this tile 

    public final static void resolveMinimal(Entity e, int x, int y) {
        float xp = x - e.cache_x_max;
        float xn = x + 1 - e.cache_x_min;
        float yp = y - e.cache_y_max;
        float yn = y + 1 - e.cache_y_min;

        float xd = absmin(xp, xn);
        float yd = absmin(yp, yn);

        if(abs(xd) < abs(yd)) {
            e.x += xd;
            e.onResolve(null, xd, 0);
            e.motion_x = 0;
            e.motion_y *= e.friction_mulitplier;
        }
        else {
            e.y += yd;
            e.onResolve(null, 0, yd);
            e.motion_y = 0;
            e.motion_x *= e.friction_mulitplier;
        }
    }

    public final static void resolveRamp(Entity e, int x, int y, float slope) {

    }

    private int mFlags;
    
    protected final void setFlags(int flags) { mFlags = flags; }

    public Tile(int flags) {
        mFlags = flags;
    }

    public void onDraw(Renderer r, int x, int y) { r.drawRect(0xFF00FFFF, x, y, 1, 1); }

    public boolean onDestroy(int mode, int x, int y) { return true; }
    public void    onTileUpdate(int x, int y) {}
    public boolean onDamage(Entity e, float amount) { return false; }
    public void    onResolve(Entity e, int x, int y) { resolveMinimal(e, x, y); }
    
    public final boolean hasFlags(int flags) { return (mFlags & flags) == flags; }
    public final boolean hasFlag(int flag) { return (mFlags & flag) != 0; }
    public final int     getFlags() { return mFlags; }

    
    public final static Backend getBackend() { return Game.getBackend(); }
    public final static Level   getLevel()   { return Game.getLevel(); }
    public final static Sprite loadSprite(String file) { return Game.getBackend().loadSprite(file); }
}
