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
public interface Damagable {
    public void onDamage(Entity e, String msg, float damage);
}
