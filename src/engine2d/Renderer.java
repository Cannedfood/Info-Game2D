package engine2d;

import engine2d.level.Hitbox;

public interface Renderer {
    public void drawSprite(Sprite sprite, float x, float y, float w, float h);
    public void drawSprite(Sprite sprite, Hitbox h);
    public void drawRect(int color, float x, float y, float w, float h);
    public void drawRect(int color, Hitbox h);
    public void drawString(String s, float x, float y);
    public void debugDraw(Hitbox h);
    public void flush();
    
    public void setCamera(float x, float y, float tiles_in_height);
    public Hitbox getCamera();
}
