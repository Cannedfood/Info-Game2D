/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;
import engine2d.level.entity.BasicEventListener;
import engine2d.level.tile.OverlayTile;
import java.util.ArrayList;

/**
 *
 * @author benno
 */
public class ChestTile extends OverlayTile {
    private final Sprite mSprite;
    
    private ArrayList<BasicEventListener> mCollectListeners
            = new ArrayList<>();
    
    public ChestTile() {
        super(new DarkTile(0));
        setFlags(getFlags() | SOLID);
        mSprite = loadSprite("tile/chest.png");
    }
    
    @Override
    public void onResolve(Entity e, int x, int y) {
        if(e instanceof Player) {
            Sprite s = loadSprite("sprite/star.png");
            getLevel().explode(
                    () -> new SpriteParticle(0, 0, 0, 0, .1f, .4f, rndf(1, 4), s).setCollisionMask(Entity.MASK_GHOST), 
                    x + .5f, y + .5f, 300, 0, 7, 0, TWO_PI);
            popTile(x, y);
            for(BasicEventListener l : mCollectListeners) {
                l.onBasicEvent();
            }
        }
        
        super.onResolve(e, x, y);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        super.onDraw(r, x, y);
        r.drawSprite(mSprite, x, y, 1, 1);
    }
    
    public void addCollectionListener(BasicEventListener l) {
        mCollectListeners.add(l);
    }
    
    public void removeCollectionListener(BasicEventListener l) {
        mCollectListeners.remove(l);
    }
}
