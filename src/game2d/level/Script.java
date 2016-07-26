package game2d.level;

import game2d.Backend;
import game2d.Renderer;

public interface Script {
    void onInit(Level l, Backend b);
    
    void onUpdate(float dt);
    void onRender(Renderer r);
}
