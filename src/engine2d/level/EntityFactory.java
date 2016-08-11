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
interface EntityFactory {
    public Entity create(float x, float y, float mx, float my, int flags);
}
