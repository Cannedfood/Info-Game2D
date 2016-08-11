package engine2d.level;

import engine2d.Backend;
import engine2d.Renderer;

public interface Script {
    void onInit(Level l, Backend b);
    
    void onUpdate(float dt);
    void onRender(Renderer r);
}
