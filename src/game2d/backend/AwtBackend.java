package game2d.backend;

import game2d.Backend;
import game2d.Game;
import game2d.Sprite;
import game2d.level.Hitbox;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public final class AwtBackend extends Backend implements MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {
    
    private final class AwtSprite implements Sprite {
        public BufferedImage mImage;
        
        AwtSprite(BufferedImage img) {
            mImage = img;
        }
        
        @Override
        public int getWidth() { return mImage.getWidth(); }
        @Override
        public int getHeight() { return mImage.getHeight(); }
        @Override
        public int colorAt(int x, int y) { return mImage.getRGB(x, y); }
    }
    
    private final JFrame mFrame;
    private final Canvas mCanvas;
    private final Graphics mGraphics;
    
    public AwtBackend() {
        Dimension size = new Dimension(1280, 720);
        mCanvas = new Canvas();
        mCanvas.setPreferredSize(size);
        
        mFrame = new JFrame("Game2D");
        mFrame.add(mCanvas);
        mFrame.pack();
        mFrame.setResizable(false);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setLocationRelativeTo(null);
        mFrame.setVisible(true);
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(AwtBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mCanvas.createBufferStrategy(2);
        mGraphics = mCanvas.getBufferStrategy().getDrawGraphics();
        updateCamCache();
        
        getInput().add("pointer.down", 0);
        getInput().add("pointer.x", 0);
        getInput().add("pointer.y", 0);
        getInput().add("scroll state", 0);
        getInput().add("move.x", 0);
        getInput().add("move.y", 0);
        getInput().add("reset", 0);
        
        mCanvas.addMouseMotionListener(this);
        mCanvas.addMouseWheelListener(this);
        mCanvas.addMouseListener(this);
        mCanvas.addKeyListener(this);
        
        mFrame.requestFocus();
    }
    
    int mouse_x, mouse_y;
    
    float cam_offx, cam_scalex;
    float cam_offy, cam_scaley;
    
    private void updateCamCache() {
        /*cam_offx = cam_offy = 0;
        cam_scalex = mCanvas.getWidth()  / 64f;
        cam_scaley = mCanvas.getHeight() / 64f;*/
        cam_offx = -getCamera().offset_x;
        cam_offy = -getCamera().offset_y;
        cam_scalex = mCanvas.getWidth() / getCamera().width;
        cam_scaley = mCanvas.getHeight() / getCamera().height;
    }
    
    @Override
    public void setCamera(float x, float y, float tiles_height) {
        getCamera().height = tiles_height;
        getCamera().width  = tiles_height * mCanvas.getWidth() / (float)mCanvas.getHeight();
        getCamera().offset_x = x - getCamera().width  * .5f;
        getCamera().offset_y = y - getCamera().height * .5f;
        getCamera().updateCache(0, 0);
        updateCamCache();
    }
    
    /* Utility functions to transform to camera space */
    int cx, cy, cw, ch;
    private void camSpace(float x, float y, float w, float h) {
        cw = (int)Math.ceil(w * cam_scalex);
        ch = (int)Math.ceil(h * cam_scaley);
        cx = (int)Math.floor((x + cam_offx) * cam_scalex);
        cy = mCanvas.getHeight() - (int)((y + cam_offy) * cam_scaley) - ch;
    }

    /* Methods from Renderer */
    @Override
    public void drawSprite(Sprite sprite, float x, float y, float w, float h) {
        BufferedImage img = ((AwtSprite)sprite).mImage;
        
        camSpace(x, y, w, h);
        //mGraphics.drawImage(img, cx, cy, null);
        mGraphics.drawImage(img, cx, cy, cx + cw, cy + ch, 0, 0, img.getWidth(), img.getHeight(), null);
    }
    
    @Override
    public void drawSprite(Sprite sprite, Hitbox h) {
        drawSprite(sprite, h.cache_x_min, h.cache_y_min, h.width, h.height);
    }
    
    int last_color = 0x00FF00FF;
    
    @Override
    public void drawRect(int color, float x, float y, float w, float h) {
        if(color != last_color)
            mGraphics.setColor(new Color(last_color = color)); //< Horrible!! DO NOT create new Objects while rendering!!
        
        camSpace(x, y, w, h);
        mGraphics.fillRect(cx, cy, cw, ch);
    }
    
    @Override
    public void drawRect(int color, game2d.level.Hitbox h) {
        this.drawRect(color, h.cache_x_min, h.cache_y_min, h.width, h.height);
    }
    
    @Override
    public void debugDraw(Hitbox h) {
        camSpace(h.cache_x_min, h.cache_y_min, h.width, h.height);
        mGraphics.setColor(Color.RED);
        mGraphics.drawRect(cx, cy, cw, ch);
    }

    @Override
    public void drawString(String s, float x, float y) {
        mGraphics.drawString(s, (int)x, (int)y);
    }
    
    @Override
    public void flush() {
        mCanvas.getBufferStrategy().show();
        mGraphics.setColor(Color.BLACK);
        mGraphics.fillRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
    }

    /* Methods from Loader */
    HashMap<String, AwtSprite> mSprites = new HashMap<>();
    
    @Override
    public Sprite loadSprite(String file) {
        AwtSprite re = mSprites.get(file);
        
        if(re == null) {
            BufferedImage bi = null;
         
           try {
                bi = ImageIO.read(new File("./ressource/" + file));
            } catch (IOException ex) {
                Logger.getLogger(AwtBackend.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(bi != null) {
                re = new AwtSprite(bi);
                mSprites.put(file, re);
            }
        }
        
        return re;
    }
    
    @Override
    public void preUpdate(Game g, float dt) {
        getInput().set("pointer.x", mouse_x / cam_scalex - cam_offx);
        getInput().set("pointer.y", (mCanvas.getHeight() - mouse_y) / cam_scaley - cam_offy);
    }
    
    @Override
    public void postUpdate(Game g, float dt) {
        g.onRender();//<< This code renders after each update
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        getInput().set("scroll state", getInput().getValue("scroll state") + (float)(e.getPreciseWheelRotation() / 3.));
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        getInput().set("pointer.down", true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        getInput().set("pointer.down", false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                getInput().set("move.x", -1f);
                break;
                
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                getInput().set("move.x", 1f);
                break;
            
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
            case KeyEvent.VK_SPACE:
                getInput().set("move.y", 1f);
                break;
            
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_SHIFT:
                getInput().set("move.y", -1f);
                break;
            
            default: break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                getInput().resetSign("move.x", -1f);
                break;
                
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: 
                getInput().resetSign("move.x", 1f);
                break;
            
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: 
            case KeyEvent.VK_SPACE:
                getInput().resetSign("move.y", 1f);
                break;
            
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
            case KeyEvent.VK_SHIFT:
                getInput().resetSign("move.y", -1f);
                break;
            
            case KeyEvent.VK_ENTER:
                getInput().set("reset", true);
                break;
                
            default: break;
        }
    }
}
