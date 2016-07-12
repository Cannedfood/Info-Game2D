package game2d.game;

import game2d.Game;
import game2d.backend.AwtBackend;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The sole purpose of this class is to hold the main method
 */
public class Main {
    public static void main(String[] args) {
        AwtBackend backend = new AwtBackend();
        Game.setBackend(backend);
        
        Game game = new GameProject();
        
        try { game.start(); } 
        catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        long dif = 0;
        do {
            try {
                Thread.sleep(15 - dif);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            long beg = System.currentTimeMillis();
            
            game.render(); //<< This code renders asynchronously
            
            dif = beg - System.currentTimeMillis();
        } while(true);
        
        /*try { game.stop(); } 
        catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }*/
    }
}
