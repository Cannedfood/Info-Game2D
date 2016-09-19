package engine2d.backend;

import engine2d.Backend;
import engine2d.Game;
import engine2d.Sprite;
import engine2d.level.Hitbox;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

        @Override
        public void setAnimationTime(float f) {}
        
        @Override
        public void setAnimationSpeed(float f) {}
        
        @Override
        public boolean isAnimated() { return false; }
    }
    
    private final JFrame mFrame;
    private final Canvas mCanvas;
    private final Graphics mGraphics;
    private final Font   mFont;
    
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
        
        mFont = new Font(Font.SANS_SERIF, Font.BOLD, 40);
        mGraphics.setFont(mFont);
        
        getInput().add("pointer.shoot1", 0);
        getInput().add("pointer.shoot2", 0);
        getInput().add("pointer.x", 0);
        getInput().add("pointer.y", 0);
        getInput().add("swap-l", 0); // TODO: hook this up to some key
        getInput().add("swap-r", 0); // TODO: hook this up to some key
        
        getInput().add("scroll state", 0);
        getInput().add("move.x", 0);
        getInput().add("move.y", 0);
        getInput().add("reset", 0);
        
        getInput().add("debug1", 0);
        getInput().add("debug2", 0);
        getInput().add("debug3", 0);
        getInput().add("debug4", 0);
        getInput().add("debug5", 0);
        getInput().add("debug6", 0);
        
        mCanvas.addMouseMotionListener(this);
        mCanvas.addMouseWheelListener(this);
        mCanvas.addMouseListener(this);
        mCanvas.addKeyListener(this);
        
        mFrame.requestFocus();
        
        mGraphics.drawString("<Preload font>", 0, 0);
    }
    
    int mouse_x, mouse_y;
    
    float cam_offx, cam_scalex;
    float cam_offy, cam_scaley;
    
    private void updateCamCache() {
        /*
        cam_offx = cam_offy = 0;
        cam_scalex = mCanvas.getWidth()  / 128f;
        cam_scaley = mCanvas.getHeight() / 64f;
        /*/
        cam_offx = -getCamera().offset_x;
        cam_offy = -getCamera().offset_y;
        cam_scalex = mCanvas.getWidth() / getCamera().width;
        cam_scaley = mCanvas.getHeight() / getCamera().height;
        //*/
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
        cw = ceil_int(w * cam_scalex);
        ch = ceil_int(h * cam_scaley);
        cx = floor_int((x + cam_offx) * cam_scalex);
        cy = mCanvas.getHeight() - (int)((y + cam_offy) * cam_scaley) - ch;
    }
    
    private void camSpace(float x, float y) {
        cx = floor_int((x + cam_offx) * cam_scalex);
        cy = mCanvas.getHeight() - (int)((y + cam_offy) * cam_scaley);
    }

    /* Methods from Renderer */
    @Override
    public void drawSprite(Sprite sprite, float x, float y, float w, float h) {
        BufferedImage img = ((AwtSprite)sprite).mImage;
        
        camSpace(x, y, w, h);
        mGraphics.drawImage(img, cx, cy, cx + cw, cy + ch, 0, 0, img.getWidth(), img.getHeight(), null);
    }
    
    @Override
    public void drawSprite(Sprite sprite, Hitbox h) {
        drawSprite(sprite, h.cache_x_min, h.cache_y_min, h.width, h.height);
    }
    
    int last_color = 0x00FF00FF;
    
    @Override
    public void drawRect(int color, float x, float y, float w, float h) {
        //if(color != last_color)
            mGraphics.setColor(new Color(last_color = color)); //< Horrible!! DO NOT create new Objects while rendering!!
        
        camSpace(x, y, w, h);
        mGraphics.fillRect(cx, cy, cw, ch);
    }
    
    @Override
    public void drawRect(int color, engine2d.level.Hitbox h) {
        this.drawRect(color, h.cache_x_min, h.cache_y_min, h.width, h.height);
    }
    
    @Override
    public void drawLine(int color, float xbeg, float ybeg, float xend, float yend) {
        mGraphics.setColor(new Color(last_color = color)); //< Horrible!! DO NOT create new Objects while rendering!!
        camSpace(xbeg, ybeg);
        int x = cx, y = cy;
        camSpace(xend, yend);
        mGraphics.drawLine(x, y, cx, cy);
    }
    
    @Override
    public void debugDraw(Hitbox h) {
        mGraphics.setColor(Color.RED);
        
        camSpace(h.cache_x_min, h.cache_y_min, h.width, h.height);
        mGraphics.drawRect(cx, cy, cw, ch);
        
        camSpace(h.getCachePositionX(), h.getCachePositionY());
        mGraphics.drawOval(cx - 3, cy - 3, 7, 7);
        
        camSpace(h.getMiddleX(), h.getMiddleY());
        mGraphics.drawOval(cx - 1, cy - 1, 3, 3);
    }

    @Override
    public void drawString(int color, String s, float x, float y) {
        if(last_color != color) {
            last_color = color;
            mGraphics.setColor(new Color(color));
        }
        
        camSpace(x, y);
        mGraphics.drawString(s, cx, cy);
    }
    
    @Override
    public void flush() {
        mCanvas.getBufferStrategy().show();
        mGraphics.setColor(Color.DARK_GRAY);
        mGraphics.fillRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
        last_color = 0x00FF00FF;
    }

    /* Methods from Loader */
    HashMap<String, AwtSprite> mSprites = new HashMap<>();
    
    @Override
    public void startTiles() {}
    @Override
    public void startRandom() {}   
    
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
        g.onRender();//<< This code renders after each update
    }
    
    @Override
    public void postUpdate(Game g, float dt) {
        flush();
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
        
        if(e.getButton() == MouseEvent.BUTTON1)
            getInput().set("pointer.shoot1", true);
        else
            getInput().set("pointer.shoot2", true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        
        if(e.getButton() == MouseEvent.BUTTON1)
            getInput().set("pointer.shoot1", false);
        else
            getInput().set("pointer.shoot2", false);
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
                
            case KeyEvent.VK_F1: getInput().set("debug1", true); break;
            case KeyEvent.VK_F2: getInput().set("debug2", true); break;
            case KeyEvent.VK_F3: getInput().set("debug3", true); break;
            case KeyEvent.VK_F4: getInput().set("debug4", true); break;
            case KeyEvent.VK_F5: getInput().set("debug5", true); break;
            case KeyEvent.VK_F6: getInput().set("debug6", true); break;
            
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
            
            case KeyEvent.VK_F1: getInput().set("debug1", false); break;
            case KeyEvent.VK_F2: getInput().set("debug2", false); break;
            case KeyEvent.VK_F3: getInput().set("debug3", false); break;
            case KeyEvent.VK_F4: getInput().set("debug4", false); break;
            case KeyEvent.VK_F5: getInput().set("debug5", false); break;
            case KeyEvent.VK_F6: getInput().set("debug6", false); break;
                
            default: break;
        }
    }
}
