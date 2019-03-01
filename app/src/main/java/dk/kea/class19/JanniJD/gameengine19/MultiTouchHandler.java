package dk.kea.class19.JanniJD.gameengine19;

import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class MultiTouchHandler implements TouchHandler, View.OnTouchListener
{
    private boolean[] isTouched = new boolean[20]; // Store first 20 touches
    private int[] touchX = new int[20];
    private int[] touchY = new int[20];

    //private List<TouchEvent> touchEventbuffer; // Buffer with touch events
    //private TouchEventPool touchEventPool;

    @Override
    public boolean isTouchDown(int pointer)
    {
        return false;
    }

    @Override
    public int getTouchX(int pointer)
    {
        return 0;
    }

    @Override
    public int getTouchY(int pointer)
    {
        return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return false;
    }
}
