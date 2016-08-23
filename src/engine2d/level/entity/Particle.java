package engine2d.level.entity;

import engine2d.level.Entity;

public class Particle extends Entity {
    
    public Particle(float x, float y) {
        setPosition(x, y);
        super.addFlags(FLAG_UNIMPORTANT);
    }
    
    public Particle(float x, float y, float xm, float ym) {
        setPosition(x, y);
        setMotion(xm, ym);
        super.addFlags(FLAG_UNIMPORTANT);
    }   
}
