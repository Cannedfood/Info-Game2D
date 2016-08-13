package engine2d.level;

import engine2d.Backend;
import engine2d.Game;
import engine2d.GameMath;
import engine2d.Renderer;
import engine2d.Sprite;
import java.util.Random;

public class Tile extends GameMath {
    public static final int
            BREAK = 0,
            REMOVE = 1;
    
    public static final int
            SOLID = 1; //!< Entities can collide with this tile 
    
    private int    mFlags;
    
    public Tile(int flags) {
        mFlags = flags;
    }
   
    public void onDraw(Renderer b, int x, int y) {
        b.drawRect(0xFF00FFFF, x, y, 1, 1);
    }
    
    public boolean onDestroy(int mode, int x, int y) { return true; }
    public void    onTileUpdate(int x, int y) {}
    public boolean onTouch(Entity e, int x, int y) { return true; }
    public void    onDamage(Entity e, float amount) {}
    
    public final boolean hasFlags(int flags) { return (mFlags & flags) == flags; }
    public final boolean hasFlag(int flag)   { return (mFlags & flag) != 0; }
    public final int     getFlags() { return mFlags; }
    
    
    public final static Backend getBackend() { return Game.getBackend(); }
    public final static Level   getLevel() { return Game.getLevel(); }
    public final static Random  getRandom() { return Game.getRandom(); }
    public final static Sprite  loadSprite(String file) { return Game.getBackend().loadSprite(file); }
}
