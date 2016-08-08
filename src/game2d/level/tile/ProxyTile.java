/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.level.tile;

import game2d.level.Entity;
import game2d.level.Tile;

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
    public void onTileUpdate(int srcx, int srcy, int x, int y) {
        mFwdTo.onTileUpdate(srcx, srcy, x, y);
    }
    
    @Override
    public boolean onTouch(Entity e, int x, int y) {
        return mFwdTo.onTouch(e, x, y);
    }
}
