package screen;

import engine.Core;
import engine.Frame;
import engine.GameSettings;
import engine.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameScreenTest {
    private int MAX_LIVES = 3;
    private GameScreen gameScreen;
    private GameState gameState;

    private GameSettings gameSettings;

    @Test
    void update() {
        gameSettings = new GameSettings(1,5, 4, 60, 2000);
        gameState = new GameState(1, 0, MAX_LIVES, 0, 0);
        gameScreen = new GameScreen(gameState, gameSettings, Frame.WIDTH, Frame.HEIGHT, Frame.FRAMEBITS);
        assertTrue(gameScreen.isRunning);
    }
}