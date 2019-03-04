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

    int cut = 0;

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.GREEN);

        /*
        if (gameEngine.isTouchDown(0));
        {
            x = gameEngine.getTouchX(0);
            y = gameEngine.getTouchY(0);
        }
        */
        float x = gameEngine.getAccelerometer()[0];
        float y = -1 * gameEngine.getAccelerometer()[1];
        x = gameEngine.getFramebufferWidth()/2 + ((x/10) * gameEngine.getFramebufferWidth()/2);
        y = gameEngine.getFramebufferHeight()/2 + ((y/10) * gameEngine.getFramebufferHeight()/2);


        gameEngine.drawBitmap(bitmap, (int)x-64, (int)y-64);
        //Where on the screen and how much of the picture to show
        //gameEngine.drawBitmap(bitmap, 200, 200, 64, 64, 64, 64);
        cut++;

        if(cut == 128)
        {
            cut = 0;
        }
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
