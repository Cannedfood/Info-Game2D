package game2d;

import engine2d.Renderer;
import engine2d.level.Tile;

public class DarkTile extends Tile {
    
    public DarkTile(int flags) {
        super(flags);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFF000000, x, y, 1, 1);
    }
}
