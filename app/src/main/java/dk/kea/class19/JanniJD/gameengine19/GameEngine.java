package dk.kea.class19.JanniJD.gameengine19;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public abstract class GameEngine extends AppCompatActivity implements Runnable
{
    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();

    public abstract Screen createStartScreen();
    public void setScreen(Screen screen){}

    public Bitmap loadBitmanp(String fileName)
    {
        return null;
    }

    public void clearFrameBuffer(int Color)
    {

    }

    public void  drawBitmap(Bitmap bitmap, int x, int y)
    {

    }

    public boolean isTouchDown(int pointer)
    {
        return false;
    }

    public int getTouchX(int pointer)
    {
        return 0;
    }

    public int getTouchY(int pointer)
    {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Main method in thread
    public void run()
    {
        while (true)
        {
            synchronized (stateChanges)
            {
                for (int i = 0; i < stateChanges.size(); i++)
                {
                    state = stateChanges.get(i);
                    if (state == State.Disposed)
                    {
                        Log.d("GameEngine", "State changed to Disposed");
                        return; //Killing the thread
                    }
                    if (state == State.Paused)
                    {
                        Log.d("GameEngine", "State changed to Paused");
                        return;
                    }
                    if (state == State.Resumed)
                    {
                        Log.d("GameEngine", "State changed to Resumed");
                    }
                }
                stateChanges.clear();
            }
        }
    }

    public  void onPause()
    {
        super.onPause();

    }

    public void onResume()
    {
        super.onResume();
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        synchronized (stateChanges)
        {

        }
    }
}
