package dk.kea.class19.JanniJD.gameengine19;

import android.graphics.Bitmap;
import android.graphics.Color;

public class TestScreen extends Screen
{
    int x = 0;
    int y = 0;
    Bitmap bitmap;

    public TestScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        bitmap = gameEngine.loadBitmanp("bob.png");
    }

    @Override
    public void update(float deltaTime)
    {
        if (gameEngine.isTouchDown(0));
        {
            x = gameEngine.getTouchX(0);
            y = gameEngine.getTouchY(0);
        }
        gameEngine.clearFrameBuffer(Color.GREEN);
        gameEngine.drawBitmap(bitmap, 0, 0, 64, 64, 64, 64);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}