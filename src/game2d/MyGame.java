package game2d;

import engine2d.Game;
import engine2d.Renderer;
import engine2d.level.Entity;
import engine2d.level.LevelLoader;
import game2d.enemy.MrMelee;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MyGame extends Game {
    private Player mPlayer;
    
    private Clip bg_music;
    
    private int mRemainingGoals = 0;
    
    public MyGame() {
        reset();
    }
    
    private void startTheMusic() {
        File f = new File("ressource/sound/Pika-remix.wav");
        try {
            bg_music = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            bg_music.open(ais);
            bg_music.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(MyGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void reset() {
        getLevel().getEntities().clear();
        
        LevelLoader ll = new LevelLoader();
        
        ll.setTile(0xFFFFFFFF, new BrightTile(0));
        ll.setTile(0xFF000000, new DarkTile(0));
        ll.setTile(0xFFFF0000, () -> new GrassTile());
        
        loadLevel(ll, getBackend().loadSprite("level/demo-large.png"));
        
        // Add player
        mPlayer = new Player(getBackend().getInput());
        mPlayer.setPosition(5, 50);
        getLevel().add(mPlayer);
        
        // Add mine thingy
        Entity e = new Mine()
                    .setPosition(10, 10)
                    .setWeight(4);
        
        getLevel().add(e);
        
        getLevel().add(new MrMelee().setPosition(635, 115));
        getLevel().add(new MrMelee().setPosition(632, 135));
        
        for(int i = 0; i < 10; i += 2) {
            mRemainingGoals++;
            ChestTile t = new ChestTile();
            t.addCollectionListener(() -> {
                mRemainingGoals--;
                System.out.println("Collected goal. " + mRemainingGoals + " remaining.");
            });
            getLevel().setTile(626 + i, 158, t);
            System.out.println("Added goal " + mRemainingGoals + " remaining.");
        }
        
        getLevel().add(new Message("Thing...Collect chests...", 3).setPosition(mPlayer.x, mPlayer.y));
        
        /*if(bg_music == null)
            startTheMusic();
        else
            bg_music.setFramePosition(0);*/
    }
    
    private Entity mWinMessage = null;
    
    @Override
    protected void onUpdate(float dt) {
        super.onUpdate(dt);
        if(mRemainingGoals == 0 && mWinMessage == null) {
            mWinMessage = new Message("Yay, you won. How great.", 3).setPosition(mPlayer.x, mPlayer.y);
            getLevel().add(mWinMessage);
            System.out.println("Spawned win message.");
        }
        
        if(getBackend().getInput().poll("reset") ||
                mWinMessage != null && mWinMessage.isDead()) 
        {
            mWinMessage.kill();
            mWinMessage = null;
            reset();
        }
    }
    
    
    float cam_x = 0, cam_y = 0;
    
    @Override
    public void onRender() {
        Renderer r = getBackend();
        float cam_size = 16 - getBackend().getInput().getValue("scroll state");
        cam_x = (mPlayer.x + cam_x) * .5f;
        cam_y = (mPlayer.y + cam_y) * .5f;
        r.setCamera(cam_x, cam_y, cam_size);
        
        super.onRender();
        
        //getBackend().flush();
    }
}
