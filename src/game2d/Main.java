package game2d;

import engine2d.Game;
import engine2d.backend.AwtBackend;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The sole purpose of this class is to hold the main method
 */
public class Main {
    public static void main(String[] args) throws IOException {
        AwtBackend backend = new AwtBackend();
        Game.setBackend(backend);
        
        Game game = new GameProject();
        
        try { game.start(); } 
        catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        /*
        while(true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*/
        long dif = 0;
        do {
            try {
                Thread.sleep(15 - dif);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            long beg = System.currentTimeMillis();
            
            game.onRender();//<< This code renders asynchronously
            
            dif = beg - System.currentTimeMillis();
        } while(true);
        //*/
    }
}
