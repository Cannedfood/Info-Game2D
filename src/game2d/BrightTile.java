package game2d;

import engine2d.Renderer;
import engine2d.level.Tile;

public class BrightTile extends Tile {
    
    public BrightTile(int flags) {
        super(flags);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFF80A0A0, x, y, 1, 1);
    }
}
