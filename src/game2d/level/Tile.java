package game2d.level;

import game2d.Backend;
import game2d.Game;
import game2d.GameMath;
import game2d.Renderer;
import game2d.Sprite;
import java.util.Random;

public class Tile extends GameMath {
    public static final int
            BREAK = 0,
            REMOVE = 1;
    
    public static final int
            FLAG_SOLID = 1; //!< Entities can collide with this tile 
    
    private int    mFlags;
    
    public Tile(int flags) {
        mFlags = flags;
    }
   
    public void onDraw(Renderer b, int x, int y) {
        b.drawRect(0xFF00FFFF, x, y, 1, 1);
    }
    
    public boolean onDestroy(int mode, int x, int y) { return true; }
    public void    onTileUpdate(int srcx, int srcy, int x, int y) {}
    public boolean onTouch(Entity e, int x, int y) { return true; }
    
    public final boolean hasFlags(int flags) { return (mFlags & flags) == flags; }
    public final boolean hasFlag(int flag)   { return (mFlags & flag) != 0; }
    public final int     getFlags() { return mFlags; }
    
    
    public final static Backend getBackend() { return Game.getBackend(); }
    public final static Level   getLevel() { return Game.getLevel(); }
    public final static Random  getRandom() { return Game.getRandom(); }
    public final static Sprite  loadSprite(String file) { return Game.getBackend().loadSprite(file); }
}
