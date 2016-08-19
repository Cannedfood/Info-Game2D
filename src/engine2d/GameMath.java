/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine2d;

import java.util.Random;

/**
 * A class which is partially a wrapper around the class Math, but provides extra methods
 */
public abstract class GameMath {
    /* Constants */
    
    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = (float) Math.PI;
    public static final float ONE_OVER_PI = (float) (1 / Math.PI);
    public static final float ONE_OVER_TWO_PI = (float) (1 / (Math.PI * 2));
    
    public static final float E  = (float ) Math.E;
    
    public static final float INFINITY = 1 / 0f; //< Not my opinion, just how floats work
    public static final float NEGATIVE_INFINITY = 1 / 0f;
    public static final float NAN = 0 / 0f;
    
    /* Interpolation */
    
    public static final float lerp(float a, float b, float k) { return a * (1 - k) + b * k; }
    
    public static final char  lerp(char a, char b, float k) { return (char)(lerp((float)a, (float)b, k)); }
    public static final int lerp_argb(int a, int b, float k) {
        return  ((int)(lerp((char)(a >> 24), (char)(b >> 24), k)) << 24) |
                ((int)(lerp((char)(a >> 16), (char)(b >> 16), k)) << 16) |
                ((int)(lerp((char)(a >>  8), (char)(b >>  8), k)) <<  8) |
                ((int)(lerp((char)(a      ), (char)(b      ), k)));
    }

    
    /* Float clamping */
    
    public static final float max(float a, float b) { return Math.max(a, b); }
    public static final float min(float a, float b) { return Math.min(a, b); }
    public static final float clamp(float val, float min, float max) { return Math.min(max, Math.max(min, val)); }
    public static final float absmin(float a, float b) { return Math.abs(a) < Math.abs(b) ? a : b; }
    public static final float absmax(float a, float b) { return Math.abs(a) > Math.abs(b) ? a : b; }
    
    
    /* Int clamping */
    
    public static final int max(int a, int b) { return Math.max(a, b); }
    public static final int min(int a, int b) { return Math.min(a, b); }
    public static final int clamp(int val, int min, int max) { return Math.min(max, Math.max(min, val)); }
    public static final int absmin(int a, int b) { return Math.abs(a) < Math.abs(b) ? a : b; }
    public static final int absmax(int a, int b) { return Math.abs(a) > Math.abs(b) ? a : b; }
    
    public static final long max(long a, long b) { return Math.max(a, b); }
    public static final long min(long a, long b) { return Math.min(a, b); }
    public static final long clamp(long val, long min, long max) { return Math.min(max, Math.max(min, val)); }
    public static final long absmin(long a, long b) { return Math.abs(a) < Math.abs(b) ? a : b; }
    public static final long absmax(long a, long b) { return Math.abs(a) > Math.abs(b) ? a : b; }
    
    
    /* Rounding */
    
    public static final float floor(float f) { return (float) Math.floor(f); }
    public static final float ceil(float f) { return (float) Math.ceil(f); }
    public static final float nearest(float f) { return (int) f; }
    
    public static final int floor_int(float f) { return (int) Math.floor(f); }
    public static final int ceil_int(float f) { return (int) Math.ceil(f); }
    public static final int nearest_int(float f) { return (int) f; }
    
    
    /* Triginometric */
    
    public static final float cos(float f) { return (float) Math.cos(f); }
    public static final float sin(float f) { return (float) Math.sin(f); }
    public static final float tan(float f) { return (float) Math.tan(f); }
    
    public static final float acos(float f) { return (float) Math.acos(f); }
    public static final float asin(float f) { return (float) Math.asin(f); }
    public static final float atan(float f) { return (float) Math.atan(f); }
    
    public static final float atan2(float x, float y) { return (float) Math.atan2(x, y); }
    
    
    /** @return converts degrees to radians */
    public static final float degrees(float degrees) { return (degrees / 180) * PI; }
    /** @return converts radians to degrees */
    public static final float to_degrees(float rads) { return (rads / PI) * 180; }
    /** @return just the input value, this is just to clarify a value is in radians */
    public static final float radians(float rads) { return rads; }
    
    
    /* General math */
    
    /** @return base raised to the power of exponent */
    public static final float pow(float base, float exponent) { return (float) Math.pow(base, exponent); }
    /** @return base raised to the power of 2 */
    public static final float pow2(float base) { return base * base; }
    /** @return the square root of f */
    public static final float sqrt(float f) { return (float) Math.sqrt(f); }
    
    /** @return the eulers number raised to f */
    public static final float exp(float f) { return (float) Math.exp(f); }
    /** @return the natuaral logarithm of f */
    public static final float log(float f) { return (float) Math.log(f); }
    
    
    /* 2D math */
    
    /** @return the length of a vector (x, y) */
    public static final float length(float x, float y) { return (float) Math.hypot(x, y); }
    /** @return the length squared of a vector (x, y) */
    public static final float length2(float x, float y) { return x * x + y * y; }
    /** @return The dot product of the vectors (x0, y0) and (x1, y1) */
    public static final float dot(float x0, float y0, float x1, float y1) { return x0 * x1 + y0 * y1; }
    
    
    /* Other */
    
    /** @return the absolute value of f (equal to the distance from 0) */
    public static final float abs(float f) { return Math.abs(f); }
    /** @return the absolute value of i (equal to the distance from 0) */
    public static final int   abs(int i)   { return Math.abs(i); }
    
    public static final float signof(float f) { return Math.copySign(1, f); }
    public static final float signof(float f, float to) { return Math.copySign(to, f); }
    
    
    /* For stepped integration */
    
    public static final float integrated_lerp(float dt, float a, float b, float k) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static final float integrated_scale(float dt, float a, float scale) { return a + a * (scale - 1) * dt; }
    
    /* Random shit */
    private static Random random = new Random();
    
    public static final float rnd_lerp(float a, float b) { return lerp(a, b, random.nextFloat()); }
    public static final float rndf(float base, float range) { return random.nextFloat() * range + base; }
    public static final float rndf() { return random.nextFloat(); }
    public static final float rndf(float a) { return random.nextFloat() * a; }
}
