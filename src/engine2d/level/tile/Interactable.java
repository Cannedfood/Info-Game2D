/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine2d.level.tile;

import engine2d.level.Entity;

/**
 *
 * @author benno
 */
public interface Interactable {
    void onInteract(Entity actor, int mode);
}
