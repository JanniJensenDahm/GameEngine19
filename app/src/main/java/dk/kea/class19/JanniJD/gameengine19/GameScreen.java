package dk.kea.class19.JanniJD.gameengine19;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.List;

public class GameScreen extends Screen
{
    enum State
    {
        Paused,
        Running,
        GameOver
    }

    State state = State.Running;
    Bitmap background;
    Bitmap resume;
    Bitmap gameOver;
    Typeface font;
    World world;
    WorldRenderer renderer;
    String showText;
    Sound bounceSound;
    Sound blockSound;

    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        background = gameEngine.loadBitmanp("background.png");
        resume = gameEngine.loadBitmanp("resume.png");
        gameOver = gameEngine.loadBitmanp("gameover.png");
        font = gameEngine.loadFont("font.ttf");
        bounceSound = gameEngine.loadSound("bounce.wav");
        blockSound = gameEngine.loadSound("blocksplosion.wav");

        world = new World(new CollisionListener()
        {
            @Override
            public void collisionWall()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionPaddle()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionBlock()
            {
                blockSound.play(1);
            }
        });
        renderer = new WorldRenderer(gameEngine, world);
    }

    @Override
    public void update(float deltaTime)
    {
        if (world.lostLife)
        {
            state = State.Paused;
            world.lostLife = false;
        }
        if (world.gameOver)
        {
            state = State.GameOver;
        }
        if (state == State.Paused && gameEngine.isTouchDown(0))
        {
            state = State.Running;
            resume();
        }
        //Restart the game
        if (state == State.GameOver) //To be fixed && gameEngine.isTouchDown(0))
        {
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for (int i = 0; i < events.size(); i++)
            {
                if (events.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
                    return;
                }
            }

        }
        if (state == State.Running && gameEngine.getTouchY(0) < 33 && gameEngine.getTouchX(0) > 320 - 33)
        {
            state = State.Paused;
            pause();
            return;
        }

        gameEngine.drawBitmap(background, 0, 0);

        if (state == State.Running)
        {
            world.update(deltaTime, gameEngine.getAccelerometer()[0], gameEngine.isTouchDown(0), gameEngine.getTouchX(0));
        }
        renderer.render();

        showText = "Lives: " + Integer.toString(world.lives) + "   Points: " + Integer.toString(world.points);
        //Set size and color of points and lives
        gameEngine.drawText(font, showText, 20, 22, Color.GREEN, 11);

        if (state == State.Paused)
        {
            pause();
            gameEngine.drawBitmap(resume, 160 - resume.getWidth() / 2, 240 - resume.getHeight() / 2);
        }
        if (state == State.GameOver)
        {
            pause();
            gameEngine.drawBitmap(gameOver, 160 - gameOver.getWidth() / 2, 240 - gameOver.getHeight() / 2);
        }
    }

    @Override
    public void pause()
    {
        gameEngine.music.pause();
        if (state == State.Running) state = State.Paused;
    }

    @Override
    public void resume()
    {
        gameEngine.music.play();
    }

    @Override
    public void dispose()
    {
        gameEngine.music.pause();
        gameEngine.music.stop();
        gameEngine.music.dispose();
    }
}
