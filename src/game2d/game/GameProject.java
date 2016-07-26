/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.game;

import game2d.Game;
import game2d.Renderer;
import game2d.level.Entity;
import game2d.level.Player;
import game2d.level.Tile;

/**
 *
 * @author benno
 */
public class GameProject extends Game {
    private Player mPlayer;
    
    public GameProject() {
        reset();
    }
    
    private void reset() {
        getLevel().getEntities().clear();
        
        // Create a checkerboard patters of tiles
        Tile t;
        
        t = new WhiteTile(null, 0);
        for(int y = 0; y < getLevel().getHeight(); ++y) {
            for(int x = y % 2; x < getLevel().getWidth(); x += 2) {
                getLevel().setTile(x, y, t);
            }
        }
        
        t = new BlackTile(null, 0);
        for(int y = 0; y < getLevel().getHeight(); ++y) {
            for(int x = (y + 1) % 2; x < getLevel().getWidth(); x += 2) {
                getLevel().setTile(x, y, t);
            }
        }
        
        t = new SolidTile();
        for(int i = 0; i < getLevel().getWidth(); ++i) {
            getLevel().setTile(i, i, t);
        }
        
        mPlayer = new Player(5, 50, 3f, 30f, getBackend().getInput());
        getLevel().add(mPlayer);
        
        Entity e = new Mine(10, getLevel().getHeight() - 1);
        e.weight = .05f;
        getLevel().add(e);
    }
    
    @Override
    protected void onUpdate(float dt) {
        super.onUpdate(dt);
        if(getBackend().getInput().poll("reset")) reset();
    }
    
    @Override
    public void onRender() {
        Renderer r = getBackend();
        float cam_size = 16 - getBackend().getInput().getValue("scroll state");
        r.setCamera(mPlayer.x, mPlayer.y, cam_size);
        super.onRender();
    }
}
