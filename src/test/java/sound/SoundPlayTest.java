package sound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundPlayTest {


    @Test
    void play() {
        SoundType[] soundTypes = SoundType.values();
        for(int i = 0; i < soundTypes.length; i++){
            SoundPlay.getInstance().play(soundTypes[i]);

            assertAll(
                    () -> assertTrue((!SoundBgm.bgmClip.equals(null) || !SoundEffect.effectClip.equals(null)))
            );
        }
    }
}