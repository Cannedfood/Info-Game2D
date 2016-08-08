package game2d.game;

import game2d.Renderer;
import game2d.Sprite;
import game2d.level.Tile;

public class BlackTile extends Tile {
    
    public BlackTile(int flags) {
        super(flags);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFF000000, x, y, 1, 1);
    }
}
