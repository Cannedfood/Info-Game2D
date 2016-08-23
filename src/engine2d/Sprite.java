package engine2d;

public interface Sprite {
    public int getWidth();
    public int getHeight();
    int colorAt(int x, int y);
    void setAnimationTime(float f);
    void setAnimationSpeed(float f);
    boolean isAnimated();
}
