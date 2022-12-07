package screen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameScreenTest {

    @Test
    void initialize() {
        assertEquals(GameScreen.lives, -1);
    }
}