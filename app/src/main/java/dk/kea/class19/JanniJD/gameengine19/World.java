package dk.kea.class19.JanniJD.gameengine19;

import java.util.ArrayList;
import java.util.List;

public class World
{
    public static float MIN_X = 0;
    public static float MAX_X = 319;
    public static float MIN_Y = 36;
    public static float MAX_Y = 479;

    Ball ball = new Ball();
    Paddle paddle = new Paddle();
    List<Block> blocks = new ArrayList<>();

    public World()
    {
        generateBlocks();
    }

    public  void  update(float deltaTime, float accelX, boolean isTouch, int touchX)
    {
        ball.x = ball.x + ball.velocityX * deltaTime;
        ball.y = ball.y + ball.velocityY * deltaTime;
        if (ball.x < MIN_X)
        {
            ball.velocityX = -ball.velocityX;
            ball.x = MIN_X;
        }
        if (ball.x > MAX_X - Ball.WIDTH)
        {
            ball.velocityX = -ball.velocityX;
            ball.x = MAX_X - Ball.WIDTH;
        }
        if (ball.y < MIN_Y)
        {
            ball.velocityY = -ball.velocityY;
            ball.y = MIN_Y;
        }
        /*
        if (ball.y > MAX_Y - Ball.HEIGHT)
        {
            ball.velocityY = -ball.velocityY;
            ball.y = MAX_Y - Ball.HEIGHT;
        }*/

        //Move paddle based on phone tilt
        paddle.x = paddle.x - accelX * 50 * deltaTime;

        //Move paddle based on touch, only for testing in emulator.
        if (isTouch)
        {
            paddle.x = touchX - paddle.WIDTH/2;
        }

        //Make sure the paddle stops at the edge of the screen
        if (paddle.x < MIN_X) paddle.x = MIN_X;
        if (paddle.x + Paddle.WIDTH > MAX_X) paddle.x = MAX_X - Paddle.WIDTH;

        collideBallPaddle();

    } //End of update() method

    private void collideBallPaddle()
    {
        if (ball.y > paddle.y + Paddle.HEIGHT) return;
        if ((ball.x >= paddle.x) && (ball.x + Ball.WIDTH < paddle.x + Paddle.WIDTH) && (ball.y + Ball.HEIGHT > paddle.y))
        {
            ball.velocityY = -ball.velocityY;
        }
    }

    private void generateBlocks()
    {
        blocks.clear();
        for (int y = 60, type = 0; y < 60 + 8 * (Block.HEIGHT + 4); y = y + (int)Block.HEIGHT, type++)
        {
            for (int x = 20; x < 320 - Block.WIDTH; x = x + (int)Block.WIDTH + 4)
            {
                blocks.add(new Block(x, y, type));
            }
        }
    }
}
