package sound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundPlayTest {


    @Test
    void play() {
            SoundPlay soundPlay = SoundPlay.getInstance();
            soundPlay.play(SoundType.mainGameBgm);
            SoundPlay.getInstance().setBgmVolume(2);
            assertAll(
                    () -> assertEquals(52, SoundBgm.getVolume())
            );
            SoundBgm.bgmClip.stop();
    }
}