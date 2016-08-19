/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine2d.level.tile;

import engine2d.Renderer;
import engine2d.level.Entity;
import engine2d.level.Tile;

/**
 *
 * @author benno
 */
public class OverlayTile extends Tile {
    private Tile mChild;

    public OverlayTile(Tile mChild) {
        super(mChild.getFlags());
        this.mChild = mChild;
    }
    
    public void popTile(int x, int y) { getLevel().setTile(x, y, mChild); }
    
    @Override
    public boolean onDestroy(int mode, int x, int y) { return mChild.onDestroy(mode, x, y); }
    @Override
    public void    onTileUpdate(int x, int y) { mChild.onTileUpdate(x, y); }
    @Override
    public boolean onDamage(Entity e, float amount) { return mChild.onDamage(e, amount); }
    @Override
    public void    onResolve(Entity e, int x, int y) { mChild.onResolve(e, x, y); }
    @Override
    public void    onDraw(Renderer b, int x, int y) { mChild.onDraw(b, x, y); }
}
