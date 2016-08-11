/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine2d.level.tile;

import engine2d.level.Entity;
import engine2d.level.Tile;

/**
 *
 * @author benno
 */
public class ProxyTile extends Tile {
    private Tile mFwdTo;

    public ProxyTile(Tile owner) {
        super(owner.getFlags());
    }
    
    @Override
    public boolean onDestroy(int mode, int x, int y) {
        return mFwdTo.onDestroy(mode, x, y);
    }
    
    @Override
    public void onTileUpdate(int x, int y) {
        mFwdTo.onTileUpdate(x, y);
    }
    
    @Override
    public boolean onTouch(Entity e, int x, int y) {
        return mFwdTo.onTouch(e, x, y);
    }
}
