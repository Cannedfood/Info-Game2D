/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.game;

import game2d.Renderer;
import game2d.Sprite;
import game2d.level.Tile;

/**
 *
 * @author benno
 */
public class SolidTile extends Tile {
    public SolidTile() {
        super(null, FLAG_SOLID);
    }
    
    @Override
    public void onDraw(Renderer r, int x, int y) {
        r.drawRect(0xFFFF0000, x, y, 1, 1);
    }
}
