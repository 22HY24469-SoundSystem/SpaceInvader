package sound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoundPlayTest {


    @Test
    void play() {
        SoundType[] soundTypes = SoundType.values();
        for(int i = 0; i < soundTypes.length; i++){
            final int a = i;
            assertAll(
                    () -> SoundPlay.getInstance().play(soundTypes[a])
            );
        }
    }
}