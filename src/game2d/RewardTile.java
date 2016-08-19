/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d;

import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;
import engine2d.level.particle.SimpleParticle;
import engine2d.level.tile.OverlayTile;

/**
 *
 * @author benno
 */
public class RewardTile extends OverlayTile {
    private final Sprite mSprite;
    
    public RewardTile() {
        super(new DarkTile(0));
        setFlags(getFlags() | SOLID);
        mSprite = loadSprite("tile/chest.png");
    }
    
    @Override
    public void onResolve(Entity e, int x, int y) {
        if(e instanceof Player) {
            getLevel().explode(
                    () -> new SimpleParticle(0, 0, 0, 0, 0xFF0000FF, .1f, .4f, rndf(1, 4)).setCollisionMask(Entity.MASK_GHOST), 
                    x + .5f, y + .5f, 300, 0, 7, 0, TWO_PI);
            popTile(x, y);
        }
        
        super.onResolve(e, x, y);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        super.onDraw(r, x, y);
        r.drawSprite(mSprite, x, y, 1, 1);
    }
}
