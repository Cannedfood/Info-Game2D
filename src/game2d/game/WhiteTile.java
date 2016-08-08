package game2d.game;

import game2d.Renderer;
import game2d.level.Tile;

public class WhiteTile extends Tile {
    
    public WhiteTile(int flags) {
        super(flags);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFFFFFFFF, x, y, 1, 1);
    }
}
