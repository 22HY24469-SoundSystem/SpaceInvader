package screen;

import engine.GameSettings;
import engine.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameScreenTest {
    private GameScreen gameScreen;
    @Test
    void update() {
        assertTrue(gameScreen.isRunning);
    }
}