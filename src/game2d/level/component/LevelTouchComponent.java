/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2d.level.component;

import game2d.level.Entity;

/**
 *
 * @author benno
 */
public class LevelTouchComponent {
    private boolean mOnGround = false;
    private boolean mOnGroundTmp = false;
    
    public void onUpdate(float dt) {
        mOnGround = mOnGroundTmp;
    }
    
    public void onPostUpdate(float dt) {
        mOnGroundTmp = false;
    }
    
    public void onResolve(Entity e, float dx, float dy) {
        if(e == null && dy > 0) mOnGroundTmp = true;
    }
    
    public boolean isOnGround() { return mOnGround; }
}
