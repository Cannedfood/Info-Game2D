package game2d.level;

public class Particle extends Entity {
    
    public Particle(float x, float y) {
        super(x, y);
        super.addFlags(FLAG_UNIMPORTANT);
    }
    
    public Particle(float x, float y, float xm, float ym) {
        super(x, y, xm, ym);
        super.addFlags(FLAG_UNIMPORTANT);
    }   
}
