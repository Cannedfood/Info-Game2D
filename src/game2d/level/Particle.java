/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.level;

/**
 *
 * @author benno
 */
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
