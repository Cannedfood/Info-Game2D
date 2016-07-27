package game2d.level;

import game2d.Game;
import game2d.Renderer;
import game2d.Sprite;
import java.util.Random;

public class Tile {
    public static final int
            FLAG_SOLID = 1; //!< Entities can collide with this tile 
    
    private int    mFlags;
    private Sprite mSprite;
    
    public Tile(Sprite sprite, int flags) {
        mSprite = sprite;
        mFlags = flags;
    }
   
    public void onDraw(Renderer b, int x, int y) {
        b.drawRect(0xFF00FFFF, x, y, 1, 1);
    }
    
    public boolean onTouch(Entity e, int x, int y) {
        return true;
    }
    
    boolean hasFlags(int flags) { return (mFlags & flags) == flags; }
    boolean hasFlag(int flag)   { return (mFlags & flag) != 0; }
    
    public final static Level getLevel() { return Game.getLevel(); }
    public final static Random getRandom() { return Game.getRandom(); }
}
