package dk.kea.class19.JanniJD.gameengine19;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class GameEngine extends AppCompatActivity implements Runnable, TouchHandler, SensorEventListener
{
    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Screen screen;
    private Bitmap offscreenSurface;
    private MultiTouchHandler touchHandler;
    private TouchEventPool touchEventPool = new TouchEventPool();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();
    private List<TouchEvent> touchEventCopied = new ArrayList<>();
    private float[] accelerometer = new float[3]; //To hold the g-forces in three dimensions x, y and z
    private SoundPool soundPool = new SoundPool.Builder().setMaxStreams(20).build();


    public abstract Screen createStartScreen();

    public void setScreen(Screen screen)
    {
        this.screen = screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); //Hide title bar
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceHolder = surfaceView.getHolder();
        screen = createStartScreen();

        if (surfaceView.getWidth() > surfaceView.getHeight())
        {
            setOffscreenSurface(480, 320);
        }
        else
        {
            setOffscreenSurface(320, 480);
        }
        touchHandler = new MultiTouchHandler(surfaceView, touchEventBuffer, touchEventPool);
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size()!= 0)
        {
            Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    }

    public void setOffscreenSurface(int width, int height)
    {
        if (offscreenSurface != null)
        {
            offscreenSurface.recycle();
        }
        offscreenSurface = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(offscreenSurface);
    }

    public int getFramebufferWidth()
    {
        return offscreenSurface.getWidth();
    }

    public int getFramebufferHeight()
    {
        return offscreenSurface.getHeight();
    }


    public Bitmap loadBitmanp(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap = null;

        try
        {
            in = getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
            {
                throw new RuntimeException("Could not load bitmap from file " + fileName);
            }
            return bitmap;
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("Could not load bitmap from asset " + fileName);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ioe)
                {
                    throw new RuntimeException("Could not close the file " + fileName);
                }
            }
        }
    }

    public void clearFrameBuffer(int color)
    {
        canvas.drawColor(color);
    }

    public void  drawBitmap(Bitmap bitmap, int x, int y)
    {
        if (canvas != null) canvas.drawBitmap(bitmap, x, y, null);
    }

    Rect src = new Rect();
    Rect dst = new Rect();

    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        if (canvas == null) return;
        src.left = srcX;
        src.top = srcY;
        src.right = srcX + srcWidth;
        src.bottom = srcY + srcHeight;

        dst.left = x;
        dst.top = y;
        dst.right = x + srcWidth;
        dst.bottom = y + srcHeight;

        canvas.drawBitmap(bitmap, src, dst, null);
    }

    public Sound loadSound(String fileName)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName);
            if (assetFileDescriptor == null) throw new RuntimeException("AssetFD null");
            if (soundPool == null) throw new RuntimeException("soundPool null");
            int soundId = soundPool.load(assetFileDescriptor, 0);

            return new Sound(soundPool, soundId);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load sound file: " + fileName);

        }
    }

    public Music loadMusic(String fileName)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName);
            return new Music(assetFileDescriptor);
        }
        catch (IOException e)
        {
            throw new RuntimeException("GameEngine: Could not load Music file: " + fileName);
        }
    }

    public boolean isTouchDown(int pointer)
    {
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer)
    {
        int scaledX = 0;
        scaledX = (int)((float)touchHandler.getTouchX(pointer)*(float)offscreenSurface.getWidth()/(float) surfaceView.getWidth());
        return scaledX;
    }

    public int getTouchY(int pointer)
    {
        int scaledY = 0;
        scaledY = (int)((float)touchHandler.getTouchY(pointer)*(float)offscreenSurface.getHeight()/(float) surfaceView.getHeight());
        return scaledY;
    }

    public float[] getAccelerometer()
    {
        return accelerometer;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, accelerometer, 0, 3);
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
                        state = State.Running;
                    }
                } // End of for-loop
                //stateChanges.clear();

                if (state == State.Running)
                {
                    Log.d("GameEngine", "State is running " + surfaceHolder.getSurface().isValid());
                    if (!surfaceHolder.getSurface().isValid())
                    {
                        continue;
                    }
                    //Log.d("GameEngine", "Trying to get canvas");
                    Canvas canvas = surfaceHolder.lockCanvas();
                    // All the drawing code should happen here
                    // canvas.drawColor(Color.BLUE);
                    if (screen != null)
                    {
                        screen.update(0);
                    }

                    src.left = 0;
                    src.top = 0;
                    src.right = offscreenSurface.getWidth() - 1;
                    src.bottom = offscreenSurface.getHeight() - 1;

                    dst.left = 0;
                    dst.top = 0;
                    dst.right = surfaceView.getWidth();
                    dst.bottom = surfaceView.getHeight();

                    canvas.drawBitmap(offscreenSurface, src, dst, null);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            } // End of synchronized
        } // End of while-loop
    }

    @Override
    public  void onPause()
    {
        super.onPause();
        synchronized (stateChanges)
        {
            if (isFinishing())
            {
                stateChanges.add(State.Disposed);
            }
            else
            {
                stateChanges.add(State.Paused);
            }
        }
        if (isFinishing())
        {
            ((SensorManager)getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
            soundPool.release();
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        synchronized (stateChanges)
        {
            stateChanges.add(stateChanges.size(), State.Resumed);
        }
    }
}
