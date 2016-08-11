package engine2d;

import java.util.HashMap;

public class Input {
    public static final int KEY_DOWN = 1;
    public static final int KEY_UP   = 0;
    
    public class Slot {
        float value;
        
    };
    
    public interface Listener {
        void onUpdate(Slot slot);
    };
   
    private final HashMap<String, Slot> mInputs = new HashMap<>();
    
    public void add(String name, float start_value) {
        Slot tmp = new Slot();
        tmp.value = start_value;
        mInputs.put(name, tmp);
    }
    
    public void set(String name, float value) { mInputs.get(name).value = value; }
    public void set(String name, boolean value) { set(name, value ? 1 : 0); }
    public void set(String name, int value) { set(name, (float)value); }
    
    public void resetSign(String s, float sign) {
        Slot slot = mInputs.get(s);
        if(slot.value * sign > 0)
            slot.value = 0;
    }
    
    public float   getValue(String s) { return mInputs.get(s).value; }
    public int     getState(String s) { return (int)getValue(s); }
    public boolean is(String s)  { return getValue(s) != 0; }
    
    public boolean poll(String s) {
        Slot slot = mInputs.get(s);
        boolean re = slot.value > 0;
        slot.value = 0;
        return re;
    }
    
    public float pollValue(String s, float reset_to) {
        Slot slot = mInputs.get(s);
        float re = slot.value;
        slot.value = reset_to;
        return re;
    }
}
