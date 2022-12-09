package sound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundPlayTest {


    @Test
    void play() {
            SoundPlay.getInstance().play(SoundType.enemyKill);
            assertAll(
                    () -> assertNotNull(SoundEffect.effectClip)
            );
    }
}