package game2d;

import engine2d.Renderer;
import engine2d.Sprite;
import engine2d.level.Entity;
import engine2d.level.entity.Mob;
import engine2d.level.particle.DamagingParticle;
import java.util.Random;

public class Mine extends Entity {

    private Sprite mSprite;

    public Mine() {
        weight = 4.f;
        mSprite = loadSprite("sprite/mine.png");
    }

    @Override
    public boolean onKill() {
        final float speed = 100f;
        final float size = 0.1f;
        final float mass = .001f;
        final float damage = 0f;

        final float distance = 3f;

        for(float b = 0; b < PI * 2; b += PI / 1000) {
            float rf = rndf();
            float inv_rf = 1 - rf;

            float sin = sin(b);
            float cos = cos(b);
            float d = distance * rf;

            int r1 = 0xFF;
            int r2 = 0x80;
            int g1 = 0xAA;
            int g2 = 0x80;

            int c = 0xFF000000
                    | (int) (r1 * rf + inv_rf * r2) << 16
                    | (int) (g1 * rf + inv_rf * g2) << 8;

            Entity e = new DamagingParticle(
                    this,
                    x + cos * d, y + sin * d, //< position
                    cos * speed, sin * speed, //< motion
                    c, //< color
                    size,
                    mass,
                    damage
            );

            e.setCollisionMask(Entity.MASK_PARTICLE | Entity.MASK_DEFAULT);

            getLevel().add(e);
        }

        return true;
    }

    @Override
    public boolean onCollide(Entity other) {
        if(other instanceof Mob)
            kill();

        return true;
    }

    @Override
    public void onDraw(Renderer r) {
        r.drawSprite(mSprite, cache_x_min, cache_y_min - .2f * height, width, height);
    }
}
