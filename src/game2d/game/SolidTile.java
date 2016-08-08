package game2d.game;

import game2d.Renderer;
import game2d.Sprite;
import game2d.level.Tile;

public class SolidTile extends Tile {
    static final int
            BIT_TOP   = 1,
            BIT_LEFT  = 2,
            BIT_RIGHT = 4;
    
    private static Sprite[] mSprites = {
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
        super(FLAG_SOLID);
    }
    
    @Override
    public void onTileUpdate(int srcx, int srcy, int x, int y) {
        mSpriteIndex = 0;
        
        if(srcx == x && srcy == y + 1 && !(getLevel().getTile(srcx, srcy) instanceof SolidTile))
            mSpriteIndex |= BIT_TOP;
        
        if(srcx == x + 1 && srcy == y && !(getLevel().getTile(srcx, srcy) instanceof SolidTile))
            mSpriteIndex |= BIT_RIGHT;
        
        if(srcx == x - 1 && srcy == y && !(getLevel().getTile(srcx, srcy) instanceof SolidTile))
            mSpriteIndex |= BIT_LEFT;
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
            r.drawSprite(mSprites[mSpriteIndex], x, y, 1, 1);
    }
}
