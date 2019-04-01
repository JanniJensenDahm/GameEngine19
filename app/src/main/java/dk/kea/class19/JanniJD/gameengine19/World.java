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
    CollisionListener collisionListener;

    int lives = 3;
    boolean lostLife = false;
    boolean gameOver = false;
    int level = 1;
    int hits = 0;
    int points = 0;

    public World(CollisionListener collisionListener)
    {
        this.collisionListener = collisionListener;
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
            collisionListener.collisionWall();
        }
        if (ball.x > MAX_X - Ball.WIDTH)
        {
            ball.velocityX = -ball.velocityX;
            ball.x = MAX_X - Ball.WIDTH;
            collisionListener.collisionWall();
        }
        if (ball.y < MIN_Y)
        {
            ball.velocityY = -ball.velocityY;
            ball.y = MIN_Y;
            collisionListener.collisionWall();
        }
        /*
        if (ball.y > MAX_Y - Ball.HEIGHT)
        {
            ball.velocityY = -ball.velocityY;
            ball.y = MAX_Y - Ball.HEIGHT;
        }*/

        if (ball.y > MAX_Y)
        {
            lives = lives - 1;
            lostLife = true;
            ball.x = paddle.x + Paddle.WIDTH/2;
            ball.y = paddle.y + Ball.HEIGHT - 5;
            ball.velocityY = -Ball.initialSpeed;
            if (lives == 0) gameOver = true;
            return;
        }

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

        collideBallPaddle(deltaTime);
        collideBallBlocks(deltaTime);

        if (blocks.size() == 0)
        {
            generateBlocks();
            level++;
            ball.x = 160;
            ball.y = 320 - 40;
            ball.velocityY = -Ball.initialSpeed * 1.3f;
            ball.velocityX = Ball.initialSpeed * 1.1f;
        }

    } //End of update() method

    private void collideBallPaddle(float deltaTime)
    {
        //if (ball.y > paddle.y + Paddle.HEIGHT) return;
        if ((ball.x + Ball.WIDTH >= paddle.x) && (ball.x < paddle.x + Paddle.WIDTH) && (ball.y + Ball.HEIGHT > paddle.y))
        {
            ball.y = ball.y - ball.velocityY * deltaTime * 1.01f;
            ball.velocityY = -ball.velocityY;
            hits++;
            collisionListener.collisionPaddle();
            if (hits == 5 )
            {
                hits = 0;
                if (level == 2)
                {
                    advanceBlocks();
                }
            }

        }
    }

    private void collideBallBlocks(float deltaTime)
    {
        Block block = null;
        for (int i = 0; i < blocks.size(); i++)
        {
            block = blocks.get(i);
            if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                    block.x, block.y, Block.WIDTH, Block.HEIGHT))
            {
                blocks.remove(i);
                float oldVelocityX = ball.velocityX;
                float oldVelocityY = ball.velocityY;

                reflectBall(ball, block);
                //Back out the ball with 1% to avoid mutiple interactions
                ball.x = ball.x - oldVelocityX * deltaTime * 1.01f;
                ball.y = ball.y - oldVelocityY * deltaTime * 1.01f;
                points = points + 10 - block.type;
                collisionListener.collisionBlock();
                //No need to check collision with other blocks when it hit this block
                break;
            }
        }
    }
    private void reflectBall(Ball ball, Block block)
    {
        //Check if the ball hit the top left corner of a block
        if (collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, 1,1))
        {
            if (ball.velocityX > 0) ball.velocityX = -ball.velocityX;
            if (ball.velocityY > 0) ball.velocityY = -ball.velocityY;
            return;
        }
        //Check if the ball hits the top right corker of a block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x + Block.WIDTH, block.y, 1,1))
        {
            if (ball.velocityX < 0) ball.velocityX = -ball.velocityX;
            if (ball.velocityY > 0) ball.velocityY = -ball.velocityY;
            return;
        }
        //Check the bottom left corner of a block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y + Block.HEIGHT, 1,1))
        {
            if (ball.velocityX > 0) ball.velocityX = -ball.velocityX;
            if (ball.velocityY < 0) ball.velocityY = -ball.velocityY;
        }
        //Check the bottom right corner of a block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x + Block.WIDTH, block.y + Block.HEIGHT, 1, 1))
        {
            if (ball.velocityX < 0) ball.velocityX = -ball.velocityX;
            if (ball.velocityY < 0) ball.velocityY = -ball.velocityY;
        }

        //Check the top edge of a block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, Block.WIDTH, 1))
        {
            if (ball.velocityY > 0) ball.velocityY = -ball.velocityY;
            return;

        }
        //Check the bottom edge of the corner
        if (collideRects(ball.x, ball.y, Block.WIDTH, Block.HEIGHT, block.x, block.y + Block.HEIGHT, Block.WIDTH, 1))
        {
            if (ball.velocityY < 0) ball.velocityY = -ball.velocityY;
            return;
        }
        //Check the left edge of a block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, 1, Block.HEIGHT))
        {
            if (ball.velocityX > 0) ball.velocityX = -ball.velocityX;
            return;
        }
        //Check the right edge of a block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x + Block.WIDTH, block.y, 1, Block.HEIGHT))
        {
            if (ball.velocityX < 0) ball.velocityX = -ball.velocityX;
        }
    }

    private boolean collideRects(float x, float y, float width, float height,
                                 float x2, float y2, float width2, float height2)
    {
        if (x < x2 + width2 && x + width > x2 && y < y2 + height2 && y + height > y2)
        {
            return true;
        }
        return false;
    }

    private void generateBlocks()
    {
        blocks.clear();
        for (int y = 60, type = 0; y < 60 + 8 * (Block.HEIGHT + 4); y = y + (int)Block.HEIGHT + 4, type++)
        {
            for (int x = 30; x < 320 - Block.WIDTH; x = x + (int)Block.WIDTH + 4)
            {
                blocks.add(new Block(x, y + level * 40, type));
            }
        }
    }

    private void advanceBlocks()
    {
        Block block;
        int stop = blocks.size();
        for (int i = 0; i < stop; i++)
        {
            block = blocks.get(i);
            block.y = block.y + 10;
        }
    }
}
