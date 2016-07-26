package game2d.game;

import game2d.Renderer;
import game2d.level.Tile;

public class SolidTile extends Tile {
    public SolidTile() {
        super(null, FLAG_SOLID);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFFFF0000, x, y, 1, 1);
    }
}
