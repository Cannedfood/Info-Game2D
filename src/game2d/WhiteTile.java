package game2d;

import engine2d.Renderer;
import engine2d.level.Tile;

public class WhiteTile extends Tile {
    
    public WhiteTile(int flags) {
        super(flags);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFFFFFFFF, x, y, 1, 1);
    }
}
