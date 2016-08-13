package engine2d.level;

import engine2d.level.tile.TileFactory;
import engine2d.Sprite;
import java.util.HashMap;

public class LevelLoader {

    private HashMap<Integer, TileFactory> mTileFactories = new HashMap<>();

    public void setTile(int color, Tile t) {
        mTileFactories.put(color, (TileFactory) () -> t);
    }

    public void setTile(int color, TileFactory tf) {
        mTileFactories.put(color, tf);
    }

    public Tile get(int color) {
        TileFactory tf = mTileFactories.get(color);
        if(tf == null)
            return null;
        else
            return tf.create();
    }

    public Level loadLevel(Sprite from) {
        Level l = new Level(from.getWidth(), from.getHeight());

        for(int y = 0; y < from.getHeight(); y++) {
            for(int x = 0; x < from.getWidth(); x++) {
                l.setTile(x, y, get(from.colorAt(from.getWidth() - 1 - x, from.getHeight() - 1 - y)));
            }
        }

        return l;
    }

    public void loadToLevel(Sprite from, Level to, int xoff, int yoff) {
        int x, y;

        x = xoff < 0 ? -xoff : 0;
        y = yoff < 0 ? -yoff : 0;

        for(; y < from.getHeight() && y + yoff < to.getHeight(); y++) {
            for(; x < from.getWidth() && x + xoff < to.getWidth(); x++) {
                to.setTile(x, y, get(from.colorAt(from.getWidth() - 1 - x, from.getHeight() - 1 - y)));
            }
        }
    }
}
