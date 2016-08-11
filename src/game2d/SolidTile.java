package game2d;

import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Tile;

public class SolidTile extends Tile {
    static final int
            BIT_TOP   = 1,
            BIT_LEFT  = 2,
            BIT_RIGHT = 4;
    
    private Sprite[] mSprites = {
        loadSprite("tile/ground.png"),
        loadSprite("tile/ground-t.png"),
        loadSprite("tile/ground-l.png"),
        loadSprite("tile/ground-tl.png"),
        loadSprite("tile/ground-r.png"),
        loadSprite("tile/ground-tr.png"),
        loadSprite("tile/ground-lr.png"),
        loadSprite("tile/ground-tlr.png"),
    };
    
    private int mSpriteIndex;
    
    public SolidTile() {
        super(SOLID);
    }
    
    @Override
    public void onTileUpdate(int x, int y) {
        Tile t;
        
        mSpriteIndex = 0;
        
        t = getLevel().probeTile(x, y + 1);
        if(t != null && !t.hasFlag(Tile.SOLID))
            mSpriteIndex |= BIT_TOP;
        
        t = getLevel().probeTile(x + 1, y);
        if(t != null && !t.hasFlag(Tile.SOLID))
            mSpriteIndex |= BIT_RIGHT;
        
        t = getLevel().probeTile(x - 1, y);
        if(t != null && !t.hasFlag(Tile.SOLID))
            mSpriteIndex |= BIT_LEFT;
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawSprite(mSprites[mSpriteIndex], x, y, 1, 1);
    }
}
