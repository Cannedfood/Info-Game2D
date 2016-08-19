/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine2d.level;

/**
 *
 * @author benno
 */
public interface Damagable {
    final static int 
            UNDEFINED = 0,
            MAGICAL = 1, 
            PHYSICAL = 2,
            VOID = 3;
    
    final static int
            DEFLECTED = -1,
            MISSED    = 0,
            DAMAGED   = 1,
            KILLED    = 2;
    
    public int onDamage(Entity source, Entity cause, String msg, float damage);
}
